/*
 * BlueALSA - cli.c
 * Copyright (c) 2016-2022 Arkadiusz Bokowy
 *
 * This file is a part of bluez-alsa.
 *
 * This project is licensed under the terms of the MIT license.
 *
 */

#if HAVE_CONFIG_H
# include <config.h>
#endif

#include <errno.h>
#include <getopt.h>
#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <strings.h>
#include <sys/param.h>
#include <unistd.h>
#include <pthread.h>

#include <dbus/dbus.h>

#include "shared/dbus-client.h"
#include "shared/defs.h"
#include "shared/hex.h"
#include "shared/log.h"

#include <gio/gio.h>
#include <glib.h>

//#include "dbus.h"
#include <alsa/asoundlib.h>
#include "a2dp_ctl.h"

#define A2DP_SOURCE_UUID	"0000110a-0000-1000-8000-00805f9b34fb"
#define A2DP_SINK_UUID		"0000110b-0000-1000-8000-00805f9b34fb"

#define DEVICE_INTERFACE "org.bluez.Device1"
#define PLAYER_INTERFACE "org.bluez.MediaPlayer1"
#define TRANSPORT_INTERFACE "org.bluez.MediaTransport1"

static GDBusConnection *conn;
static gboolean bluealsa_running = FALSE;

static struct ba_dbus_ctx dbus_ctx;
static char dbus_ba_service[32] = BLUEALSA_SERVICE;
static bool verbose = false;
static bool initFlag = false;
static pthread_mutex_t mtx;
static pthread_mutex_t pcm_mtx;

static snd_pcm_t *pcm_handle_playback = NULL;
static char pcm_device_spk[] = "bluealsa:DEV=00:00:00:00:00:00";
static void (*mconnect_call_back)(char *addr, bool is_sink, A2DP_Event_t event);

typedef struct {
	bool connect_flag;
	struct ba_pcm pcm;
}ba_pcm_status_t;

#define INIT_BA_PCM_NUM 4
typedef struct {
	size_t max_alloc_num;
	ba_pcm_status_t *pcms;
}ba_pcm_info_t;

static ba_pcm_info_t ba_pcm_info;

static gint ifa_signal_handle = 0;
static gint ifr_signal_handle = 0;
static char TRANSPORT_OBJECT[128] = {0};
static GMainLoop *main_loop;
static pthread_t thread_id;


static void signal_interfaces_added(GDBusConnection *conn,
		const gchar *sender,
		const gchar *path,
		const gchar *interface,
		const gchar *signal,
		GVariant *params,
		void *userdata)
{
	GVariantIter *interfaces, *interface_content;
	char *object, *interface_name;


	g_variant_get(params, "(oa{sa{sv}})", &object, &interfaces);

	while (g_variant_iter_next(interfaces, "{sa{sv}}", &interface_name, &interface_content)) {
		if (strcmp(interface_name, TRANSPORT_INTERFACE) == 0) {
			memset(TRANSPORT_OBJECT, 0, sizeof(TRANSPORT_OBJECT));
			info("Media_Transport registered: %s\n", object);
			strncpy(TRANSPORT_OBJECT, object, sizeof(TRANSPORT_OBJECT) -1);
		}

		g_free(interface_name);
		g_variant_iter_free(interface_content);
	}

	g_variant_iter_free(interfaces);
	g_free(object);
}

static void signal_interfaces_removed(GDBusConnection *conn,
		const gchar *sender,
		const gchar *path,
		const gchar *interface,
		const gchar *signal,
		GVariant *params,
		void *userdata)
{
	GVariantIter *interfaces;
	char *object, *interface_name;

	g_variant_get(params, "(oas)", &object, &interfaces);

	while (g_variant_iter_next(interfaces, "s", &interface_name)) {
		if (strcmp(interface_name, TRANSPORT_INTERFACE) == 0) {
			info("Media_Transport unregisterd\n");
			memset(TRANSPORT_OBJECT, 0, sizeof(TRANSPORT_OBJECT));
		}
		g_free(interface_name);
	}

	g_variant_iter_free(interfaces);
	g_free(object);
}

static void subscribe_signals(void)
{
	//we monitor interface added here
	ifa_signal_handle = g_dbus_connection_signal_subscribe(conn, "org.bluez", "org.freedesktop.DBus.ObjectManager",
			"InterfacesAdded", NULL, NULL, G_DBUS_SIGNAL_FLAGS_NONE,
			signal_interfaces_added, NULL, NULL);

	//we monitor interface remove here
	ifr_signal_handle = g_dbus_connection_signal_subscribe(conn, "org.bluez", "org.freedesktop.DBus.ObjectManager",
			"InterfacesRemoved", NULL, NULL, G_DBUS_SIGNAL_FLAGS_NONE,
			signal_interfaces_removed, NULL, NULL);
}

static void unsubscribe_signals(void)
{
	g_dbus_connection_signal_unsubscribe(conn, ifa_signal_handle);
	g_dbus_connection_signal_unsubscribe(conn, ifr_signal_handle);
}

static void *bluez_dbus_thread(void *user_data)
{
	main_loop = g_main_loop_new(NULL, FALSE);
	g_main_loop_run(main_loop);

	return NULL;
}

static int bluez_init() {
	gchar *address;
	GError *err = NULL;

	address = g_dbus_address_get_for_bus_sync(G_BUS_TYPE_SYSTEM, NULL, NULL);
	if (( conn = g_dbus_connection_new_for_address_sync(address,
					G_DBUS_CONNECTION_FLAGS_AUTHENTICATION_CLIENT |
					G_DBUS_CONNECTION_FLAGS_MESSAGE_BUS_CONNECTION,
					NULL, NULL, &err)) == NULL) {
		error("Couldn't obtain D-Bus connection: %s", err->message);
		return -1;
	}

	subscribe_signals();
	if (pthread_create(&thread_id, NULL, bluez_dbus_thread, NULL)) {
		info("thread create failed\n");
		unsubscribe_signals();
		return -1;
	}

	return 0;
}

