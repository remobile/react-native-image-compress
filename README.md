### Installation
npm install @remobile/react-native-image-compress --save

### Installation (Android)
- settings.gradle `
include ':react-native-image-compress'
project(':react-native-image-compress').projectDir = new File(settingsDir, '../node_modules/@remobile/react-native-image-compress/android')`

- build.gradle `compile project(':react-native-image-compress')`

- MainApplication`new ImageCompressPackage()`

### Installation (iOS)
- Project navigator->Libraries->Add Files to 选择 @remobile/react-native-image-compress/ios/RCTImageCompress.xcodeproj
- Project navigator->Build Phases->Link Binary With Libraries 加入 libRCTImageCompress.a

### Usage 使用方法

    const ImageCompress =  require('react-native-image-compress');
    ImageCompress.compressImage(data,500*1024)//data:(base64 data), 500*1024(maxFileSize)
    .then((data)=>{
        console.warn(data, 'data');
    })
    .catch(e => {
        console.warn(e, 'error');
    });
