package pay2go.sdk.lutas.pay2go;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

/**
 * Created by lutas on 16/2/16.
 * 智付寶MPG API
 * 請注意set所有必填value
 * start後自動開啟PayActivity
 */
public class Mpg
{
    final static private String TAG = Mpg.class.getName();
    Activity activity;
    Intent intent;

    public Mpg(Activity activity) {
        this.activity = activity;
        intent = activity.getIntent();
    }

    public void start(String hashKey,String hashIV){
        intent.putExtra("hashKey", hashKey);
        intent.putExtra("hashIV", hashIV);

        intent.setClass(activity, PayActivity.class);
        activity.startActivity(intent);
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
        intent.putExtra("MerchantID", MerchantID);
    }

    /**
     * @param RespondType 回傳格式: JSON or String
     */
    public void setRespondType(String RespondType){
        if (!RespondType.equals("JSON") && !RespondType.equals("String")){
            Log.e(TAG, "RespondType most be 'JSON' or 'String'!");
            return;
        }
        intent.putExtra("RespondType", RespondType);
    }

    /**
     * @param LangType 頁面語系:en or zh-tw 預設為:zh-tw
     */
    public void setLangType(String LangType){
        if (!LangType.equals("en") && !LangType.equals("zh-tw")){
            Log.e(TAG, "LangType most be 'en' or 'zh-tw'!");
            return;
        }
        intent.putExtra("RespondType", LangType);
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
        intent.putExtra("MerchantOrderNo", MerchantOrderNo);
    }

    /**
     * 必填
     * @param Amt 訂單金額(int(10))
     */
    public void setAmt(int Amt){
        intent.putExtra("Amt", String.valueOf(Amt));
    }

    /**
     * @param ItemDesc 必填-商品資訊(Varchar(50))
     */
    public void setItemDesc(String ItemDesc){
        if (ItemDesc.length() > 50){
            Log.e(TAG, "ItemDesc length > 50");
            return;
        }
        intent.putExtra("ItemDesc", ItemDesc);
    }

    /**
     * @param TradeLimit 交易限制秒數(int(3)),秒數下限為 60 秒
     */
    public void setTradeLimit(int TradeLimit){
        intent.putExtra("TradeLimit", String.valueOf(TradeLimit));
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
        intent.putExtra("Email", Email);
    }

    /**
     * 必填-是否需要登入智付寶會員
     * @param flag true:登入 false:不登入
     */
    public void setLoginType(boolean flag){
        String loginType = (flag)? "1" : "0";
        intent.putExtra("LoginType", loginType);
    }
}
