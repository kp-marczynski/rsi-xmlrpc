package pl.marczynki.pwr.rsi.xmlrpc_app.server;

import org.apache.xmlrpc.WebServer;
import pl.marczynki.pwr.rsi.xmlrpc_app.shared.AppType;
import pl.marczynki.pwr.rsi.xmlrpc_app.shared.CliArgsParser;
import pl.marczynki.pwr.rsi.xmlrpc_app.shared.MethodDefinition;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ServerRpc {
    public static void main(String[] args) {
        if (!validateMethodDefinitions()) {
            throw new IllegalStateException("All server non-static public methods must be annotated with @MethodDefinition and must have documented correct number of params");
        }

        HashMap<String, Object[]> cliParams = CliArgsParser.getAppOption(args, AppType.SERVER);
        try {
            System.out.println("Startuje serwer XML-RPC...");
            int port = (int) cliParams.get("port")[0];
            String serverName = (String) cliParams.get("server-name")[0];
            WebServer server = new WebServer(port);
            server.addHandler(serverName, new ServerRpc());
            server.start();

            System.out.println("Serwer wystartowal pomyslnie.");
            System.out.println("Bierzacy adres IP: " + InetAddress.getLocalHost().getHostAddress());
            System.out.println("Nasluchuje na porcie: " + port);
            System.out.println("Aby zatrzymac serwer nacisnij crl+c");
        } catch (Exception exception) {
            System.err.println("Serwer XML-RPC: " + exception);
        }
    }

    public static void minimalMain(String[] args) {
        try {
            WebServer server = new WebServer(1000);
            server.addHandler("MojSerwer", new ServerRpc());
            server.start();
        } catch (Exception exception) {
            System.err.println("Serwer XML-RPC: " + exception);
        }
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

    @MethodDefinition(description = "Przykladowa metoda tesujaca wystapienie wyscigu", params = {"int upperBound: liczba inkrementacji"})
    public int race(int upperBound) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        Counter counter = new Counter();

        for (int i = 0; i < upperBound; i++) {
            executorService.submit(counter::increment);
        }

        executorService.shutdown();
        executorService.awaitTermination(60, TimeUnit.SECONDS);

        return counter.getCount();
    }

    @MethodDefinition(description = "Wyraza opinie o Twoim nastroju", params = {"String moodDescription: Opis Twojego nastroju", "Boolean areYouHappy: pytanie czy jestes szczesliwy"})
    public String moodOpinion(String moodDescription, boolean areYouHappy) {
        if (areYouHappy) {
            return "To swietnie, ze " + moodDescription;
        } else {
            return "Mam nadzieje, ze wkrotce bedzie lepiej...";
        }
    }

    @MethodDefinition(description = "Zwraca losowa liczbe", params = {"int lowerBound: dolna granica losowania liczby", "int upperBound: gorna granica losowania liczby"})
    public int getRandomNumber(int lowerBound, int upperBound) {
        return new Random().nextInt(upperBound - lowerBound) + lowerBound;
    }

    private static boolean validateMethodDefinitions() {
        Method[] declaredMethods = ServerRpc.class.getDeclaredMethods();
        for (Method method : declaredMethods) {
            MethodDefinition annotation = method.getAnnotation(MethodDefinition.class);
            if (Modifier.isPublic(method.getModifiers()) && !Modifier.isStatic(method.getModifiers())) {
                if (annotation == null || annotation.params().length != method.getParameterCount()) {
                    return false;
                }
            }
        }
        return true;
    }

    private class Counter {
        private int count = 0;

        void increment() {
            count = count + 1;
        }

        int getCount() {
            return count;
        }
    }


    /*************** default methods *****************/

    @MethodDefinition(description = "Metoda dodajaca 2 liczby", params = {"int x: pierwsza liczba", "int y: druga liczba"})
    public Integer echo(int x, int y) {
        return x + y;
    }

    @MethodDefinition(description = "Przykladowa metoda asynchroniczna", params = {"int sleepTime: wyznacznik jak dlugo watek ma byc wstrzymany"})
    public int execAsync(int sleepTime) {
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
}
