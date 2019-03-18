package pl.marczynki.pwr.rsi.xmlrpc_app.server;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.xmlrpc.WebServer;
import pl.marczynki.pwr.rsi.xmlrpc_app.shared.CliArgsParser;

import java.net.InetAddress;
import java.util.HashMap;

public class ServerRpc {
    public static void main(String[] args) {
//        System.out.println("server");
//        System.out.println("hallo " + args[0]);
        HashMap<String, String[]> cliParams = getCliParams(args);
        try {
            System.out.println("Startuje serwer XML-RPC...");
            int port = Integer.parseInt(cliParams.get("port")[0]);
            WebServer server = new WebServer(port);
            server.addHandler("MojSerwer", new ServerRpc());
            server.start();

            System.out.println("Serwer wystartowal pomyslnie.");
            System.out.println("Bierzacy adres IP: " + InetAddress.getLocalHost().getHostAddress());
            System.out.println("Nasluchuje na porcie: " + port);
            System.out.println("Aby zatrzymac serwer nacisnij crl+c");
        } catch (Exception exception) {
            System.err.println("Serwer XML-RPC: " + exception);
        }
    }

    public Integer echo(int x, int y) {
        return x + y;
    }

    public int execAsy(int sleepTime) {
        System.out.println("... wywolano asy - odliczam " + sleepTime);
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
            Thread.currentThread().interrupt();
        }
        System.out.println("... asy - koniec odliczania");
        return (123);
    }

    private static HashMap<String, String[]> getCliParams(String[] args) {
        Options options = new Options();

        Option port = new Option("p", "port", true, "server port");
        port.setRequired(true);
        options.addOption(port);

        return CliArgsParser.getAppOption(args, options);
    }
}
