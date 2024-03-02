For HDMI RX capture and recorder.

## example
```bash

# NV21
# NV12

# capture hdmirx to a jpeg file
./hdmirx-capture.sh 
./hdmirx-capture.sh 1920 1080
./hdmirx-capture.sh 3840 2160

# show hdmirx in OSD layer
./hdmirx-preview-osdlayer.sh
./hdmirx-preview-osdlayer.sh 1920 1080
./hdmirx-preview-osdlayer.sh 3840 2160

# show hdmirx on video layer, need disable gnome now
./hdmirx-preview-videolayer.sh
./hdmirx-preview-videolayer.sh 1920 1080
./hdmirx-preview-videolayer.sh 3840 2160

# recorde hdmirx stream to a mp4 file
./hdmirx-recorder.sh
./hdmirx-recorder.sh 1920 1080 h264
./hdmirx-recorder.sh 1920 1080 h265
./hdmirx-recorder.sh 3840 2160 h264
./hdmirx-recorder.sh 3840 2160 h265
```

## about tvin-port
for HDMI RX using video71, the default  tvin-port is 0, 
it's the 1st video layer. 
