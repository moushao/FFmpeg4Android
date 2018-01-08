[ ![LastVersion](https://img.shields.io/badge/LastVersion-1.0.0beat-blue.svg)]() [ ![Download](https://img.shields.io/badge/Download-zip-brightgreen.svg) ](https://codeload.github.com/moushao/FFmpeg4Android/zip/master)  [ ![Apk下载](https://img.shields.io/badge/APK%20%E4%B8%8B%E8%BD%BD-1.0.0-yellowgreen.svg)](https://raw.githubusercontent.com/moushao/FFmpeg4Android/master/ffmpeg.apk)  [ ![AppVeyor](https://img.shields.io/appveyor/ci/gruntjs/grunt.svg)](https://bintray.com/moushao/maven/ffmpeg-4-android/_latestVersion)  
 
 
 ## *How to use 
 in your project build.gradle,add jecenter()
 
    buildscript {
        repositories {
        ...
        jcenter()
        ...
    }
    
 and then,in your module build.gradle add
 
    dependencies {
          compile 'com.moushao.tech:ffmpeg-4-android:LastVersion'
    }
   

 
 
 ## *The following is how to compile FFmpeg as a .so library on the Android platform

#### 本篇文章已授权微信公众号 [guolin_blog （郭霖）]()独家发布

#### 背景：
公司项目，iOS端说Android端拍的视频，码率不对，他无法播放。图片和视频的选择或拍摄一直用的**[PictureSelector](https://github.com/LuckSiege/PictureSelector)**，问了开源作者才知道录制的没做任何处理，直接调用的系统拍摄，只提供了设置拍摄的质量为0还是1的方法，无奈，只能想着自己压缩了。说到视频的处理，现在最火的肯定还是FFmpeg。FFmpeg的移植，不难，就是太坑了。既然移植比较麻烦，我想着能不能将FFpemg移植后封装成一个lib，以后其他项目使用直接引用，不需要再移植了，经过三天的各种折腾和测试，终于成功了。。。。


#### A 源码下载
首先把[ffmpeg-3.2.4](http://download.csdn.net/download/moushao/10139387)的源码下载下来，源码最好就下载我这个，我最开始用的官网的最新版本，build的时候老是报错，最后换成3.2.4，就不再报错了！

下载下来后解压放到纯英文路径下，然后将源码路径添加到环境变量中

#### B 编译FFmpeg生成.so文件
##### 1 在main目录下新建jni文件夹
![图片.png](http://upload-images.jianshu.io/upload_images/927828-2043b0876e23578c.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

直接点击finish自动生成
##### 2 copy FFmpeg的源码和.mk文件到jni
将Demo中jni里的这些文件拷贝到你model中的jni，图中红色选中的三个不要复制，这个是根据项目生成的（如果你直接引用这个model，就不用管）。
![图片.png](http://upload-images.jianshu.io/upload_images/927828-dce456e330f41c44.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

##### 3 如图将FFmpegKit的代码copy到java工程中
![图片.png](http://upload-images.jianshu.io/upload_images/927828-6f3a057bb3b5307b.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


##### 4 编译FFmpegKit
控制台中切到java文件夹目录下，输入**javah com.ffmpegtest.xxx.utils.FFmpegKit** (FFmpegKit的引用路径)，只有在java目录下执行这行命令才能成功，直接进到Utils目录执行会报找不到.so文件，编译成功后会生成图中黑框中的两个c++文件，剪贴这两个文件到jni文件夹中
![图片.png](http://upload-images.jianshu.io/upload_images/927828-391a0236cf18b4c7.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

##### 5 创建com_ffmpegtest_www_utils_FFmpegKit.c文件
![图片.png](http://upload-images.jianshu.io/upload_images/927828-f8fe1cf4c27c883b.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
你或许会注意到新建的文件和生成的哪个.h文件名称一致，只是格式不同。对，没错，必须一致，这是按照项目包名生成的，不能随便更改！

##### 6 修改Android.mk和Application.mk
1 更改Android.mk中LOCAL_SRC_FILES值和FFmpeg源码路径
![图片.png](http://upload-images.jianshu.io/upload_images/927828-3a0a764d6cf19bf1.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

2 Application.mk中，设置生成so库，这里我们只设置生成armeabi-v7a下的（其他库空格添加生成，考虑到生成的so文件都是10M+，就没设置其他的）
![图片.png](http://upload-images.jianshu.io/upload_images/927828-05af584e97683a04.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
#####6 其他配置
1 在app下的gradle中设置so库支持和路径读取：
![图片.png](http://upload-images.jianshu.io/upload_images/927828-cf9219046e0a2835.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

2 Manifest中添加版本设置（别忘了添加读写和摄像头权限）
![图片.png](http://upload-images.jianshu.io/upload_images/927828-c5ec7996d72b75b8.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
3 gradle.properties中添加 **android.useDeprecatedNdk=true**，不添加可能会爆**xxx.so" is 32-bit instead of 64-bit**的错误
![图片.png](http://upload-images.jianshu.io/upload_images/927828-0d6f664538605905.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)



#### C 编译FFmpeg生成.so文件
以上配置完成后，就可以编译了，控制台中切换到jni目录下，执行**ndk-build**命令[(需要配置ndk)](http://blog.csdn.net/u012737144/article/details/52943918)，执行完成后，会自动生成libs和obj两个文件夹，这就是我们需要的.so文件了。
![](http://upload-images.jianshu.io/upload_images/927828-7b0abd411959d7e7.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
这里插一句：如果编译的时候报各种警告，生成的.so是不行的，把我们上面的新建的哪个**com_ffmpegtest_www_utils_FFmpegKit.c**文件，随便乱删除点东西保存后执行ndk-build，然后再恢复正确，再执行ndk-build，不要问我为什么，姑且理解为编译器不识好歹！

##### D 总结
到此为止，FFmpeg的移植就完成了，至于怎么压缩怎么添加水印，那是FFmpeg命令行的具体应用了，大家结合着Demo和FFmpeg文档自求多福吧。Demo中的ffmpeglib已经封装好，大家可以在项目中直接import model使用（亲测没问题），避免了每次都要编译，而且独立的mk和gradle配置，免去了和其他第三方库的冲突。

十分感谢[reverse_Android](http://www.jianshu.com/u/575222264e2d)对此次集成的帮助和解答。

参考文章：
[最简单的基于FFmpeg的移动端例子：Android HelloWorld](http://blog.csdn.net/leixiaohua1020/article/details/47008825/)
[FFmpeg-Android拍摄压缩Demo](http://www.jianshu.com/p/fd748001ca53)（敲黑板）
[仿微信视频拍摄UI, 基于ffmpeg的视频录制编辑(上)](http://www.jianshu.com/p/5a173841a828)（api>22的情况下无法找不到so文件，慎用）



[项目源码github传送门](https://github.com/moushao/FFmpeg4Android)

碎碎念：诸君若是喜欢，请点个赞，谢谢！

更多问题加群:**584275290**
