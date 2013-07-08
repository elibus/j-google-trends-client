package org.freaknet.gtrends.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.cli.ParseException;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.NTCredentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.freaknet.gtrends.api.GoogleAuthenticator;
import org.freaknet.gtrends.api.GoogleTrendsClient;
import org.freaknet.gtrends.api.GoogleTrendsCsvParser;
import org.freaknet.gtrends.api.GoogleTrendsRequest;
import org.freaknet.gtrends.api.exceptions.GoogleTrendsClientException;
import org.freaknet.gtrends.api.exceptions.GoogleTrendsRequestException;

/**
 *
 * @author Marco Tizzoni <marco.tizzoni@gmail.com>
 */
public class App {

    private static int requestsLimit = 5;
    private static int slowDown = 0;

    public static void main(String[] args) throws GoogleTrendsClientException, IOException, InterruptedException,  GoogleTrendsRequestException, URISyntaxException, ParseException, ConfigurationException {
        int requestCount = 0;
        CmdLineParser cmdLine = new CmdLineParser().parse(args);

        DefaultHttpClient httpClient = getHttpClient(cmdLine.getProxy(), cmdLine.getProxyCredentials());

        GoogleAuthenticator authenticator = new GoogleAuthenticator(cmdLine.getUsername(), cmdLine.getPassword(), httpClient);
        GoogleTrendsClient client = new GoogleTrendsClient(authenticator, httpClient);
        GoogleTrendsRequest request;
        String csvContent;
        GoogleTrendsCsvParser csvParser;

        HashMap<String, ArrayList> timeSeries = new HashMap();
        Queue<String> queue = new LinkedList<String>();
        
        String firstQuery = cmdLine.getQuery();
        queue.add(firstQuery);

        while ((!queue.isEmpty()) && (requestCount < requestsLimit)) {
            String queryString = queue.remove();
            if (!timeSeries.containsKey(queryString)) {
                request = new GoogleTrendsRequest(queryString);
                csvContent = client.execute(request);
                Thread.sleep(slowDown);
                requestCount++;
                if (csvContent == null) {
                    System.err.append("ERROR!");
                    System.exit(1);
                } else {
                    System.out.println("#" + requestCount + ": " + queryString);
                }

                csvParser = new GoogleTrendsCsvParser(csvContent);
                // get Time Series and store it
                ArrayList<String> interestOverTime = csvParser.getSectionAsStringList("Interest over time", false, ",");
                timeSeries.put(queryString, interestOverTime);

                ArrayList<String> topSearches = csvParser.getSectionAsStringList("Top searches for", false, ",");
                final int topFirst = 10;
                for (int i = 0; (i < topFirst) && (i < topSearches.size()); i++) {
                    String col = topSearches.get(i);
                    String q = col.split(",")[0];
                    if (!timeSeries.containsKey(q)) {
                        queue.add(q);
                    }
                }
            }
        }

        // Print everything
        for (Map.Entry<String, ArrayList> entry : timeSeries.entrySet()) {
            ArrayList rows = entry.getValue();
            System.out.println("=== " + entry.getKey() + " ===");
            Iterator iterator = rows.iterator();
            while (iterator.hasNext()) {
                //String[] o = (String[]) iterator.next();
                System.out.println(iterator.next());
            }
        }

    }


    private static DefaultHttpClient getHttpClient(String proxy, String proxyCredentials) throws UnknownHostException {
        DefaultHttpClient httpClient = new DefaultHttpClient();

        if (proxy != null) {
            String proxyHostName = proxy.split(":")[1].substring(2);
            String proxyProtocol = proxy.split(":")[0];
            int proxyPort = Integer.valueOf(proxy.split(":")[2]);

            HttpHost proxyHost = new HttpHost(proxyHostName, proxyPort, proxyProtocol);

            if (proxyCredentials != null) {
                Credentials credentials;
                Pattern pattern = Pattern.compile(".*/.*:");
                Matcher matcher = pattern.matcher(proxyCredentials);
                if (matcher.find()) {
                    int atColon = proxyCredentials.indexOf(':');
                    String username = proxyCredentials.substring(0, atColon);
                    int atSlash = username.indexOf('/');
                    String password = proxyCredentials.substring(atColon + 1);
                    String domain = username.substring(0, atSlash);
                    username = proxyCredentials.substring(atSlash + 1, atColon);

                    credentials = new NTCredentials(username, password, InetAddress.getLocalHost().getHostName(), domain);
                } else {
                    credentials = new UsernamePasswordCredentials(proxyCredentials);
                }
                httpClient.getCredentialsProvider().setCredentials(new AuthScope(proxyHost.getHostName(), proxyHost.getPort()), credentials);
            }
            httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxyHost);
        }

        return httpClient;
    }
}
