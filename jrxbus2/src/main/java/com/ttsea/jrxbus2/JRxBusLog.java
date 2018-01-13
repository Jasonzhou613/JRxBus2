package com.ttsea.jrxbus2;

import android.util.Log;

/**
 * log，所有log含有前缀"RxBus2-" <br>
 * <p>
 * <b>more:</b>更多请点 <a href="http://www.ttsea.com" target="_blank">这里</a> <br>
 * <b>date:</b> 2017/1/17 10:26 <br>
 * <b>author:</b> Jason <br>
 * <b>version:</b> 1.0 <br>
 * <b>last modified date:</b> 2017/1/17 10:26.
 */
final class JRxBusLog {
    private static boolean DEBUG = false;
    /**
     * 输出日志等级，当DEBUG为false的时候会根据设置的等级来输出日志<br>
     * 从高到低为ASSERT, ERROR, WARN, INFO, DEBUG, VERBOSE<br>
     * 使用adb shell setprop log.tag.{@link #LOG_TAG}来控制输出log等级
     */
    private static String LOG_TAG = "jrxbus2.log.LEVEL";

    protected static void enableLog(boolean enable) {
        DEBUG = enable;
    }

    public static void v(String msg) {
        v(null, msg);
    }

    public static void v(String tag, String msg) {
        if (DEBUG || Log.isLoggable(LOG_TAG, Log.VERBOSE)) {
            msg = combineLogMsg(msg);
            Log.i(getTag(tag), "" + msg);
        }
    }

    public static void d(String msg) {
        d(null, msg);
    }

    public static void d(String tag, String msg) {
        if (DEBUG || Log.isLoggable(LOG_TAG, Log.DEBUG)) {
            msg = combineLogMsg(msg);
            Log.d(getTag(tag), "" + msg);
        }
    }

    public static void i(String msg) {
        i(null, msg);
    }

    public static void i(String tag, String msg) {
        if (DEBUG || Log.isLoggable(LOG_TAG, Log.INFO)) {
            msg = combineLogMsg(msg);
            Log.i(getTag(tag), "" + msg);
        }
    }

    public static void w(String msg) {
        w(null, msg);
    }

    public static void w(String tag, String msg) {
        if (DEBUG || Log.isLoggable(LOG_TAG, Log.WARN)) {
            msg = combineLogMsg(msg);
            Log.w(getTag(tag), "" + msg);
        }
    }

    public static void e(String msg) {
        e(null, msg);
    }

    public static void e(String tag, String msg) {
        if (DEBUG || Log.isLoggable(LOG_TAG, Log.ERROR)) {
            msg = combineLogMsg(msg);
            Log.e(getTag(tag), "" + msg);
        }
    }

    /**
     * 当传入的tag为null时，默认获取类名来作为tag
     */
    private static String getTag(String tag) {
        String tagPrefix = "RxBus2-";
        if (tag != null) {
            return tagPrefix + tag;
        }
        StackTraceElement[] traces = new Throwable().fillInStackTrace().getStackTrace();
        String callingClass = "";

        for (StackTraceElement trace : traces) {
            callingClass = trace.getClassName();
            if (!callingClass.equals(JRxBusLog.class.getName())) {
                if (callingClass.lastIndexOf('.') != -1) {
                    callingClass = callingClass.substring(callingClass.lastIndexOf('.') + 1);
                }
                if (callingClass.lastIndexOf('$') != -1) {
                    callingClass = callingClass.substring(0, callingClass.indexOf('$'));
                }
                break;
            }
        }

        return tagPrefix + callingClass;
    }

    /**
     * 组装动态传参的字符串 将动态参数的字符串拼接成一个字符串
     */
    private static String combineLogMsg(String... msg) {
        StringBuilder sb = new StringBuilder();
        sb.append("[Thread:").append(Thread.currentThread().getId()).append("]");

        StackTraceElement[] traces = new Throwable().fillInStackTrace().getStackTrace();
        String caller = "<unknown>";
        for (StackTraceElement trace : traces) {
            String callingClass = trace.getClassName();
            if (!callingClass.equals(JRxBusLog.class.getName())) {
                if (callingClass.lastIndexOf('.') != -1) {
                    callingClass = callingClass.substring(callingClass.lastIndexOf('.') + 1);
                }
                caller = callingClass + "." + trace.getMethodName() + "(rows:" + trace.getLineNumber() + ")";
                break;
            }
        }

        sb.append(caller).append(": ");
        if (null != msg) {
            for (String s : msg) {
                sb.append(s);
            }
        }

        return sb.toString();
    }
}