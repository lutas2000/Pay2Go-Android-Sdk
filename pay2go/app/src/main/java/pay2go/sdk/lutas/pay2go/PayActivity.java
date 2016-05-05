package pay2go.sdk.lutas.pay2go;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import org.apache.http.util.EncodingUtils;

import java.util.HashMap;

/**
 * Created by lutas on 16/2/16.
 * 智付寶支付頁面
 */
public class PayActivity extends Activity
{
    private final static String TAG = PayActivity.class.getName();
    WebView webView;
    TextView errorText;
    TextView returnBtn;

    String cancelBtnText;
    String successBtnText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        initView();

        Intent intent = getIntent();
        HashMap<String, String> inputValues =
                (HashMap<String, String>) intent.getSerializableExtra("inputValues");
        String urlPath = intent.getStringExtra("urlPath");

        webView.postUrl(urlPath, getPostData(inputValues));
    }

    private void initView(){
        webView = (WebView) findViewById(R.id.pay_webView);
        errorText = (TextView) findViewById(R.id.pay_tv_error);
        returnBtn = (TextView) findViewById(R.id.pay_tv_back);

        setupToolBar();
        setupWebView();
    }

    private void setupToolBar(){
        Intent intent = getIntent();
        if (intent.getBooleanExtra("toolBarEnable", false)) {
            String title = intent.getStringExtra("title");
            int backgroundRes = intent.getIntExtra("toolBarBackgroungRes", 0);
            int textColor = intent.getIntExtra("textColor", 0);

            Toolbar toolbar = (Toolbar) findViewById(R.id.pay_toolBar);
            TextView titleText = (TextView) findViewById(R.id.pay_tv_title);
            toolbar.setBackgroundResource(backgroundRes);
            titleText.setTextColor(getResources().getColor(textColor));
            titleText.setText(title);
        }

        cancelBtnText = intent.getStringExtra("cancelBtnText");
        successBtnText = intent.getStringExtra("successBtnText");
        if (cancelBtnText == null)
            cancelBtnText = "取消";
        if (successBtnText == null)
            successBtnText = "完成";
    }

    private void setupWebView(){
        WebSettings settings = webView.getSettings();
        settings.setDefaultTextEncodingName("utf-8");
        settings.setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());
        //webView.addJavascriptInterface(new MyJavaScriptInterface(this), "HtmlViewer");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @Override
            public void onPageFinished(WebView mWebView, String url) {
                super.onPageFinished(mWebView, url);

                if (url.contains("Status=SUCCESS")) {
                    returnBtn.setText(successBtnText);
                    returnBtn.setTextColor(Color.GREEN);
                } else {
                    returnBtn.setText(cancelBtnText);
                }
            }
        });
    }

    private byte[] getPostData(HashMap<String, String> inputValues){
        StringBuilder stringBuilder = new StringBuilder();
        for(String key : inputValues.keySet())
            stringBuilder.append('&').append(key).append('=')
                    .append(inputValues.get(key));
        stringBuilder.delete(0, 1);

        return EncodingUtils.getBytes(stringBuilder.toString(), "BASE64");
    }

    public void onReturnClick(View view){
        finish();
    }
}
