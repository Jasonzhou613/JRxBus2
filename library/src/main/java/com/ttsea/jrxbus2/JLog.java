package com.ttsea.jrxbus2;

import android.util.Log;

/**
 * log <br>
 * <p>
 * <b>more:</b>更多请点 <a href="http://www.ttsea.com" target="_blank">这里</a> <br>
 * <b>date:</b> 2017/1/17 10:26 <br>
 * <b>author:</b> Jason <br>
 * <b>version:</b> 1.0 <br>
 * <b>last modified date:</b> 2017/1/17 10:26.
 */
class JLog {
    private static boolean DEBUG = true;

    public static void enableLog(boolean enable) {
        DEBUG = enable;
    }

    public static void d(String tag, String msg) {
        if (DEBUG) {
            msg = combineLogMsg(msg);
            Log.d(tag, "" + msg);
        }
    }

    public static void e(String tag, String msg) {
        if (DEBUG) {
            msg = combineLogMsg(msg);
            Log.e(tag, "" + msg);
        }
    }

    /** 组装动态传参的字符串 将动态参数的字符串拼接成一个字符串 */
    private static String combineLogMsg(String... msg) {
        StringBuilder sb = new StringBuilder();
        sb.append("[Thread:").append(Thread.currentThread().getId()).append("]");
        sb.append(getCaller()).append(": ");
        if (null != msg) {
            for (String s : msg) {
                sb.append(s);
            }
        }
        return sb.toString();
    }

    private static String getCaller() {
        StackTraceElement[] trace = new Throwable().fillInStackTrace().getStackTrace();
        String caller = "<unknown>";
        for (int i = 3; i < trace.length; i++) {
            Class<?> clazz = trace[i].getClass();
            if (!clazz.equals(JLog.class)) {
                String callingClass = trace[i].getClassName();
                callingClass = callingClass.substring(callingClass.lastIndexOf('.') + 1);
                callingClass = callingClass.substring(callingClass.lastIndexOf('$') + 1);
                caller = callingClass + "." + trace[i].getMethodName()
                        + "(rows:" + trace[i].getLineNumber() + ")";
                break;
            }
        }
        return caller;
    }
}

