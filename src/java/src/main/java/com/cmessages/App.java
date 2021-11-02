package com.cmessages;

import com.cmessages.entity.CMessagesAuth;
import com.cmessages.entity.QueryResult;
import com.cmessages.entity.SendResult;
import com.cmessages.http.Client;
import org.apache.http.HttpException;

import java.io.IOException;
import java.util.Arrays;

public class App {
    public static void main(String[] args) throws HttpException, IOException {
        CMessagesAuth auth = new CMessagesAuth("dp76QL927CwxV8483e", "A4dQ42b8rykuxn3ah38qMEB9ft26Lm7D");
        Client client = new Client(auth);
        Integer[] messageIds  = new Integer[2];
        String mobile = "+16173297868";
        System.out.println("client = " + client);
        System.out.println("Query balance for : " + auth.getAppId() +" => balance:" + client.getBalance());
        SendResult result = client.sendVerify(mobile, "Your verify code is:1234");
        messageIds[0] = result.getId();
        System.out.println("Send verify result:" + result);
        result = client.sendSMS(mobile, "for test")[0];
        messageIds[1] = result.getId();
        System.out.println("Send message result :" + result);
        QueryResult[] queryResults = client.query(messageIds);
        System.out.println("Query send result:" + mobile + " => " + Arrays.deepToString(queryResults));
    }
}
