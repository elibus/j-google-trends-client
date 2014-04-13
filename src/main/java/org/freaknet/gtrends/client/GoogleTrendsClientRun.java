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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.freaknet.gtrends.client;

import org.freaknet.gtrends.client.json.RegionsParser;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Filter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.freaknet.gtrends.api.GoogleAuthenticator;
import org.freaknet.gtrends.api.GoogleConfigurator;
import org.freaknet.gtrends.api.GoogleTrendsClient;
import org.freaknet.gtrends.client.exceptions.CmdLineParserException;
import org.freaknet.gtrends.client.exceptions.GoogleTrendsClientRunException;
import org.freaknet.gtrends.client.json.Region;
import org.freaknet.gtrends.client.writers.MultipleFileWriter;

/**
 *
 * @author elibus
 */
public class GoogleTrendsClientRun {

  private static GoogleTrendsClient client = null;

  private static final String DEFAULT_LOGGING_LEVEL = "WARNING";
  private static final int DEFAULT_SLEEP_TIME = 10000;

  private static DefaultHttpClient httpClient = null;
  private static GoogleAuthenticator authenticator = null;
  private static int sleepTime = DEFAULT_SLEEP_TIME;
  private static String section = "";
  private static MultipleFileWriter writer = null;
  private static List<Region> regions = null;
  private static int maxRequests = 1;
  private static String query;
  private static List<NameValuePair> queryOpts;
  private static Integer dateWindow;
  private static String dateSince;

  /**
   * @return the httpClient
   */
  public static DefaultHttpClient getHttpClient() {
    return httpClient;
  }

  /**
   * @return the authenticator
   */
  public static GoogleAuthenticator getAuthenticator() {
    return authenticator;
  }

  /**
   * @return the sleepTime
   */
  public static int getSleepTime() {
    return sleepTime;
  }

  /**
   * @return the csvSection
   */
  public static String getSection() {
    return section;
  }

  /**
   * @return the writer
   */
  public static MultipleFileWriter getWriter() {
    return writer;
  }

  /**
   * @return the regions
   */
  public static List<Region> getRegions() {
    return regions;
  }

  /**
   * @return the maxRequests
   */
  public static int getMaxRequests() {
    return maxRequests;
  }

  /**
   * @return the query
   */
  public static String getQuery() {
    return query;
  }

  /**
   * @return the queryOpts
   */
  public static List<NameValuePair> getQueryOpts() {
    return queryOpts;
  }

  /**
   * @return the dateWindow
   */
  public static Integer getDateWindow() {
    return dateWindow;
  }

  /**
   * @return the dateSince
   */
  public static String getDateSince() {
    return dateSince;
  }

  private GoogleTrendsClientRun() {
  }

  /**
   *
   * @param args
   * @return
   * @throws
   * org.freaknet.gtrends.client.exceptions.GoogleTrendsClientRunException
   */
  public static GoogleTrendsClient initialize(String[] args) throws GoogleTrendsClientRunException {
    if (client == null) {
      client = _parse(args);
      return client;
    }
    return client;
  }

  private static GoogleTrendsClient _parse(String[] args) throws GoogleTrendsClientRunException {
    CmdLineParser cmdLine = new CmdLineParser().parse(args);

    setLogLevel(cmdLine);
    try {
      // Prints all available regions and exists
      if (cmdLine.getPrintRegionsOpt()) {

        RegionsParser.getInstance().printAll();
        System.exit(0);

      }

      // Setup the HTTP Client
      httpClient = new DefaultHttpClient();
      if (cmdLine.getProxyHostname() != null) {
        HttpHost proxyHost = new HttpHost(cmdLine.getProxyHostname(), cmdLine.getProxyPort(), cmdLine.getProxyProtocol());
        Credentials credentials = cmdLine.getProxyCredentials();

        if (credentials != null) {
          getHttpClient().getCredentialsProvider().setCredentials(new AuthScope(proxyHost.getHostName(), proxyHost.getPort()), credentials);
        }
        getHttpClient().getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxyHost);
      }

      // Setup Google Authenticator
      authenticator = new GoogleAuthenticator(cmdLine.getUsername(), cmdLine.getPassword(), getHttpClient());

      writer = new MultipleFileWriter(cmdLine.getOutputDir());

      if (cmdLine.getSleep() > 0) {
        sleepTime = cmdLine.getSleep();
      }

      section = cmdLine.getSection();
      regions = cmdLine.getRegions();
      maxRequests = cmdLine.getmaxRequests();
      query = cmdLine.getQuery();
      queryOpts = cmdLine.getQueryOpts();
      dateSince = cmdLine.getDateSince();
      dateWindow = cmdLine.getDateWindow();
    } catch (FileNotFoundException ex) {
      throw new GoogleTrendsClientRunException(ex);
    } catch (CmdLineParserException ex) {
      throw new GoogleTrendsClientRunException(ex);
    } catch (IOException ex) {
      throw new GoogleTrendsClientRunException(ex);
    }

    return new GoogleTrendsClient(GoogleTrendsClientRun.getAuthenticator(), GoogleTrendsClientRun.getHttpClient());
  }

  private static void setLogLevel(CmdLineParser cmdLine) throws SecurityException, IllegalArgumentException {
    final Level level;
    if (cmdLine.getLogLevel() != null) {
      level = Level.parse(cmdLine.getLogLevel());
    } else {
      level = Level.parse(DEFAULT_LOGGING_LEVEL);
    }
    Logger log = LogManager.getLogManager().getLogger("");

    for (Handler h : log.getHandlers()) {
      log.removeHandler(h);
    }
    Handler handler = new ConsoleHandler();
    handler.setFormatter(new LogFormatter());
    handler.setLevel(level);
    log.setUseParentHandlers(false);

    Logger defaultLog = Logger.getLogger(GoogleConfigurator.getLoggerPrefix());
    defaultLog.addHandler(handler);
    defaultLog.setLevel(level);
    defaultLog.setFilter(new Filter() {
      @Override
      public boolean isLoggable(LogRecord record) {
        if (record.getSourceClassName().startsWith(GoogleConfigurator.getLoggerPrefix())) {
          return (record.getLevel().intValue() >= level.intValue());
        }
        return false;
      }
    });
  }

}