static int bluez_deinit() {
	unsubscribe_signals();
	conn = NULL;

	if (NULL != main_loop)
		g_main_loop_quit(main_loop);
}

bool pcm_info_init() {
	ba_pcm_info.max_alloc_num = INIT_BA_PCM_NUM;
	ba_pcm_info.pcms = calloc(sizeof(ba_pcm_status_t)*ba_pcm_info.max_alloc_num, 1);
}

bool pcm_info_deinit() {
	ba_pcm_info.max_alloc_num = 0;
	if (ba_pcm_info.pcms) {
		free(ba_pcm_info.pcms);
		ba_pcm_info.pcms = NULL;
	}
}

bool add_pcm(struct ba_pcm *pcm) {
	int idx;

	pthread_mutex_lock(&mtx);
	for (idx = 0; idx < ba_pcm_info.max_alloc_num; idx++) {
		if (!ba_pcm_info.pcms[idx].connect_flag) {
			memcpy(&ba_pcm_info.pcms[idx].pcm, pcm, sizeof(ba_pcm_status_t));
			ba_pcm_info.pcms[idx].connect_flag = true;
			break;
		}
	}
	if (idx == ba_pcm_info.max_alloc_num) {
		int num = idx + 2;
		ba_pcm_status_t *tmp = ba_pcm_info.pcms;

		ba_pcm_info.pcms = realloc(ba_pcm_info.pcms, sizeof(ba_pcm_status_t) * num);
		if (ba_pcm_info.pcms == NULL) {
			error("Couldn't (re)allocate memory for PCM workers: %s", strerror(ENOMEM));
			ba_pcm_info.pcms = tmp;
			return false;
		};

		memcpy(&ba_pcm_info.pcms[idx].pcm, pcm, sizeof(ba_pcm_status_t));
		ba_pcm_info.pcms[idx].connect_flag = true;
		ba_pcm_info.max_alloc_num = num;
	}
	pthread_mutex_unlock(&mtx);
	return true;
}

bool remove_pcm(const char *pcm_path) {
	int idx;
	bool flag;

	pthread_mutex_lock(&mtx);
	for (idx = 0; idx < ba_pcm_info.max_alloc_num; idx++) {
		if (ba_pcm_info.pcms[idx].connect_flag) {
			if (strcmp(ba_pcm_info.pcms[idx].pcm.pcm_path, pcm_path) == 0) {
				ba_pcm_info.pcms[idx].connect_flag = false;
				break;
			}
		}
	}
	flag = idx == ba_pcm_info.max_alloc_num ? false : true;
	pthread_mutex_unlock(&mtx);
	return flag;
}

static const char *transport_code_to_string(int transport_code) {
	switch (transport_code) {
	case BA_PCM_TRANSPORT_A2DP_SOURCE:
		return "A2DP-source";
	case BA_PCM_TRANSPORT_A2DP_SINK:
		return"A2DP-sink";
	case BA_PCM_TRANSPORT_HFP_AG:
		return "HFP-AG";
	case BA_PCM_TRANSPORT_HFP_HF:
		return "HFP-HF";
	case BA_PCM_TRANSPORT_HSP_AG:
		return "HSP-AG";
	case BA_PCM_TRANSPORT_HSP_HS:
		return "HSP-HS";
	case BA_PCM_TRANSPORT_MASK_A2DP:
		return "A2DP";
	case BA_PCM_TRANSPORT_MASK_HFP:
		return "HFP";
	case BA_PCM_TRANSPORT_MASK_HSP:
		return "HSP";
	case BA_PCM_TRANSPORT_MASK_SCO:
		return "SCO";
	case BA_PCM_TRANSPORT_MASK_AG:
		return "AG";
	case BA_PCM_TRANSPORT_MASK_HF:
		return "HF";
	default:
		return "Invalid";
	}
}

static const char *pcm_mode_to_string(int pcm_mode) {
	switch (pcm_mode) {
	case BA_PCM_MODE_SINK:
		return "sink";
	case BA_PCM_MODE_SOURCE:
		return "source";
	default:
		return "Invalid";
	}
}

static const char *pcm_format_to_string(int pcm_format) {
	switch (pcm_format) {
	case 0x0108:
		return "U8";
	case 0x8210:
		return "S16_LE";
	case 0x8318:
		return "S24_3LE";
	case 0x8418:
		return "S24_LE";
	case 0x8420:
		return "S32_LE";
	default:
		return "Invalid";
	}
}

static bool get_pcm(const char *path, struct ba_pcm *pcm) {

	struct ba_pcm *pcms = NULL;
	size_t pcms_count = 0;
	bool found = false;
	size_t i;

	DBusError err = DBUS_ERROR_INIT;
	if (!bluealsa_dbus_get_pcms(&dbus_ctx, &pcms, &pcms_count, &err))
		return false;

	for (i = 0; i < pcms_count; i++)
		if (strcmp(pcms[i].pcm_path, path) == 0) {
			memcpy(pcm, &pcms[i], sizeof(*pcm));
			found = true;
			break;
		}

	free(pcms);
	return found;
}

