package com.example.jonathanplay.twitter;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HttpsURLConnection;

public class NetworkManager {

    private static NetworkManager manager = null;
    private Context context = null;

    private String consumer_key="DlswONaD8NzHvgBKWYfwOiE1L";
    private String mySecretKey="cd15T497RjJe9q95yW26xWhn2RTedHFCdeupagjViaMEJ7CBAB";
    private String oauth_token_secret = "0VzvczW7mcnzrPfKFnnvcqVCUOjxe0O9QHpdJKYyqiGQ3";
    private String token = "4503603927-JnS1E8hBDQv9z0Vek76K5myzcVDEQQiCJVSELtH";

    private NetworkManager(Context context){
        this.context=context;
    }

    public static NetworkManager getInstance(Context context){
        if(manager==null)
            manager = new NetworkManager(context);
        return manager;
    }

    private String buildOAuthHeader(String httpMeth, String baseUrl, String headerExtra, String extraUrl, String requestStr) {

        Map<String, String> parameters = new HashMap<>();
        if(requestStr!=null && !requestStr.isEmpty() && requestStr.contains("=")) {
            parameters.putAll(parseExtraUrl(requestStr));
        }
        parameters.putAll(parseExtraUrl(extraUrl));
        parameters.put("oauth_consumer_key",consumer_key);
        String nonce = generate_nonce();
        parameters.put("oauth_nonce",nonce);
        parameters.put("oauth_signature_method", "HMAC-SHA1");
        String timestamp = String.valueOf(Calendar.getInstance().getTimeInMillis()/1000);
        parameters.put("oauth_timestamp", timestamp);
        if(headerExtra != null && !headerExtra.isEmpty())
            parameters.putAll(parseExtraUrl(headerExtra.replace("\"", "")));
        if(token != null)
            parameters.put("oauth_token", token);
        parameters.put("oauth_version", "1.0");

        String header = "OAuth ";
        if(headerExtra != null && !headerExtra.isEmpty())
            header += headerExtra+",";
        header +="oauth_consumer_key=\""+consumer_key+"\",";
        header +="oauth_nonce=\""+nonce+"\",";
        header +="oauth_signature=\""+generate_signature(parameters, httpMeth, baseUrl)+"\",";
        header +="oauth_signature_method=\""+"HMAC-SHA1"+"\",";
        header +="oauth_timestamp=\""+timestamp+"\",";
        if(token != null)
            header +="oauth_token=\""+token+"\",";
        header +="oauth_version=\""+"1.0"+"\"";


        return header;
    }

    private String generate_nonce()
    {
        String nonce = "";
        Random rand = new Random(Calendar.getInstance().getTimeInMillis());
        nonce+=String.valueOf(Calendar.getInstance().getTimeInMillis());
        nonce+=String.valueOf(rand.nextInt());
        nonce = String.format("%04x", new BigInteger(1, nonce.getBytes()));
        return nonce.substring(0,32);
    }

    private String generate_signature(Map<String,String> parameters, String httpMethod, String baseUrl) {

        String outputString = "";
        String parametersString="";
        List<String> keys = new ArrayList<>(parameters.keySet());
        Collections.sort(keys);
        for(String key : keys)
        {
            parametersString += key;
            parametersString += "=";
            try {
                parametersString += URLEncoder.encode(parameters.get(key), "UTF-8").replace("+","%20");
            }catch(Exception e){
                e.printStackTrace();
            }
            parametersString +="&";
        }
        parametersString = parametersString.substring(0,parametersString.lastIndexOf("&"));

        String signatureBaseString = httpMethod.toUpperCase();
        try {
            signatureBaseString += "&";
            signatureBaseString += URLEncoder.encode(baseUrl, "UTF-8");
            signatureBaseString += "&";
            signatureBaseString += URLEncoder.encode(parametersString,"UTF-8");
        }catch (Exception e){
            e.printStackTrace();
        }


        String signingKey = mySecretKey;
        signingKey += "&";
        if(oauth_token_secret != null)
            signingKey += oauth_token_secret;
        try {
            SecretKeySpec key = new SecretKeySpec(signingKey.getBytes("UTF-8"), "HmacSHA1");
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(key);
            byte[] data = mac.doFinal(signatureBaseString.getBytes("UTF-8"));
            outputString = Base64.encodeToString(data,Base64.DEFAULT);
            outputString = outputString.substring(0, outputString.length() - 1);
            outputString = URLEncoder.encode(outputString,"UTF-8");
        }catch (Exception e){
            e.printStackTrace();
        }

        return outputString;

    }

