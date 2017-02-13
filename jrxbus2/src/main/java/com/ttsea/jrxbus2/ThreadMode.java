package com.ttsea.jrxbus2;

/**
 * // to do <br>
 * <p>
 * <b>more:</b>更多请点 <a href="http://www.ttsea.com" target="_blank">这里</a> <br>
 * <b>date:</b> 2017/1/11 15:19 <br>
 * <b>author:</b> Jason <br>
 * <b>version:</b> 1.0 <br>
 * <b>last modified date:</b> 2017/1/11 15:19.
 */
public enum ThreadMode {
    /**
     * current thread
     */
    CURRENT_THREAD,

    /**
     * android main thread, see AndroidSchedulers.mainThread()
     */
    MAIN,

    /**
     * new thread, see  Schedulers.newThread()
     */
    NEW_THREAD,

    /**
     * io, see Schedulers.io()
     */
    IO,

    /**
     * single, see Schedulers.single()
     */
    SINGLE,

    /**
     * computation, see Schedulers.computation()
     */
    COMPUTATION,

    /**
     * trampoline, see Schedulers.trampoline()
     */
    TRAMPOLINE
}
