#include <stdio.h>
#include <sys/ioctl.h>
#include <fcntl.h>
 
#define GBUFF_SIZE                12
#define GSENSOR_IOCTL_MAGIC       'a'
 
#define GSENSOR_IOCTL_INIT              _IO(GSENSOR_IOCTL_MAGIC, 0x01)
#define GSENSOR_IOCTL_RESET             _IO(GSENSOR_IOCTL_MAGIC, 0x04)
#define GSENSOR_IOCTL_CLOSE             _IO(GSENSOR_IOCTL_MAGIC, 0x02)
#define GSENSOR_IOCTL_START             _IO(GSENSOR_IOCTL_MAGIC, 0x03)
#define GSENSOR_IOCTL_GETDATA           _IOR(GSENSOR_IOCTL_MAGIC, 0x08, char[GBUFF_SIZE+1])
#define GSENSOR_IOCTL_APP_SET_RATE      _IOW(GSENSOR_IOCTL_MAGIC, 0x10, short)
#define GSENSOR_IOCTL_GET_CALIBRATION   _IOR(GSENSOR_IOCTL_MAGIC, 0x11, int[3])
 
 
struct sensor_axis {
    int x;
    int y;
    int z;
};
 
char *gsensor_device = "/dev/accel";
int gsensor_fd = -1;
 
int main(int argc, char **argv){
 
        struct sensor_axis gsensor_data;
 
        gsensor_fd = open(gsensor_device, O_RDWR);
 
        if (0 > gsensor_fd){
                printf("gsensor node open failed ...\n");
                exit(-1);
        }else{
                printf("gsensor node open success!!!\n");
        }
 
        if(ioctl(gsensor_fd, GSENSOR_IOCTL_START, NULL) == -1) {
                printf("gsensor start failed ... \n");
                close(gsensor_fd);
                exit(-1);
        }else{
                printf("gsensor start sueecss !!!\n");
        }
 
        printf("start to get gsensor data ...\n");
        while(1){
 
                if(ioctl(gsensor_fd, GSENSOR_IOCTL_GETDATA, &gsensor_data) == -1) {
                        printf("gsensor get data faile ... \n");
                        close(gsensor_fd);
                        exit(-1);
                }
 
                printf("gsensor_data -- x:%d,y:%d,z:%d \n", gsensor_data.x, gsensor_data.y, gsensor_data.z);
                sleep(1);
        }
        close(gsensor_fd);
 
		return 0;
}