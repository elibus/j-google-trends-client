/**
 * Copyright (C) 2013 Marco Tizzoni <marco.tizzoni@gmail.com>
 *
 * This file is part of j-google-trends-client
 *
 * j-google-trends-client is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * j-google-trends-client is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * j-google-trends-client. If not, see <http://www.gnu.org/licenses/>.
 */
package org.freaknet.gtrends.client;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.configuration.ConfigurationException;
import org.freaknet.gtrends.api.GoogleConfigurator;
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
 * downloading the relative CSV file or _section. Then Look for the first
 * TopFirst of "Top searches" and download them all until requestsLimit is
 * reached.
 *
 * @author Marco Tizzoni <marco.tizzoni@gmail.com>
 */
public class HierarchicalDownloader {

  private GoogleTrendsClient _client;
  private DataWriter _writer;

  private final GoogleTrendsRequestBuilder _builder;

  public HierarchicalDownloader(GoogleTrendsClient client, GoogleTrendsRequestBuilder builder, DataWriter writer) {
    _client = client;
    _writer = writer;
    _builder = builder;
  }

  /**
   * Starts the download.
   *
   * @param ms ms time in ms
   * @throws HierarchicalDownloaderException
   */
  public void start(int ms) throws HierarchicalDownloaderException {
    try {
      GoogleTrendsRequest request;
      while ((request = _builder.getNext()) != null) {

        String csvContent = _client.execute(request);
        Logger.getLogger(GoogleConfigurator.getLoggerPrefix()).log(Level.INFO, "Sleeping for {0} seconds", randomSleep(ms) / 1000);

        if (csvContent == null) {
          throw new HierarchicalDownloaderException("CSV is empty. It looks like something went wrong! :/");
        } 
        
        _writer.write(request, csvContent);

        GoogleTrendsCsvParser csvParser = new GoogleTrendsCsvParser(csvContent);
        List<String> topSearches = csvParser.getTopSearches(10);
        _builder.enqueue(topSearches);
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

  private int randomSleep(int ms) throws InterruptedException {
    int random = ms + (int) ( ( Math.random() * 10000));
    Thread.sleep( random );
    return random;
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
    _writer = writer;
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
    _client = client;
  }

}
