# ADS_Frontend_Test

实现了安卓前端与后端的通信（主要用于 ADS 和 HOP 项目），使用 OkHttp3 发送请求和处理响应。  
项目地址：https://github.com/qwq-y/ADS_Frontend_Test

## 环境配置

注：版本号及具体细节等可参见本项目的相关配置文件。

1. 在模块级别的 build.gradle 文件中配置依赖：
    
    在 dependencies 中添加添加以下代码

        dependencies {
            ......
            implementation 'com.google.code.gson:gson:2.8.8'
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

### HOP

HOP 项目可使用 postHOP 方法，下面对该接口的使用进行介绍。

    private CompletableFuture<CustomResponse> postHOP(String url, Map<String, String> params, File imageFile)

#### 参数

- String url： 后端接口地址
- Map<String, String> params：以键值对的形式储存的多个文本参数（可以用于字符串、整形、长整型等，但均需以 String 类型传参）
- File imageFile：单张图片文件

注：params 或 imageFile 若为空请传入 null
    
#### 返回类型

返回类型为 CompletableFuture<CustomResponse> ，其中 CustomResponse 是自定义类，包括四个私有参数：

    private String status;    // 状态码

    private List<String> images;    // base64 编码的图片列表

    List<Map<String, Object>> messages;    // 消息列表

    private File video;    // 单个视频文件（HOP 没有用到）

可以通过 Getter 和 Setter 进行获取或者修改。注意，CustomPesponse 只可以使用无参的构造函数。

#### 使用示例

    private void testHop() {

        // 准备图片参数
        File imageFile = getImageFileFromDrawable(MainActivity.this, R.drawable.test_image);

        // 准备文本参数
        Map<String, String> params = new HashMap<>();
        params.put("point_coord_0", "200");
        params.put("point_coord_1", "100");
        params.put("point_label", "1");
        params.put("use_mask", "0");
        params.put("mode", "0");

        // 后端接口地址（请替换为实际地址）
        String url = "http://192.168.3.124:80/imagelist";

        // 调用 postHOP 方法，与 HOP 后端进行通信
        postHOP(url, params, imageFile)
                .thenAccept(customResponse -> {
                    
                    // 获取收到的响应信息（这里获取了图片列表和消息列表）
                    List<String> encodedImages = customResponse.getImages();
                    List<Map<String, Object>> messages = customResponse.getMessages();

                    // 处理收到的图片列表（这里将指定图片显示在页面中）
                    String image = encodedImages.get(imageNo);
                    byte[] decodedBytes = Base64.decode(image, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                    runOnUiThread(() -> {
                        setTextView("image number: " + imageNo);
                        imageView.setImageBitmap(bitmap);
                    });

                    // 处理收到的消息列表（这里将消息作为日志打印）
                    for (Map<String, Object> message : messages) {
                        Log.d(TAG, message.toString());
                    }

                })
                .exceptionally(e -> {
                    // 处理异常
                    Log.d(TAG, "exception in interface: " + e.getMessage());
                    return null;
                });
    }

### ADS

目前只写了单独的视频收发，见 postVideo 方法和 testVideo 方法。

## 注意事项

1. 网络请求需要单开线程，对 UI 界面的更改需要在主线程进行。
2. 不能与 localhost 进行通信（如需在本机测试，可以用 Apache 进行转发代理）。
3. 后端返回的应答需为对应格式，键值名也需要保持一致。
