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
import org.apache.commons.cli.ParseException;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.freaknet.gtrends.api.GoogleAuthenticator;
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
    public static void main(String[] args) throws GoogleTrendsClientException, IOException, InterruptedException,  GoogleTrendsRequestException, URISyntaxException, ParseException, ConfigurationException, DataWriterException, HierarchicalDownloaderException {
        CmdLineParser cmdLine = new CmdLineParser().parse(args);

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
        csvDownloader.setSleep(cmdLine.getSleep());
        csvDownloader.setQueryOpts(cmdLine.getQueryOptions());
        csvDownloader.start(cmdLine.getQuery(), cmdLine.getmaxRequests());
    }

}
