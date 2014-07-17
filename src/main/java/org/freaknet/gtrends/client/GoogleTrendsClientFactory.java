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
import org.freaknet.gtrends.client.exceptions.GoogleTrendsClientRunException;
import org.freaknet.gtrends.client.json.Region;

/**
 *
 * @author Marco Tizzoni <marco.tizzoni@gmail.com>
 */
public class GoogleTrendsClientFactory {

  private static GoogleTrendsClient client = null;

  private static final String DEFAULT_LOGGING_LEVEL = "WARNING";

  private static DefaultHttpClient _httpClient = null;
  private static GoogleAuthenticator _authenticator = null;

  private GoogleTrendsClientFactory() {
  }

  /**
   *
   * @param cmdLine
   * @return
   * @throws org.freaknet.gtrends.client.exceptions.GoogleTrendsClientRunException
   */
  public static GoogleTrendsClient buildClient(CmdLineParser cmdLine) throws GoogleTrendsClientRunException {
    if (client == null) {
      client = _parse(cmdLine);
      return client;
    }
    return client;
  }

  private static GoogleTrendsClient _parse(CmdLineParser cmdLine) throws GoogleTrendsClientRunException {
    setLogLevel(cmdLine);
    try {
      // TODO - Move outside
      // Prints all available regions and exists
      if (cmdLine.getPrintRegionsOpt()) {
        RegionsParser.getInstance().printAll();
        System.exit(0);
      }

      // HTTP Client setup
      _httpClient = new DefaultHttpClient();
      if (cmdLine.getProxyHostname() != null) {
        HttpHost proxyHost = new HttpHost(cmdLine.getProxyHostname(), cmdLine.getProxyPort(), cmdLine.getProxyProtocol());
        Credentials credentials = cmdLine.getProxyCredentials();
        if (credentials != null) {
          _httpClient.getCredentialsProvider().setCredentials(new AuthScope(proxyHost.getHostName(), proxyHost.getPort()), credentials);
        }
        _httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxyHost);
      }

      // Setup Google Authenticator
      _authenticator = new GoogleAuthenticator(cmdLine.getUsername(), cmdLine.getPassword(), _httpClient);

    } catch (FileNotFoundException ex) {
      throw new GoogleTrendsClientRunException(ex);
    }

    return new GoogleTrendsClient(_authenticator, _httpClient);
  }

  // TODO - is this the right place? Guess no...
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
