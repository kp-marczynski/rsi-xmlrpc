package pl.marczynki.pwr.rsi.xmlrpc_app.client;

import org.apache.commons.cli.*;
import org.apache.xmlrpc.XmlRpcClient;
import pl.marczynki.pwr.rsi.xmlrpc_app.shared.CliArgsParser;

import java.util.HashMap;
import java.util.Vector;

public class ClientRpc {
    public static void main(String[] args) {
        System.out.println("client");
        HashMap<String, String[]> cliParams = getCliParams(args);
        try {
            String host = "http://" + cliParams.get("ip")[0] + ":" + cliParams.get("port")[0];
//            System.out.println(host);
//            int serverPort = 10000;
            String selectedMethod;
            Vector<Object> params = new Vector<>();

            if (cliParams.containsKey("method")) {
                String[] cliMethod = cliParams.get("method");
                selectedMethod = cliMethod[0];
                for (int i = 1; i < cliMethod.length; i++) {
                    params.add(tryCastParamToNumber(cliMethod[i]));
                }
            } else {
                selectedMethod = "show";
            }

            XmlRpcClient srv = new XmlRpcClient(host);
//            Vector<Integer> params = new Vector<>();
//            params.addElement(13);
//            params.addElement(21);
//            Object result = srv.execute("MojSerwer.echo", params);
//            int wynik = (Integer) result;
//            System.out.println("wynik: " + wynik);
//
//            AsyncCallbackImpl cb = new AsyncCallbackImpl();
//            Vector<Integer> params2 = new Vector<>();
//            params2.addElement(3000);
//            srv.executeAsync("MojSerwer.execAsy", params2, cb);
//            System.out.println("Wywolano asynchronicznie");

            System.out.println("Wybrana metoda: " + selectedMethod);

            if (cliParams.containsKey("async")) {
                AsyncCallbackImpl cb = new AsyncCallbackImpl();
                srv.executeAsync("MojSerwer." + selectedMethod, params, cb);
            } else {
                Object result = srv.execute("MojSerwer." + selectedMethod, params);
                System.out.println("Wynik: " + result);
            }

        } catch (Exception exception) {
            System.err.println("Klient XML-RPC: " + exception);
        }
    }

    private static Object tryCastParamToNumber(String param) {
        Integer intValue = Integer.valueOf(param);
        if (intValue.toString().equals(param)) {
            return intValue;
        }
        Double doubleValue = Double.valueOf(param);
        if (doubleValue.toString().equals(param)) {
            return doubleValue;
        }
        return param;
    }

    private static HashMap<String, String[]> getCliParams(String[] args) {
        Options options = new Options();
        Option ip = new Option("i", "ip", true, "server ip");
        ip.setRequired(true);
        options.addOption(ip);

        Option port = new Option("p", "port", true, "server port");
        port.setRequired(true);
        options.addOption(port);

        Option async = new Option("a", "async", false, "specifying if method should be called asynchronously");
        async.setRequired(false);
        options.addOption(async);

        Option method = new Option("m", "method", true, "server method");
        method.setRequired(false);
        method.setArgs(Option.UNLIMITED_VALUES);
        options.addOption(method);

        return CliArgsParser.getAppOption(args, options);
    }

}


