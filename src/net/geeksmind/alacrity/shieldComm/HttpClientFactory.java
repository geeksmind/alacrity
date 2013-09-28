package net.geeksmind.alacrity.shieldComm;

import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.RedirectHandler;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

/**
 * Author: coderh
 * Date: 9/28/13
 * Time: 9:56 PM
 * <p/>
 * This factory creates HttpClients. It also allows to inject custom HttpClients
 * for testing.
 */
public class HttpClientFactory {

    private static AbstractHttpClient httpClient;
    private static final int HTTP_PORT = 80;
    private final static int HTTPS_PORT = 443;
    private final static int CONNECTION_TIMEOUT = 3000;
    private final static int SOCKET_TIMEOUT = 1000;


    public static synchronized AbstractHttpClient getInstance() {
        if (httpClient == null) {
            httpClient = create();
        }

        return httpClient;
    }

    public static synchronized void setInstance(AbstractHttpClient instance) {
        httpClient = instance;
    }

    final private static RedirectHandler REDIRECT_NO_FOLLOW = new RedirectHandler() {
        public boolean isRedirectRequested(HttpResponse response, HttpContext context) {
            return false;
        }

        public URI getLocationURI(HttpResponse response, HttpContext context) throws org.apache.http.ProtocolException {
            return null;
        }
    };

    private static AbstractHttpClient create() {
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), HTTP_PORT));
        schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), HTTPS_PORT));
        HttpParams params = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(params, CONNECTION_TIMEOUT);
        HttpConnectionParams.setSoTimeout(params, SOCKET_TIMEOUT);
        ThreadSafeClientConnManager connManager = new ThreadSafeClientConnManager(params, schemeRegistry);
        AbstractHttpClient result = new DefaultHttpClient(connManager, params);
        result.setRedirectHandler(REDIRECT_NO_FOLLOW);
        return result;
    }

}
