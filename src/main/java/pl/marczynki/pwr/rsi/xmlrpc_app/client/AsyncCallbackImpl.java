package pl.marczynki.pwr.rsi.xmlrpc_app.client;

import org.apache.xmlrpc.AsyncCallback;

import java.net.URL;

public class AsyncCallbackImpl implements AsyncCallback {
    @Override
    public void handleResult(Object result, URL url, String s) {
//        System.out.println("HandleResult{[Object: " + o + "] [URL: " + url + "] [String: " + s + "]}");
        System.out.println("Wynik asynchronicznie: " + result);
    }

    @Override
    public void handleError(Exception e, URL url, String s) {
        System.out.println("HandleError{{Exception: " + e + "] [URL: " + url + "] [String: " + s + "]}");
    }
}