static bool print_pcm_codecs(const char *path, DBusError *err) {

	DBusMessage *msg = NULL, *rep = NULL;
	bool result = false;
	int count = 0;

	printf("Available codecs:");

	if ((msg = dbus_message_new_method_call(dbus_ctx.ba_service, path,
					BLUEALSA_INTERFACE_PCM, "GetCodecs")) == NULL) {
		dbus_set_error(err, DBUS_ERROR_NO_MEMORY, NULL);
		goto fail;
	}

	if ((rep = dbus_connection_send_with_reply_and_block(dbus_ctx.conn,
					msg, DBUS_TIMEOUT_USE_DEFAULT, err)) == NULL) {
		goto fail;
	}

	DBusMessageIter iter;
	if (!dbus_message_iter_init(rep, &iter)) {
		dbus_set_error(err, DBUS_ERROR_NO_MEMORY, NULL);
		goto fail;
	}

	DBusMessageIter iter_codecs;
	for (dbus_message_iter_recurse(&iter, &iter_codecs);
			dbus_message_iter_get_arg_type(&iter_codecs) != DBUS_TYPE_INVALID;
			dbus_message_iter_next(&iter_codecs)) {

		if (dbus_message_iter_get_arg_type(&iter_codecs) != DBUS_TYPE_DICT_ENTRY) {
			dbus_set_error(err, DBUS_ERROR_FAILED, "Message corrupted");
			goto fail;
		}

		DBusMessageIter iter_codecs_entry;
		dbus_message_iter_recurse(&iter_codecs, &iter_codecs_entry);

		if (dbus_message_iter_get_arg_type(&iter_codecs_entry) != DBUS_TYPE_STRING) {
			dbus_set_error(err, DBUS_ERROR_FAILED, "Message corrupted");
			goto fail;
		}

		const char *codec;
		dbus_message_iter_get_basic(&iter_codecs_entry, &codec);
		printf(" %s", codec);
		++count;

		/* Ignore the properties field, get next codec. */
	}
	result = true;

fail:
	if (count == 0)
		printf(" [ Unknown ]");
	printf("\n");

	if (msg != NULL)
		dbus_message_unref(msg);
	if (rep != NULL)
		dbus_message_unref(rep);
	return result;
}

static void print_adapters(const struct ba_service_props *props) {
	printf("Adapters:");
	for (size_t i = 0; i < props->adapters_len; i++)
		printf(" %s", props->adapters[i]);
	printf("\n");
}

static void print_profiles_and_codecs(const struct ba_service_props *props) {
	printf("Profiles:\n");
	for (size_t i = 0; i < props->profiles_len; i++) {
		printf("  %-11s :", props->profiles[i]);
		size_t len = strlen(props->profiles[i]);
		for (size_t ii = 0; ii < props->codecs_len; ii++)
			if (strncmp(props->codecs[ii], props->profiles[i], len) == 0)
				printf(" %s", &props->codecs[ii][len + 1]);
		printf("\n");
	}
}

static void print_volume(const struct ba_pcm *pcm) {
	if (pcm->channels == 2)
		printf("Volume: L: %u R: %u\n", pcm->volume.ch1_volume, pcm->volume.ch2_volume);
	else
		printf("Volume: %u\n", pcm->volume.ch1_volume);
}

static void print_mute(const struct ba_pcm *pcm) {
	if (pcm->channels == 2)
		printf("Muted: L: %c R: %c\n",
				pcm->volume.ch1_muted ? 'Y' : 'N', pcm->volume.ch2_muted ? 'Y' : 'N');
	else
		printf("Muted: %c\n", pcm->volume.ch1_muted ? 'Y' : 'N');
}

static void print_properties(const struct ba_pcm *pcm, DBusError *err) {
	printf("Device: %s\n", pcm->device_path);
	printf("Sequence: %u\n", pcm->sequence);
	printf("Transport: %s\n", transport_code_to_string(pcm->transport));
	printf("Mode: %s\n", pcm_mode_to_string(pcm->mode));
	printf("Format: %s\n", pcm_format_to_string(pcm->format));
	printf("Channels: %d\n", pcm->channels);
	printf("Sampling: %d Hz\n", pcm->sampling);
	print_pcm_codecs(pcm->pcm_path, err);
	printf("Selected codec: %s\n", pcm->codec);
	printf("Delay: %#.1f ms\n", (double)pcm->delay / 10);
	printf("SoftVolume: %s\n", pcm->soft_volume ? "Y" : "N");
	print_volume(pcm);
	print_mute(pcm);
}

typedef bool (*get_services_cb)(const char *name, void *data);

static bool check_bluealsa_service(const char *name, void *data) {
	bool *result = data;
	if (strcmp(name, BLUEALSA_SERVICE) == 0) {
		*result = true;
		return false;
	}
	*result = false;
	return true;
}

static void get_services(get_services_cb func, void *data, DBusError *err) {

	DBusMessage *msg = NULL, *rep = NULL;

	if ((msg = dbus_message_new_method_call(DBUS_SERVICE_DBUS,
					DBUS_PATH_DBUS, DBUS_INTERFACE_DBUS, "ListNames")) == NULL) {
		dbus_set_error(err, DBUS_ERROR_NO_MEMORY, NULL);
		goto fail;
	}

	if ((rep = dbus_connection_send_with_reply_and_block(dbus_ctx.conn,
					msg, DBUS_TIMEOUT_USE_DEFAULT, err)) == NULL) {
		goto fail;
	}

	DBusMessageIter iter;
	if (!dbus_message_iter_init(rep, &iter)) {
		dbus_set_error(err, DBUS_ERROR_INVALID_SIGNATURE, "Empty response message");
		goto fail;
	}

	DBusMessageIter iter_names;
	for (dbus_message_iter_recurse(&iter, &iter_names);
			dbus_message_iter_get_arg_type(&iter_names) != DBUS_TYPE_INVALID;
			dbus_message_iter_next(&iter_names)) {

		if (dbus_message_iter_get_arg_type(&iter_names) != DBUS_TYPE_STRING) {
			char *signature = dbus_message_iter_get_signature(&iter);
			dbus_set_error(err, DBUS_ERROR_INVALID_SIGNATURE,
					"Incorrect signature: %s != as", signature);
			dbus_free(signature);
			goto fail;
		}

		const char *name;
		dbus_message_iter_get_basic(&iter_names, &name);
		if (!func(name, data)) {
			break;
		}

	}

fail:
	if (msg != NULL)
		dbus_message_unref(msg);
	if (rep != NULL)
		dbus_message_unref(rep);
}

