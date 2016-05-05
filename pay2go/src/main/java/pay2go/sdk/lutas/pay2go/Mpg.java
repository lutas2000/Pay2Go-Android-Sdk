package pay2go.sdk.lutas.pay2go;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.ColorRes;
import android.util.Log;

import java.security.MessageDigest;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by lutas on 16/2/16.
 * 智付寶MPG API
 * 用於建立交易
 * 請注意set所有必填value
 * start後自動開啟PayActivity
 */
public class Mpg
{
    boolean isTest = false;
    final static String testUrlPath = "https://capi.pay2go.com/MPG/mpg_gateway"; //測試專用網址
    final static String urlPath = "https://api.pay2go.com/MPG/mpg_gateway"; //正式網址
    final static private String TAG = Mpg.class.getName();

    Activity activity;
    Intent intent;

    String hashKey;
    String hashIV;
    HashMap<String,String> inputValues;

    public Mpg(Activity activity) {
        this.activity = activity;
        inputValues = new HashMap<>();
        intent = activity.getIntent();
    }

    /**
     * 打開PayActivity，傳送input values
     */
    public void start(){
        inputValues.put("Version", activity.getString(R.string.api_version));
        long timestamp = Calendar.getInstance().getTimeInMillis();
        inputValues.put("TimeStamp", String.valueOf(timestamp));
        String checkValue = CreateCheckValue();
        inputValues.put("CheckValue", checkValue);

        intent.putExtra("inputValues", inputValues);
        if (isTest)
            intent.putExtra("urlPath", testUrlPath);
        else
            intent.putExtra("urlPath", urlPath);
        intent.setClass(activity, PayActivity.class);
        activity.startActivity(intent);
    }

    private String CreateCheckValue(){
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
     * 是否使用測試API
     */
    public void setTest(boolean flag){
        isTest = flag;
    }

    /**
     * 設定商店資訊
     * @param MerchantID 商店代號
     * @param hashKey
     * @param hashIV
     */
    public void setShop(String MerchantID,String hashKey,String hashIV){
        this.hashKey = hashKey;
        this.hashIV = hashIV;
        inputValues.put("MerchantID", MerchantID);
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
     * 繳費有效期限
     * @param ExpireDate 格式為 YYYYmmDD, a.g:20140620, 預設為7天, 最大值180天
     */
    public void setExpireDate(String ExpireDate){
        inputValues.put("ExpireDate", String.valueOf(ExpireDate));
    }

    /**
     * 交易完成後,以 Form Post 方式導回ReturnURL
     * @param ReturnURL 支付完成 返回商店網址(Varchar(50))
     */
    public void setReturnURL(String ReturnURL){
        inputValues.put("ReturnURL", ReturnURL);
    }

    /**
     * 以幕後方式回傳給商店相關支付結果資料
     * @param NotifyURL 支付通知網址(Varchar(50))
     */
    public void setNotifyURL(String NotifyURL){
        if (NotifyURL.length() > 50){
            Log.e(TAG, "NotifyURL length > 50");
            return;
        }
        inputValues.put("NotifyURL", NotifyURL);
    }

    /**
     * 系統取號後以 form post 方式將結果導回 CustomerURL
     * @param CustomerURL 商店取號網址(Varchar(50))
     */
    public void setCustomerURL(String CustomerURL){
        inputValues.put("CustomerURL", CustomerURL);
    }

    /**
     * 當交易取消時,平台會出現返回鈕,使消費者依以此參數網址返回商店指定的頁面
     * 此參數若為空值時,則無返回鈕
     * @param ClientBackURL 支付取消 返回商店網址(Varchar(50))
     */
    public void setClientBackURL(String ClientBackURL){
        inputValues.put("ClientBackURL", ClientBackURL);
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
     * 是否開放修改emial, 預設為開放
     * @param flag true:開放 false:不開放
     */
    public void setEmailModify(boolean flag){
        String EmailModify = (flag)? "1" : "0";
        inputValues.put("EmailModify", EmailModify);
    }

    /**
     * 必填-是否需要登入智付寶會員
     * @param flag true:登入 false:不登入
     */
    public void setLoginType(boolean flag){
        String loginType = (flag)? "1" : "0";
        inputValues.put("LoginType", loginType);
    }

    /**
     * @param OrderComment 商店備註
     */
    public void setOrderComment(String OrderComment){
        if (OrderComment.length() > 50){
            Log.e(TAG, "OrderComment length > 300");
            return;
        }
        inputValues.put("OrderComment", OrderComment);
    }

    /**
     * 信用卡 一次付清啟用
     * @param flag true:啟用 false:不啟用
     */
    public void setCREDIT(boolean flag){
        String CREDIT = (flag)? "1" : "0";
        inputValues.put("CREDIT", CREDIT);
    }

    /**
     * 信用卡 紅利啟用
     * @param flag true:啟用 false:不啟用
     */
    public void setCreditRed(boolean flag){
        String CreditRed = (flag)? "1" : "0";
        inputValues.put("CreditRed", CreditRed);
    }

    /**
     * 信用卡 紅利啟用
     * @param InstFlag
     * 1.此欄位值=1 時,即代表開啟所有分期期別,且不可帶入其他期別參數。
     * 2.此欄位值為下列數值時,即代表開啟該分期期別。可分成3, 6, 12, 18, 24期
     * 3.同時開啟多期別時,將此參數用”,”分隔,例如:3,6,9
     * 4.此欄位值==0或null時,即代表不開啟分期。
     */
    public void setInstFlag(String InstFlag){
        inputValues.put("InstFlag", InstFlag);
    }

    /**
     * 信用卡 銀聯卡啟用
     */
    public void setUNIONPAY(boolean flag){
        String UNIONPAY = (flag)? "1" : "0";
        inputValues.put("UNIONPAY", UNIONPAY);
    }

    /**
     * WEBATM 啟用
     */
    public void setWEBATM(boolean flag){
        String WEBATM = (flag)? "1" : "0";
        inputValues.put("WEBATM", WEBATM);
    }

    /**
     * ATM 轉帳啟用
     */
    public void setCVS(boolean flag){
        String CVS = (flag)? "1" : "0";
        inputValues.put("CVS", CVS);
    }

    /**
     * 超商代碼繳費 啟用
     */
    public void setVACC(boolean flag){
        String VACC = (flag)? "1" : "0";
        inputValues.put("VACC", VACC);
    }

    /**
     * 條碼繳費啟用
     */
    public void setBARCODE(boolean flag){
        String BARCODE = (flag)? "1" : "0";
        inputValues.put("BARCODE", BARCODE);
    }

    /**
     * 自訂支付啟用
     */
    public void setCUSTOM(boolean flag){
        String CUSTOM = (flag)? "1" : "0";
        inputValues.put("CUSTOM", CUSTOM);
    }

    public void setupToolBar(String title,int toolBarBackgroungRes,int textColor){
        intent.putExtra("toolBarEnable", true);
        intent.putExtra("title", title);
        intent.putExtra("toolBarBackgroungRes", toolBarBackgroungRes);
        intent.putExtra("textColor", textColor);
    }

    public void setupCancelBtn(String text){
        intent.putExtra("cancelBtnText", text);
    }

    public void setupSuccessBtn(String text){
        intent.putExtra("successBtnText", text);
    }
}
