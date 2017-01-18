package com.ttsea.jrxbus2;

import java.lang.reflect.Method;

/**
 * 订阅者中订阅方法的一些信息 <br>
 * <p>
 * <b>more:</b>更多请点 <a href="http://www.ttsea.com" target="_blank">这里</a> <br>
 * <b>date:</b> 2017/1/11 15:19 <br>
 * <b>author:</b> Jason <br>
 * <b>version:</b> 1.0 <br>
 * <b>last modified date:</b> 2017/1/11 15:19.
 */
class SubscriberMethodInfo {
    private Object subscriber;
    private Method method;
    private Class<?> eventType;
    private int code;
    private ThreadMode threadMode;
    private boolean receiveStickyEvent;

    /**
     * 订阅者方法信息
     *
     * @param subscriber         订阅者
     * @param method             订阅者的订阅方法
     * @param eventType          订阅的事件类型
     * @param code               自定义的code
     * @param threadMode         线程类型{@link ThreadMode}
     * @param receiveStickyEvent 是否接收粘性事件
     */
    SubscriberMethodInfo(Object subscriber, Method method, Class<?> eventType, int code, ThreadMode threadMode, boolean receiveStickyEvent) {
        this.method = method;
        this.threadMode = threadMode;
        this.eventType = eventType;
        this.subscriber = subscriber;
        this.code = code;
        this.receiveStickyEvent = receiveStickyEvent;
    }

    public Object getSubscriber() {
        return subscriber;
    }

    public void setSubscriber(Object subscriber) {
        this.subscriber = subscriber;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Class<?> getEventType() {
        return eventType;
    }

    public void setEventType(Class<?> eventType) {
        this.eventType = eventType;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public ThreadMode getThreadMode() {
        return threadMode;
    }

    public void setThreadMode(ThreadMode threadMode) {
        this.threadMode = threadMode;
    }

    public boolean isReceiveStickyEvent() {
        return receiveStickyEvent;
    }

    public void setReceiveStickyEvent(boolean receiveStickyEvent) {
        this.receiveStickyEvent = receiveStickyEvent;
    }

    @Override
    public String toString() {
        return "SubscriberMethodInfo{" +
                "method=" + method +
                ", threadMode=" + threadMode +
                ", eventType=" + eventType +
                ", subscriber=" + subscriber +
                ", code=" + code +
                ", receiveStickyEvent=" + receiveStickyEvent +
                '}';
    }
}
