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

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Filter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.apache.commons.cli.ParseException;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.freaknet.gtrends.api.GoogleAuthenticator;
import org.freaknet.gtrends.api.GoogleConfigurator;
import org.freaknet.gtrends.api.GoogleTrendsClient;
import org.freaknet.gtrends.api.exceptions.GoogleTrendsClientException;
import org.freaknet.gtrends.api.exceptions.GoogleTrendsRequestException;
import org.freaknet.gtrends.client.exceptions.HierarchicalDownloaderException;
import org.freaknet.gtrends.client.writers.MultipleFileWriter;
import org.freaknet.gtrends.client.writers.exceptions.DataWriterException;

/**
 *
 * @author Marco Tizzoni <marco.tizzoni@gmail.com>
 */
public class App {

  private static final String DEFAULT_LOGGING_LEVEL = "WARNING";

  public static void main(String[] args) throws GoogleTrendsClientException, IOException, InterruptedException, GoogleTrendsRequestException, URISyntaxException, ParseException, DataWriterException, HierarchicalDownloaderException {
    CmdLineParser cmdLine = new CmdLineParser().parse(args);
    setLogLevel(cmdLine);

    DefaultHttpClient httpClient = new DefaultHttpClient();

    if (cmdLine.getProxyHostname() != null) {
      HttpHost proxyHost = new HttpHost(cmdLine.getProxyHostname(), cmdLine.getProxyPort(), cmdLine.getProxyProtocol());
      Credentials credentials = cmdLine.getProxyCredentials();

      if (credentials != null) {
        httpClient.getCredentialsProvider().setCredentials(new AuthScope(proxyHost.getHostName(), proxyHost.getPort()), credentials);
      }
      httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxyHost);
    }

    GoogleAuthenticator authenticator = new GoogleAuthenticator(cmdLine.getUsername(), cmdLine.getPassword(), httpClient);
    GoogleTrendsClient client = new GoogleTrendsClient(authenticator, httpClient);
    MultipleFileWriter writer = new MultipleFileWriter(cmdLine.getOutputDir());
    HierarchicalDownloader csvDownloader = new HierarchicalDownloader(client, writer);
    if (cmdLine.getSleep() >= 0) {
      csvDownloader.setSleep(cmdLine.getSleep());
    }
    if (cmdLine.getSection() != null) {
      csvDownloader.setSection(cmdLine.getSection());
    }

    csvDownloader.setQueryOpts(cmdLine.getQueryOptions());
    csvDownloader.start(cmdLine.getQuery(), cmdLine.getmaxRequests());
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
        if ( record.getSourceClassName().startsWith(GoogleConfigurator.getLoggerPrefix())) {
          return (record.getLevel().intValue() >= level.intValue());
        }
        return false;
      }
    });
  }

}
