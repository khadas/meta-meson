For screen capture and recorder.


## single usb screen preview
```bash
# test all the test case of screen, include all the following test cases
./screen-burning.sh 1

# capture from screen to jpeg file
./screen-capture.sh
# test all the test cases about capture function
./screen-capture-checklist.sh

# preview the screen to OSD layer
./screen-preview.sh
# test all the test cases about preview function
./screen-preview-checklist.sh

# recorde the screen stream to a file
./screen-recorder.sh
# test all the test cases about recorde function
./screen-recorder-checklist.sh


```


## about tvin-port
```bash
$ gst-launch-1.0 -v v4l2src device=/dev/video12 io-mode=mmap ! video/x-raw,width=1920,height=1080,format=BGR,framerate=30000/1001 ! clutterautovideosink
```

for screen recorder, you can get the screen data from video12, 
for tvin-port, there are 4 types value.
```
 tvin-port=0x11000000  //vpp0 video only; the main screen video only
 tvin-port=0x11000001  //vpp0(osd+video); the main screen osd+video
 tvin-port=0x11000002  //vpp1 video only; the sub screen video only
 tvin-port=0x11000003  //vpp1(osd+video); the sub screen osd+video
```

the default  tvin-port is 0x11000001, and only this is verified for every release. 
you needn't set the propoty, if you use the main screen osd+video. 




