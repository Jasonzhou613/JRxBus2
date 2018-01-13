package com.ttsea.jrxbus2.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ttsea.jrxbus2.RxBus2;
import com.ttsea.jrxbus2.Subscribe;
import com.ttsea.jrxbus2.ThreadMode;

public class MainActivity extends Activity implements View.OnClickListener {
    private final String TAG = "MainActivity";

    private Button btnSendEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RxBus2.getInstance().register(this);

        btnSendEvent = (Button) findViewById(R.id.btnSendEvent);

        btnSendEvent.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus2.getInstance().unRegister(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        Bundle bundle;

        switch (view.getId()) {
            case R.id.btnSendEvent:
                intent = new Intent(this, SendEventActivity.class);
                startActivity(intent);
                break;

            default:
                break;
        }
    }

    //默认普通事件
    @Subscribe
    public void onRxBusEvent(String msg) {
        Log.d(TAG, "msg:" + msg);
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    //默认普通事件，不是以"Event"结尾，是收不到消息的
    @Subscribe
    public void onRxBusEvent2(String msg) {
        Log.d(TAG, "msg:" + msg);
        Toast.makeText(this, msg + "2", Toast.LENGTH_SHORT).show();
    }

    //普通事件，并且设定该方法只接收指定code的事件
    @Subscribe(code = 2)
    public void onCodeEvent(String msg) {
        Log.d(TAG, "msg:" + msg + "\ncode 2\n" + Thread.currentThread().getName());
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    //普通事件，并且设定该方法运行的线程和设定只接收指定code的事件
    @Subscribe(threadMode = ThreadMode.IO, code = 2)
    public void onThreadModeEvent(String msg) {
        Log.d(TAG, "msg:" + msg + "\ncode 2\n" + Thread.currentThread().getName());
    }

    //只接收粘性事件
    @Subscribe(receiveStickyEvent = true)
    public void onRxStickyEvent(String msg) {
        Log.d(TAG, "msg:" + msg);
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