    private Map<String,String> parseExtraUrl(String extraUrl){
        Map<String, String> extras = new HashMap<>();
        boolean next;
        if(extraUrl == null)
            next = false;
        else
            next =  extraUrl.contains("=");

        while(next)
        {
            String key = extraUrl.substring(0,extraUrl.indexOf("="));
            extraUrl = extraUrl.substring(extraUrl.indexOf("=")+1);
            String val;
            if(extraUrl.contains("&"))
                val = extraUrl.substring(0,extraUrl.indexOf("&")-1);
            else {
                val = extraUrl;
                next = false;
            }
            extras.put(key,val);

        }
        return extras;
    }

    private String makeTwitterRequest(String httpMethod, String baseUrl, String urlExtra, String message){
        String header = buildOAuthHeader(httpMethod, baseUrl, null, urlExtra, message);
        String resultLine;
        String result = "";
        URL url;
        HttpURLConnection c;

        try{
            if(httpMethod.equals("GET")){

                // GET Request
                if(urlExtra != null){
                    url = new URL(baseUrl + "?" + urlExtra);
                }
                else {
                    url = new URL(baseUrl);
                }

                c = (HttpURLConnection) url.openConnection();
                c.setRequestMethod("GET");
                c.setDoInput(true);
                c.setRequestProperty("Authorization", header);
                c.setRequestProperty("Accept", "*/*");

                InputStream isStream = c.getInputStream();
                InputStreamReader isr = new InputStreamReader(isStream);
                BufferedReader br = new BufferedReader(isr);

                while((resultLine = br.readLine()) != null){
                    result += resultLine;
                }
                isStream.close();
            }
            if(httpMethod.equals("POST")){
                // POST Request
                url = new URL(baseUrl);
                c = (HttpURLConnection) url.openConnection();
                c.setDoOutput(true);
                c.setDoInput(true);
                c.setRequestMethod("POST");
                c.setRequestProperty("Accept", "*/*");
                c.setRequestProperty("Authorization", header);
                OutputStream outputStream = c.getOutputStream();

                DataOutputStream dos = new DataOutputStream(outputStream);
                String message1, message2;
                message1=message.substring(0, 7);
                message2=URLEncoder.encode(message.substring(7), "UTF-8");
                System.out.println("POST : " + message1 + message2);
                dos.writeBytes(message1 + message2);
                dos.flush();
                outputStream.close();
                result=message1+message2;
                InputStream isStream = c.getInputStream();
                InputStreamReader isr = new InputStreamReader(isStream);
                BufferedReader br = new BufferedReader(isr);

                while((resultLine = br.readLine()) != null){
                    result += resultLine;
                }
                isStream.close();
                System.out.println(result);

            }

            return result;
        }
        catch (Exception e) {
            System.out.println("Exception : \n" + e.toString());
            return null;
        }
    }

    private boolean isConnectedToInternet(Context context)
    {
        //verify the connectivity
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null)
        {
            NetworkInfo.State networkState = networkInfo.getState();
            if (networkState.equals(NetworkInfo.State.CONNECTED))
            {
                return true;
            }
        }
        return false;
    }

    public void getTimeLine(final Listener lt){

        new Thread(new Runnable(){
            @Override
            public void run() {
                String result = null;
                result = makeTwitterRequest("GET", "https://api.twitter.com/1.1/statuses/home_timeline.json", null, null);
                System.out.println("RESULTAT : " + result);

                lt.timelineChanged(result);
            }
        }
        ).start();
    }

    public void postTweet(final String message){

        System.out.println("MESSAGE TWIT:"+message);


        new Thread(new Runnable(){
            @Override
            public void run() {
                String result = null;
                result = makeTwitterRequest("POST", "https://api.twitter.com/1.1/statuses/update.json", null, "status="+message);
                System.out.println("RESULTAT : " + result);

            }
        }
        ).start();

    }
}
