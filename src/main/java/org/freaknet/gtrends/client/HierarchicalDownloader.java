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
import org.freaknet.gtrends.api.GoogleConfigurator;
import org.freaknet.gtrends.api.GoogleTrendsClient;
import org.freaknet.gtrends.api.GoogleTrendsCsvParser;
import org.freaknet.gtrends.api.GoogleTrendsRequest;
import org.freaknet.gtrends.api.exceptions.GoogleTrendsClientException;
import org.freaknet.gtrends.api.exceptions.GoogleTrendsRequestException;
import org.freaknet.gtrends.client.exceptions.HierarchicalDownloaderException;
import org.freaknet.gtrends.client.json.Region;
import org.freaknet.gtrends.client.writers.DataWriter;
import org.freaknet.gtrends.client.writers.exceptions.DataWriterException;

/**
 * Recursively downloads CSV files from Google Trends. It starts from firstQuery
 * downloading the relative CSV file or _section. Then Look for the first
 * TopFirst of "Top searches" and download them all until requestsLimit is
 * reached.
 *
 * @author Marco Tizzoni <marco.tizzoni@gmail.com>
 */
public class HierarchicalDownloader {

  private static final String SECTION_TOP_SEARCHES_FOR = "Top searches for";
  private GoogleTrendsClient _client;
  private int topMax = 10;
  private DataWriter _writer;
  private int _sleep;
  private String _section;
  private List<NameValuePair> _queryOpts;
  private List<Region> _regions;

  public HierarchicalDownloader(GoogleTrendsClient client, DataWriter writer) {
    _queryOpts = null;
    _section = null;
    _regions = null;
    _sleep = 0;
    _client = client;
    _writer = writer;
  }

  /**
   * @return the _sleep
   */
  public int getSleep() {
    return this._sleep;
  }

  /**
   * @param sleep the _sleep to set
   */
  public void setSleep(int sleep) {
    this._sleep = sleep;
  }

  public void start(String firstQuery, int requestsLimit) throws HierarchicalDownloaderException {
    for (Region r : _regions) {
      _start(firstQuery, requestsLimit, r.getId(), _section);
    }

  }

  /**
   * Starts the download.
   *
   * @param firstQuery First query to issue.
   * @param requestsLimit Maximum number of request to issue.
   * @throws HierarchicalDownloaderException
   */
  private void _start(String firstQuery, int requestsLimit, String regionId, String date) throws HierarchicalDownloaderException {
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
          request.setGeo(regionId);
          request.setDate(date);

          csvContent = getClient().execute(request);
          requestCount++;
          if (getSleep() != 0) {
            Logger.getLogger(GoogleConfigurator.getLoggerPrefix()).log(Level.FINE, "Sleeping for {0} seconds", this.getSleep() / 1000);
            Thread.sleep(this.getSleep());
          }

          if (csvContent == null) {
            throw new HierarchicalDownloaderException("CSV is empty. It looks like something went wrong! :/");
          } else {
            Logger.getLogger(GoogleConfigurator.getLoggerPrefix()).log(Level.INFO, "#{0}, {1}", new Object[]{requestCount, query});
          }

          GoogleTrendsCsvParser csvParser = new GoogleTrendsCsvParser(csvContent);
          if (getSection() != null) {
            csvContent = csvParser.getSectionAsString(getSection(), false);
            if (csvContent == null) {
              throw new HierarchicalDownloaderException("Cannot find the requested section \"" + getSection() + "\".");
            }
          }

          queries.put(query, 0);
          _writer.write(request, csvContent);
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
   * @return the _writer
   */
  public DataWriter getWriter() {
    return _writer;
  }

  /**
   * @param writer the _writer to set
   */
  public void setWriter(DataWriter writer) {
    this._writer = writer;
  }

  private void enqueueTopSearches(GoogleTrendsCsvParser csvParser, Map<String, Integer> queries, Queue<String> queue) throws IOException, ConfigurationException {
    // Add next queries
    List<String> topSearches = csvParser.getSectionAsStringList(SECTION_TOP_SEARCHES_FOR, false, csvParser.getSeparator());
    for (int i = 0; (i < getTopMax()) && (i < topSearches.size()); i++) {
      String col = topSearches.get(i);
      String q = col.split(csvParser.getSeparator())[0];
      if (!queries.containsKey(q)) {
        Logger.getLogger((String) GoogleConfigurator.getConfiguration().getProperty("defaultLoggerPrefix")).log(Level.FINE, "Adding query for: {0}", q);
        queue.add(q);
      }
    }
  }

  /**
   * @return the _client
   */
  public GoogleTrendsClient getClient() {
    return _client;
  }

  /**
   * @param client the _client to set
   */
  public void setClient(GoogleTrendsClient client) {
    this._client = client;
  }

  /**
   * @return the _section
   */
  public String getSection() {
    return _section;
  }

  /**
   * @param section the _section to set
   */
  public void setSection(String section) {
    this._section = section;
  }

  /**
   * @return the queryParams
   */
  public List<NameValuePair> getQueryOpts() {
    return _queryOpts;
  }

  /**
   * @param queryOpts
   */
  public void setQueryOpts(List<NameValuePair> queryOpts) {
    this._queryOpts = queryOpts;
  }

  /**
   *
   * @param regions
   */
  void setRegions(List<Region> regions) {
    _regions = regions;
  }

  private String getNextRegion() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  void setDate(String dateSince) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  void setDateWindow(Integer dateWindow) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }
}
