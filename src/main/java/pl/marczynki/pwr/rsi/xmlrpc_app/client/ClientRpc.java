package pl.marczynki.pwr.rsi.xmlrpc_app.client;

import org.apache.xmlrpc.AsyncCallback;
import org.apache.xmlrpc.XmlRpcClient;
import pl.marczynki.pwr.rsi.xmlrpc_app.shared.AppType;
import pl.marczynki.pwr.rsi.xmlrpc_app.shared.CliArgsParser;

import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;

public class ClientRpc {
    public static void main(String[] args) {
        System.out.println("client");
        HashMap<String, Object[]> cliParams = CliArgsParser.getAppOption(args, AppType.CLIENT);
        try {
            String host = "http://" + cliParams.get("ip")[0] + ":" + cliParams.get("port")[0];
            String selectedMethod;
            String serverName = (String) cliParams.get("server-name")[0];
            Vector<Object> params = new Vector<>();

            if (cliParams.containsKey("method")) {
                Object[] cliMethod = cliParams.get("method");
                selectedMethod = cliMethod[0].toString();
                params.addAll(Arrays.asList(cliMethod).subList(1, cliMethod.length));
            } else {
                selectedMethod = "show";
            }

            XmlRpcClient srv = new XmlRpcClient(host);

            System.out.println("Wybrana metoda: " + selectedMethod);
            String methodCall = serverName + "." + selectedMethod;
            if (cliParams.containsKey("async")) {
                AsyncCallbackImpl callback = new AsyncCallbackImpl();
                srv.executeAsync(methodCall, params, callback);
                System.out.println("Wywolano asynchronicznie");
            } else {
                Object result = srv.execute(methodCall, params);
                System.out.println("Wynik: " + result);
            }

        } catch (Exception exception) {
            System.err.println("Klient XML-RPC: " + exception);
        }
    }

    public static void minimalMain(String[] args) {
        try {
            XmlRpcClient srv = new XmlRpcClient("http://localhost:1000");

            Object result = srv.execute("MojSerwer.show", new Vector());
            System.out.println("Wynik: " + result);

            Vector<Object> params = new Vector<>();
            params.add(1000);
            srv.executeAsync("MojSerwer.race", params, new AsyncCallback() {
                @Override
                public void handleResult(Object o, URL url, String s) {
                    System.out.println("Wynik asynchronicznie: " + o);
                }

                @Override
                public void handleError(Exception e, URL url, String s) {
                    e.printStackTrace();
                }
            });
            System.out.println("Wywolano asynchronicznie");
        } catch (Exception exception) {
            System.err.println("Klient XML-RPC: " + exception);
        }
    }
}


