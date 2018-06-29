# VR-AltPlayer-SDK-Android

[![](https://img.shields.io/badge/Powered%20by-vrviu.com-brightgreen.svg)](https://vrviu.com)

## 版本
20180403 V1.2 发布；
  
## 功能说明
支持点播以及直播功能，其中直播是网络主播实时推送的视频流，用户能够及时看到主播的画面。点播是播放云端或者本地的文件。  
本SDK免费使用，帐号需要申请授权。

## 产品特点
**1. 播放器格式支持**：  
可以支持常见视频格式播放，也可以播放使用威尔云 [**FE算法**](https://www.vrviu.com/technology.html) 编码后的视频。经测试，FE编码在同样清晰度的前提下能够进一步节省最高40%的码率。  

**2. 渲染类型**： 支持360度2D ERP视频点播直播，支持180度2D FISH-EYE视频点播直播。

**3. 直播视频秒开**：通过优化播放器缓冲策略、网络加载等，该SDK可以实现秒开。

**4. 多协议支持**：支持HLS/RTMP/HTTP-FLV/HTTP-MP4等常见标准协议，以及本地文件的播放。

**5. 接口简单全面**：实现播放接口简单，可快速实现播放。提供播放器状态监听接口以及错误信息通知接口、日志接口、算法参数配置接口等。

**6. 解码性能强大**：支持H264、H265、AAC，支持8K视频硬件解码以及2K以下视频软件解码。

**7. 多平台**：支持ARMV7、ARM64和X86平台。

## 开发环境
Android Studio

## 导入工程
### 1. 开发准备
下载最新的Demo和SDK

### 2. 导入工程
##### 2.1 导入aar包
将aar包放到工程libs目录下，如图
![](https://github.com/vrviu-sdk/VRVIU-VR-AltPlayer-Demo-Android/blob/master/Image/libpath.png)

修改build.gradle文件，确保添加

```gradle
repositories{
    flatDir{
        dirs 'libs'
    }
}
dependencies {
    compile 'com.google.vr:sdk-common:1.80.0'
    compile 'com.google.vr:sdk-base:1.80.0'
    compile 'com.google.vr:sdk-commonwidget:1.80.0'
    compile(name:'vrviu-vrp-altlayer1.1.0',ext:'aar')
}
```

##### 2.2 配置工程权限
在AndroidManifest.xml中配置APP的权限，一般需要以下权限：

```xml
<!-- The GVR SDK requires API 19+ and OpenGL ES 2+. -->
<uses-sdk android:minSdkVersion="24" android:targetSdkVersion="25" />
<uses-feature android:glEsVersion="0x00020000" android:required="true" />

<!-- Required for vibration feedback when the trigger action is performed. -->
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS"/>
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>

<!-- Make accelerometer and gyroscope hard requirements for good head tracking. -->
<uses-feature android:name="android.hardware.sensor.accelerometer" android:required="true"/>
<uses-feature android:name="android.hardware.sensor.gyroscope" android:required="true"/>

<!-- Indicates use of Android's VR-mode, available only on Android N+. -->
<uses-feature android:name="android.software.vr.mode" android:required="true"/>
<!-- Indicates use of VR features that are available only on Daydream-ready devices. -->
<uses-feature android:name="android.hardware.vr.high_performance" android:required="true"/>
```

### 3. 引用SDK
##### 3.1 添加控件
在PlayActivity使用的布局文件中添加界面控件

```xml
<com.viu.vrplayer.vrwidget.AltVrVideoView
    android:id="@+id/vr_video"
    android:layout_width="match_parent"
    android:layout_height="match_parent"/>
```

##### 3.2 调用接口
在onCreate()中首先调用init接口实施鉴权，具体参数见表1.1，然后使用setUrl设置播放地址，使用start开始播放。

```java
videoView = (AltVrVideoView)findViewById(R.id.surfaceView);
videoView.init(mAppid,mAccessKey,mAccessKeyId,mBizId);
videoView.setUrl(uriString);
videoView.start();
```
##### 3.3 设置mesh格式
```java
mVideoView.setVideoFormat(VideoFormat.FT_ERP_360_2D);
```
##### 3.4 设置本地文件
```java
videoView.setDataSource(file);
```
##### 3.5 设置监听事件
```java
videoView.setOnErrorListener(this);
videoView.setOnVideoSizeChangedListener(this);
videoView.setOnInfoListener(this);
videoView.setOnPreparedListener(this);
videoView.setOnSeekCompleteListener(this);
videoView.setOnCompleteListener(this);
```
##### 3.6 暂停点播播放
```java
videoView.pause();
```
##### 3.7 点播播放时长
```java
videoView.getDuration();
```
##### 3.8 点播播放进度
```java
videoView.getCurrentPosition();
```
##### 3.9 点播跳转
```java
videoView.seekTo(msec);
```
##### 3.10 设置音量
```java
videoView.setVolume(left,right);
```
##### 3.11 设置点播播放速度
```java
videoView.setSpeed(1.0f);
```
##### 3.12 获取点播播放速度
```java
videoView.getSpeed();
```
##### 3.13 获取播放状态
```java
videoView.getPlayState();
```
##### 3.14 设置播放时屏幕常亮与否
```java
videoView.setScreenOnWhilePlaying(true);
```
##### 3.15 结束点播（直播）播放
```java
videoView.release();
```

### 4. 检查混淆
```
-dontwarn com.viu.*.*
-keep class com.viu.** {*;}

-dontwarn com.google.*.*
-keep class com.google.** {*;}
-keep class android.**{*;}
-keep class com.google.vr.**{*;}
-keep class com.google.vr.cardboard.**{*;}
-keep public class * extends com.google.*
```

## 账号鉴权参数表
|参数|说明|是否必填|类型|
|:---|:---|:---|:---|
|AppId|分配给用户的ID，可通过 www.vrviu.com 填写表单或者联系客服申请|必填|String|
|AccessKeyId|分配给用户的ID，可通过 www.vrviu.com 填写表单或者联系客服申请|必填|String|
|BizId|分配给用户的ID，可通过 www.vrviu.com 填写表单或者联系客服申请|必填|String|
|AccessKey|分配给用户的ID，可通过 www.vrviu.com 填写表单或者联系客服申请|必填|String|

## 联系我们
 如果有技术问题咨询，请加入官方QQ群：136562408；   
 商务合作请电话：0755-86960615；邮箱：business@vrviu.com；或者至[官网](http://www.vrviu.com)"联系我们" 。 
