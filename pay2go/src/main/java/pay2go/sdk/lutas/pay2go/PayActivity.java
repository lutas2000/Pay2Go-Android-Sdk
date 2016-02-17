package pay2go.sdk.lutas.pay2go;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.HashMap;

/**
 * Created by lutas on 16/2/16.
 * 智付寶支付頁面
 */
public class PayActivity extends Activity
{
    private ApiConnect api;
    ProgressBar progressBar;
    WebView webView;
    TextView errorText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        initView();

        api = new ApiConnect(mHandler);
        Intent intent = getIntent();
        HashMap<String, String> inputValues =
                (HashMap<String, String>)intent.getSerializableExtra("inputValues");
        String urlPath = intent.getStringExtra("urlPath");

        api.send(urlPath, inputValues);
    }

    private void initView(){
        progressBar = (ProgressBar) findViewById(R.id.pay_progressBar);
        webView = (WebView) findViewById(R.id.pay_webView);
        errorText = (TextView) findViewById(R.id.pay_tv);
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            closeProgress();
            if (msg.what == ApiConnect.RESPONSE_CODE){
                WebSettings settings = webView.getSettings();
                settings.setDefaultTextEncodingName("utf-8");
                settings.setJavaScriptEnabled(true);
                webView.loadData(api.getHtml(), "text/html; charset=utf-8", "utf-8");
                //webView.loadUrl("https://html5test.com/");
            } else if (msg.what == ApiConnect.RESPONSE_ERROR){
                errorText.setText("連接智付寶時出現錯誤！");
            }
        }
    };

    private void closeProgress(){
        progressBar.setVisibility(View.GONE);
    }
}
