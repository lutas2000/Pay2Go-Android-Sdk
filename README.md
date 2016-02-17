Pay2Go-Android-Sdk
===================================
智付寶非官方Android SDK
簡單使用android串接智付寶API
目前只支援MPG API

一、申請支付寶帳號
----------------------------------- 
正式網址：https://www.pay2go.com/<br /> 
測試網址：https://cweb.pay2go.com/<br /> 
註冊完帳號後在銷售中心開立一個商店<br /> 
記下以下資訊：<br /> 
商店代號、hashKey、hashIV

二、Import Module
----------------------------------- 
在Android Studio -> File -> new -> Import module選擇pay2go資料夾
####加入permission:
    <uses-permission android:name="android.permission.INTERNET"/>
  
三、使用
----------------------------------- 
在Activity中加入:
### 
    Mpg mpg = new Mpg(this);
    // ============== 必填 =====================
    mpg.setAmt(1000); //支付金額
    mpg.setItemDesc("商品資訊");
    mpg.setLoginType(false); //user是否需要登入支付寶
    mpg.setShop("商店代號", "your hashKey", "your hashIV");
    mpg.setMerchantOrderNo("自訂訂單編號");
    mpg.setEmail("付款者emial");
    mpg.setRespondType("JSON");
    // ============== 選填 =====================
    mpg.setCREDIT(true);
    mpg.setWEBATM(true);
    mpg.setCVS(true);
    mpg.setVACC(true);
    mpg.setTest(true); //設定使用test API,不填則使用正式API
    
    mpg.start();
