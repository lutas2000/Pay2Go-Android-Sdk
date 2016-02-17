package pay2go.sdk.lutas.pay2go;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;

import pay2go.sdk.lutas.pay2go.api.ApiMPG;

/**
 * Created by lutas on 16/2/16.
 * 智付寶支付頁面
 */
public class PayActivity extends Activity
{
    private ApiMPG mpg;
    ProgressBar progressBar;
    WebView webView;
    TextView errorText;

    String hashKey;
    String hashIV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        initView();
/*
        String hashKey = "QmQJV1K9fBVj7K3mDYaq4BNXxwy64J6U";
        String hashIV = "5WE1Ypbd06x8RAvu";
        String merchantID = "11494569";
        mpg = new MPG(hashKey, hashIV, merchantID);
        mpg.setHandler(mHandler);
        mpg.setEmail("wsrew2000@yahoo.com.tw");
        mpg.pay(2000, "201602050002", "測試商品");*/
        mpg = new ApiMPG(mHandler);
        getIntentDatas();
        mpg.send(hashKey, hashIV);
    }

    private void getIntentDatas(){
        Intent intent = getIntent();
        hashKey = intent.getStringExtra("hashKey");
        hashIV = intent.getStringExtra("hashIV");

        String[] keys = {"MerchantID", "RespondType", "LangType", "MerchantOrderNo",
                         "Amt", "ItemDesc", "TradeLimit", "RespondType", "ExpireDate",
                         "ReturnURL", "NotifyURL", "CustomerURL", "ClientBackURL",
                         "Email", "EmailModify", "LoginType", "OrderComment", "CREDIT",
                         "CreditRed", "WEBATM", "VACC", "CVS", "CUSTOM"};
        for(String key : keys) {
            String value = intent.getStringExtra(key);
            if(value != null)
                mpg.put(key, value);
        }
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
            if (msg.what == mpg.RESPONSE_CODE){
                WebSettings settings = webView.getSettings();
                settings.setDefaultTextEncodingName("utf-8");
                webView.loadData(mpg.getHtml(), "text/html; charset=utf-8", "utf-8");
            } else if (msg.what == mpg.RESPONSE_ERROR){
                errorText.setText("連接智付寶時出現錯誤！");
            }
        }
    };

    private void closeProgress(){
        progressBar.setVisibility(View.GONE);
    }
}
