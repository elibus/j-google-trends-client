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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.configuration.ConfigurationException;
import org.freaknet.gtrends.api.GoogleConfigurator;
import org.freaknet.gtrends.api.GoogleTrendsRequest;
import org.freaknet.gtrends.api.exceptions.GoogleTrendsRequestException;
import org.freaknet.gtrends.client.exceptions.RegionFinderException;
import org.freaknet.gtrends.client.json.Region;

/**
 *
 * @author elibus
 */
public class GoogleTrendsRequestBuilder {

  private final List<Region> _regions;
  private final String _q;
  private final int _max;
  private String _currentRegionId;
  private String _regionIdPrefix = "";

  Map<String, Integer> _queriesDone;
  Queue<String> _queriesQ;

  public GoogleTrendsRequestBuilder(String q, String region, int max) throws RegionFinderException {
    _queriesDone = new HashMap();
    _queriesQ = new LinkedList<String>();
    _q = q;
    _queriesQ.add(_q);
    _max = max;

    _regions = new LinkedList<Region>();
    if (region != null) {
      Region root = RegionFinder.find(region);
      _regions.add(root);
    } else {
      _regions.add(new Region("", "Worldwide", true));
    }
    _currentRegionId = getCurrentRegionId();
  }

  public GoogleTrendsRequest getNext() throws GoogleTrendsRequestException, ConfigurationException {
//    System.out.println("getNext()");
    while (!_regions.isEmpty()) {
      // Se la coda e' vuota passiamo alla prossima regione
      if (_queriesQ.isEmpty()) {
        nextRegion();
      }
      
//      System.out.println("region size: " + _regions.size());
//      System.out.println("Queue size:" + _queriesQ.size());
      //System.out.println();
      
      String q = _queriesQ.remove();
      // Skip if already done (avoid searching in the list while adding)
      while (_queriesDone.containsKey(q)){
        q = _queriesQ.remove();
      }
      _queriesDone.put(q, 0);
      GoogleTrendsRequest request = new GoogleTrendsRequest(q);
      if (_currentRegionId != null) {
        request.setGeo(_currentRegionId);
      }

      // Se size() > _max abbiamo raggiunto il max numero di richieste per questa region
      if (_queriesDone.size() >= _max) {
        Logger.getLogger((String) GoogleConfigurator.getConfiguration().getProperty("defaultLoggerPrefix")).log(Level.INFO, "Builder: max number of request per query reached! Starting over with the next.");
        nextRegion();
      }

      return request;
    }

    return null;
  }

  private void nextRegion() {
    Region r = _regions.remove(0);
    _regionIdPrefix += r.getId() + "-";
    if (r.getChildren() != null)
      _regions.addAll(r.getChildren()); 
    _currentRegionId = getCurrentRegionId();
    _queriesDone.clear();
    _queriesQ.clear();
    _queriesQ.add(_q);
  }

  public void enqueue(List<String> queries) throws IOException, ConfigurationException {
    // Add next queries
    for (String q : queries) {
      if (!_queriesDone.containsKey(q)) {
        Logger.getLogger((String) GoogleConfigurator.getConfiguration().getProperty("defaultLoggerPrefix")).log(Level.INFO, "Adding query for: {0}", q);
        _queriesQ.add(q);
      }
    }
  }

  private String getCurrentRegionId() {
    return _regionIdPrefix + _regions.get(0).getId();
  }
}