static int parse_pcm_path(const char *pcm_path,  char *addr,  bool *is_sink) {
	// pcm path like:  /org/bluealsa/hci0/dev_E8_07_BF_3A_1F_9E/a2dpsrc/sink
	char *p = strstr(pcm_path, "dev_");
	if (p == NULL) {
		error("invalid pcm path.\n");
		return -1;
	}
	p += 4;

	strncpy(addr, p, 17);
	addr[2] = addr[5] = addr[8] = addr[11] = addr[14] = ':';
	addr[17] = 0;

	p += 18;
	if (!strncmp(p, "a2dpsrc", 7)) {
		*is_sink = true;
	}

	error("yym: pcm path: %s addr: %s, source flag: %d\n", pcm_path, addr, *is_sink);
	return 0;
}

static bool get_current_pcm_by_type(struct ba_pcm *pcm, bool is_sink) {
	size_t i;
	bool found = false;

	pthread_mutex_lock(&mtx);
	for (i = 0; i < ba_pcm_info.max_alloc_num; i++) {
		if (!ba_pcm_info.pcms[i].connect_flag)
			continue;

		info("%s\n", ba_pcm_info.pcms[i].pcm.pcm_path);
		if ((ba_pcm_info.pcms[i].pcm.transport == BA_PCM_TRANSPORT_A2DP_SINK  && is_sink) ||
			(ba_pcm_info.pcms[i].pcm.transport == BA_PCM_TRANSPORT_A2DP_SOURCE  && !is_sink)) {
			memcpy(pcm, &ba_pcm_info.pcms[i].pcm, sizeof (struct ba_pcm));
			found = true;
			break;
		}
	}
	pthread_mutex_unlock(&mtx);
	return found;
}

static bool get_current_pcm_by_addr(struct ba_pcm *pcm, const char *addr) {
	size_t i;
	bool found = false;

	pthread_mutex_lock(&mtx);
	for (i = 0; i < ba_pcm_info.max_alloc_num; i++) {
		if (!ba_pcm_info.pcms[i].connect_flag)
			continue;

		// pcm path like:  /org/bluealsa/hci0/dev_E8_07_BF_3A_1F_9E/a2dpsrc/sink
		char *p = strstr(ba_pcm_info.pcms[i].pcm.pcm_path, "dev_");
		p += 4;

		char bt_addr[18];
		strncpy(bt_addr, p, 17);
		bt_addr[2] = bt_addr[5] = bt_addr[8] = bt_addr[11] = bt_addr[14] = ':';
		bt_addr[17] = 0;

		if (strcmp(bt_addr, addr) == 0) {
			memcpy(pcm, &ba_pcm_info.pcms[i].pcm, sizeof (struct ba_pcm));
			found = true;
			break;
		}
	}
	pthread_mutex_unlock(&mtx);
	return found;
}

static struct ba_pcm *get_ba_pcm(const char *path) {

	size_t i;
	struct ba_pcm *tmp = NULL;
	pthread_mutex_lock(&mtx);
	for (i = 0; i < ba_pcm_info.max_alloc_num; i++) {
		if (!ba_pcm_info.pcms[i].connect_flag)
			continue;
		if (strcmp(ba_pcm_info.pcms[i].pcm.pcm_path, path) == 0) {
			tmp = &ba_pcm_info.pcms[i].pcm;
			break;
		}
	}
	pthread_mutex_unlock(&mtx);
	return tmp;
}

static bool get_adapter_status() {
	struct ba_service_props props = { 0 };

	DBusError err = DBUS_ERROR_INIT;
	if (!bluealsa_dbus_get_props(&dbus_ctx, &props, &err)) {
		error("D-Bus error: %s", err.message);
		bluealsa_dbus_props_free(&props);
		return false;
	}

	printf("Service: %s\n", dbus_ctx.ba_service);
	printf("Version: %s\n", props.version);

	bool ready = props.adapters_len;

	//print_adapters(&props);
	//print_profiles_and_codecs(&props);
	bluealsa_dbus_props_free(&props);

	return ready;
}

static int set_volume(const char *path, int ch1_vol, int ch2_vol) {
	struct ba_pcm pcm;
	if (!get_pcm(path, &pcm)) {
		error("Invalid BlueALSA PCM path: %s", path);
		return EXIT_FAILURE;
	}

	if (pcm.transport & BA_PCM_TRANSPORT_MASK_A2DP) {
		if (ch1_vol < 0 || ch1_vol > 127) {
			error("Invalid volume [0, 127]: %d", ch1_vol);
			return EXIT_FAILURE;
		}
		pcm.volume.ch1_volume = ch1_vol;
		if (pcm.channels == 2) {
			if (ch2_vol < 0 || ch2_vol > 127) {
				error("Invalid volume [0, 127]: %d", ch2_vol);
				return EXIT_FAILURE;
			}
			pcm.volume.ch2_volume = ch2_vol;
		}
	}
	else {
		if (ch1_vol < 0 || ch1_vol > 15) {
			error("Invalid volume [0, 15]: %d", ch1_vol);
			return EXIT_FAILURE;
		}
		pcm.volume.ch1_volume = ch1_vol;
	}

	DBusError err = DBUS_ERROR_INIT;
	if (!bluealsa_dbus_pcm_update(&dbus_ctx, &pcm, BLUEALSA_PCM_VOLUME, &err)) {
		error("Volume loudness update failed: %s", err.message);
		return EXIT_FAILURE;
	}

	return EXIT_SUCCESS;
}

static int set_mute(struct ba_pcm pcm, bool muted) {
	pcm.volume.ch1_muted = pcm.volume.ch2_muted = muted;

	DBusError err = DBUS_ERROR_INIT;
	if (!bluealsa_dbus_pcm_update(&dbus_ctx, &pcm, BLUEALSA_PCM_VOLUME, &err)) {
		error("Volume mute update failed: %s", err.message);
		return EXIT_FAILURE;
	}

	return EXIT_SUCCESS;
}

