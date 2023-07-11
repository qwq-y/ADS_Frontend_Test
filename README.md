# ADS_Frontend_Test

实现了安卓前端与后端的通信（RESTful 风格），使用 OkHttp3 发送请求和处理响应。包括多个重载的 okhttpGet 方法和 okhttpPost 方法，多个用于实例测试的方法，以及一些辅助函数。  
项目地址：https://github.com/qwq-y/ADS_Frontend_Test

## 环境配置

注：版本号及具体细节等可参见本项目的相关配置文件。

1. 在模块级别的 build.gradle 文件中配置依赖：
    
    在 dependencies 中添加添加以下代码

        dependencies {
            ......
            implementation 'com.squareup.picasso:picasso:2.8'
            implementation 'com.github.bumptech.glide:glide:4.12.0'
            annotationProcessor 'com.github.bumptech.glide:compiler:4.12.0'
            implementation 'com.squareup.okhttp3:okhttp:4.10.0'
            implementation 'com.android.volley:volley:1.2.0'
            ......
        }
    
    修改 build.gradle 后需要重新进行 sync 操作。

2. 在 AndroidManifest.xml 文件中设置权限：  
   
    在 ```<manifest></manifest>``` 中添加以下代码
   
        <uses-permission android:name="android.permission.INTERNET" />
        <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
        <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />

    在 ```<application></application>``` 中添加相关设置

        android:allowBackup="true"
        android:dataExtractionRules="@xml/data_extraction_rules"
        android:fullBackupContent="@xml/backup_rules"
        android:usesCleartextTraffic="true"
        tools:targetApi="31">        

    在 ```<application></application>``` 中添加以下代码

        <uses-library android:name="org.apache.http.legacy" android:required="false"/>

## 接口使用

HOP 和 ADS 项目主要使用 okhttpPost 方法，下面对该接口的使用进行介绍。

    private CompletableFuture<CustomResponse> okhttpPost(String url, Map<String, String> params, File imageFile,  File videoFile, Boolean isImage, Boolean isText, Boolean isVideo, Boolean isMultipleImage) {}

### 参数

- String url： 后端接口地址
- Map<String, String> params：以键值对的形式储存的多个文本参数（可以用于字符串、整形、长整型等，但均需以 String 类型传参）（若为空请传入 null）
- File imageFile：图片文件（若为空请传入 null）
- File videoFile：视频文件（若为空请传入 null）
- Boolean isImage：是否需要返回单张图片
- Boolean isText：是否需要返回文本
- Boolean isVideo：是否需要返回视频
- Boolean isMultipleImage：是否需要返回多图片压缩文件

注：
  - DINO v2 部分参数可设置为 (url, null, imageFile, null, false, true, false, true)
  - MINI-GPT-4 部分参数可设置为 (url, params, imageFile, null, false, true, false, false)
  - SAM 部分参数可设置为 (url, params, imageFile, null, true, true, false, false)
    
### 返回类型

返回类型为 CompletableFuture<CustomResponse> ，其中 CustomResponse 是自定义类，包括四个私有参数：

        private String text;    // 文本数据

        private File image;    // 单张图片数据

        private File video;    // 视频数据

        private File zipImage;    // 多图片的压缩数据

可以通过 Getter 和 Setter 进行获取或者修改。注意，CustomPesponse 只可以使用无参的构造函数。

### 使用示例

1. 完整的调用和接收示例：

        private void demo() {
        
            File imageFile = getImageFileFromDrawable(MainActivity.this, R.drawable.test_image);

            Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());

            Map<String, String> params = new HashMap<>();
            params.put("point_coord_0", "200");
            params.put("point_coord_1", "100");
            params.put("point_label", "1");
            params.put("use_mask", "0");
            params.put("mode", "0");

            String url = "http://172.18.36.107:5000/sam";

            okhttpPost(url, params, imageFile, null, true, false, false, false)
                    .thenAccept(customResponse -> {
                        // 在这里处理返回的 customResponse

                        String text = customResponse.getText();
                        File image = customResponse.getImage();

                        if (text != null) {
                            // 在这里处理返回的文本数据 text
                        }

                        if (image != null) {
                            // 在这里处理返回的图片数据 image
                            // 可以这样转化为 Bitmap 之后显示在页面
                            Bitmap bitmapNew = BitmapFactory.decodeFile(image.getAbsolutePath());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    imageView.setImageBitmap(bitmapNew);
                                }
                            });
                        }
                    })
                    .exceptionally(e -> {
                        // 处理异常
                        Log.d(TAG, "exception in interface: " + e.getMessage());
                        return null;
                    });
        }

2. 对接收到的视频文件的处理

        File videoFile = customResponse.getVideo();
        runOnUiThread(() -> {
            VideoView videoView = findViewById(R.id.videoView);
            videoView.setVideoPath(videoFile.getAbsolutePath());
            videoView.start();
        });
   
3. 对接收到的多图压缩文件的处理

        File zipFile = customResponse.getZipImage();
        private void unzipImages(File zipFile) throws IOException {
            ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile));
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                if (!entry.isDirectory() && isImageFile(entry.getName())) {
                    String fileName = entry.getName();
                    String extractedFilePath = MainActivity.this.getFilesDir() + File.separator + fileName;
                    File extractedFile = new File(extractedFilePath);
                    FileOutputStream fos = new FileOutputStream(extractedFile);
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = zipInputStream.read(buffer)) > 0) {
                        fos.write(buffer, 0, length);
                    }
                    fos.close();
                    runOnUiThread(() -> {
                        imageView.setImageURI(Uri.fromFile(extractedFile));
                    });
                }
                zipInputStream.closeEntry();
            }
            zipInputStream.close();
        }
        
## 辅助函数

使用 okhttpPost 时，请将这些辅助函数也复制到项目中：
- convertResponseBodyToImage()
- convertResponseBodyToVideo()
- convertResponseBodyToFile()
    

## 注意事项

1. 网络请求需要单开线程，对 UI 界面的更改需要在主线程进行。
2. 不能与 localhost 进行通信（如需在本机测试，可以用 Apache 进行转发代理）。
3. 后端返回的应答需为对应格式。
