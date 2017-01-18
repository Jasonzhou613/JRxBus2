package com.ttsea.jrxbus2;

/**
 * 存储即将被执行的方法的信息 <br>
 * <p>
 * <b>more:</b>更多请点 <a href="http://www.ttsea.com" target="_blank">这里</a> <br>
 * <b>date:</b> 2017/1/13 13:47 <br>
 * <b>author:</b> Jason <br>
 * <b>version:</b> 1.0 <br>
 * <b>last modified date:</b> 2017/1/13 13:47.
 */
class InvokeMethodInfo {
    private final String TAG = "InvokeMethodInfo";

    private SubscriberMethodInfo subscriberMethodInfo;
    private Object param;

    public InvokeMethodInfo(SubscriberMethodInfo subscriberMethodInfo, Object param) {
        this.subscriberMethodInfo = subscriberMethodInfo;
        this.param = param;
    }

    public SubscriberMethodInfo getSubscriberMethodInfo() {
        return subscriberMethodInfo;
    }

    public void setSubscriberMethodInfo(SubscriberMethodInfo subscriberMethodInfo) {
        this.subscriberMethodInfo = subscriberMethodInfo;
    }

    public Object getParam() {
        return param;
    }

    public void setParam(Object param) {
        this.param = param;
    }

    /** 调用方法 */
    public void invoke() {
        try {
            subscriberMethodInfo.getMethod().invoke(subscriberMethodInfo.getSubscriber(), param);

        } catch (Exception e) {
            e.printStackTrace();
            JLog.e(TAG, "Exception e:" + e.toString());
        }
    }

    @Override
    public String toString() {
        return "InvokeMethodInfo{" +
                ", subscriberMethodInfo=" + subscriberMethodInfo +
                ", param=" + param +
                '}';
    }
}
