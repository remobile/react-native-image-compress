//
//  GoelocationModule.m
//  RCTImageCompress
//
//  Created by honggao on 2018/9/2.
//  Copyright © 2016年 honggao. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "ImageCompressModule.h"


@implementation ImageCompressModule

RCT_EXPORT_MODULE(ImageCompress);


RCT_EXPORT_METHOD(compressImage: (NSString *) imgSrc : (int) maxFileSize
                  resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)
{
    UIImage *image = [self dataURL2Image: imgSrc];
    if (!image) {
        resolve(@{
                  @"success": @(false),
                  });
    }
    CGFloat compression = 0.9f;
    NSData *compressedData = UIImageJPEGRepresentation(image, compression);
    while ([compressedData length] > maxFileSize) {
        compression *= 0.9;
        compressedData = UIImageJPEGRepresentation([self compressImage:image newWidth:image.size.width*compression], compression);
    }
    NSDictionary* coor = @{
                           @"success": @(true),
                           @"base64": [compressedData base64EncodedStringWithOptions: 0]
                           };
    resolve(coor);
}

/**
 *  等比缩放本图片大小
 *
 *  @param newImageWidth 缩放后图片宽度，像素为单位
 *
 *  @return self-->(image)
 */
- (UIImage *)compressImage:(UIImage *)image newWidth:(CGFloat)newImageWidth
{
    if (!image) return nil;
    float imageWidth = image.size.width;
    float imageHeight = image.size.height;
    float width = newImageWidth;
    float height = image.size.height/(image.size.width/width);

    float widthScale = imageWidth /width;
    float heightScale = imageHeight /height;

    // 创建一个bitmap的context
    // 并把它设置成为当前正在使用的context
    UIGraphicsBeginImageContext(CGSizeMake(width, height));

    if (widthScale > heightScale) {
        [image drawInRect:CGRectMake(0, 0, imageWidth /heightScale , height)];
    }
    else {
        [image drawInRect:CGRectMake(0, 0, width , imageHeight /widthScale)];
    }

    // 从当前context中创建一个改变大小后的图片
    UIImage *newImage = UIGraphicsGetImageFromCurrentImageContext();
    // 使当前的context出堆栈
    UIGraphicsEndImageContext();

    return newImage;

}

- (UIImage *) dataURL2Image: (NSString *) imgSrc
{
    NSURL *url = [NSURL URLWithString: imgSrc];
    NSData *data = [NSData dataWithContentsOfURL: url];
    UIImage *image = [UIImage imageWithData: data];

    return image;
}

@end
