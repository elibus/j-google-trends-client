/**
 * Copyright (C) 2013 Marco Tizzoni <marco.tizzoni@gmail.com>
 *
 * This file is part of j-google-trends-client
 *
 *     j-google-trends-client is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     j-google-trends-client is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with j-google-trends-client.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.freaknet.gtrends.client;

import com.sun.org.apache.xerces.internal.impl.xs.identity.Selector;
import org.freaknet.gtrends.client.json.RegionsParser;
import org.freaknet.gtrends.client.exceptions.CmdLineParserException;
import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.http.NameValuePair;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.NTCredentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.message.BasicNameValuePair;
import org.freaknet.gtrends.api.GoogleConfigurator;
import org.freaknet.gtrends.client.json.Region;

/**
 *
 * @author Marco Tizzoni <marco.tizzoni@gmail.com>
 */
public class CmdLineParser {

  private static final String PARAMS_SEP = "&";
  private static final String PARAMS_NAME_VALUE_SEP = "=";
  private static final String USER_PASS_SEP = ":";
  public static final char DOMAIN_SEP = '/';

  private final Options options;
  private final CommandLineParser parser;
  private CommandLine cmd;
  private static final String HELP_HEADER = "This is a client for Google Trends. It allows to:\n"
          + "- Download the entire CSV file (the same file that can be downloaded through the browser)\n"
          + "- Download a single section of the same CSV file\n"
          + "- Iterate over the hierarchical tree of \"Top Searches\" and download section/CSV file\n";
  private static final String HELP_FOOTER = "EXAMPLE: gtclient.sh -u user@google.com - p passwd -d ./outdir -q \"jobs -'steve jobs'\"\n";
  private static final String DEFAULT_OUTPUT_DIR = "out";

  public CmdLineParser() {
    parser = new GnuParser();
    this.options = new Options();
    Option usernameOpt = OptionBuilder.withArgName("username")
            .hasArg()
            .withDescription("Username (example: user@google.com)")
            .withLongOpt("username")
            .create("u");

    Option passwordOpt = OptionBuilder.withArgName("password")
            .hasArg()
            .withDescription("Password")
            .withLongOpt("password")
            .create("p");

    Option dirOpt = OptionBuilder.withArgName("dir")
            .hasArg()
            .withDescription("Output directory (default: \"./out\")")
            .withLongOpt("dir")
            .create("d");

    Option sleepOpt = OptionBuilder.withArgName("sleep")
            .hasArg()
            .withDescription("Sleep in ms between two different requests (might help in case the QoS threshold is exceeded - default: 10000 (10 secs))")
            .withLongOpt("sleep")
            .withType(Number.class)
            .create("S");

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
            .withDescription("Google query string")
            .withLongOpt("query")
            .create("q");

    Option sectionOpt = OptionBuilder.withArgName("section")
            .hasArg()
            .withDescription("CSV section to retrieve")
            .withLongOpt("section")
            .create("s");

    Option maxRequestsOpt = OptionBuilder.withArgName("maxRequests")
            .hasArg()
            .withDescription("Maximum number of requests to perform.")
            .withLongOpt("maxRequests")
            .create("m");

    Option queryOptionsOpt = OptionBuilder.withArgName("queryOptions")
            .hasArg()
            .withDescription("Query options.")
            .withLongOpt("queryOptions")
            .create("o");

    Option logLevelOpt = OptionBuilder.withArgName("-l")
            .hasArg()
            .withDescription("Log level <INFO|WARNING|SEVERE> (default WARNING)")
            .withLongOpt("-logLevel")
            .create("l");

    Option regionOpt = OptionBuilder.withArgName("-r")
            .hasArg()
            .withDescription("Region to download (default World Wide)")
            .withLongOpt("-region")
            .create("r");

    Option printRegionsOpt = OptionBuilder.withArgName("-R")
            .withDescription("Print all available regions")
            .withLongOpt("-printRegions")
            .create("R");

    Option dateSinceOpt = OptionBuilder.withArgName("-D")
            .hasArg()
            .withDescription("Time frame in the format MM/YYYY:N "
                    + "Meaning: Since MM/YYYY with a time window of N months")
            .withLongOpt("-dateSince")
            .create("D");

    Option dateWindowOpt = OptionBuilder.withArgName("-w")
            .hasArg()
            .withDescription("Set a time window. Works in conjuction with '-D'."
                    + "Example: '-D 02/2014:4 -w 1' downloads the monthly statistics from February up to May")
            .withLongOpt("-window")
            .create("w");

    options.addOption(queryOpt);
    options.addOption(usernameOpt);
    options.addOption(passwordOpt);
    options.addOption(dirOpt);
    options.addOption(sleepOpt);
    options.addOption(proxyOpt);
    options.addOption(proxyCredentialsOpt);
    options.addOption(maxRequestsOpt);
    options.addOption(sectionOpt);
    options.addOption(queryOptionsOpt);
    options.addOption(logLevelOpt);
    options.addOption(regionOpt);
    options.addOption(printRegionsOpt);
    options.addOption(dateSinceOpt);
    options.addOption(dateWindowOpt);
  }

