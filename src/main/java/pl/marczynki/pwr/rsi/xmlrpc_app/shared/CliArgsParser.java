package pl.marczynki.pwr.rsi.xmlrpc_app.shared;

import org.apache.commons.cli.*;

import java.util.HashMap;

public class CliArgsParser {
    public static HashMap<String, String[]> getAppOption(String[] args, Options options) {
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("utility-name", options);

            System.exit(1);
        }

        HashMap<String, String[]> results = new HashMap<>();
        for (Option option : options.getOptions()) {
            results.put(option.getLongOpt(), cmd.getOptionValues(option.getLongOpt()));
        }
        return results;
    }
}
