#ifndef _A2DP_CTL_H_
#define _A2DP_CTL_H_

#ifdef __cplusplus
extern "C" {
#endif

typedef enum{
    A2DP_EVENT_CONNECT,
    A2DP_EVENT_DISCONNECT,
    A2DP_EVENT_PLAYING,
    A2DP_EVENT_PAUSE,
    A2DP_EVENT_STOP,
    A2DP_EVENT_SERVICE_START,
    A2DP_EVENT_SERVICE_STOP,
}A2DP_Event_t;

typedef enum {
    A2DP_CTRL_PLAY,
    A2DP_CTRL_PAUSE,
    A2DP_CTRL_STOP,
    A2DP_CTRL_NEXT,
    A2DP_CTRL_PRE,
    A2DP_CTRL_VOLUP,
    A2DP_CTRL_VOLDOWN
}A2DP_Control_t;

typedef struct {
    char addr[18];
    bool is_sink;
}A2DP_Dev_t;

int a2dp_ctl_init(void);
void a2dp_ctl_deinit(void);
bool is_bluealsa_ready();
int a2dp_control(A2DP_Control_t control);
bool adapter_ready(void);
int adapter_scan(int onoff);
int connect_dev(const char* bddr, bool is_sink);
int disconnect_dev(const char *bddr);
unsigned char get_connect_status(A2DP_Dev_t **dev, int *cnt);
void print_connect_status(void);
void print_scan_results(void);

int pcm_bluealsa_open(const char *bddr);
int pcm_bluealsa_close();
int pcm_bluealsa_write(void *buf, size_t bytes) ;

void register_callback(void (*conn_cb)(char *addr, bool is_sink, A2DP_Event_t type));

#ifdef __cplusplus
}
#endif

#endif
