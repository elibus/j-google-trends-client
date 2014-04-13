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

import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

/**
 *
 * @author elibus
 */
public class QueryOptionsBuilder {
  private static final String OPT_GEO = "geo";
  private static final String OPT_DATE = "date";
  
  private final List<NameValuePair> _queryOpts;
  private String _region = null;
  private String _date = null;
  
  public QueryOptionsBuilder(List<NameValuePair> queryOpts){
    this._queryOpts = queryOpts;
  }

  /**
   * @return the region
   */
  public String getRegion() {
    return _region;
  }

  /**
   * @param region the regions to set
   */
  public void setRegion(String region) {
    this._region = region;
  }
  
  public List<NameValuePair> build(){
    if (_region != null){
      BasicNameValuePair geo = new BasicNameValuePair(OPT_GEO, _region);
      _queryOpts.add(geo);
    }
    if (_date != null) {
      BasicNameValuePair date = new BasicNameValuePair(OPT_DATE, _date);
      _queryOpts.add(date);
    }
    
    return _queryOpts;
  }

  /**
   * @return the date
   */
  public String getDate() {
    return _date;
  }

  /**
   * @param date the date to set
   */
  public void setDate(String date) {
    this._date = date;
  }
  
}
