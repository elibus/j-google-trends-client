package org.freaknet.gtrends.client;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 *
 * @author Marco Tizzoni <marco.tizzoni@gmail.com>
 */
public class CmdLineParser {

    private Options options;
    private CommandLineParser parser;
    private CommandLine cmd;

    public CmdLineParser() {
        parser = new GnuParser();
        this.options = new Options();
        Option usernameOpt = OptionBuilder.withArgName("username")
                .hasArg()
                .isRequired()
                .withDescription("Username (example: user@google.com)")
                .withLongOpt("username")
                .create("u");

        Option passwordOpt = OptionBuilder.withArgName("password")
                .hasArg()
                .isRequired()
                .withDescription("Password")
                .withLongOpt("password")
                .create("p");

        Option dirOpt = OptionBuilder.withArgName("dir")
                .hasArg()
                .isRequired()
                .withDescription("Output directory")
                .withLongOpt("dir")
                .create("d");

        Option sleepOpt = OptionBuilder.withArgName("sleep")
                .hasArg()
                .withDescription("Sleep in ms between two different requests")
                .withLongOpt("sleep")
                .create("s");

        Option proxyOpt = OptionBuilder.withArgName("proxy")
                .hasArg()
                .withDescription("Proxy host in the form \"protocol://host:port\" (example: http://proxy.domain.com:8080)")
                .withLongOpt("proxy")
                .create("P");

        Option proxyCredentialsOpt = OptionBuilder.withArgName("proxy")
                .hasArg()
                .withDescription("Proxy Credentials in the form \"[DOMAIN/]username:password\". DOMAIN is required only for NTLM authentication")
                .withLongOpt("credentials")
                .create("C");

        Option queryOpt = OptionBuilder.withArgName("query")
                .hasArg()
                .isRequired()
                .withDescription("Google query string")
                .withLongOpt("query")
                .create("q");

        options.addOption(queryOpt);
        options.addOption(usernameOpt);
        options.addOption(passwordOpt);
        options.addOption(dirOpt);
        options.addOption(sleepOpt);
        options.addOption(proxyOpt);
        options.addOption(proxyCredentialsOpt);
    }

    private void showHelp() {
        HelpFormatter h = new HelpFormatter();
        h.printHelp("help", options);
        System.exit(-1);
    }

    public CmdLineParser parse(String[] args) {
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException ex) {
            Logger.getLogger(CmdLineParser.class.getName()).log(Level.SEVERE, ex.getLocalizedMessage());
            showHelp();
        }
        return this;
    }

    public String getUsername() {
        return cmd.getOptionValue("u");
    }

    public String getPassword() {
        return cmd.getOptionValue("p");
    }

    public String getOutputDir() {
        return cmd.getOptionValue("d");
    }

    public int getSleep() {
        return Integer.valueOf(cmd.getOptionValue("s"));
    }

    public String getProxy() {
        return cmd.getOptionValue("P");
    }

    public String getProxyCredentials() {
        return cmd.getOptionValue("C");
    }

    public String getQuery() {
        return cmd.getOptionValue("q");
    }
}
