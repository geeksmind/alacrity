package net.geeksmind.alacrity.shieldComm;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.os.AsyncTask;
import android.widget.Toast;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import net.geeksmind.alacrity.console.consoleActivity;
import java.io.IOException;
import java.util.List;

/**
 * Author: coderh
 * Date: 9/26/13
 * Time: 10:57 PM
 */
public class HttpGetTask extends AsyncTask<String, Void, String> {
    private int statusCode = 0;
    private String msgRcv = "";

    private OnTaskCompleted listener;

    public HttpGetTask(OnTaskCompleted listener){
        this.listener=listener;
    }

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
        msgRcv = new BasicResponseHandler().handleResponse(response);
        return msgRcv;
    }

    @Override
    protected void onPostExecute(String result) {
        listener.onTaskCompleted(result);
    }

//    public static String getMsgRcv() {
//        return msgRcv;
//    }
//
//    public static int getStatusCode() {
//        return statusCode;
//    }


}
