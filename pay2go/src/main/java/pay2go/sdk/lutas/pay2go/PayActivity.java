package pay2go.sdk.lutas.pay2go;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import org.apache.http.util.EncodingUtils;

import java.util.HashMap;

/**
 * Created by lutas on 16/2/16.
 * 智付寶支付頁面
 */
public class PayActivity extends Activity
{
    WebView webView;
    TextView errorText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        initView();

        Intent intent = getIntent();
        HashMap<String, String> inputValues =
                (HashMap<String, String>)intent.getSerializableExtra("inputValues");
        String urlPath = intent.getStringExtra("urlPath");


        webView.postUrl(urlPath, getPostData(inputValues));
    }

    private void initView(){
        webView = (WebView) findViewById(R.id.pay_webView);
        errorText = (TextView) findViewById(R.id.pay_tv);
        setupWebView();
    }

    private void setupWebView(){
        WebSettings settings = webView.getSettings();
        settings.setDefaultTextEncodingName("utf-8");

        settings.setJavaScriptEnabled(true);
    }

    private byte[] getPostData(HashMap<String, String> inputValues){
        StringBuilder stringBuilder = new StringBuilder();
        for(String key : inputValues.keySet())
            stringBuilder.append('&').append(key).append('=')
                    .append(inputValues.get(key));
        stringBuilder.delete(0, 1);

        return EncodingUtils.getBytes(stringBuilder.toString(), "BASE64");
    }
}
