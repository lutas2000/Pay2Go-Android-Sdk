package pay2go.sdk.lutas.pay2go;

import android.os.Handler;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

/**
 * Created by lutas on 16/2/5.
 * API串接
 */
public class ApiConnect
{
    public final static int RESPONSE_CODE = 15;
    public final static int RESPONSE_ERROR = -1;

    private HashMap<String,String> inputValues;
    private String urlPath;
    Handler handler;
    String html;

    public ApiConnect(Handler handler) {
        this.handler = handler;
    }

    public void send(String urlPath,HashMap<String,String> inputValues){
        this.inputValues = inputValues;
        this.urlPath = urlPath;

        post();
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
                //刪除多餘的'&'
                stringBuilder.delete(0, 1);
                String urlParameters = stringBuilder.toString();

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
