package com.cmessages.http;

import com.cmessages.entity.CMessagesAuth;
import com.cmessages.entity.QueryResult;
import com.cmessages.entity.SendResult;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.TimeZone;

public class Client {
    public static final String BASE_URI = "https://api.cmessages.com";
    public static final String DATE_FMT = "yyyyMMddHHmmss";
    public static final String MD5 = "MD5";

    protected CMessagesAuth auth;

    public Client(CMessagesAuth auth) {
        this.auth = auth;
    }

    @Override
    public String toString() {
        return "Client{" +
                "auth=" + auth +
                '}';
    }

    // 以下是签名算法需要的helper函数
    public static String toHexString(byte[] bytes) {
        Formatter formatter = new Formatter();
        for (byte b : bytes) {
            formatter.format("%02x", b);
        }
        return formatter.toString();
    }

    /**
     * MD5算法
     *
     * @param str
     *
     * @return
     */
    public static String md5(final String str) {
        try {
            MessageDigest md = MessageDigest.getInstance(MD5);
            md.update(str.getBytes());
            return toHexString(md.digest());
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    /**
     * 签名算法,注意时区必须是Shanghai(北京时间)
     *
     * @param json
     *
     * @return
     */
    protected String preparePayload(JSONObject json) {
         SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FMT);
         dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
         String ts = dateFormat.format(new Date());
         if (json == null) {
             json = new JSONObject();
         }
         json.put("app_id", auth.getAppId());
         json.put("datetime", ts);
         json.put("sign", md5(auth.getAppId() + ts + auth.getAppKey()));
         return json.toString();
    }

    public String postJson(final String endPoint, JSONObject json) throws IOException, HttpException {
        CloseableHttpClient client = HttpClients.createDefault();
        StringEntity requestEntity = new StringEntity(preparePayload(json), ContentType.APPLICATION_JSON);
        HttpPost target = new HttpPost(BASE_URI + endPoint);
        target.addHeader("User-Agent", "CMessagesSDK-java/examples");
        target.setEntity(requestEntity);
        CloseableHttpResponse response = client.execute(target);
        int statusCode = response.getStatusLine().getStatusCode();
        String body;
        try {
            HttpEntity entity = response.getEntity();
            body = EntityUtils.toString(entity);
            EntityUtils.consume(entity);
        } finally {
            client.close();
            response.close();
        }
        if (statusCode < 200 || statusCode >= 400) {
            throw new HttpException("invalid status code for: " + statusCode + " ,body="+ body);
        }
        return body;
    }

    public String postJson(final String endPoint) throws IOException, HttpException {
        return postJson(endPoint, null);
    }


    // 以下是短信API
    public float getBalance() throws IOException, HttpException {
        JSONObject json = new JSONObject(this.postJson("/sms/get_balance"));
        if (json.has("balance")) return json.getFloat("balance");
        throw new HttpException("Invalid response for api::getBalance:" + json);
    }

    public QueryResult[] query(Integer[] ids) throws IOException, HttpException {
        JSONObject payload = new JSONObject();
        payload.put("id_list", ids);
        JSONArray arr = new JSONArray(this.postJson("/sms/query", payload));
        QueryResult[] queryResults = new QueryResult[arr.length()];
        for (int i = 0; i < arr.length(); i++) {
            JSONObject jo = arr.getJSONObject(i);
            queryResults[i] = new QueryResult(jo.getInt("id"), jo.getString("mobile"), jo.getInt("message_status"));
        }
        return queryResults;
    }

    public SendResult sendVerify(String mobile, String content) throws IOException, HttpException {
        JSONObject payload = new JSONObject();
        payload.put("mobile", mobile);
        payload.put("content", content);
        JSONObject resp = new JSONObject(this.postJson("/sms/send_verify", payload));
        return new SendResult(
                resp.getInt("id"),
                resp.getString("mobile")
        );
    }

    public SendResult[] sendSMS(String[] mobiles, String content)  throws IOException, HttpException {
        JSONObject payload = new JSONObject();
        payload.put("mobile", mobiles);
        payload.put("content", content);
        JSONArray arr = new JSONArray(this.postJson("/sms/send_message", payload));
        SendResult[] sendResults = new SendResult[arr.length()];
        for (int i = 0; i < arr.length(); i++) {
            JSONObject jo = arr.getJSONObject(i);
            sendResults[i] = new SendResult(
                    jo.getInt("id"),
                    jo.getString("mobile")
            );
        }
        return sendResults;
    }

    public SendResult[] sendSMS(String mobile, String content)  throws IOException, HttpException {
        return sendSMS(new String[]{mobile}, content);
    }
}
