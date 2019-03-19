package pl.marczynki.pwr.rsi.xmlrpc_app.server;

import org.apache.xmlrpc.WebServer;
import pl.marczynki.pwr.rsi.xmlrpc_app.shared.AppType;
import pl.marczynki.pwr.rsi.xmlrpc_app.shared.CliArgsParser;
import pl.marczynki.pwr.rsi.xmlrpc_app.shared.MethodDefinition;
import pl.marczynki.pwr.rsi.xmlrpc_app.shared.ParamDefinition;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
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
            MethodDefinition methodAnnotation = method.getAnnotation(MethodDefinition.class);
            if (Modifier.isPublic(method.getModifiers()) && !Modifier.isStatic(method.getModifiers()) && methodAnnotation != null) {
                result.append("\n")
                        .append(method.getName())
                        .append("\n\t").append("Opis:")
                        .append("\n\t\t").append(methodAnnotation.description());
                Parameter[] parameters = method.getParameters();
                if (parameters.length > 0) {
                    result.append("\n\t").append("Parametry:");
                }
                for (Parameter parameter : parameters) {
                    ParamDefinition parameterAnnotation = parameter.getAnnotation(ParamDefinition.class);
                    result.append("\n\t\t")
                            .append(parameter.getType().getSimpleName()).append(" ")
                            .append(parameterAnnotation.name()).append(": ")
                            .append(parameterAnnotation.description());
                }
            }
        }
        return result.toString();
    }

    @MethodDefinition(description = "Przykladowa metoda tesujaca wystapienie wyscigu")
    public int race(@ParamDefinition(name = "upperBound", description = "liczba inkrementacji") int upperBound) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        Counter counter = new Counter();

        for (int i = 0; i < upperBound; i++) {
            executorService.submit(counter::increment);
        }

        executorService.shutdown();
        executorService.awaitTermination(60, TimeUnit.SECONDS);

        return counter.getCount();
    }

    @MethodDefinition(description = "Wyraza opinie o Twoim nastroju")
    public String moodOpinion(@ParamDefinition(name = "moodDescription", description = "Opis Twojego nastroju") String moodDescription, @ParamDefinition(name = "areYouHappy", description = "Pytanie czy jestes szczesliwy") boolean areYouHappy) {
        if (areYouHappy) {
            return "To swietnie, ze " + moodDescription;
        } else {
            return "Mam nadzieje, ze wkrotce bedzie lepiej...";
        }
    }

    @MethodDefinition(description = "Zwraca losowa liczbe")
    public int getRandomNumber(@ParamDefinition(name = "lowerBound", description = "Dolna granica losowania liczby") int lowerBound, @ParamDefinition(name = "upperBound", description = "Gorna granica losowania liczby") int upperBound) {
        return new Random().nextInt(upperBound - lowerBound) + lowerBound;
    }

    @MethodDefinition(description = "Zaookragla liczbe")
    public int roundDouble(@ParamDefinition(name = "doubleVal", description = "Liczba do zaokraglenia") double doubleVal) {
        return (int) Math.round(doubleVal);
    }

    private static boolean validateMethodDefinitions() {
        Method[] declaredMethods = ServerRpc.class.getDeclaredMethods();
        for (Method method : declaredMethods) {
            MethodDefinition methodAnnotation = method.getAnnotation(MethodDefinition.class);
            if (Modifier.isPublic(method.getModifiers()) && !Modifier.isStatic(method.getModifiers())) {
                if (methodAnnotation == null) {
                    return false;
                }
                Parameter[] parameters = method.getParameters();
                for (Parameter parameter : parameters) {
                    ParamDefinition parameterAnnotation = parameter.getAnnotation(ParamDefinition.class);
                    if (parameterAnnotation == null) {
                        return false;
                    }
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

    @MethodDefinition(description = "Metoda dodajaca 2 liczby")
    public Integer echo(@ParamDefinition(name = "x", description = "Pierwsza liczba") int x, @ParamDefinition(name = "y", description = "Duga liczba") int y) {
        return x + y;
    }

    @MethodDefinition(description = "Przykladowa metoda asynchroniczna")
    public int execAsync(@ParamDefinition(name = "sleepTime", description = "Wyznacznik jak dlugo watek ma byc wstrzymany") int sleepTime) {
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
