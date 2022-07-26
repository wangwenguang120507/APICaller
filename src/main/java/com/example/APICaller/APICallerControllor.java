package com.example.APICaller;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.apache.commons.lang3.StringEscapeUtils;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class APICallerControllor {

	// アルゴリズム/ブロックモード/パディング方式
	private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
	// 暗号化＆復号化で使用する鍵_暗号化キー
	private static final String ENCRYPT_KEY = "yourEncryptKey01";
	// 初期ベクトル
	private static final String INIT_VECTOR = "yourInitVector01";
	
    @RequestMapping("/testRssideApi")
    public String testRssideApi(Model model) {
    	try {
            RestTemplate rest = new RestTemplate();
        	
            final String url = "https://yhat-rssidecn-stg.herokuapp.com/callRssideApi?kbn=heroku";
    	
            ResponseEntity<String> response = rest.getForEntity(url, String.class);
    	
            String json = response.getBody();
            
            model.addAttribute("hello", json + "！");
            
            System.out.println("■■■■■■■■■■　" + json);
          
    	} catch (Exception e) {
    		model.addAttribute("hello", "Applicationの呼び出すエラーが発生してしまった！");
    		return "APICaller";
    	}
      
        return "APICaller";
    }
	
    /********************* サンプルコードをいったんコメントアウトとする***********************
     
    @RequestMapping("/APICaller")
    public String hello(Model model) {
	    
        RestTemplate rest = new RestTemplate();
	
        final String url = "https://rssideapicall.herokuapp.com/getting?name=heroku";
	
        ResponseEntity<String> response = rest.getForEntity(url, String.class);
	
        String json = response.getBody();
        
        model.addAttribute("hello", encode(json));
        
        System.out.println(StringEscapeUtils.unescapeJava(json));
      
        return "APICaller";
    }
    
    @RequestMapping("/HttpRequest")
    public String httpRequestMethod(Model model) {
	    try {
	
		    HttpRequest request = HttpRequest.newBuilder(URI.create("http://192.168.103.16:8080")).build();
		    BodyHandler<String> bodyHandler = HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8);
		    HttpResponse<String> response = HttpClient.newBuilder().build().send(request, bodyHandler);
		    System.out.println(response.body());
	    }catch (Exception e) {
				e.printStackTrace();
	    }
	    return null;
    }
    
    @RequestMapping("/RestTemplate")
    public String jsonRequest(Model model) {
	
	    HttpHeaders headers = new HttpHeaders();// ヘッダ部
	    RestTemplate restTemplate = new RestTemplate();
	    ResponseEntity<String> response;
	    HttpEntity<String> entity;
	    String requestBobgJson = "";
	    
	    // トークン取得
	    final String tokenGeturl = "https://rssideapicall.herokuapp.com/generateToken";
	    headers.setContentType(MediaType.APPLICATION_JSON);
	    headers.set("kaisyacd", "01"); // 会社コード(01固定)
	    headers.set("reqymd", "xxxxx"); // リクエスト日付
	    headers.set("reqtm", "xxxxx"); // リクエスト時間
	    headers.set("tencd", "xxxxx"); // 店舗コード
	    headers.set("tanmatsuno", "xxxxx"); // 端末No
	    headers.set("prcno", "xxxxx"); // 処理通番
	    headers.set("reqno", "xxxxx"); // リクエスト番号
	    headers.set("reqsystem", "1"); // リクエストしたシステム(1:Webシステム、2:作業受付支援システム)
	    headers.set("reqprogram", "yhat-api-stg"); // リクエストしたプログラム
	    requestBobgJson ="{"
			    + "client_id : XXXXXXXXX,"
			    + "signin_key : XXXXXXXXXXX,"
			    + "syspasswd : XXXXXXXXXXXX"
			    +"}";
	    entity = new HttpEntity<String>(requestBobgJson, headers);
	    response = restTemplate.exchange(tokenGeturl, HttpMethod.GET, entity, String.class, "");
	    String tokenResponse = response.getBody(); // レスポンス
	    tokenResponse = encode(tokenResponse); // 復号化
	    
	    final String url = "https://rssideapicall.herokuapp.com/getting?name=heroku";
	    headers = new HttpHeaders();// ヘッダ部
	    // headers.setBearerAuth(tokenResponse);
	    headers.setContentType(MediaType.APPLICATION_JSON);
	    headers.set("Authorization", "Bearer " + tokenResponse); // トークン
	    headers.set("kaisyacd", "01"); // 会社コード(01固定)
	    headers.set("reqymd", "xxxxx"); // リクエスト日付
	    headers.set("reqtm", "xxxxx"); // リクエスト時間
	    headers.set("tencd", "xxxxx"); // 店舗コード
	    headers.set("tanmatsuno", "xxxxx"); // 端末No
	    headers.set("prcno", "xxxxx"); // 処理通番
	    headers.set("reqno", "xxxxx"); // リクエスト番号
	    headers.set("reqsystem", "1"); // リクエストしたシステム(1:Webシステム、2:作業受付支援システム)
	    headers.set("reqprogram", "yhat-api-stg"); // リクエストしたプログラム
	    requestBobgJson ="{"
			    + "custcd : jsonvalue," // 顧客コード
			    + "carinfogetflg : 0" // 所有車情報取得フラグ(0:所有車リクエスト無し, 1:所有車リクエスト有り):0固定
			    +"}";
	    entity = new HttpEntity<String>(encrypter(requestBobgJson), headers);
	    response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class, "");
	    String json = response.getBody();
	    model.addAttribute("hello", encode(json));
	    
        return "APICaller";	
    }
     ***********************************************/
    
    /**
     * 復号化処理
     * @param inputStr　復号化対象文字列
     * @return
     */
    private String encode(String inputString) {
	    String encryptedToken = "";
        IvParameterSpec ivP = new IvParameterSpec(INIT_VECTOR.getBytes());
        SecretKeySpec sKey = new SecretKeySpec(ENCRYPT_KEY.getBytes(), "AES");
	    try {
	        Cipher decrypter = Cipher.getInstance(ALGORITHM);
	        decrypter.init(Cipher.DECRYPT_MODE, sKey, ivP);
		    byte[] byteToken = Base64.getDecoder().decode(encryptedToken);
		    encryptedToken =  new String(decrypter.doFinal(byteToken));
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    
	    return encryptedToken;
	      
    }
    
    /**
     * 暗号化処理
     * @param inputStr　暗号化対象文字列
     * @return
     */
    private String encrypter(String inputStr) {
	      //////////// 暗号化処理 ///////////////////////
	      String encryptedToken = "";
	      IvParameterSpec ivP = new IvParameterSpec(INIT_VECTOR.getBytes());
	      SecretKeySpec sKey = new SecretKeySpec(ENCRYPT_KEY.getBytes(), "AES");
	      
	      try {
	      	Cipher encrypter = Cipher.getInstance(ALGORITHM);
				encrypter.init(Cipher.ENCRYPT_MODE, sKey, ivP);
				byte[] byteToken = encrypter.doFinal(inputStr.getBytes());
				encryptedToken =  new String(Base64.getEncoder().encode(byteToken));
			} catch (Exception e) {
				e.printStackTrace();
			}
	      
	      return encryptedToken;
	}
}
