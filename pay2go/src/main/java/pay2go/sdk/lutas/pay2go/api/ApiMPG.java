package pay2go.sdk.lutas.pay2go.api;

import android.os.Handler;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by lutas on 16/2/5.
 * 智付寶MPG API串接
 */
public class ApiMPG
{
    public final static int RESPONSE_CODE = 15;
    public final static int RESPONSE_ERROR = -1;
    final static String urlPath = "https://capi.pay2go.com/MPG/mpg_gateway"; //測試專用網址
    final static String VERSION = "1.2";

    HashMap<String,String> inputValues;
    Handler handler;
    String html;

    public ApiMPG(Handler handler) {
        this.handler = handler;
        inputValues = new HashMap<>();
    }

    public void put(String key,String value){
        inputValues.put(key, value);
    }

    public void send(String hashKey,String hashIV){
        long timestamp = Calendar.getInstance().getTimeInMillis();
        String checkValue = CreateCheckValue(hashKey, hashIV);
        //Log.i("check value", checkValue);
        inputValues.put("CheckValue", checkValue);
        inputValues.put("TimeStamp", String.valueOf(timestamp));
        inputValues.put("Version", VERSION);

        post();
    }

    private String CreateCheckValue(String hashKey,String hashIV){
        String checkValue =
                "HashKey=" + hashKey+
                "&Amt="+ inputValues.get("Amt")+
                "&MerchantID="+ inputValues.get("MerchantID")+
                "&MerchantOrderNo="+ inputValues.get("MerchantID")+
                "&TimeStamp="+ inputValues.get("TimeStamp")+
                "&Version="+ inputValues.get("Version")+
                "&HashIV="+ hashIV;
        return encrypt(checkValue);
    }

    private String encrypt(String s){
        try{
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            sha.update(s.getBytes());
            return byte2hex(sha.digest());
        }catch(Exception e){
            e.printStackTrace();
            return "";
        }
    }

    private String byte2hex(byte[] b){
        String hs="";
        for (int n=0;n<b.length;n++){
            String stmp = (Integer.toHexString(b[n] & 0XFF));
            if (stmp.length()==1)
                hs = hs+ "0"+ stmp;
            else
                hs = hs + stmp;
        }
        return hs.toUpperCase();
    }

    private void post(){
        Thread postThread = new Thread(runPost);
        postThread.start();
    }

    private Runnable runPost = new Runnable () {
        @Override
        public void run() {
            try {
                HttpURLConnection urlConnection = setupConnection();
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);

                StringBuilder stringBuilder = new StringBuilder();
                for(String key : inputValues.keySet())
                    stringBuilder.append('&').append(encodeUrl(key)).append('=')
                            .append(encodeUrl(inputValues.get(key)));
                    //stringBuilder.append('&').append(key).append('=')
                      //      .append(inputValues.get(key));
                //刪除多餘的'&'
                stringBuilder.delete(0, 1);
                /*
                stringBuffer.append(encodeUrl("MerchantID")).append('=').append(encodeUrl(merchantID))
                        .append('&').append(encodeUrl("RespondType")).append('=').append(encodeUrl(respondType))
                        .append('&').append(encodeUrl("CheckValue")).append('=').append(encodeUrl(checkValue))
                        .append('&').append(encodeUrl("TimeStamp")).append('=').append(encodeUrl(String.valueOf(timestamp)))
                        .append('&').append(encodeUrl("Version")).append('=').append(encodeUrl(VERSION))
                        .append('&').append(encodeUrl("MerchantOrderNo")).append('=').append(encodeUrl(merchantOrderNo))
                        .append('&').append(encodeUrl("Amt")).append('=').append(encodeUrl(String.valueOf(amt)))
                        .append('&').append(encodeUrl("ItemDesc")).append('=').append(encodeUrl(itemDesc))
                        .append('&').append(encodeUrl("Email")).append('=').append(encodeUrl(email))
                        .append('&').append(encodeUrl("LoginType")).append('=').append(encodeUrl(String.valueOf(loginType)));*/
                String urlParameters = stringBuilder.toString();
                Log.i("urlParameters", urlParameters);

                DataOutputStream wr = new DataOutputStream (
                        urlConnection.getOutputStream());
                wr.write(urlParameters.getBytes());
                wr.flush();
                wr.close();

                getResponse(urlConnection);
            } catch (IOException e) {
                e.printStackTrace();
                handler.sendEmptyMessage(RESPONSE_ERROR);
            }
        }
    };

    private String encodeUrl(String key){
        try {
            return URLEncoder.encode(key, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }

    private void getResponse(HttpURLConnection urlConnection) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(
                urlConnection.getInputStream()));

        StringBuilder stringBuilder = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null)
            stringBuilder.append(inputLine);
        //Log.i("response", stringBuilder.toString());
        html = stringBuilder.toString();

        in.close();
        urlConnection.disconnect();

        handler.sendEmptyMessage(RESPONSE_CODE);
    }

    private HttpURLConnection setupConnection() throws IOException {
        URL url = new URL(urlPath);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("POST");
        urlConnection.setConnectTimeout(2000);
        urlConnection.setReadTimeout(2000);

        return urlConnection;
    }

    public String getHtml(){ return html; }
}
