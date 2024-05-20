/*
 * BlueALSA - aplay.c
 * Copyright (c) 2016-2021 Arkadiusz Bokowy
 *
 * This file is a part of bluez-alsa.
 *
 * This project is licensed under the terms of the MIT license.
 *
 */

#if HAVE_CONFIG_H
# include <config.h>
#endif

#include <assert.h>
#include <errno.h>
#include <getopt.h>
#include <math.h>
#include <poll.h>
#include <pthread.h>
#include <signal.h>
#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

#include <alsa/asoundlib.h>
#include <bluetooth/bluetooth.h>
#include <dbus/dbus.h>

#include "shared/dbus-client.h"
#include "shared/defs.h"
#include "shared/ffb.h"
#include "shared/log.h"
#include "../aplay/alsa-mixer.h"
#include "../aplay/alsa-pcm.h"
#include "../aplay/dbus.h"

#include "audio_if.h"

struct pcm_worker {
	pthread_t thread;
	/* used BlueALSA PCM device */
	struct ba_pcm ba_pcm;
	/* file descriptor of PCM FIFO */
	int ba_pcm_fd;
	/* file descriptor of PCM control */
	int ba_pcm_ctrl_fd;
	/* opened playback PCM device */
//	snd_pcm_t *pcm;
	/* mixer for volume control */
//	snd_mixer_t *mixer;
//	snd_mixer_elem_t *mixer_elem;
	/* if true, playback is active */
	bool active;
	/* human-readable BT address */
	char addr[18];
};

/* handler for aml audio server */
static audio_hw_device_t *hal_audio_device = NULL;
static struct audio_stream_out *hal_audio_stream = NULL;
static struct audio_config hal_audio_config;
static bool  audio_hal_open_flag = false;

static unsigned int verbose = 0;
static bool list_bt_devices = false;
static bool list_bt_pcms = false;
static const char *pcm_device = "default";
static const char *mixer_device = "default";
static const char *mixer_elem_name = "Master";
static unsigned int mixer_elem_index = 0;
static bool ba_profile_a2dp = true;
static bool ba_addr_any = false;
static bdaddr_t *ba_addrs = NULL;
static size_t ba_addrs_count = 0;
static unsigned int pcm_buffer_time = 500000;
static unsigned int pcm_period_time = 100000;
static bool pcm_mixer = true;

static struct ba_dbus_ctx dbus_ctx;
static char dbus_ba_service[32] = BLUEALSA_SERVICE;

static struct ba_pcm *ba_pcms = NULL;
static size_t ba_pcms_count = 0;

static pthread_rwlock_t workers_lock = PTHREAD_RWLOCK_INITIALIZER;
static struct pcm_worker *workers = NULL;
static size_t workers_count = 0;
static size_t workers_size = 0;

static bool main_loop_on = true;
static void main_loop_stop(int sig) {
	/* Call to this handler restores the default action, so on the
	 * second call the program will be forcefully terminated. */

	struct sigaction sigact = { .sa_handler = SIG_DFL };
	sigaction(sig, &sigact, NULL);

	main_loop_on = false;
}

static const char *bluealsa_get_profile(const struct ba_pcm *pcm) {
	switch (pcm->transport) {
	case BA_PCM_TRANSPORT_A2DP_SOURCE:
	case BA_PCM_TRANSPORT_A2DP_SINK:
		return "A2DP";
	case BA_PCM_TRANSPORT_HFP_AG:
	case BA_PCM_TRANSPORT_HFP_HF:
	case BA_PCM_TRANSPORT_HSP_AG:
	case BA_PCM_TRANSPORT_HSP_HS:
		return "SCO";
	default:
		error("Unknown transport: %#x", pcm->transport);
		return "[...]";
	}
}

static snd_pcm_format_t bluealsa_get_snd_pcm_format(const struct ba_pcm *pcm) {
	switch (pcm->format) {
	case 0x0108:
		return SND_PCM_FORMAT_U8;
	case 0x8210:
		return SND_PCM_FORMAT_S16_LE;
	case 0x8318:
		return SND_PCM_FORMAT_S24_3LE;
	case 0x8418:
		return SND_PCM_FORMAT_S24_LE;
	case 0x8420:
		return SND_PCM_FORMAT_S32_LE;
	default:
		error("Unknown PCM format: %#x", pcm->format);
		return SND_PCM_FORMAT_UNKNOWN;
	}
}

