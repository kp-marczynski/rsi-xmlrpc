package pl.marczynki.pwr.rsi.xmlrpc_app.shared;

import org.apache.commons.cli.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Stream;

public class CliArgsParser {
    public static HashMap<String, Object[]> getAppOption(String[] args, AppType appType) {
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;
        Options options = getOptions(appType);

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("utility-name", options);

            System.exit(1);
        }

        HashMap<String, Object[]> results = new HashMap<>();
        for (Option option : options.getOptions()) {
            if (cmd.hasOption(option.getLongOpt())) {
                String[] optionValues = cmd.getOptionValues(option.getLongOpt());
                results.put(option.getLongOpt(), Arrays.stream(optionValues).map(CliArgsParser::tryCastParamToStandardTypes).toArray(Object[]::new));
            }
        }
        return results;
    }

    private static Object tryCastParamToStandardTypes(String param) {
        try {
            Integer intValue = Integer.valueOf(param);
            if (intValue.toString().equals(param)) {
                return intValue;
            }
        } catch (Exception ignored) {
        }
        try {
            Double doubleValue = Double.valueOf(param);
            if (doubleValue.toString().equals(param)) {
                return doubleValue;
            }
        } catch (Exception ignored) {
        }
        try {
            boolean boolValue = param.equals("true");
            if (Boolean.toString(boolValue).equals(param)) {
                return boolValue;
            }
        } catch (Exception ignored) {
        }
        return param;
    }

    private static Options getOptions(AppType type) {
        switch (type) {
            case CLIENT:
                return getOptions(getClientOptions());
            case SERVER:
                return getOptions(getServerOptions());
            default:
                return new Options();
        }
    }

    private static Options getOptions(Option[] optionTab) {
        Options options = new Options();
        for (Option option : optionTab) {
            options.addOption(option);
        }
        return options;
    }

    private static Option[] getServerOptions() {
        Option port = new Option("p", "port", true, "server port");
        port.setRequired(true);

        Option serverName = new Option("n", "server-name", true, "server name");
        serverName.setRequired(true);

        return new Option[]{port, serverName};
    }

    private static Option[] getClientOptions() {
        Option ip = new Option("i", "ip", true, "server ip");
        ip.setRequired(true);

        Option async = new Option("a", "async", false, "specifying if method should be called asynchronously");
        async.setRequired(false);

        Option method = new Option("m", "method", true, "server method");
        method.setRequired(false);
        method.setArgs(Option.UNLIMITED_VALUES);

        return Stream.concat(Stream.of(ip, async, method), Arrays.stream(getServerOptions())).toArray(Option[]::new);
    }
}
