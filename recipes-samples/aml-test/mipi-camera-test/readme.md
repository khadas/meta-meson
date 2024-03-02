## check the mipi camera device
```bash
$ ls /dev/media*
/dev/media0
/dev/media1
```


## single mipi camera preview
```bash
# test all the test case of camera, include all the following test cases
./mipi-camera-burning.sh media0 1

# capture from camera to jpeg file
./mipi-camera-capture.sh media0
# test all the test cases about capture function
./mipi-camera-capture-checklist.sh media0

# preview the camera to OSD layer
./mipi-camera-preview.sh media0
# test all the test cases about preview function
./mipi-camera-preview-checklist.sh media0

# recorde the camera stream to a file
./mipi-camera-recorder.sh media0
# test all the test cases about recorde function
./mipi-camera-recorder-checklist.sh media0

# preview the camera to OSD layer (with HW convert)
./mipi-camera-convert-preview.sh media0
# test all the test cases about preview function
./mipi-camera-convert-preview-checklist.sh media0

# recorde the camera stream to a file (with HW convert)
./mipi-camera-convert-recorder.sh media0
# test all the test cases about recorde function
./mipi-camera-convert-recorder-checklist.sh media0

```

## multiple mipi camera preview


You can use the following command to preview 1st camera
```bash
$./mipi-camera-preview.sh media0 &
```

You can use the following command to preview 2nd camera
```bash
$./mipi-camera-preview.sh media1 &
```