static void audio_hal_close_stream() {
	error("close output speaker...\n");
	if (hal_audio_device && hal_audio_stream) {
		hal_audio_device->close_output_stream(hal_audio_device, hal_audio_stream);
		hal_audio_stream = NULL;
	}

	audio_hal_open_flag = false;
}

static bool audio_hal_open_stream(struct pcm_worker *w) {

	debug("Used configuration:\n"
		  "  Sampling rate: %u Hz\n"
		  "  Channels: %u\n",
	w->ba_pcm.sampling, w->ba_pcm.channels);
	memset(&hal_audio_config, 0, sizeof(hal_audio_config));
	hal_audio_config.sample_rate = w->ba_pcm.sampling;
	hal_audio_config.channel_mask = AUDIO_CHANNEL_OUT_STEREO;
	hal_audio_config.format = AUDIO_FORMAT_PCM_16_BIT;

	debug("open hal_audio_device speaker...\n");
	if (hal_audio_device->open_output_stream(hal_audio_device,
			0, AUDIO_DEVICE_OUT_SPEAKER,
			AUDIO_OUTPUT_FLAG_PRIMARY, &hal_audio_config,
			&hal_audio_stream, NULL)) {
		error("output speaker open failed\n");
		return false;
	}

	int ret = hal_audio_stream->common.standby(&hal_audio_stream->common);
	if (ret) {
		error("hal_audio_stream standby error: %s", strerror(errno));
		audio_hal_close_stream();
		return false;
	}
	audio_hal_open_flag = true;
#if 0
	err = hal_audio_stream->set_volume(hal_audio_stream, 0.8f, 0.8f);
	if (err) {
		fprintf(stderr, "%s %d, err:%x\n", __func__, __LINE__, err);
		return -1;
	}
#endif
	return true;
}

static void print_bt_device_list(void) {

	static const struct {
		const char *label;
		unsigned int mode;
	} section[2] = {
		{ "**** List of PLAYBACK Bluetooth Devices ****", BA_PCM_MODE_SINK },
		{ "**** List of CAPTURE Bluetooth Devices ****", BA_PCM_MODE_SOURCE },
	};

	const char *tmp;
	size_t i, ii;

	for (i = 0; i < ARRAYSIZE(section); i++) {
		printf("%s\n", section[i].label);
		for (ii = 0, tmp = ""; ii < ba_pcms_count; ii++) {

			struct ba_pcm *pcm = &ba_pcms[ii];
			struct bluez_device dev = { 0 };

			if (!(pcm->mode == section[i].mode))
				continue;

			if (strcmp(pcm->device_path, tmp) != 0) {
				tmp = ba_pcms[ii].device_path;

				DBusError err = DBUS_ERROR_INIT;
				if (dbus_bluez_get_device(dbus_ctx.conn, pcm->device_path, &dev, &err) == -1) {
					warn("Couldn't get BlueZ device properties: %s", err.message);
					dbus_error_free(&err);
				}

				char bt_addr[18];
				ba2str(&dev.bt_addr, bt_addr);

				printf("%s: %s [%s], %s%s\n",
					dev.hci_name, bt_addr, dev.name,
					dev.trusted ? "trusted ": "", dev.icon);

			}

			printf("  %s (%s): %s %d channel%s %d Hz\n",
				bluealsa_get_profile(pcm),
				pcm->codec,
				snd_pcm_format_name(bluealsa_get_snd_pcm_format(pcm)),
				pcm->channels, pcm->channels != 1 ? "s" : "",
				pcm->sampling);

		}
	}

}

static void print_bt_pcm_list(void) {

	DBusError err = DBUS_ERROR_INIT;
	struct bluez_device dev = { 0 };
	const char *tmp = "";
	size_t i;

	for (i = 0; i < ba_pcms_count; i++) {
		struct ba_pcm *pcm = &ba_pcms[i];

		if (strcmp(pcm->device_path, tmp) != 0) {
			tmp = ba_pcms[i].device_path;
			if (dbus_bluez_get_device(dbus_ctx.conn, pcm->device_path, &dev, &err) == -1) {
				warn("Couldn't get BlueZ device properties: %s", err.message);
				dbus_error_free(&err);
			}
		}

		char bt_addr[18];
		ba2str(&dev.bt_addr, bt_addr);

		printf(
				"bluealsa:SRV=%s,DEV=%s,PROFILE=%s\n"
				"    %s, %s%s, %s\n"
				"    %s (%s): %s %d channel%s %d Hz\n",
			dbus_ba_service,
			bt_addr,
			pcm->transport & BA_PCM_TRANSPORT_MASK_A2DP ? "a2dp" : "sco",
			dev.name,
			dev.trusted ? "trusted ": "", dev.icon,
			pcm->mode == BA_PCM_MODE_SINK ? "playback" : "capture",
			bluealsa_get_profile(pcm),
			pcm->codec,
			snd_pcm_format_name(bluealsa_get_snd_pcm_format(pcm)),
			pcm->channels, pcm->channels != 1 ? "s" : "",
			pcm->sampling);
	}

}

