package com.van.rtvsplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;


public class MainActivity extends AppCompatActivity {

    IjkplayerVideoView videoView;
    WebView mWebView;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mWebView = findViewById(R.id.WebView);

        WebSettings webSettings = mWebView.getSettings();

        // 设置与Js交互的权限
        webSettings.setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(new IjkJs(), "ijk");

        mWebView.loadUrl("file:///android_asset/rtvsdemo.html");

        EditText txtsim = findViewById(R.id.txtsim);
        txtsim.setText("013777883241");
        button = findViewById(R.id.btn);

        videoView = findViewById(R.id.VideoView);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sim= txtsim.getText().toString();
                // 必须另开线程进行JS方法调用(否则无法调用)
                mWebView.post(new Runnable() {
                    @Override
                    public void run() {
                        mWebView.evaluateJavascript("javascript:Connect(\""+sim+"\",1,1,0,\"et.test.cvtsp.com\",15001,0);", new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String value) {


                            }
                        });

                    }
                });

            }


        });

    }
    // 定义接收js调用对象
    public class IjkJs extends Object {

        // 定义JS需要调用的方法
        // 被JS调用的方法必须加入@JavascriptInterface注解
        @JavascriptInterface
        public void play(String rtmp) {
            //此处为 js 返回的结果
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    videoView.setVideoPath(rtmp);
                    videoView.start();
                }
            });
        }
        @JavascriptInterface
        public void log(String log) {
            System.out.println(log);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        videoView.stop();
        videoView.release();
    }

}