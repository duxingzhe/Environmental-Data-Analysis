package com.luxuan.httprequest;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private TextView requestTextView;
    private TextView resultTextView;

    private final static int REQUEST_SUCCESS=0x1;
    private final static int REQUEST_FAILED=0x2;

    private Handler mHandler=new Handler(){

        @Override
        public void handleMessage(Message msg){
            switch(msg.what){
                case REQUEST_SUCCESS:
                    resultTextView.setText((String)msg.obj);
                    break;
                case REQUEST_FAILED:
                    resultTextView.setText("Request Error");
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestTextView=(TextView)findViewById(R.id.request);
        resultTextView=(TextView)findViewById(R.id.result);

        OkhttpUtil.getOkhttpUtil().initOkHttpClient();

        requestTextView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                request();
            }
        });

    }

    private void request(){

        //请求数据
        new Thread(){
            @Override
            public void run(){
                try {
                    Message msg=new Message();
                    msg.what=REQUEST_SUCCESS;
                    msg.obj=OkhttpUtil.getOkhttpUtil().getResponse("河源");
                    mHandler.sendMessage(msg);
                }catch(IOException e){
                    e.printStackTrace();
                    mHandler.sendEmptyMessage(REQUEST_FAILED);
                }
            }
        }.start();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }

}
