package com.ttsea.jrxbus2;

/**
 * 一般消息 <br>
 * <p>
 * <b>more:</b>更多请点 <a href="http://www.ttsea.com" target="_blank">这里</a> <br>
 * <b>date:</b> 2017/1/11 15:19 <br>
 * <b>author:</b> Jason <br>
 * <b>version:</b> 1.0 <br>
 * <b>last modified date:</b> 2017/1/11 15:19.
 */
class Message {
    private int code = -1;
    protected Object object;

    /**
     * 构造函数
     *
     * @param code 自定义code
     * @param o    事件类型
     */
    public Message(int code, Object o) {
        this.code = code;
        this.object = o;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    @Override
    public String toString() {
        return "Message{" +
                "code=" + code +
                ", object=" + object +
                '}';
    }
}
