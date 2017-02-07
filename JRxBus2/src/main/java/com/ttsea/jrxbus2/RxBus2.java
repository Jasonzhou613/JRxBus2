package com.ttsea.jrxbus2;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * RxBus2，该事件总线是基于RxJava2.x版本开发的，建议同时连续发送的信息少于1000 <br>
 * <b><i>如何使用</i></b><br>
 * 1.在Activity或者Fragment的onCreate中调用{@link #register(Object)}进行注册<br>
 * 2.在Activity或者Fragment的onDestroy中调用{@link #unRegister(Object)} 进行反注册<br>
 * 3.使用@{@link Subscribe}来标识订阅方法，订阅方法允许有且只有一个参数<br>
 * 4.使用{@link #post(Object)}或者使用{@link #post(int, Object)}来发送一个事件<br>
 * 5.使用{@link #postStickyEvent(Object)}或者使用{@link #postStickyEvent(int, Object)} 来发送一个粘性事件<br>
 * 6.使用{@link #debugMode(boolean)}来设置{@link RxBus2}是否打印日志<br>
 * <p>
 * <b><i>注意事项</i></b><br>
 * 1.如果不进行反注册可能会引起内存溢出<br>
 * 2.如果使用{@link #post(int, Object)}来发送事件，则在注册的方法中需要声明对应的code，否则无法接收到该事件，如：<br>
 * post(1, object)，则注册的方法对应需要用“@Subscribe(code = 1)”来修饰<br>
 * 3.在app退出时，建议调用{@link #removeAllStickyEvents()}来清除所有粘性事件<br>
 * 4.粘性事件最大存储值为{@link #MAX_STICKY_EVENT_COUNT},超出这个值，如果想继续加入下一个，则会移除最先加入的那个粘性事件<br>
 * 5.粘性事件是以事件的参数类型为key来存储的，如：使用postStickyEvent(String)发送一个事件后，再次使用postStickyEvent(String)发送一个事件，
 * 则后面的事件会覆盖掉前面的事件，因为这两个事件都是以"java.lang.String"为key进行存储的，所以后面put进来的事件会覆盖前面的事件<br>
 * 6.如果需要接收粘性事件，则需要用“@Subscribe(receiveStickyEvent = true)”来标识该方法，默认情况下是不接收粘性事件的<br>
 * 7.建议连续发送事件数少于1000<br>
 * 8.如果连续发送的事件量比较大，建议使用“@Subscribe(threadMode = ThreadMode.NEW_THREAD)”来标识接收事件的方法，
 * 使其运行在新线程中避免阻碍UI线程<br>
 * <p>
 * <b>more:</b>更多请点 <a href="http://www.ttsea.com" target="_blank">这里</a> <br>
 * <b>date:</b> 2017/1/10 10:49 <br>
 * <b>author:</b> Jason <br>
 * <b>version:</b> 1.0 <br>
 * <b>last modified date:</b> 2017/1/10 10:49.
 */
public final class RxBus2 {
    private final String TAG = "RxBus2";

    private static volatile RxBus2 rxBus;

    //存储所有订阅者
    private Map<Object, Object> subscribersMap = new HashMap<>();
    //存储所有订阅者返回的Disposable,便于取消订阅
    private Map<Object, List<Disposable>> disposablesMap = new HashMap<>();
    //存储所有订阅的方法中带有@Subscribe方法的信息
    private Map<Object, List<SubscriberMethodInfo>> subscriberMethodInfosMap = new HashMap<>();
    //存储所有粘性事件
    private final Map<Class<?>, StickyMessage> stickyEventMap = new ConcurrentHashMap<>();
    //保存stickyEventMap的key
    private final ConcurrentLinkedQueue<Class<?>> stickyEventKeys = new ConcurrentLinkedQueue<>();
    // 主题，PublishSubject只会把在订阅发生的时间点之后来自原始Observable的数据发射给观察者
    private final Subject<Object> bus = PublishSubject.create().toSerialized();

    /** 保存粘性事件的最大值 */
    private int MAX_STICKY_EVENT_COUNT = 10000;

    private RxBus2() {
    }

    public static RxBus2 getInstance() {
        if (rxBus == null) {
            synchronized (RxBus2.class) {
                if (rxBus == null) {
                    rxBus = new RxBus2();
                }
            }
        }
        return rxBus;
    }

    public void debugMode(boolean debug) {
        JLog.enableLog(debug);
    }

    /**
     * 发送一个新的事件，事件code默认为-1
     *
     * @param o 事件类型
     */
    public void post(Object o) {
        post(-1, o);
    }

    /**
     * 发送一个事件，根据code进行区分，只有用同样的code标识了的方法才会接收到此事件
     *
     * @param code 自定义code
     * @param o    时间类型
     */
    public void post(int code, Object o) {
        bus.onNext(new Message(code, o));
    }

    /**
     * 发送一个粘性事件，事件默认消费一次后消亡
     *
     * @param o 事件类型
     */
    public void postStickyEvent(Object o) {
        postStickyEvent(1, o);
    }

    /**
     * 发送一个粘性事件，事件默认消费dieAfterExecuteCount次后消亡
     *
     * @param dieAfterExecuteCount 可消费次数，如果该值小于0，即表示该粘性事件会一直存在
     * @param o                    事件类型
     */
    public void postStickyEvent(int dieAfterExecuteCount, Object o) {
        StickyMessage stickyMessage = new StickyMessage(dieAfterExecuteCount, o);

        synchronized (stickyEventMap) {
            if (stickyEventMap.size() > MAX_STICKY_EVENT_COUNT) {
                //超过最大值的时候，移除最先加入的那个粘性事件
                Class<?> c = stickyEventKeys.poll();
                stickyEventMap.remove(c);
                JLog.d(TAG, "sticky event size is larger than " + MAX_STICKY_EVENT_COUNT +
                        ", we will remove the first one, c:" + c.getSimpleName());
            }
            //先移除，在添加，确保后发送的粘性事件排在最后
            stickyEventKeys.remove(o.getClass());
            stickyEventKeys.add(o.getClass());
            stickyEventMap.put(o.getClass(), stickyMessage);

            printlnStickyEvent();
        }

        bus.onNext(stickyMessage);
    }

    /** 注册 */
    public void register(Object subscriber) {
        //避免重复
        if (subscribersMap.containsKey(subscriber)) {
            return;
        }
        Class<?> subClass = subscriber.getClass();
        Method[] methods = subClass.getDeclaredMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(Subscribe.class)) {
                //获得参数类型
                Class[] parameterType = method.getParameterTypes();
                //参数不为空 且参数个数为1
                if (parameterType != null && parameterType.length == 1) {
                    Subscribe sub = method.getAnnotation(Subscribe.class);
                    int code = sub.code();
                    ThreadMode threadMode = sub.threadMode();
                    boolean receiveStickyEvent = sub.receiveStickyEvent();

                    Class eventType = parameterType[0];
                    SubscriberMethodInfo subscriberMethodInfo = new SubscriberMethodInfo(subscriber,
                            method, eventType, code, threadMode, receiveStickyEvent);

                    addSubscriber(subscriber);
                    addSubscriberMethodInfoToMap(subscriber, subscriberMethodInfo);
                    bindObservable(subscriberMethodInfo);
                }
            }
        }

        printlnRxBusInfo();
    }

    /**
     * 将订阅者添加到map中，便于避免重复注册
     *
     * @param subscriber 订阅者
     */
    private void addSubscriber(Object subscriber) {
        subscribersMap.put(subscriber, subscriber);
    }

    /**
     * 将所有订阅者的订阅方法信息 以subscriber为key保存到map中，便于反注册和执行
     *
     * @param subscriber           订阅者
     * @param subscriberMethodInfo 订阅者的订阅方法信息
     */
    private void addSubscriberMethodInfoToMap(Object subscriber, SubscriberMethodInfo subscriberMethodInfo) {
        List<SubscriberMethodInfo> subscriberMethodInfos = subscriberMethodInfosMap.get(subscriber);
        if (subscriberMethodInfos == null) {
            subscriberMethodInfos = new ArrayList<>();
            subscriberMethodInfosMap.put(subscriber, subscriberMethodInfos);
        }

        if (!subscriberMethodInfos.contains(subscriberMethodInfo)) {
            subscriberMethodInfos.add(subscriberMethodInfo);
        }
    }

    /** 绑定observable */
    private void bindObservable(final SubscriberMethodInfo subscriberMethodInfo) {
        try {
            Disposable disposable = toObservable(Message.class, subscriberMethodInfo)
                    //.observeOn(Schedulers.io())
                    //在io线程中通过subscriber和方法参数，筛选出所有符合执行条件的方法，并放回
                    //这里考虑到，如果项目比大时，注册的方法可能比较多，所以筛选工作放在io线程中，避免引起界面卡顿
                    .map(new Function<Message, List<InvokeMethodInfo>>() {
                        @Override
                        public List<InvokeMethodInfo> apply(Message message) throws Exception {
                            println("map->apply to List<InvokeMethodInfo>, thread:" + Thread.currentThread());
                            List<InvokeMethodInfo> methodInfos = getInvokeMethods(subscriberMethodInfo, message);
                            //如果是粘性事件，计算粘性事件的课消费次数
                            if (message instanceof StickyMessage) {
                                StickyMessage stickyMessage = (StickyMessage) message;
                                int canExecuteTimes = stickyMessage.getCanExecuteTimes();
                                //只有消费次数大于等于0的粘性事件才会消亡，消费次数小于0的粘性事件是不会消亡的
                                if (canExecuteTimes >= 0) {
                                    //若粘性事件剩余消费次数小于方法数，则表明粘性事件的消费次数不足，有些方法不能执行到，这里将多出来的方法移除掉
                                    if (canExecuteTimes < methodInfos.size()) {
                                        Class<?> key = subscriberMethodInfo.getEventType();
                                        stickyEventMap.remove(key);
                                        stickyEventKeys.remove(key);
                                        String methodNames = "";
                                        while (methodInfos.size() > canExecuteTimes) {
                                            //移除最后一个
                                            InvokeMethodInfo info = methodInfos.remove(methodInfos.size() - 1);
                                            methodNames = methodNames + "," + info.getSubscriberMethodInfo().getMethod().getName();
                                        }
                                        methodNames = methodNames.replaceFirst(",", "");
                                        println("The " + stickyMessage.toString() + " canExecuteTimes is not enough, the method["
                                                + methodNames + "] will not invoked.");
                                    } else {
                                        stickyMessage.setCanExecuteTimes(stickyMessage.getCanExecuteTimes() - methodInfos.size());
                                    }
                                }
                                printlnStickyEvent();
                            }

                            return methodInfos;
                        }
                    })
                    //指定observeOn线程
                    .observeOn(getObserveOnScheduler(subscriberMethodInfo.getThreadMode()))
                    .subscribe(
                            new Consumer<List<InvokeMethodInfo>>() {
                                @Override
                                public void accept(List<InvokeMethodInfo> invokeMethodInfos) throws Exception {
                                    println("subscribe->accept, thread:" + Thread.currentThread());
                                    if (invokeMethodInfos == null) {
                                        return;
                                    }
                                    //执行上一步map方法所返回的方法集合
                                    for (int i = 0; i < invokeMethodInfos.size(); i++) {
                                        invokeMethodInfos.get(i).invoke();
                                    }
                                }
                            },
                            new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) throws Exception {
                                    println("thread:" + Thread.currentThread());
                                    String msg = "Error msg:";
                                    if (throwable != null) {
                                        msg = msg + throwable.getMessage();
                                    }
                                    JLog.e(TAG, "throwable, msg:" + msg);
                                }
                            });

            addDisposableToMap(subscriberMethodInfo.getSubscriber(), disposable);

        } catch (Exception e) {
            e.printStackTrace();
            JLog.e(TAG, "Exception e:" + e.getMessage());
        }
    }

    /** 根据传递的 eventType 类型返回特定类型(eventType)的 被观察者 */
    private <T> Observable<T> toObservable(final Class<T> eventType, SubscriberMethodInfo methodInfo) {
        if (methodInfo.isReceiveStickyEvent()) {
            synchronized (stickyEventMap) {
                Observable<T> observable = bus.ofType(eventType).subscribeOn(Schedulers.io());
                final StickyMessage stickyMessage = stickyEventMap.get(methodInfo.getEventType());

                if (stickyMessage != null) {
                    return observable.mergeWith(new Observable<T>() {
                        @Override
                        protected void subscribeActual(Observer<? super T> observer) {
                            observer.onNext(eventType.cast(stickyMessage));
                        }
                    });
                } else {
                    return observable;
                }
            }

        } else {
            return bus.ofType(eventType).subscribeOn(Schedulers.io());
        }
    }

    /**
     * 将订阅事件的Disposable以subscriber为Key保存到map中，用于取消订阅时用
     *
     * @param subscriber event类型
     * @param disposable 订阅事件返回的disposable
     */
    private void addDisposableToMap(Object subscriber, Disposable disposable) {
        List<Disposable> disposables = disposablesMap.get(subscriber);
        if (disposables == null) {
            disposables = new ArrayList<>();
            disposablesMap.put(subscriber, disposables);
        }

        if (!disposables.contains(disposable)) {
            disposables.add(disposable);
        }
    }

    /** 根据threadMode来设置Observe的ObserveOn线程, 默认为Main线程 */
    private Scheduler getObserveOnScheduler(ThreadMode threadMode) {
        Scheduler scheduler = AndroidSchedulers.mainThread();

        switch (threadMode) {
            case CURRENT_THREAD:
            case MAIN:
                scheduler = AndroidSchedulers.mainThread();
                break;
            case NEW_THREAD:
                scheduler = Schedulers.newThread();
                break;
            case IO:
                scheduler = Schedulers.io();
                break;
            case SINGLE:
                scheduler = Schedulers.single();
                break;
            case COMPUTATION:
                scheduler = Schedulers.computation();
                break;
            case TRAMPOLINE:
                scheduler = Schedulers.trampoline();
                break;

            default:
                break;
        }
        return scheduler;
    }

    /**
     * 根据当前的订阅的方法信息实例和发送的消息筛选出符合执行条件的方法
     *
     * @param currentMethodInfo 订阅者
     * @param message           发送的消息
     */
    private List<InvokeMethodInfo> getInvokeMethods(SubscriberMethodInfo currentMethodInfo, Message message) {
        List<InvokeMethodInfo> invokeMethodInfos = new ArrayList<>();

        for (Map.Entry<Object, List<SubscriberMethodInfo>> entry : subscriberMethodInfosMap.entrySet()) {
            List<SubscriberMethodInfo> methodInfos = entry.getValue();
            //跳过空值
            if (methodInfos == null) {
                continue;
            }
            for (int i = 0; i < methodInfos.size(); i++) {
                SubscriberMethodInfo methodInfo = methodInfos.get(i);
                Class objectType = message.getObject().getClass();
                //订阅方法中的订阅者与当前订阅值是同一个，并且订阅事件类型一样，则符合条件
                if (currentMethodInfo == methodInfo
                        && objectType == methodInfo.getEventType()
                        && message.getCode() == methodInfo.getCode()) {

                    InvokeMethodInfo invokeMethodInfo = new InvokeMethodInfo(methodInfo, message.getObject());

                    //如果是粘性事件，则还需判断该方法是否声明了接收粘性事件
                    if (message instanceof StickyMessage) {
                        if (methodInfo.isReceiveStickyEvent()) {
                            invokeMethodInfos.add(invokeMethodInfo);
                        }

                    } else {
                        if (!methodInfo.isReceiveStickyEvent()) {
                            invokeMethodInfos.add(invokeMethodInfo);
                        }
                    }
                }
            }
        }

        return invokeMethodInfos;
    }

    /** 反注册 */
    public void unRegister(Object subscriber) {
        unSubscribeBySubscriber(subscriber);
        subscriberMethodInfosMap.remove(subscriber);
        subscribersMap.remove(subscriber);

        printlnRxBusInfo();
    }

    /**
     * 取消订阅关系
     *
     * @param subscriber key
     */
    private void unSubscribeBySubscriber(Object subscriber) {
        List<Disposable> disposables = disposablesMap.get(subscriber);
        if (disposables != null) {
            while (disposables.size() > 0) {
                Disposable disposable = disposables.remove(0);
                if (disposable != null) {
                    disposable.dispose();
                }
            }
        }
        disposablesMap.remove(subscriber);
    }

    /** 移除指定eventType的Sticky事件 */
    public <T> T removeStickyEvent(Class<T> eventType) {
        synchronized (stickyEventMap) {
            return eventType.cast(stickyEventMap.remove(eventType));
        }
    }

    /** 移除所有的Sticky事件 */
    public void removeAllStickyEvents() {
        synchronized (stickyEventMap) {
            stickyEventMap.clear();
        }
    }

    private void println(String msg) {
        JLog.d(TAG, msg);
    }

    private void printlnRxBusInfo() {
        JLog.d(TAG, "==========subscriber size:" + subscribersMap.size());
        int position = 0;
        for (Map.Entry<Object, Object> subscriberEntry : subscribersMap.entrySet()) {
            String msg = subscriberEntry.getKey().getClass().getSimpleName() + "{";

            List<SubscriberMethodInfo> subscriberMethodInfos = subscriberMethodInfosMap.get(subscriberEntry.getKey());
            if (subscriberMethodInfos != null) {
                msg = msg + "[method:" + subscriberMethodInfos.size() + "(";
                String methodStr = "";
                for (int i = 0; i < subscriberMethodInfos.size(); i++) {
                    SubscriberMethodInfo methodInfo = subscriberMethodInfos.get(i);
                    methodStr = methodStr + ", " + methodInfo.getMethod().getName() + ":"
                            + methodInfo.getEventType().getSimpleName();
                }
                methodStr = methodStr.replaceFirst(", ", "");
                msg = msg + methodStr + ")], ";
            }

            List<Disposable> disposables = disposablesMap.get(subscriberEntry.getKey());
            if (disposables != null) {
                msg = msg + "[disposable:" + disposables.size() + "(";
                String disposableStr = "";
                for (int i = 0; i < disposables.size(); i++) {
                    disposableStr = disposableStr + ", " + disposables.get(i).isDisposed();
                }
                disposableStr = disposableStr.replaceFirst(", ", "");
                msg = msg + disposableStr + ")]";
            }

            msg = msg + "}";
            JLog.d(TAG, "subscriber(" + position + ")->" + msg);
            position++;
        }
    }

    private void printlnStickyEvent() {
        JLog.d(TAG, "==========stickyEventMap size:" + stickyEventMap.size() + ", key size:" + stickyEventKeys.size());
        int position = 0;
        for (Map.Entry<Class<?>, StickyMessage> entry : stickyEventMap.entrySet()) {
            String msg = entry.getKey().getName() + "[" +
                    entry.getValue().getCanExecuteTimes() + ", " + entry.getValue().getObject() +
                    "]";
            JLog.d(TAG, "stickyEvent(" + position + ")->" + msg);
            position++;
        }
    }
}
