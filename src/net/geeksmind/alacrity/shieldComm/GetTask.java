package net.geeksmind.alacrity.shieldComm;

import android.os.AsyncTask;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;

/**
 * Author: coderh
 * Date: 9/26/13
 * Time: 10:57 PM
 */
public class GetTask extends AsyncTask<String, Void, String> {
    private int statusCode = 0;
    private String msgRcv = "";

    @Override
    protected String doInBackground(String... params) {
        try {
            return getInfo(params[0]);
        } catch (IOException e) {
            return "<ERROR: " + statusCode + ">";
        }
    }

    private String getInfo(String url) throws IOException {
        HttpClient Client = new DefaultHttpClient();

        // Create Request to server and get response
        HttpGet httpget = new HttpGet(url);
        HttpResponse response = Client.execute(httpget);

        // Get status code
        statusCode = response.getStatusLine().getStatusCode();
        return new BasicResponseHandler().handleResponse(response);
    }


    protected void onPostExecute(String result) {
        // the final result depends on whether IOException is thrown, should wait until bg task finish
        msgRcv = result;
    }

    public String getMsgRcv() {
        return msgRcv;
    }

    public int getStatusCode() {
        return statusCode;
    }


}