  /**
   * Shows help
   */
  private void showHelp() {
    new HelpFormatter().printHelp("gtclient.sh", HELP_HEADER, options, HELP_FOOTER, true);
    System.exit(-1);
  }

  /**
   * Return the command line parser.
   *
   * @param args
   * @return parser
   */
  public CmdLineParser parse(String[] args) {
    try {
      cmd = parser.parse(options, args);
    } catch (ParseException ex) {
      Logger.getLogger(GoogleConfigurator.getLoggerPrefix()).log(Level.SEVERE, ex.getLocalizedMessage());
      showHelp();
    }
    return this;
  }

  /**
   * Gets the Google account username.
   *
   * @return
   */
  public String getUsername() {
    return cmd.getOptionValue("u");
  }

  /**
   * Gets the Google account password.
   *
   * @return password
   */
  public String getPassword() {
    return cmd.getOptionValue("p");
  }

  /**
   * Gets the directory where to store the output.
   *
   * @return direcotry
   */
  public String getOutputDir() {
    if (cmd.getOptionValue("d") == null) {
      return System.getProperty("user.dir") + java.io.File.separator + DEFAULT_OUTPUT_DIR;
    } else {
      return cmd.getOptionValue("d");
    }
  }

  /**
   * Gets the number of ms between one request and the next one.
   *
   * @return sleep (in ms)
   */
  public int getSleep() {
    try {
      return Integer.valueOf(cmd.getOptionValue("S"));
    } catch (java.lang.NumberFormatException e) {
      return -1;
    }
  }

  /**
   * Gets the proxy string as provided on the command line.
   *
   * @return proxy string
   */
  private String getProxy() {
    return cmd.getOptionValue("P");
  }

  /**
   * Gets the <code>Credentials</code> for proxy authentication.
   *
   * @return credentials
   */
  public Credentials getProxyCredentials() {
    String c = cmd.getOptionValue("C");
    Credentials credentials;
    Pattern pattern = Pattern.compile(".*" + DOMAIN_SEP + ".*" + USER_PASS_SEP);
    Matcher matcher = pattern.matcher(c);
    if (matcher.find()) {
      try {
        credentials = new NTCredentials(getProxyUsername(), getProxyPassword(), InetAddress.getLocalHost().getHostName(), getProxyUserDomain());
      } catch (UnknownHostException ex) {

        Logger.getLogger(GoogleConfigurator.getLoggerPrefix()).log(Level.WARNING, "Could not retrieve workstation name. Trying authentication without it.", ex);
        credentials = new NTCredentials(getProxyUsername(), getProxyPassword(), "", getProxyUserDomain());
      }
    } else {
      credentials = new UsernamePasswordCredentials(getProxyUsername(), getProxyPassword());
    }

    return credentials;
  }

  /**
   * Gets the Google query string.
   *
   * @return
   */
  public String getQuery() {
    return cmd.getOptionValue("q");
  }

  /**
   * Gets the section to retrieve from the CSV.
   *
   * @return
   */
  public String getSection() {
    return cmd.getOptionValue("s");
  }

  /**
   * Gets the log Level.
   *
   * @return
   */
  public String getLogLevel() {
    return cmd.getOptionValue("l");
  }