static int set_softvol(struct ba_pcm pcm, bool softvol) {
	pcm.soft_volume = softvol;

	DBusError err = DBUS_ERROR_INIT;
	if (!bluealsa_dbus_pcm_update(&dbus_ctx, &pcm, BLUEALSA_PCM_SOFT_VOLUME, &err)) {
		error("SoftVolume update failed: %s", err.message);
		return EXIT_FAILURE;
	}

	return EXIT_SUCCESS;
}

static DBusHandlerResult dbus_signal_handler(DBusConnection *conn, DBusMessage *message, void *data) {
	(void)conn;
	(void)data;

	if (dbus_message_get_type(message) != DBUS_MESSAGE_TYPE_SIGNAL)
		return DBUS_HANDLER_RESULT_NOT_YET_HANDLED;

	const char *interface = dbus_message_get_interface(message);
	const char *signal = dbus_message_get_member(message);

	DBusMessageIter iter;
	if (!dbus_message_iter_init(message, &iter))
		goto fail;

	error("dbus signal handler: interface: %s, signal: %s\n", interface, signal);
	if (strcmp(interface, DBUS_INTERFACE_OBJECT_MANAGER) == 0) {

		const char *path;
		if (dbus_message_iter_get_arg_type(&iter) != DBUS_TYPE_OBJECT_PATH)
			goto fail;
		dbus_message_iter_get_basic(&iter, &path);

		if (!dbus_message_iter_next(&iter))
			goto fail;

		if (strcmp(signal, "InterfacesAdded") == 0) {

			DBusMessageIter iter_ifaces;
			for (dbus_message_iter_recurse(&iter, &iter_ifaces);
					dbus_message_iter_get_arg_type(&iter_ifaces) != DBUS_TYPE_INVALID;
					dbus_message_iter_next(&iter_ifaces)) {

				DBusMessageIter iter_iface_entry;
				if (dbus_message_iter_get_arg_type(&iter_ifaces) != DBUS_TYPE_DICT_ENTRY)
					goto fail;
				dbus_message_iter_recurse(&iter_ifaces, &iter_iface_entry);

				const char *iface;
				if (dbus_message_iter_get_arg_type(&iter_iface_entry) != DBUS_TYPE_STRING)
					goto fail;
				dbus_message_iter_get_basic(&iter_iface_entry, &iface);

				if (strcmp(iface, BLUEALSA_INTERFACE_PCM) == 0) {

					printf("yym: PCMAdded %s\n", path);
					char addr[18];
					bool is_sink = false;
					parse_pcm_path(path, addr, &is_sink);

					if (mconnect_call_back)
						mconnect_call_back(addr, is_sink, A2DP_EVENT_CONNECT);

					DBusMessageIter iter2;
					if (!dbus_message_iter_init(message, &iter2))
						goto fail;

					struct ba_pcm pcm;
					DBusError err = DBUS_ERROR_INIT;
					if (!bluealsa_dbus_message_iter_get_pcm(&iter2, &err, &pcm)) {
						error("Couldn't read PCM properties: %s", err.message);
						dbus_error_free(&err);
						goto fail;
					}
					add_pcm(&pcm);
					if (verbose) {
						print_properties(&pcm, &err);
						printf("\n");
					}
				}
				else if (strcmp(iface, BLUEALSA_INTERFACE_RFCOMM) == 0) {
					printf("RFCOMMAdded %s\n", path);
				}
			}

			return DBUS_HANDLER_RESULT_HANDLED;
		}
		else if (strcmp(signal, "InterfacesRemoved") == 0) {

			DBusMessageIter iter_ifaces;
			for (dbus_message_iter_recurse(&iter, &iter_ifaces);
					dbus_message_iter_get_arg_type(&iter_ifaces) != DBUS_TYPE_INVALID;
					dbus_message_iter_next(&iter_ifaces)) {

				const char *iface;
				if (dbus_message_iter_get_arg_type(&iter_ifaces) != DBUS_TYPE_STRING)
					goto fail;
				dbus_message_iter_get_basic(&iter_ifaces, &iface);

				if (strcmp(iface, BLUEALSA_INTERFACE_PCM) == 0) {
					char addr[18];
					bool is_sink = false;
					parse_pcm_path(path, addr, &is_sink);
					if (mconnect_call_back)
						mconnect_call_back(addr, is_sink, A2DP_EVENT_DISCONNECT);
					remove_pcm(path);
					error("yym:  PCMRemoved %s\n", path);
				}
				else if (strcmp(iface, BLUEALSA_INTERFACE_RFCOMM) == 0)
					printf("RFCOMMRemoved %s\n", path);

			}

			return DBUS_HANDLER_RESULT_HANDLED;
		}
	}	else if (strcmp(interface, DBUS_INTERFACE_DBUS) == 0) {
			if (strcmp(signal, "NameOwnerChanged") == 0) {

				const char *arg0 = NULL, *arg1 = NULL, *arg2 = NULL;
				if (dbus_message_iter_init(message, &iter) &&
						dbus_message_iter_get_arg_type(&iter) == DBUS_TYPE_STRING)
					dbus_message_iter_get_basic(&iter, &arg0);
				else
					goto fail;
				if (dbus_message_iter_next(&iter) &&
						dbus_message_iter_get_arg_type(&iter) == DBUS_TYPE_STRING)
					dbus_message_iter_get_basic(&iter, &arg1);
				else
					goto fail;
				if (dbus_message_iter_next(&iter) &&
						dbus_message_iter_get_arg_type(&iter) == DBUS_TYPE_STRING)
					dbus_message_iter_get_basic(&iter, &arg2);
				else
					goto fail;

				if (strcmp(arg0, dbus_ctx.ba_service))
					goto fail;

				if (strlen(arg1) == 0) {
					error("ServiceRunning %s\n", dbus_ctx.ba_service);
					bluealsa_running = true;
					if (mconnect_call_back)
						mconnect_call_back(NULL, false, A2DP_EVENT_SERVICE_START);
				}   else if (strlen(arg2) == 0) {
					bluealsa_running = false;
					error("ServiceStopped %s\n", dbus_ctx.ba_service);
					if (mconnect_call_back)
						mconnect_call_back(NULL, false, A2DP_EVENT_SERVICE_STOP);
				}

				else
					goto fail;

				return DBUS_HANDLER_RESULT_HANDLED;
			}
	} else if (strcmp(interface, DBUS_INTERFACE_PROPERTIES) == 0) {
		const char *path;
		if (dbus_message_iter_get_arg_type(&iter) != DBUS_TYPE_OBJECT_PATH)
			goto fail;
		dbus_message_iter_get_basic(&iter, &path);

		if (!dbus_message_iter_next(&iter))
			goto fail;

		struct ba_pcm *pcm;
		if ((pcm = get_ba_pcm(path)) == NULL)
			goto fail;
		if (!dbus_message_iter_init(message, &iter) ||
				dbus_message_iter_get_arg_type(&iter) != DBUS_TYPE_STRING) {
			error("Couldn't update PCM: %s", "Invalid signal signature");
			goto fail;
		}
		dbus_message_iter_get_basic(&iter, &interface);
		dbus_message_iter_next(&iter);
		if (!bluealsa_dbus_message_iter_get_pcm_props(&iter, NULL, pcm))
			goto fail;

		return DBUS_HANDLER_RESULT_HANDLED;
	}


fail:
	return DBUS_HANDLER_RESULT_NOT_YET_HANDLED;
}

