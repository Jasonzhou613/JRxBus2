package com.ttsea.jrxbus2.sample;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.ttsea.jrxbus2.RxBus2;

/**
 * 用于发送普通事件和粘性事件的页面 <br>
 * <p>
 * <b>more:</b> 更多请参考<a href="http://www.ttsea.com" title="小周博客">www.ttsea.com</a> <br>
 * <b>date:</b> 2017/1/17 10:34 <br>
 * <b>author:</b> Jason <br>
 * <b>version:</b> 1.0 <br>
 * <b>last modified date:</b> 2017/1/17 10:34.
 */
public class SendEventActivity extends Activity implements View.OnClickListener {
    private Button btnSendNormalEvent;
    private Button btnSendNormalEvent2;
    private Button btnSendStickEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_event_main);

        btnSendNormalEvent = (Button) findViewById(R.id.btnSendNormalEvent);
        btnSendNormalEvent2 = (Button) findViewById(R.id.btnSendNormalEvent2);
        btnSendStickEvent = (Button) findViewById(R.id.btnSendStickEvent);

        btnSendNormalEvent.setOnClickListener(this);
        btnSendNormalEvent2.setOnClickListener(this);
        btnSendStickEvent.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSendNormalEvent:
                RxBus2.getInstance().post("normal event");
                break;

            case R.id.btnSendNormalEvent2:
                RxBus2.getInstance().post(2, "normal event");
                break;

            case R.id.btnSendStickEvent:
                RxBus2.getInstance().postStickyEvent(2, "sticky event");
                break;

            default:
                break;
        }
    }
}