  /**
   * Gets proxy Host name
   *
   * @return hostname
   */
  public String getProxyHostname() {
    if (getProxy() != null) {
      return getProxy().split(USER_PASS_SEP)[1].substring(2);
    } else {
      return null;
    }
  }

  /**
   * Gets proxy protocol.
   *
   * @return protocol
   */
  public String getProxyProtocol() {
    if (getProxy() != null) {
      return getProxy().split(USER_PASS_SEP)[0];
    } else {
      return null;
    }
  }

  /**
   * Gets proxy port.
   *
   * @return port
   */
  public Integer getProxyPort() {
    if (getProxy() != null) {
      return Integer.valueOf(getProxy().split(USER_PASS_SEP)[2]);
    } else {
      return null;
    }
  }

  /**
   * Gets the NT DOMAIN for NTLM Authentication.
   *
   * @return
   */
  public String getProxyUserDomain() {
    String c = cmd.getOptionValue("C");
    int atColon = c.indexOf(USER_PASS_SEP);
    String username = c.substring(0, atColon);
    int atSlash = username.indexOf(DOMAIN_SEP);
    String domain = null;
    if (atSlash > 0) {
      domain = username.substring(0, atSlash);
    }

    return domain;
  }

  /**
   * Gets the Username for the proxy authentication.
   *
   * @return username
   */
  public String getProxyUsername() {
    String c = cmd.getOptionValue("C");
    int atColon = c.indexOf(USER_PASS_SEP);
    String username = c.substring(0, atColon);
    int atSlash = username.indexOf(DOMAIN_SEP);
    if (atSlash > 0) {
      username = c.substring(atSlash + 1, atColon);
    }

    return username;
  }

  /**
   * Gets the Password for the proxy authentication.
   *
   * @return password
   */
  public String getProxyPassword() {
    String c = cmd.getOptionValue("C");
    int atColon = c.indexOf(USER_PASS_SEP);
    return c.substring(atColon + 1);
  }

  /**
   * Gets the maximum number of requests to issue.
   *
   * @return
   */
  public int getmaxRequests() {
    try {
      return Integer.valueOf(cmd.getOptionValue("m"));
    } catch (java.lang.NumberFormatException e) {
      return 1;
    }
  }

  public List<NameValuePair> getQueryOpts() {
    List<NameValuePair> ret = new LinkedList<NameValuePair>();
    String opts = cmd.getOptionValue("o");
    String[] optsArray;

    if (opts != null) {
      optsArray = opts.split(PARAMS_SEP);
      for (String optsArray1 : optsArray) {
        String[] s = optsArray1.split(PARAMS_NAME_VALUE_SEP);
        ret.add(new BasicNameValuePair(s[0], s[1]));
      }
    }

    return ret;
  }

  public Boolean getPrintRegionsOpt() {
    return cmd.hasOption('R');
  }

  public List<Region> getRegions() throws FileNotFoundException, CmdLineParserException {
    if (!cmd.hasOption('r')) {
      return null;
    }

    LinkedList<Region> list = new LinkedList<Region>();
    RegionsParser p = RegionsParser.getInstance();
    StringTokenizer st = new StringTokenizer(cmd.getOptionValue('r'), ",");
    while (st.hasMoreTokens()) {
      String id = st.nextToken();
      Region r = p.find(id);
      if (r != null) {
        list.add(r);
      } else {
        throw new CmdLineParserException("Region with id " + id + " is not available!");
      }
    }
    return list;
  }

  public String getDateSince() throws CmdLineParserException {
    if (!cmd.hasOption('D')) {
      return null;
    }

    String v = cmd.getOptionValue('D');
    Pattern p = Pattern.compile("\\d{1,2}/\\d{4}:\\d+");
    Matcher m = p.matcher(v);
    
    if (m.matches()) {
      return v;
    }

    throw new CmdLineParserException("Date '" + v + "' has an invalid format!");
  }

  public Integer getDateWindow() throws CmdLineParserException {
    if (!cmd.hasOption('D')) {
      return 0;
    }
    try {
      return Integer.valueOf(cmd.getOptionValue("w"));
    } catch (java.lang.NumberFormatException e) {
      throw new CmdLineParserException("Window '" + cmd.getOptionValue("w") + "' has an invalid format!");
    }
  }
}
