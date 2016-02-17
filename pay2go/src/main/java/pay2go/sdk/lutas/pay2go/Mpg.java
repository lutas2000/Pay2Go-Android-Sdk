package pay2go.sdk.lutas.pay2go;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import java.security.MessageDigest;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by lutas on 16/2/16.
 * 智付寶MPG API
 * 請注意set所有必填value
 * start後自動開啟PayActivity
 */
public class Mpg
{
    final static String urlPath = "https://capi.pay2go.com/MPG/mpg_gateway"; //測試專用網址
    final static private String TAG = Mpg.class.getName();

    Activity activity;
    HashMap<String,String> inputValues;

    public Mpg(Activity activity) {
        this.activity = activity;
        inputValues = new HashMap<>();
    }

    /**
     * 打開PayActivity，傳送input values
     * @param hashKey
     * @param hashIV
     */
    public void start(String hashKey,String hashIV){
        inputValues.put("Version", activity.getString(R.string.api_version));
        long timestamp = Calendar.getInstance().getTimeInMillis();
        inputValues.put("TimeStamp", String.valueOf(timestamp));
        String checkValue = CreateCheckValue(hashKey, hashIV);
        inputValues.put("CheckValue", checkValue);

        Intent intent = activity.getIntent();
        intent.putExtra("inputValues", inputValues);
        intent.putExtra("urlPath", urlPath);
        intent.setClass(activity, PayActivity.class);
        activity.startActivity(intent);
    }

    private String CreateCheckValue(String hashKey,String hashIV){
        String checkValue =
                "HashKey=" + hashKey+
                        "&Amt="+ inputValues.get("Amt")+
                        "&MerchantID="+ inputValues.get("MerchantID")+
                        "&MerchantOrderNo="+ inputValues.get("MerchantOrderNo")+
                        "&TimeStamp="+ inputValues.get("TimeStamp")+
                        "&Version="+ inputValues.get("Version")+
                        "&HashIV="+ hashIV;
        //Log.i("checkValue", checkValue);
        //Log.i("checkValue", encrypt(checkValue));
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

    /**
     * 必填
     * @param MerchantID 智付寶商店代號(Varchar(15))
     */
    public void setMerchantID(String MerchantID){
        if (MerchantID.length() > 15){
            Log.e(TAG, "MerchantID length > 15");
            return;
        }
        inputValues.put("MerchantID", MerchantID);
    }

    /**
     * @param RespondType 回傳格式: JSON or String
     */
    public void setRespondType(String RespondType){
        if (!RespondType.equals("JSON") && !RespondType.equals("String")){
            Log.e(TAG, "RespondType most be 'JSON' or 'String'!");
            return;
        }
        inputValues.put("RespondType", RespondType);
    }

    /**
     * @param LangType 頁面語系:en or zh-tw 預設為:zh-tw
     */
    public void setLangType(String LangType){
        if (!LangType.equals("en") && !LangType.equals("zh-tw")){
            Log.e(TAG, "LangType most be 'en' or 'zh-tw'!");
            return;
        }
        inputValues.put("RespondType", LangType);
    }

    /**
     * 必填
     * @param MerchantOrderNo 商店訂單編號(Varchar(20)):限英、數字、”_”,同一商店中編號不可重覆
     */
    public void setMerchantOrderNo(String MerchantOrderNo){
        if (MerchantOrderNo.length() > 15){
            Log.e(TAG, "MerchantID length > 15");
            return;
        }
        inputValues.put("MerchantOrderNo", MerchantOrderNo);
    }

    /**
     * 必填
     * @param Amt 訂單金額(int(10))
     */
    public void setAmt(int Amt){
        inputValues.put("Amt", String.valueOf(Amt));
    }

    /**
     * @param ItemDesc 必填-商品資訊(Varchar(50))
     */
    public void setItemDesc(String ItemDesc){
        if (ItemDesc.length() > 50){
            Log.e(TAG, "ItemDesc length > 50");
            return;
        }
        inputValues.put("ItemDesc", ItemDesc);
    }

    /**
     * @param TradeLimit 交易限制秒數(int(3)),秒數下限為 60 秒
     */
    public void setTradeLimit(int TradeLimit){
        inputValues.put("TradeLimit", String.valueOf(TradeLimit));
    }

    /**
     * 必填
     * @param Email 付款者Email(Varchar(50))
     */
    public void setEmail(String Email){
        if (Email.length() > 50){
            Log.e(TAG, "Email length > 50");
            return;
        }
        inputValues.put("Email", Email);
    }

    /**
     * 必填-是否需要登入智付寶會員
     * @param flag true:登入 false:不登入
     */
    public void setLoginType(boolean flag){
        String loginType = (flag)? "1" : "0";
        inputValues.put("LoginType", loginType);
    }
}
