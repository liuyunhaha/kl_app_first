## 一、集成SDK
### 1.1 gradle集成
+ 在 **Project** 级别的 **build.gradle** 文件中添加如下配置依赖

```java
buildscript {
    repositories {
        jcenter()
        mavenCentral()
    }
}
```

+ 在 **Module** 工程目录下的 **build.gradle**文件中添加依赖项：

1）引入**dataeye **sdk

```java
dependencies {
implementation 'io.github.dataeyesdk:dataeye-android-sdk:2.7.6'
}
```

最新版本从[ReleaseNote](https://www.yuque.com/maticoo/eetpxb/fg99bg1lg3us1c9o)中获取


混淆规则：

```java
-keep class com.bun.miitmdid.** {*;}
-keep public class com.netease.nis.sdkwrapper.Utils {public <methods>;}
```

### 1.2 手动集成
[获取aar](#M2Llz)

## 二、初始化
```java
//方式一
DataEyeAnalyticsSDK instance = DataEyeAnalyticsSDK.sharedInstance(this, APPID, SERVER_URL);

//方式二
DataEyeConfig config = DataEyeConfig.getInstance(this, APPID, SERVER_URL);
DataEyeAnalyticsSDK instance = DataEyeAnalyticsSDK.sharedInstance(config);
```

参数说明:

+ APPID: 您的产品的 APP_ID，需要进行配置，dataeye系统生成，可通过在[项目产品](https://www.yuque.com/maticoo/eetpxb/hp4ms7m961op8rly)页面获取产品ID

<img src="https://cdn.nlark.com/yuque/0/2024/png/38964713/1728468453743-afebdc95-52bc-44e3-9167-3e2df989e610.png" width="1887" title="" crop="0,0,1,1" id="uf8582320" class="ne-image">

+ SERVER_URL: 数据上传的 URL，从这里获取：[打点开发API](https://www.yuque.com/maticoo/eetpxb/rlxfst5gst64dnd2)

## 三、常用功能
### 3.1 设置公共事件属性
公共事件属性指的就是每个事件都会带有的属性，您可以调用 **setSuperProperties** 来设置公共事件属性，我们推荐您在发送事件前，先设置公共事件属性。对于一些重要的属性，譬如用户的会员等级、来源渠道等，这些属性需要设置在每个事件中，此时您可以将这些属性设置为公共事件属性。

```java
try {
        JSONObject superProperties = new JSONObject();
        superProperties.put("channel","ta");//字符串
        superProperties.put("age",1);//数字
        superProperties.put("isSuccess",true);//布尔
        superProperties.put("birthday",new Date());//时间
    
        JSONObject object = new JSONObject();
        object.put("key", "value");
        superProperties.put("object",object);//对象
        
        JSONObject object1 = new JSONObject();
        object1.put("key", "value");
        JSONArray  arr    = new JSONArray();
        arr.put(object1);
        superProperties.put("object_arr",arr);//对象组
 
        //设置公共事件属性
        instance.setSuperProperties(superProperties);
    } catch (JSONException e) {
        e.printStackTrace();
    }

```

公共事件属性将会被保存到缓存中，无需每次启动 App 时调用。如果调用` <font style="color:rgb(143,149,158);">setSuperProperties</font>` 上传了先前已设置过的公共事件属性，则会覆盖之前的属性。

+ Key 为该属性的名称，为字符串类型，规定只能以字母开头，包含数字，字母和下划线 "_"，长度最大为 50 个字符，对字母大小写不敏感,TA会统一转化为小写字母
+ Value 为该属性的值，支持字符串、数字、布尔、时间、对象、对象组、数组

### 3.2 开启自动采集
以下代码示例开启安装、启动、关闭事件，详细了解[SDK的自动采集](https://www.yuque.com/maticoo/eetpxb/lba4h57sa87i42im)能力。

```java
List<DataEyeAnalyticsSDK.AutoTrackEventType> eventTypeList = new ArrayList<>();
//APP安装事件
eventTypeList.add(DataEyeAnalyticsSDK.AutoTrackEventType.APP_INSTALL);
//APP启动事件
eventTypeList.add(DataEyeAnalyticsSDK.AutoTrackEventType.APP_START);
//APP关闭事件
eventTypeList.add(DataEyeAnalyticsSDK.AutoTrackEventType.APP_END);
//APP崩溃事件
eventTypeList.add(DataEyeAnalyticsSDK.AutoTrackEventType.APP_CRASH); 
//开启自动采集事件
DataEyeAnalyticsSDK.sharedInstance(this, APP_ID).enableAutoTrack(eventTypeList);
```

### 3.3 发送事件
您可以调用` <font style="color:rgb(143,149,158);">track</font>` 来上传事件，建议您根据先前梳理的埋点文档来设置事件的属性，此处以用户购买某商品作为范例：

```java
try {
    JSONObject properties = new JSONObject();
    properties.put("product_name","商品名");
    instance.track("product_buy",properties);} 
catch (JSONException e) {
    e.printStackTrace();
}
```

事件的名称是字符串类型，只能以字母开头，可包含数字，字母和下划线 "_"，长度最大为 50 个字符。

## 四、最佳实践
以下示例代码包含以上所有操作，我们推荐按照如下步骤使用:

```java
DataEyeAnalyticsSDK instance = DataEyeAnalyticsSDK.sharedInstance(context, APP_ID, SERVER_URL);

// 设置公共事件属性以后，每个事件都会带有公共事件属性
try {
    JSONObject superProperties = new JSONObject();
    superProperties.put("channel", "ta");//字符串
    superProperties.put("age", 1);//数字
    superProperties.put("isSuccess", true);//布尔
    superProperties.put("birthday", new Date());//时间

    JSONObject object = new JSONObject();
    object.put("key", "value");
    superProperties.put("object", object);//对象

    JSONObject object1 = new JSONObject();
    object1.put("key", "value");
    JSONArray arr = new JSONArray();
    arr.put(object1);
    superProperties.put("object_arr", arr);//对象组
    
    //设置公共事件属性
    instance.setSuperProperties(superProperties);
} catch (JSONException e) {
    e.printStackTrace();
}

//开启自动采集事件
List<DataEyeAnalyticsSDK.AutoTrackEventType> eventTypeList = new ArrayList<>();
eventTypeList.add(DataEyeAnalyticsSDK.AutoTrackEventType.APP_INSTALL);
eventTypeList.add(DataEyeAnalyticsSDK.AutoTrackEventType.APP_START);
eventTypeList.add(DataEyeAnalyticsSDK.AutoTrackEventType.APP_END);
instance.enableAutoTrack(eventTypeList);

//发送事件
try {
    JSONObject properties = new JSONObject();
    properties.put("product_name", "商品名");
    instance.track("product_buy", properties);
} catch (JSONException e) {
    e.printStackTrace();
}
```

## 五、进阶使用
[DataEye SDK(Android)进阶文档](https://www.yuque.com/maticoo/eetpxb/fihe0d7cxb47h1yh)

## 六、更新日志
[BI SDK release note](https://www.yuque.com/maticoo/eetpxb/fg99bg1lg3us1c9o)

## 七、FAQ
暂无
