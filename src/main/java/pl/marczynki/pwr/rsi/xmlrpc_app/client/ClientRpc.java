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
            if(cliParams.containsKey("method")){
                for (String method : cliParams.get("method")) {
                    System.out.println(method);
                }
            }

            XmlRpcClient srv = new XmlRpcClient(host);
            Vector<Integer> params = new Vector<>();
            params.addElement(13);
            params.addElement(21);
            Object result = srv.execute("MojSerwer.echo", params);
            int wynik = (Integer) result;
            System.out.println("wynik: " + wynik);

            AsyncCallbackImpl cb = new AsyncCallbackImpl();
            Vector<Integer> params2 = new Vector<>();
            params2.addElement(3000);
            srv.executeAsync("MojSerwer.execAsy", params2, cb);
            System.out.println("Wywolano asynchronicznie");

        } catch (Exception exception) {
            System.err.println("Klient XML-RPC: " + exception);
        }
    }

    private static HashMap<String, String[]> getCliParams(String[] args) {
        Options options = new Options();
        Option ip = new Option("i", "ip", true, "server ip");
        ip.setRequired(true);
        options.addOption(ip);

        Option port = new Option("p", "port", true, "server port");
        port.setRequired(true);
        options.addOption(port);

        Option method = new Option("m", "method", true, "server method");
        method.setRequired(false);
        method.setArgs(Option.UNLIMITED_VALUES);
        options.addOption(method);

        return CliArgsParser.getAppOption(args, options);
    }

}


