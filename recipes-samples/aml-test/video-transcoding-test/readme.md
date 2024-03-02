For single camera test, you can use the script directly,
For multiple camera test,

You should check the device name. then start multiple instance.
The 2 camera can use at the same time.


## 2 mipi camera preview
```bash
$ ls /dev/media*
/dev/media0
/dev/media2
```

You can use the following command to preview 1st camera
```bash
$./mipi-camera-preview.sh
```

You can use the following command to preview 2nd camera
```bash
$./mipi-camera-preview.sh media2
```


## 2 USB camera preview
```bash
$ ls /dev/video*
/dev/video0
/dev/video1
```

You can use the following command to preview 1st camera
```bash
$./usb-camera-preview.sh
```

You can use the following command to preview 2nd camera
```bash
$./usb-camera-preview.sh video1
```

## example
### USB (please refer to usb-camera-burning.sh file)
```bash
# capture camera to a jpeg file
./usb-camera-capture.sh
./usb-camera-capture.sh video0 1920 1080 YUY2 30/1
./usb-camera-capture.sh video0 1280 720 YUY2 30/1
./usb-camera-capture.sh video0 640 480 YUY2 30/1

# show camera in OSD layer
./usb-camera-preview.sh
./usb-camera-preview.sh video0 1920 1080 YUY2 30/1 100
./usb-camera-preview.sh video0 1280 720 YUY2 30/1 100
./usb-camera-preview.sh video0 640 480 YUY2 30/1 100

# show camera with NN result in OSD layer
./usb-camera-nn.sh
./usb-camera-nn.sh video0 1280 720 YUY2 30/1 100 amlfd
./usb-camera-nn.sh video0 1280 720 YUY2 30/1 100 yolov3

# recorde camera stream to a mp4 file(use mjpeg)
./usb-camera-recorder-mjpeg.sh
./usb-camera-recorder-mjpeg.sh video0 100 h264
./usb-camera-recorder-mjpeg.sh video0 100 h265

# recorde camera stream to a mp4 file(use YUY2)
./usb-camera-recorder-yuy2.sh
./usb-camera-recorder-yuy2.sh video0 1920 1080 YUY2 30/1 100 h264
./usb-camera-recorder-yuy2.sh video0 1920 1080 YUY2 30/1 100 h265
```

### MIPI (please refer to mipi-camera-burning.sh file)
```bash
# capture camera to a jpeg file
./mipi-camera-capture.sh
./mipi-camera-capture.sh media0 1920 1080 RGB 30/1
./mipi-camera-capture.sh media0 3840 2160 RGB 30/1

# show camera in OSD layer
./mipi-camera-preview.sh
./mipi-camera-preview.sh media0 1920 1080 RGB 30/1 100
./mipi-camera-preview.sh media0 3840 2160 RGB 30/1 100

# show camera with NN result in OSD layer
./mipi-camera-nn.sh
./mipi-camera-nn.sh media0 1920 1080 RGB 30/1 100 amlfd
./mipi-camera-nn.sh media0 1920 1080 RGB 30/1 100 yolov3
./mipi-camera-nn.sh media0 3840 2160 RGB 30/1 100 amlfd
./mipi-camera-nn.sh media0 3840 2160 RGB 30/1 100 yolov3

# recorde camera stream to a mp4 file
./mipi-camera-recorder.sh
./mipi-camera-recorder.sh media0 1920 1080 RGB 30/1 100 h264
./mipi-camera-recorder.sh media0 1920 1080 RGB 30/1 100 h265
./mipi-camera-recorder.sh media0 3840 2160 RGB 30/1 100 h264
./mipi-camera-recorder.sh media0 3840 2160 RGB 30/1 100 h265

./mipi-camera-convert-preview.sh
./mipi-camera-convert-preview.sh media0 1920 1080 NV21 30/1 1920 1080 RGB 30/1 100
./mipi-camera-convert-preview.sh media0 3840 2160 NV21 30/1 1920 1080 RGB 30/1 100

./mipi-camera-convert-preview.sh
./mipi-camera-convert-recorder.sh media0 1920 1080 NV21 30/1 1920 1080 RGB 30/1 100 h265
./mipi-camera-convert-recorder.sh media0 3840 2160 NV21 30/1 1920 1080 RGB 30/1 100 h265
```
