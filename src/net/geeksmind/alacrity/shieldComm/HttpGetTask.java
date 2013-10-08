package net.geeksmind.alacrity.shieldComm;

import android.os.AsyncTask;
import android.util.Log;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;

import java.io.IOException;

/**
 * Author: coderh
 * Date: 9/26/13
 * Time: 10:57 PM
 */
public class HttpGetTask extends AsyncTask<String, Void, String> {
    private int statusCode = 0;
    private OnAsynTaskCallback listener;

    public HttpGetTask(OnAsynTaskCallback listener) {
        this.listener = listener;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            return getInfo(params[0]);
        } catch (IOException e) {
            return "ERROR:" + statusCode;
        }
    }

    private String getInfo(String url) throws IOException {

        // Create Request to server and get response
        HttpGet httpget = new HttpGet(url);
        Log.d("SYNC", "msgRcv1 = ");
        HttpResponse response = HttpClientFactory.getInstance().execute(httpget);
        Log.d("SYNC", "msgRcv2 = ");

        // Get status code
        statusCode = response.getStatusLine().getStatusCode();
        return new BasicResponseHandler().handleResponse(response);
    }

    @Override
    protected void onPreExecute() {
        listener.onTaskStarted();
    }

    @Override
    protected void onPostExecute(String result) {
        listener.onTaskCompleted(result);
    }
}