static pthread_t thread_id;
static bool dbus_loop_running = false;
static void *dbus_thread(void *user_data)
{
	while (dbus_loop_running) {
		struct pollfd pfds[10];
		nfds_t pfds_len = ARRAYSIZE(pfds);

		if (!bluealsa_dbus_connection_poll_fds(&dbus_ctx, pfds, &pfds_len)) {
			error("Couldn't get D-Bus connection file descriptors");
			return NULL;
		}

		if (poll(pfds, pfds_len, -1) == -1 &&
				errno == EINTR)
			continue;

		if (bluealsa_dbus_connection_poll_dispatch(&dbus_ctx, pfds, pfds_len))
			while (dbus_connection_dispatch(dbus_ctx.conn) == DBUS_DISPATCH_DATA_REMAINS)
				continue;
	}

	return NULL;
}

int a2dp_ctl_init(void) {
	log_open("a2dp_ctl", false, false);
	pcm_info_init();
	dbus_threads_init_default();

	pthread_mutex_init(&mtx, NULL);
	pthread_mutex_init(&pcm_mtx, NULL);

	DBusError err = DBUS_ERROR_INIT;
	if (!bluealsa_dbus_connection_ctx_init(&dbus_ctx, dbus_ba_service, &err)) {
		error("Couldn't initialize D-Bus context: %s", err.message);
		goto fail1;
	}

	bluealsa_dbus_connection_signal_match_add(&dbus_ctx,
			dbus_ba_service, NULL, DBUS_INTERFACE_OBJECT_MANAGER, "InterfacesAdded",
			"path_namespace='/org/bluealsa'");
	bluealsa_dbus_connection_signal_match_add(&dbus_ctx,
			dbus_ba_service, NULL, DBUS_INTERFACE_OBJECT_MANAGER, "InterfacesRemoved",
			"path_namespace='/org/bluealsa'");
	bluealsa_dbus_connection_signal_match_add(&dbus_ctx,
			dbus_ba_service, NULL, DBUS_INTERFACE_PROPERTIES, "PropertiesChanged",
			"arg0='"BLUEALSA_INTERFACE_PCM"'");

	char dbus_args[50];
	snprintf(dbus_args, sizeof(dbus_args), "arg0='%s',arg2=''", dbus_ctx.ba_service);
	bluealsa_dbus_connection_signal_match_add(&dbus_ctx,
			DBUS_SERVICE_DBUS, NULL, DBUS_INTERFACE_DBUS, "NameOwnerChanged", dbus_args);
	snprintf(dbus_args, sizeof(dbus_args), "arg0='%s',arg1=''", dbus_ctx.ba_service);
	bluealsa_dbus_connection_signal_match_add(&dbus_ctx,
			DBUS_SERVICE_DBUS, NULL, DBUS_INTERFACE_DBUS, "NameOwnerChanged", dbus_args);

	if (!dbus_connection_add_filter(dbus_ctx.conn, dbus_signal_handler, NULL, NULL)) {
		error("Couldn't add D-Bus filter: %s", err.message);
		goto fail;
	}

	bool running = false;
	get_services(check_bluealsa_service, &running, &err);
	if (dbus_error_is_set(&err)) {
		error("D-Bus error: %s", err.message);
		goto fail;
	}

	if (running) {
		bluealsa_running = true;
		printf("ServiceRunning %s\n", dbus_ctx.ba_service);
	} else {
		bluealsa_running = false;
		printf("ServiceStopped %s\n", dbus_ctx.ba_service);
	}

	if (bluealsa_running) {
		struct ba_pcm *pcms = NULL;
		size_t pcms_count = 0;
		int i;
		if (!bluealsa_dbus_get_pcms(&dbus_ctx, &pcms, &pcms_count, &err)) {
			error("Couldn't get BlueALSA PCM list: %s", err.message);
			goto fail;
		}

		for (i=0; i < pcms_count; i++) {
			add_pcm(&pcms[i]);
		}
		if (pcms) {
			free(pcms);
			pcms = NULL;
		}
	}

	dbus_loop_running = true;
	if (pthread_create(&thread_id, NULL, dbus_thread, NULL)) {
		error("thread create failed\n");
		goto fail;
	}
	initFlag = true;

	bluez_init();
	return 0;

fail:
	bluealsa_dbus_connection_ctx_free(&dbus_ctx);
fail1:
	pcm_info_deinit();
	pthread_mutex_destroy(&mtx);
	pthread_mutex_destroy(&pcm_mtx);
	bluealsa_running = false;
	return EXIT_FAILURE;
}