static struct ba_pcm *get_ba_pcm(const char *path) {

	size_t i;

	for (i = 0; i < ba_pcms_count; i++)
		if (strcmp(ba_pcms[i].pcm_path, path) == 0)
			return &ba_pcms[i];

	return NULL;
}

static struct pcm_worker *get_active_worker(void) {

	struct pcm_worker *w = NULL;
	size_t i;

	pthread_rwlock_rdlock(&workers_lock);

	for (i = 0; i < workers_count; i++)
		if (workers[i].active) {
			w = &workers[i];
			break;
		}

	pthread_rwlock_unlock(&workers_lock);

	return w;
}

static int pause_device_player(const struct ba_pcm *ba_pcm) {

	DBusMessage *msg = NULL, *rep = NULL;
	DBusError err = DBUS_ERROR_INIT;
	char path[160];
	int ret = 0;

	snprintf(path, sizeof(path), "%s/player0", ba_pcm->device_path);
	msg = dbus_message_new_method_call("org.bluez", path, "org.bluez.MediaPlayer1", "Pause");

	if ((rep = dbus_connection_send_with_reply_and_block(dbus_ctx.conn, msg,
					DBUS_TIMEOUT_USE_DEFAULT, &err)) == NULL) {
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


static void pcm_worker_routine_exit(struct pcm_worker *worker) {
	if (worker->ba_pcm_fd != -1) {
		close(worker->ba_pcm_fd);
		worker->ba_pcm_fd = -1;
	}
	if (worker->ba_pcm_ctrl_fd != -1) {
		close(worker->ba_pcm_ctrl_fd);
		worker->ba_pcm_ctrl_fd = -1;
	}
	audio_hal_close_stream();
	debug("Exiting PCM worker %s", worker->addr);
}

static void *pcm_worker_routine(struct pcm_worker *w) {

	snd_pcm_format_t pcm_format = bluealsa_get_snd_pcm_format(&w->ba_pcm);
	ssize_t pcm_format_size = 2; //* w->ba_pcm.channels;//snd_pcm_format_size(pcm_format, 1);
	size_t pcm_1s_samples = w->ba_pcm.sampling * w->ba_pcm.channels;
	ffb_t buffer = { 0 };

	/* Cancellation should be possible only in the carefully selected place
	 * in order to prevent memory leaks and resources not being released. */
	pthread_setcancelstate(PTHREAD_CANCEL_DISABLE, NULL);

	pthread_cleanup_push(PTHREAD_CLEANUP(pcm_worker_routine_exit), w);
	pthread_cleanup_push(PTHREAD_CLEANUP(ffb_free), &buffer);

	/* create buffer big enough to hold 100 ms of PCM data */
	if (ffb_init(&buffer, pcm_1s_samples / 10, pcm_format_size) == -1) {
		error("Couldn't create PCM buffer: %s", strerror(errno));
		goto fail;
	}

	DBusError err = DBUS_ERROR_INIT;
	if (!bluealsa_dbus_pcm_open(&dbus_ctx, w->ba_pcm.pcm_path,
				&w->ba_pcm_fd, &w->ba_pcm_ctrl_fd, &err)) {
		error("Couldn't open PCM: %s", err.message);
		dbus_error_free(&err);
		goto fail;
	}

	error("create worker routine for: %s\n", w->ba_pcm.pcm_path);

	/* Initialize the max read length to 10 ms. Later, when the PCM device
	 * will be opened, this value will be adjusted to one period size. */
	size_t pcm_max_read_len_init = pcm_1s_samples / 100 * pcm_format_size;
	size_t pcm_max_read_len = pcm_max_read_len_init;
	size_t pcm_open_retries = 0;

	/* These variables determine how and when the pause command will be send
	 * to the device player. In order not to flood BT connection with AVRCP
	 * packets, we are going to send pause command every 0.5 second. */
	size_t pause_threshold = pcm_1s_samples / 2 * pcm_format_size;
	size_t pause_counter = 0;
	size_t pause_bytes = 0;

	struct pollfd pfds[] = {{ w->ba_pcm_fd, POLLIN, 0 }};
	int timeout = -1;

	debug("Starting PCM loop");
	while (main_loop_on) {
		pthread_setcancelstate(PTHREAD_CANCEL_ENABLE, NULL);

		ssize_t ret;

		/* Reading from the FIFO won't block unless there is an open connection
		 * on the writing side. However, the server does not open PCM FIFO until
		 * a transport is created. With the A2DP, the transport is created when
		 * some clients (BT device) requests audio transfer. */
		switch (poll(pfds, ARRAYSIZE(pfds), timeout)) {
		case -1:
			if (errno == EINTR)
				continue;
			error("PCM FIFO poll error: %s", strerror(errno));
			goto fail;
		case 0:
			debug("Device marked as inactive: %s", w->addr);
			pthread_setcancelstate(PTHREAD_CANCEL_DISABLE, NULL);
			pcm_max_read_len = pcm_max_read_len_init;
			pause_counter = pause_bytes = 0;
			ffb_rewind(&buffer);
			audio_hal_close_stream();
			w->active = false;
			timeout = -1;
			continue;
		}

		/* FIFO has been terminated on the writing side */
		if (pfds[0].revents & POLLHUP)
			break;

		#define MIN(a,b) a < b ? a : b
		size_t _in = MIN(pcm_max_read_len, ffb_blen_in(&buffer));
		if ((ret = read(w->ba_pcm_fd, buffer.tail, _in)) == -1) {
			if (errno == EINTR)
				continue;
			error("PCM FIFO read error: %s", strerror(errno));
			goto fail;
		}

		pthread_setcancelstate(PTHREAD_CANCEL_DISABLE, NULL);

		/* If PCM mixer is disabled, check whether we should play audio. */
		#if 0
		if (!pcm_mixer) {
			struct pcm_worker *worker = get_active_worker();
			if (worker != NULL && worker != w) {
				if (pause_counter < 5 && (pause_bytes += ret) > pause_threshold) {
					if (pause_device_player(&w->ba_pcm) == -1)
						/* pause command does not work, stop further requests */
						pause_counter = 5;
					pause_counter++;
					pause_bytes = 0;
					timeout = 100;
				}
				continue;
			}
		}
		#endif

		if (!audio_hal_open_flag) {

			unsigned int buffer_time = pcm_buffer_time;
			unsigned int period_time = pcm_period_time;
			//snd_pcm_uframes_t buffer_size;
			snd_pcm_uframes_t period_size = 128*8;
			char *tmp;

			/* After PCM open failure wait one second before retry. This can not be
			 * done with a single sleep() call, because we have to drain PCM FIFO. */
			if (pcm_open_retries++ % 20 != 0) {
				usleep(50000);
				continue;
			}
			audio_hal_open_stream(w);

#if 0
			if (alsa_pcm_open(&w->pcm, pcm_device, pcm_format, w->ba_pcm.channels,
						w->ba_pcm.sampling, &buffer_time, &period_time, &tmp) != 0) {
				warn("Couldn't open PCM: %s", tmp);
				pcm_max_read_len = pcm_max_read_len_init;
				usleep(50000);
				free(tmp);
				continue;
			}

			if (alsa_mixer_open(&w->mixer, &w->mixer_elem,
						mixer_device, mixer_elem_name, mixer_elem_index, &tmp) != 0) {
				warn("Couldn't open mixer: %s", tmp);
				free(tmp);
			}

			/* initial volume synchronization */
			pcm_worker_mixer_volume_sync(w, &w->ba_pcm);

			snd_pcm_get_params(w->pcm, &buffer_size, &period_size);
#endif
			pcm_max_read_len = period_size * w->ba_pcm.channels * pcm_format_size;
			pcm_open_retries = 0;
#if 0
			if (verbose >= 2) {
				printf("Used configuration for %s:\n"
						"  PCM buffer time: %u us (%zu bytes)\n"
						"  PCM period time: %u us (%zu bytes)\n"
						"  PCM format: %s\n"
						"  Sampling rate: %u Hz\n"
						"  Channels: %u\n",
						w->addr,
						buffer_time, snd_pcm_frames_to_bytes(w->pcm, buffer_size),
						period_time, snd_pcm_frames_to_bytes(w->pcm, period_size),
						snd_pcm_format_name(pcm_format),
						w->ba_pcm.sampling,
						w->ba_pcm.channels);
			}
#endif
		}


		/* mark device as active and set timeout to 500ms */
		w->active = true;
		timeout = 500;

		ffb_seek(&buffer, ret / pcm_format_size);

		/* calculate the overall number of frames in the buffer */
		snd_pcm_sframes_t frames = ffb_len_out(&buffer) / w->ba_pcm.channels;

		int bytes = frames * 4;
		if ((ret = hal_audio_stream->write(hal_audio_stream, buffer.data, bytes)) < 0)
			switch (-ret) {
			case EPIPE:
				debug("An underrun has occurred");
				//snd_pcm_prepare(w->pcm);
				usleep(50000);
				//frames = 0;
				break;
			default:
				error("Couldn't write to PCM: %s", snd_strerror(ret));
				goto fail;
			}
		//error("yym:  frames:%d, bytes: %d\n", frames, bytes);
		/* move leftovers to the beginning and reposition tail */
		ffb_shift(&buffer, frames * w->ba_pcm.channels);

	}

fail:
	pthread_setcancelstate(PTHREAD_CANCEL_DISABLE, NULL);
	pthread_cleanup_pop(1);
	pthread_cleanup_pop(1);
	return NULL;
}

static bool pcm_hw_params_equal(
		const struct ba_pcm *ba_pcm_1,
		const struct ba_pcm *ba_pcm_2) {
	if (ba_pcm_1->format != ba_pcm_2->format)
		return false;
	if (ba_pcm_1->channels != ba_pcm_2->channels)
		return false;
	if (ba_pcm_1->sampling != ba_pcm_2->sampling)
		return false;
	return true;
}

/**
 * Stop the worker thread at workers[index]. */
static void pcm_worker_stop(size_t index) {
	error("pcm worker stop: index %d\n", index);
	/* Safety check for out-of-bounds read. */
	assert(index < workers_count);

	pthread_rwlock_wrlock(&workers_lock);

	pthread_cancel(workers[index].thread);
	pthread_join(workers[index].thread, NULL);

	if (index != --workers_count)
		/* Move the last worker in the array to position
		 * index, to prevent any "gaps" in the array. */
		memcpy(&workers[index], &workers[workers_count], sizeof(workers[index]));

	pthread_rwlock_unlock(&workers_lock);

}

static struct pcm_worker *supervise_pcm_worker_start(const struct ba_pcm *ba_pcm) {

	size_t i;
	for (i = 0; i < workers_count; i++) {
		error("pcm worker start:  %d :  exist path: %s, connected path %s\n", i, workers[i].ba_pcm.pcm_path, ba_pcm->pcm_path);
		if (strcmp(workers[i].ba_pcm.pcm_path, ba_pcm->pcm_path) == 0) {
			/* If the codec has changed after the device connected, then the
			 * audio format may have changed. If it has, the worker thread
			 * needs to be restarted. */
			if (!pcm_hw_params_equal(&workers[i].ba_pcm, ba_pcm))
				pcm_worker_stop(i);
			else
				return &workers[i];
		} else {
			pcm_worker_stop(i);
		}
	}

	pthread_rwlock_wrlock(&workers_lock);

	workers_count++;
	if (workers_size < workers_count) {
		struct pcm_worker *tmp = workers;
		workers_size += 4;  /* coarse-grained realloc */
		if ((workers = realloc(workers, sizeof(*workers) * workers_size)) == NULL) {
			error("Couldn't (re)allocate memory for PCM workers: %s", strerror(ENOMEM));
			workers = tmp;
			pthread_rwlock_unlock(&workers_lock);
			return NULL;
		}
	}

	struct pcm_worker *worker = &workers[workers_count - 1];
	memcpy(&worker->ba_pcm, ba_pcm, sizeof(worker->ba_pcm));
	ba2str(&worker->ba_pcm.addr, worker->addr);
	worker->active = false;
	worker->ba_pcm_fd = -1;
	worker->ba_pcm_ctrl_fd = -1;
	//worker->pcm = NULL;

	pthread_rwlock_unlock(&workers_lock);

	debug("Creating PCM worker %s", worker->addr);

	if ((errno = pthread_create(&worker->thread, NULL,
					PTHREAD_ROUTINE(pcm_worker_routine), worker)) != 0) {
		error("Couldn't create PCM worker %s: %s", worker->addr, strerror(errno));
		workers_count--;
		return NULL;
	}

	return worker;
}

static struct pcm_worker *supervise_pcm_worker_stop(const struct ba_pcm *ba_pcm) {

	size_t i;
	for (i = 0; i < workers_count; i++)
		if (strcmp(workers[i].ba_pcm.pcm_path, ba_pcm->pcm_path) == 0)
			pcm_worker_stop(i);

	return NULL;
}

static struct pcm_worker *supervise_pcm_worker(const struct ba_pcm *ba_pcm) {

	if (ba_pcm == NULL)
		return NULL;

	if (ba_pcm->mode != BA_PCM_MODE_SOURCE)
		goto stop;

	if ((ba_profile_a2dp && !(ba_pcm->transport & BA_PCM_TRANSPORT_MASK_A2DP)) ||
			(!ba_profile_a2dp && !(ba_pcm->transport & BA_PCM_TRANSPORT_MASK_SCO)))
		goto stop;

	/* check whether SCO has selected codec */
	if (ba_pcm->transport & BA_PCM_TRANSPORT_MASK_SCO &&
			ba_pcm->sampling == 0) {
		debug("Skipping SCO with codec not selected");
		goto stop;
	}

	if (ba_addr_any)
		goto start;

	size_t i;
	for (i = 0; i < ba_addrs_count; i++)
		if (bacmp(&ba_addrs[i], &ba_pcm->addr) == 0)
			goto start;

stop:
	return supervise_pcm_worker_stop(ba_pcm);
start:
	return supervise_pcm_worker_start(ba_pcm);
}

static DBusHandlerResult dbus_signal_handler(DBusConnection *conn, DBusMessage *message, void *data) {
	(void)conn;
	(void)data;

	if (dbus_message_get_type(message) != DBUS_MESSAGE_TYPE_SIGNAL)
		return DBUS_HANDLER_RESULT_NOT_YET_HANDLED;

	const char *path = dbus_message_get_path(message);
	const char *interface = dbus_message_get_interface(message);
	const char *signal = dbus_message_get_member(message);

	DBusMessageIter iter;
	struct pcm_worker *worker;

	if (strcmp(interface, DBUS_INTERFACE_OBJECT_MANAGER) == 0) {

		if (strcmp(signal, "InterfacesAdded") == 0) {
			if (!dbus_message_iter_init(message, &iter))
				goto fail;
			struct ba_pcm pcm;
			DBusError err = DBUS_ERROR_INIT;
			debug("InterfacesAdded\n");
			if (!bluealsa_dbus_message_iter_get_pcm(&iter, &err, &pcm)) {
				error("Couldn't add new PCM: %s", err.message);
				dbus_error_free(&err);
				goto fail;
			}
			if (pcm.transport == BA_PCM_TRANSPORT_NONE)
				goto fail;
			struct ba_pcm *tmp = ba_pcms;
			if ((ba_pcms = realloc(ba_pcms, (ba_pcms_count + 1) * sizeof(*ba_pcms))) == NULL) {
				error("Couldn't add new PCM: %s", strerror(ENOMEM));
				ba_pcms = tmp;
				goto fail;
			}
			memcpy(&ba_pcms[ba_pcms_count++], &pcm, sizeof(*ba_pcms));
			supervise_pcm_worker(&pcm);
			return DBUS_HANDLER_RESULT_HANDLED;
		}

		if (strcmp(signal, "InterfacesRemoved") == 0) {
			debug("InterfacesRemoved\n");
			if (!dbus_message_iter_init(message, &iter) ||
					dbus_message_iter_get_arg_type(&iter) != DBUS_TYPE_OBJECT_PATH) {
				error("Couldn't remove PCM: %s", "Invalid signal signature");
				goto fail;
			}
			dbus_message_iter_get_basic(&iter, &path);
			struct ba_pcm *pcm;
			if ((pcm = get_ba_pcm(path)) == NULL)
				goto fail;
			supervise_pcm_worker_stop(pcm);
			return DBUS_HANDLER_RESULT_HANDLED;
		}

	}

	if (strcmp(interface, DBUS_INTERFACE_PROPERTIES) == 0) {
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
		//if ((worker = supervise_pcm_worker(pcm)) != NULL)
		//	pcm_worker_mixer_volume_update(worker, pcm);
		return DBUS_HANDLER_RESULT_HANDLED;
	}

fail:
	return DBUS_HANDLER_RESULT_NOT_YET_HANDLED;
}

int main(int argc, char *argv[]) {

	int opt;
	const char *opts = "hVvlLB:D:M:";
	const struct option longopts[] = {
		{ "help", no_argument, NULL, 'h' },
		{ "version", no_argument, NULL, 'V' },
		{ "verbose", no_argument, NULL, 'v' },
		{ "list-devices", no_argument, NULL, 'l' },
		{ "list-pcms", no_argument, NULL, 'L' },
		{ "dbus", required_argument, NULL, 'B' },
		{ "pcm", required_argument, NULL, 'D' },
		{ "pcm-buffer-time", required_argument, NULL, 3 },
		{ "pcm-period-time", required_argument, NULL, 4 },
//		{ "mixer-device", required_argument, NULL, 'M' },
//		{ "mixer-name", required_argument, NULL, 6 },
//		{ "mixer-index", required_argument, NULL, 7 },
		{ "profile-a2dp", no_argument, NULL, 1 },
		{ "profile-sco", no_argument, NULL, 2 },
		{ "single-audio", no_argument, NULL, 5 },
		{ 0, 0, 0, 0 },
	};

	while ((opt = getopt_long(argc, argv, opts, longopts, NULL)) != -1)
		switch (opt) {
		case 'h' /* --help */ :
			printf("Usage:\n"
					"  %s [OPTION]... [BT-ADDR]...\n"
					"\nOptions:\n"
					"  -h, --help\t\t\tprint this help and exit\n"
					"  -V, --version\t\t\tprint version and exit\n"
					"  -v, --verbose\t\t\tmake output more verbose\n"
					"  -l, --list-devices\t\tlist available BT audio devices\n"
					"  -L, --list-pcms\t\tlist available BT audio PCMs\n"
					"  -B, --dbus=NAME\t\tBlueALSA service name suffix\n"
					"  -D, --pcm=NAME\t\tplayback PCM device to use\n"
					"  --pcm-buffer-time=INT\t\tplayback PCM buffer time\n"
					"  --pcm-period-time=INT\t\tplayback PCM period time\n"
					"  -M, --mixer-device=NAME\tmixer device to use\n"
					"  --mixer-name=NAME\t\tmixer element name\n"
					"  --mixer-index=NUM\t\tmixer element channel index\n"
					"  --profile-a2dp\t\tuse A2DP profile (default)\n"
					"  --profile-sco\t\t\tuse SCO profile\n"
					"  --single-audio\t\tsingle audio mode\n"
					"\nNote:\n"
					"If one wants to receive audio from more than one Bluetooth device, it is\n"
					"possible to specify more than one MAC address. By specifying any/empty MAC\n"
					"address (00:00:00:00:00:00), one will allow connections from any Bluetooth\n"
					"device. Without given explicit MAC address any/empty MAC is assumed.\n",
					argv[0]);
			return EXIT_SUCCESS;

		case 'V' /* --version */ :
			printf("%s\n", PACKAGE_VERSION);
			return EXIT_SUCCESS;

		case 'v' /* --verbose */ :
			verbose++;
			break;

		case 'l' /* --list-devices */ :
			list_bt_devices = true;
			break;
		case 'L' /* --list-pcms */ :
			list_bt_pcms = true;
			break;

		case 'B' /* --dbus=NAME */ :
			snprintf(dbus_ba_service, sizeof(dbus_ba_service), BLUEALSA_SERVICE ".%s", optarg);
			if (!dbus_validate_bus_name(dbus_ba_service, NULL)) {
				error("Invalid BlueALSA D-Bus service name: %s", dbus_ba_service);
				return EXIT_FAILURE;
			}
			break;

		case 'D' /* --pcm=NAME */ :
			pcm_device = optarg;
			break;
		case 3 /* --pcm-buffer-time=INT */ :
			pcm_buffer_time = atoi(optarg);
			break;
		case 4 /* --pcm-period-time=INT */ :
			pcm_period_time = atoi(optarg);
			break;

		case 'M' /* --mixer-device=NAME */ :
			mixer_device = optarg;
			break;
		case 6 /* --mixer-name=NAME */ :
			mixer_elem_name = optarg;
			break;
		case 7 /* --mixer-index=NUM */ :
			mixer_elem_index = atoi(optarg);
			break;

		case 1 /* --profile-a2dp */ :
			ba_profile_a2dp = true;
			break;
		case 2 /* --profile-sco */ :
			ba_profile_a2dp = false;
			break;

		case 5 /* --single-audio */ :
			pcm_mixer = false;
			break;

		default:
			fprintf(stderr, "Try '%s --help' for more information.\n", argv[0]);
			return EXIT_FAILURE;
		}

	log_open(argv[0], false, false);
	dbus_threads_init_default();

	DBusError err = DBUS_ERROR_INIT;
	if (!bluealsa_dbus_connection_ctx_init(&dbus_ctx, dbus_ba_service, &err)) {
		error("Couldn't initialize D-Bus context: %s", err.message);
		return EXIT_FAILURE;
	}

	if (list_bt_devices || list_bt_pcms) {

		if (!bluealsa_dbus_get_pcms(&dbus_ctx, &ba_pcms, &ba_pcms_count, &err)) {
			warn("Couldn't get BlueALSA PCM list: %s", err.message);
			return EXIT_FAILURE;
		}

		if (list_bt_pcms)
			print_bt_pcm_list();

		if (list_bt_devices)
			print_bt_device_list();

		return EXIT_SUCCESS;
	}

	ba_addr_any = true;

	bluealsa_dbus_connection_signal_match_add(&dbus_ctx,
			dbus_ba_service, NULL, DBUS_INTERFACE_OBJECT_MANAGER, "InterfacesAdded",
			"path_namespace='/org/bluealsa'");
	bluealsa_dbus_connection_signal_match_add(&dbus_ctx,
			dbus_ba_service, NULL, DBUS_INTERFACE_OBJECT_MANAGER, "InterfacesRemoved",
			"path_namespace='/org/bluealsa'");
	bluealsa_dbus_connection_signal_match_add(&dbus_ctx,
			dbus_ba_service, NULL, DBUS_INTERFACE_PROPERTIES, "PropertiesChanged",
			"arg0='"BLUEALSA_INTERFACE_PCM"'");

	if (!dbus_connection_add_filter(dbus_ctx.conn, dbus_signal_handler, NULL, NULL)) {
		error("Couldn't add D-Bus filter: %s", err.message);
		return EXIT_FAILURE;
	}

	if (!bluealsa_dbus_get_pcms(&dbus_ctx, &ba_pcms, &ba_pcms_count, &err))
		warn("Couldn't get BlueALSA PCM list: %s", err.message);

	size_t i;
	for (i = 0; i < ba_pcms_count; i++)
		supervise_pcm_worker(&ba_pcms[i]);

	struct sigaction sigact = { .sa_handler = main_loop_stop };
	sigaction(SIGTERM, &sigact, NULL);
	sigaction(SIGINT, &sigact, NULL);

	int inited = audio_hw_load_interface(&hal_audio_device);
	if (inited) {
		fprintf(stderr, "%s %d error:%d\n", __func__, __LINE__, inited);
		return EXIT_FAILURE;
	}

	if (hal_audio_device->get_supported_devices) {
		uint32_t support_dev = 0;
		support_dev = hal_audio_device->get_supported_devices(hal_audio_device);
		debug("supported device: %x\n", support_dev);
	}

	inited = hal_audio_device->init_check(hal_audio_device);
	if (inited) {
		error("device not inited, quit\n");
		audio_hw_unload_interface(hal_audio_device);
		return EXIT_FAILURE;
	}

	debug("Starting main loop, init audio hal success\n");
	while (main_loop_on) {

		struct pollfd pfds[10];
		nfds_t pfds_len = ARRAYSIZE(pfds);

		if (!bluealsa_dbus_connection_poll_fds(&dbus_ctx, pfds, &pfds_len)) {
			error("Couldn't get D-Bus connection file descriptors");
			return EXIT_FAILURE;
		}

		if (poll(pfds, pfds_len, -1) == -1 &&
				errno == EINTR)
			continue;

		if (bluealsa_dbus_connection_poll_dispatch(&dbus_ctx, pfds, pfds_len))
			while (dbus_connection_dispatch(dbus_ctx.conn) == DBUS_DISPATCH_DATA_REMAINS)
				continue;

	}

	audio_hw_unload_interface(hal_audio_device);
	return EXIT_SUCCESS;
}
