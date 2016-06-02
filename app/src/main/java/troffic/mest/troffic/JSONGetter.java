package troffic.mest.troffic;

import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by BlackHat on 5/29/2016.
 */
public class JSONGetter {
    String json_string = null;
    BufferedReader reader = null;
    //konstructur
    public JSONGetter(){

    }

    public String makeHttpRequest(String url_string, String method) {

        HttpURLConnection connection;
        /*String encodedStr = getEncodedData(dataTosend);

        if(method == "GET") {
            url_string += "?" + encodedStr;
        }*/

        try {

            URL url = new URL(url_string);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);

            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            //connection.setRequestProperty("Content-Length", "" + Integer.toString(encodedStr.getBytes().length));
            connection.setRequestProperty("Content-Language", "en-US");

            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            /*send request
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(encodedStr);
            wr.flush();*/

            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            reader.close();

            json_string = sb.toString();

            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("mkHttpRequest Error", "Error converting result " + e.toString());
        } finally {

        }
        return json_string;
    }


    public String DoHttpRequest(String url_string, String method, Bundle dataTosend){

        HttpURLConnection connection;
        String encodedStr = getEncodedData(dataTosend);

        if(method == "GET") {
            url_string += "?" + encodedStr;
        }

        try {

            URL url = new URL(url_string);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);

            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Length", "" + Integer.toString(encodedStr.getBytes().length));
            connection.setRequestProperty("Content-Language", "en-US");

            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            //send request
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(encodedStr);
            wr.flush();

            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            StringBuilder sb = new StringBuilder();
            String line;

            while((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            reader.close();

            json_string = sb.toString();

            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("mkHttpRequest Error", "Error converting result " + e.toString());
        } finally {

        }

        return json_string;
    }

    public String getEncodedData(Bundle data) {
        StringBuilder sb = new StringBuilder();
        Log.d("dataToSend: ", data.toString());
        for(String key : data.keySet()) {
            String value = null;
            try {
                value = URLEncoder.encode(data.getString(key), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            if(sb != null && sb.length()>0)
                sb.append("&");

            sb.append(key + "=" + value);
        }
        return sb.toString();
    }
}