void a2dp_ctl_deinit(void) {
	if (initFlag) return;

	dbus_loop_running = false;
	bluealsa_running = false;
	pcm_info_deinit();

	pthread_mutex_destroy(&mtx);
	pthread_mutex_destroy(&pcm_mtx);
	bluealsa_dbus_connection_ctx_free(&dbus_ctx);

	bluez_deinit();
	initFlag = false;
}

static bool check_conn_dev_valid(const char *baddr, char *obj) {
	if (strlen(baddr) != strlen("xx:xx:xx:xx:xx:xx")) {
		error("Bad bddr\n");
		return false;
	}

	char addr[32];
	strcpy(addr, baddr);
	addr[2] = addr[5] = addr[8] = addr[11] = addr[14] = '_';
	sprintf(obj, "/org/bluez/hci0/dev_%s", addr);
	return true;
}

static int conn_with_bluez(char *obj, char * interface, char *method, GVariant *param, bool resultFlag, GVariant **result) {
	GVariant *tmp;
	int ret = 1;
	GError *error = NULL;

	if (NULL == conn) {
		error("No connection!! Please init first\n");
		return -1;
	}

	error("Target obj: %s\n", obj);
	tmp = g_dbus_connection_call_sync(conn,
			"org.bluez",
			obj,
			interface,
			method,
			param,
			NULL,
			G_DBUS_CALL_FLAGS_NONE,
			-1,
			NULL,
			&error);

	if (tmp == NULL) {
		error("Error: %s\n", error->message);
		g_error_free (error);
		return ret;
	} else {
		if (resultFlag) {
			*result = tmp;
		} else {
			g_variant_unref(tmp);
		}
	}
	error("connect bluez successfully\n");

	return 0;
}

static int device_player_action(struct ba_pcm ba_pcm, const char *action) {

  DBusMessage *msg = NULL, *rep = NULL;
  DBusError err = DBUS_ERROR_INIT;
  char path[160];
  int ret = 0;

  snprintf(path, sizeof(path), "%s/player0", ba_pcm.device_path);
  msg = dbus_message_new_method_call("org.bluez", path,
                                     "org.bluez.MediaPlayer1", action);

  if ((rep = dbus_connection_send_with_reply_and_block(
           dbus_ctx.conn, msg, DBUS_TIMEOUT_USE_DEFAULT, &err)) == NULL) {
    warn("Couldn't pause player: %s", err.message);
    dbus_error_free(&err);
    goto fail;
  }

  debug("Requested playback pause");
  goto final;

fail:
  ret = -1;

final:
  if (msg != NULL)
    dbus_message_unref(msg);
  if (rep != NULL)
    dbus_message_unref(rep);
  return ret;
}


static int modify_tansport_volume_property(gboolean up)
{
	GVariant *result = NULL, *child = NULL, *parameters = NULL;
	int value = 0, temp = 0, ret = -1;
	GError *error = NULL;

	parameters = g_variant_new("(ss)", TRANSPORT_INTERFACE, "Volume");
	/*------------------read volume-----------------------------------------------*/
	if (conn_with_bluez(TRANSPORT_OBJECT, "org.freedesktop.DBus.Properties", "Get", parameters, true, &result)) {
		g_variant_unref(parameters);
		info("volume read failed\n");
		return -1;
	}

	debug("result: %s\n", g_variant_print(result, TRUE));
	debug("result type : %s\n", g_variant_get_type_string(result));
	g_variant_get(result, "(v)", &child);
	g_variant_get(child, "q", &value);

	/*-------------------modify value--------------------------------------------*/
	temp = value;
	value = up ? (value + 10) : (value - 10);

	//volume rang from 0~127
	value = value > 127 ? 127 : value;
	value = value > 0   ? value : 0;
	info("volume set: %u->%u\n", temp, value);

	g_variant_unref(child);
	g_variant_unref(parameters);
	g_variant_unref(result);

	/*------------------set volume-----------------------------------------------*/
	child = g_variant_new_uint16(value);
	parameters = g_variant_new("(ssv)", TRANSPORT_INTERFACE, "Volume", child);
	if (conn_with_bluez(TRANSPORT_OBJECT, "org.freedesktop.DBus.Properties", "Set", parameters, true, &result)) {
		info("volume set failed\n");
		return -1;
	}

	g_variant_unref(child);
	g_variant_unref(parameters);
	g_variant_unref(result);
	return ret;
}

int a2dp_control(A2DP_Control_t control) {
	if (!bluealsa_running) return -1;
	struct ba_pcm pcm;
	char ctrl_string[16];

	if (control == A2DP_CTRL_PLAY) {
		strcpy(ctrl_string, "Play");
	} else if (control == A2DP_CTRL_PAUSE) {
		strcpy(ctrl_string, "Pause");
	} else if (control == A2DP_CTRL_STOP) {
		strcpy(ctrl_string, "Stop");
	} else if (control == A2DP_CTRL_NEXT) {
		strcpy(ctrl_string, "Next");
	} else if (control == A2DP_CTRL_PRE) {
		strcpy(ctrl_string, "Previous");
	} else if (control == A2DP_CTRL_VOLUP) {
		return modify_tansport_volume_property(true);
	} else if (control == A2DP_CTRL_VOLDOWN) {
		return modify_tansport_volume_property(false);
	} else {
		error ("invalid control %d\n", control);
		return -1;
	}
	if (get_current_pcm_by_type(&pcm, true)) {
		device_player_action(pcm, ctrl_string);
	}
	return 0;
}

bool adapter_ready(void) {
	if (!bluealsa_running) return -1;
	return get_adapter_status();
}

