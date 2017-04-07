# JRxBus2
在RxJava2.x的基础上实现(RxBus)事件总线，2017/01/17<br>

####如何引用
 * 1.Maven

```xml
    <dependency>
      <groupId>com.ttsea.jrxbus2</groupId>
      <artifactId>jrxbus2</artifactId>
      <version>1.0.2</version>
      <type>pom</type>
    </dependency>
```

 * 2.Gradle

```xml
    compile 'com.ttsea.jrxbus2:jrxbus2:1.0.2'
```

 * 3.Ivy

```xml
    <dependency org='com.ttsea.jrxbus2' name='jrxbus2' rev='1.0.2'>
      <artifact name='jrxbus2' ext='pom' ></artifact>
    </dependency>
```

####如何使用
 * 1.在Activity或者Fragment的onCreate中调用 register(Object) 进行注册
 * 2.在Activity或者Fragment的onDestroy中调用 unRegister(Object) 进行反注册

```java
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //注册
        RxBus2.getInstance().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //反注册
        RxBus2.getInstance().unRegister(this);
    }
```

 * 3.使用@Subscribe来标识订阅方法，订阅方法允许有且只有一个参数

```java
    //默认普通事件
    @Subscribe
    public void onRxBusEvent(String msg) {
        Log.d(TAG, "msg:" + msg);
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    //普通事件，并且设定该方法运行的线程和设定只接收指定code的事件
    @Subscribe(threadMode = ThreadMode.IO, code = 2)
    public void onRxBusEvent2(String msg) {
        Log.d(TAG, "msg:" + msg + "\ncode 2\n" + Thread.currentThread().getName());
    }

    //只接收粘性事件
    @Subscribe(receiveStickyEvent = true)
    public void onRxBusEvent3(String msg) {
        Log.d(TAG, "msg:" + msg);
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
```

 * 4.使用post(Object)或者使用post(int, Object)来发送一个普通事件

```java
      RxBus2.getInstance().post("normal event");
```
 * 5.使用 postStickyEvent(Object) 或者使用 postStickyEvent(int, Object)  来发送一个粘性事件

```java
     RxBus2.getInstance().post(2, "normal event");
```
 * 6.使用 debugMode(boolean) 来设置 RxBus2 是否打印日志

```java
     RxBus2.getInstance().debugMode(true);
```

####注意事项
 * 1.如果不进行反注册可能会引起内存溢出
 * 2.如果使用 post(int, Object) 来发送事件，则在注册的方法中需要声明对应的code，否则无法接收到该事件，如：post(1, object)，则注册的方法对应需要用“@Subscribe(code = 1)”来修饰
 * 3.在app退出时，建议调用 removeAllStickyEvents() 来清除所有粘性事件
 * 4.粘性事件最大存储值为 MAX_STICKY_EVENT_COUNT ,超出这个值，如果想继续加入下一个，则会移除最先加入的那个粘性事件
 * 5.粘性事件是以事件的参数类型为key来存储的，如：使用postStickyEvent(String)发送一个事件后，再次使用postStickyEvent(String)发送一个事件，则后面的事件会覆盖掉前面的事件，因为这两个事件都是以"java.lang.String"为key进行存储的，所以后面put进来的事件会覆盖前面的事件
 * 6.如果需要接收粘性事件，则需要用“@Subscribe(receiveStickyEvent = true)”来标识该方法，默认情况下是不接收粘性事件的
 * 7.建议连续发送事件数少于1000
 * 8.如果连续发送的事件量比较大，建议使用“@Subscribe(threadMode = ThreadMode.NEW_THREAD)”来标识接收事件的方法，使其运行在新线程中避免阻碍UI线程



