package pl.marczynki.pwr.rsi.xmlrpc_app.server;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.xmlrpc.WebServer;
import pl.marczynki.pwr.rsi.xmlrpc_app.shared.CliArgsParser;
import pl.marczynki.pwr.rsi.xmlrpc_app.shared.MethodDefinition;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.InetAddress;
import java.util.HashMap;

public class ServerRpc {
    public static void main(String[] args) {
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

    @MethodDefinition(description = "Metoda dodajaca 2 liczby", params = {"int x: pierwsza liczba", "int y: druga liczba"})
    public Integer echo(int x, int y) {
        return x + y;
    }

    @MethodDefinition(description = "Przykladowa metoda asynchroniczna", params = {"int sleepTime: wyznacznik jak dlugo watek ma byc wstrzymany"})
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

    @MethodDefinition(description = "Zwraca liste dostepnych metod")
    public String show() {
        StringBuilder result = new StringBuilder();
        result.append("***** Dostepne metody *****");
        Method[] declaredMethods = ServerRpc.class.getDeclaredMethods();
        for (Method method : declaredMethods) {
            MethodDefinition annotation = method.getAnnotation(MethodDefinition.class);
            if (Modifier.isPublic(method.getModifiers()) && !Modifier.isStatic(method.getModifiers()) && annotation != null) {
                result.append("\n")
                        .append(method.getName())
                        .append("\n\t").append("Opis:")
                        .append("\n\t\t").append(annotation.description());
                if (annotation.params().length > 0) {
                    result.append("\n\t").append("Parametry:");
                }
                for (String param : annotation.params()) {
                    result.append("\n\t\t").append(param);
                }
            }
        }
        return result.toString();
    }

    @MethodDefinition(description = "Wyraza opinie o Twoim nastroju", params = {"String moodDescription: Opis Twojego nastroju", "Boolean areYouHappy: pytanie czy jestes szczesliwy"})
    public String moodOpinion(String moodDescription, boolean areYouHappy) {
        if (areYouHappy) {
            return "To swietnie, ze " + moodDescription;
        } else {
            return "Mam nadzieje, ze wkrotce bedzie lepiej...";
        }
    }

    private static HashMap<String, String[]> getCliParams(String[] args) {
        Options options = new Options();

        Option port = new Option("p", "port", true, "server port");
        port.setRequired(true);
        options.addOption(port);

        return CliArgsParser.getAppOption(args, options);
    }
}