int adapter_scan(int onoff) {
	if (!bluealsa_running) return -1;
	return 0;
}

int disconnect_dev(const char *baddr)
{
	if (!bluealsa_running) return -1;

	char obj[256] = {0};

	if (!check_conn_dev_valid(baddr, obj))
		return -1;

	if (conn_with_bluez(obj, DEVICE_INTERFACE, "Disconnect", NULL, false, NULL))
		return -1;

	return 0;
}

int connect_dev(const char* baddr, bool is_sink)
{
	if (!bluealsa_running) return -1;

	char obj[256] = {0};
	int idx;

	for (idx = 0; idx < ba_pcm_info.max_alloc_num; idx++) {
		if (ba_pcm_info.pcms[idx].connect_flag) {
			char addr[18];
			bool sinkFlag;
			parse_pcm_path(ba_pcm_info.pcms[idx].pcm.pcm_path, addr, &sinkFlag);
			if (strcmp(addr, baddr) == 0) {
				error("device : %s connected\n", baddr);
				return 0;
			}
		}
	}

	if (!check_conn_dev_valid(baddr, obj))
		return -1;

	GVariant *param;
	/*if we are central, we should connect target's sink uuid*/
	if (is_sink) //strcmp(device_mode, "central") == 0)
		param = g_variant_new("(s)", A2DP_SINK_UUID);
	else
		param = g_variant_new("(s)", A2DP_SOURCE_UUID);

	if (conn_with_bluez(obj, DEVICE_INTERFACE, "ConnectProfile", param, false, NULL))
		return -1;

	return 0;
}

unsigned char get_connect_status(A2DP_Dev_t **dev, int *cnt) {
	if (!bluealsa_running) return -1;

	struct ba_pcm pcm;
	size_t connect_cnt = 0;
	int i;

	for (i = 0; i < ba_pcm_info.max_alloc_num; i++) {
		if (ba_pcm_info.pcms[i].connect_flag) {
			connect_cnt++;
		}
	}
	if (!connect_cnt) return false;

	A2DP_Dev_t *dev_info = calloc(sizeof(A2DP_Dev_t) * connect_cnt, 1);
	int idx = 0;;

	for (i = 0; i < ba_pcm_info.max_alloc_num; i++) {
		error("yym: pcm_path: %s\n", ba_pcm_info.pcms[i].pcm.pcm_path);
		if (!ba_pcm_info.pcms[i].connect_flag)
			continue;

		parse_pcm_path(ba_pcm_info.pcms[i].pcm.pcm_path, dev_info[idx].addr, &dev_info[idx].is_sink);
		idx++;
	}
	*cnt = connect_cnt;
	*dev = dev_info;
	return true;
}

int pcm_bluealsa_open(const char *bddr) {
	if (!bluealsa_running) return -1;

	int ret = -1;

	pthread_mutex_lock(&pcm_mtx);
	if (pcm_handle_playback) {
		error("device opened: %s, close before reopen\n", pcm_device_spk);
		pcm_bluealsa_close();
	}

	snprintf(pcm_device_spk, sizeof(pcm_device_spk), "bluealsa:DEV=%s", bddr);
	info("yym: %s %s\n", __func__, pcm_device_spk);
	ret = snd_pcm_open(&pcm_handle_playback, pcm_device_spk,
						  SND_PCM_STREAM_PLAYBACK,	/*speaker as output device*/
						  SND_PCM_NONBLOCK);						/*NOBLOCK MODE*/
	if (ret < 0) {
		error("speaker open %s fail:%s\n", pcm_device_spk, strerror(errno));
		pthread_mutex_unlock(&pcm_mtx);
		return ret;
	}

	ret = snd_pcm_set_params(pcm_handle_playback, SND_PCM_FORMAT_S16_LE,
								  SND_PCM_ACCESS_RW_INTERLEAVED,
								  2,			  /*1 channel*/
								  48000,		  /*8k sample rete*/
								  1,			  /*allow alsa resample*/
								  500000);		  /*expected max latence = 500ms*/
	if (ret < 0) {
		error("snd_pcm_set_params %s fail:%s\n", pcm_device_spk, strerror(errno));
	}
	pthread_mutex_unlock(&pcm_mtx);
	return ret;
}

int pcm_bluealsa_close() {
	if (!bluealsa_running) return -1;

	int ret;
	pthread_mutex_lock(&pcm_mtx);
	if (pcm_handle_playback == NULL) {
		error("%s already closed\n", __func__);
		pthread_mutex_unlock(&pcm_mtx);
		return 0;
	}
	snd_pcm_drop(pcm_handle_playback);

	ret = snd_pcm_close(pcm_handle_playback);
	if (ret < 0)
		error("speaker close fail:%s\n", strerror(errno));

	pcm_handle_playback = NULL;
	pthread_mutex_unlock(&pcm_mtx);
	return 0;
}

int pcm_bluealsa_write(void *buf, size_t bytes) {
	if (!bluealsa_running) return -1;

	pthread_mutex_lock(&pcm_mtx);
	if (pcm_handle_playback == NULL) {
		error("%s dev not open\n", __func__);
		pthread_mutex_unlock(&pcm_mtx);
		return 0;
	}
	snd_pcm_uframes_t frames = bytes / 4;
	int ret;

	ret = snd_pcm_writei(pcm_handle_playback, buf, frames);
	/*if write failed somehow, just ignore, we don't want to wast too much time*/
	if (ret == -EPIPE) {
		error("speaker write underrun\n");
		snd_pcm_prepare(pcm_handle_playback);
	} else if (ret == -EBADFD) {
		error("speaker write  EBADFD\n");
	}
	pthread_mutex_unlock(&pcm_mtx);
	return ret;
}

void register_callback(void (*conn_cb)(char *addr, bool is_source, A2DP_Event_t type)){
	mconnect_call_back = conn_cb;
}

bool  is_bluealsa_ready() {
	return bluealsa_running;
}

