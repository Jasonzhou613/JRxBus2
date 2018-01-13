package com.ttsea.jrxbus2;

/**
 * 粘性消息 <br>
 * <p>
 * <b>more:</b>更多请点 <a href="http://www.ttsea.com" target="_blank">这里</a> <br>
 * <b>date:</b> 2017/1/14 10:00 <br>
 * <b>author:</b> Jason <br>
 * <b>version:</b> 1.0 <br>
 * <b>last modified date:</b> 2017/1/14 10:00.
 */
class StickyMessage extends Message {
    private int canExecuteTimes = 1;

    /**
     * 构造函数
     *
     * @param canExecuteTimes 可被执行次数，当该事件被执行的次数达到canExecuteTimes时，该事件即会消亡<br/>
     *                        当canExecuteTimes小于0时，该粘性事会一直存在
     * @param o               事件类型
     */
    protected StickyMessage(int canExecuteTimes, Object o) {
        super(-1, o);
        this.canExecuteTimes = (canExecuteTimes == 0 ? 1 : canExecuteTimes);
    }

    public void setCanExecuteTimes(int canExecuteTimes) {
        this.canExecuteTimes = canExecuteTimes;
    }

    public int getCanExecuteTimes() {
        return canExecuteTimes;
    }

    @Override
    public String toString() {
        return "StickyMessage{" +
                "canExecuteTimes=" + canExecuteTimes +
                "code=" + getCode() +
                ", object=" + getObject() +
                '}';
    }
}
