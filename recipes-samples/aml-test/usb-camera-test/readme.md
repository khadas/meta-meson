## check the usb camera device
```bash
$ ls /dev/video*
/dev/video0
/dev/video1
```

## # you should use a 1080p USB camera for testing


## single usb camera preview
```bash
# test all the test case of camera, include all the following test cases
./usb-camera-burning.sh video0 1

# capture from camera to jpeg file
./usb-camera-capture.sh video0
# test all the test cases about capture function
./usb-camera-capture-checklist.sh video0

# preview the camera to OSD layer
./usb-camera-preview.sh video0
# test all the test cases about preview function
./usb-camera-preview-checklist.sh video0

# recorde the camera stream to a file
./usb-camera-recorder.sh video0
# test all the test cases about recorde function
./usb-camera-recorder-checklist.sh video0

# preview the camera to OSD layer (with HW convert)
./usb-camera-convert-preview.sh video0
# test all the test cases about preview function
./usb-camera-convert-preview-checklist.sh video0

# recorde the camera stream to a file (with HW convert)
./usb-camera-convert-recorder.sh video0
# test all the test cases about recorde function
./usb-camera-convert-recorder-checklist.sh video0

```

## multiple usb camera preview


You can use the following command to preview 1st camera
```bash
$./usb-camera-preview.sh video0 &
```

You can use the following command to preview 2nd camera
```bash
$./usb-camera-preview.sh video1 &
```
