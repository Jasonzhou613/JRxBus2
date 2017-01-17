package com.ttsea.jrxbus2;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * // to do <br/>
 * <p>
 * <b>more:</b> 更多请参考<a href="http://www.ttsea.com" title="小周博客">www.ttsea.com</a> <br/>
 * <b>date:</b> 2017/1/11 15:19 <br/>
 * <b>author:</b> Jason <br/>
 * <b>version:</b> 1.0 <br/>
 * <b>last modified date:</b> 2017/1/11 15:19.
 */
public enum ThreadMode {
    /**
     * current thread
     */
    CURRENT_THREAD,

    /**
     * android main thread, see {@link AndroidSchedulers#mainThread()}
     */
    MAIN,

    /**
     * new thread, see {@link Schedulers#newThread()}
     */
    NEW_THREAD,

    /**
     * io, see {@link Schedulers#io()}
     */
    IO,

    /**
     * single, see {@link Schedulers#single()}
     */
    SINGLE,

    /**
     * computation, see {@link Schedulers#computation()}
     */
    COMPUTATION,

    /**
     * trampoline, see {@link Schedulers#trampoline()}
     */
    TRAMPOLINE
}
