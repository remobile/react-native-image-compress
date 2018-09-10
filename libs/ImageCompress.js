import {
    requireNativeComponent,
    NativeModules,
    Platform,
    DeviceEventEmitter
} from 'react-native';

import React, {
    Component,
    PropTypes
} from 'react';

const _module = NativeModules.ImageCompress;
export default {
    compressImage(imgSrc, size) {
         return _module.compressImage(imgSrc, size)
    },
};
