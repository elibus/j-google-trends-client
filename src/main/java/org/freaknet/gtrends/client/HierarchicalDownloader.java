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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.http.NameValuePair;
import org.freaknet.gtrends.api.GoogleTrendsClient;
import org.freaknet.gtrends.api.GoogleTrendsCsvParser;
import org.freaknet.gtrends.api.GoogleTrendsRequest;
import org.freaknet.gtrends.api.exceptions.GoogleTrendsClientException;
import org.freaknet.gtrends.api.exceptions.GoogleTrendsRequestException;
import org.freaknet.gtrends.client.exceptions.HierarchicalDownloaderException;
import org.freaknet.gtrends.client.writers.DataWriter;
import org.freaknet.gtrends.client.writers.exceptions.DataWriterException;

/**
 * Recursively downloads CSV files from Google Trends. It starts from firstQuery
 * downloading the relative CSV file or section. Then Look for the first
 * TopFirst of "Top searches" and download them all until requestsLimit is
 * reached.
 *
 * @author Marco Tizzoni <marco.tizzoni@gmail.com>
 */
public class HierarchicalDownloader {

  private static final String SECTION_TOP_SEARCHES_FOR = "Top searches for";
  private GoogleTrendsClient client;
  private int topMax = 10;
  private DataWriter writer;
  private int sleep = 10000;
  private String section = null;
  private List<NameValuePair> queryParams = null;

  public HierarchicalDownloader(GoogleTrendsClient client, DataWriter writer) {
    this.client = client;
    this.writer = writer;
  }

  /**
   * @return the sleep
   */
  public int getSleep() {
    return this.sleep;
  }

  /**
   * @param sleep the sleep to set
   */
  public void setSleep(int sleep) {
    this.sleep = sleep;
  }

  /**
   * Starts the download.
   *
   * @param firstQuery First query to issue.
   * @param requestsLimit Maximum number of request to issue.
   * @throws HierarchicalDownloaderException
   */
  void start(String firstQuery, int requestsLimit) throws HierarchicalDownloaderException {
    String csvContent;
    int requestCount = 0;
    Map<String, Integer> queries = new HashMap();
    Queue<String> queue = new LinkedList<String>();
    queue.add(firstQuery);

    try {
      while ((!queue.isEmpty()) && (requestCount < requestsLimit)) {
        String query = queue.remove();
        if (!queries.containsKey(query)) {

          GoogleTrendsRequest request = new GoogleTrendsRequest(query);
          request.setQueryParams(queryParams);

          csvContent = getClient().execute(request);
          requestCount++;
          if (getSleep() != 0) {
            Thread.sleep(this.getSleep());
          }

          if (csvContent == null) {
            throw new HierarchicalDownloaderException("CSV is empty. It looks like something went wrong! :/");
          } else {
            Logger.getLogger(HierarchicalDownloader.class.getName()).log(Level.INFO, "#{0}, {1}", new Object[]{requestCount, query});
          }

          GoogleTrendsCsvParser csvParser = new GoogleTrendsCsvParser(csvContent);
          if (getSection() != null) {
            csvContent = csvParser.getSectionAsString(getSection(), false);
            if (csvContent == null) {
              throw new HierarchicalDownloaderException("Cannot find the requested section \"" + getSection() + "\".");
            }
          }

          queries.put(query, 0);
          writer.write(query, csvContent);
          enqueueTopSearches(csvParser, queries, queue);
        }
      }
    } catch (ConfigurationException ex) {
      throw new HierarchicalDownloaderException(ex);
    } catch (GoogleTrendsClientException ex) {
      throw new HierarchicalDownloaderException(ex);
    } catch (IOException ex) {
      throw new HierarchicalDownloaderException(ex);
    } catch (DataWriterException ex) {
      throw new HierarchicalDownloaderException(ex);
    } catch (GoogleTrendsRequestException ex) {
      throw new HierarchicalDownloaderException(ex);
    } catch (InterruptedException ex) {
      throw new HierarchicalDownloaderException(ex);
    }
  }

  /**
   * @return the topMax
   */
  public int getTopMax() {
    return topMax;
  }

  /**
   * @param topFirst
   */
  public void setTopMax(int topFirst) {
    this.topMax = topFirst;
  }

  /**
   * @return the writer
   */
  public DataWriter getWriter() {
    return writer;
  }

  /**
   * @param writer the writer to set
   */
  public void setWriter(DataWriter writer) {
    this.writer = writer;
  }

  private void enqueueTopSearches(GoogleTrendsCsvParser csvParser, Map<String, Integer> queries, Queue<String> queue) throws IOException {
    // Add next queries
    List<String> topSearches = csvParser.getSectionAsStringList(SECTION_TOP_SEARCHES_FOR, false, csvParser.getSeparator());
    for (int i = 0; (i < getTopMax()) && (i < topSearches.size()); i++) {
      String col = topSearches.get(i);
      String q = col.split(csvParser.getSeparator())[0];
      if (!queries.containsKey(q)) {
        queue.add(q);
      }
    }
  }

  /**
   * @return the client
   */
  public GoogleTrendsClient getClient() {
    return client;
  }

  /**
   * @param client the client to set
   */
  public void setClient(GoogleTrendsClient client) {
    this.client = client;
  }

  /**
   * @return the section
   */
  public String getSection() {
    return section;
  }

  /**
   * @param section the section to set
   */
  public void setSection(String section) {
    this.section = section;
  }

  /**
   * @return the queryParams
   */
  public List<NameValuePair> getQueryOpts() {
    return queryParams;
  }

  /**
   * @param queryOpts
   */
  public void setQueryOpts(List<NameValuePair> queryOpts) {
    this.queryParams = queryOpts;
  }
}
