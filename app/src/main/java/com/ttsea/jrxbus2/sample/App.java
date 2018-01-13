package com.ttsea.jrxbus2.sample;

import android.app.Application;

import com.ttsea.jrxbus2.RxBus2;

/**
 * // to do <br>
 * <p>
 * <b>date:</b> 2018/1/7 10:45 <br>
 * <b>author:</b> zhijian.zhou <br>
 * <b>version:</b> 1.0 <br>
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        RxBus2.getInstance().debugMode(true);
    }
}
