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
package org.freaknet.gtrends.client.json;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;

/**
 *
 * @author elibus
 */
public class RegionParser {
  
  private static final String SPACE_INDENT = "  ";
  private final static String JSON_FILE = "regions.json";
  private static Region _r;
  private static RegionParser _instance = null;

  private RegionParser() {
  }

  public static RegionParser getInstance() throws FileNotFoundException {
    if (_instance == null) {
      _instance = new RegionParser();
      InputStream in = _instance.getClass().getClassLoader().getResourceAsStream(JSON_FILE);
      _r = new Gson().fromJson(new BufferedReader(new InputStreamReader(in)), Region.class);
    }

    return _instance;
  }

  ;

  /**
   * @return the _r
   */
  public Region getR() {
    return _r;
  }

  /**
   * @param r the _r to set
   */
  public void setR(Region r) {
    _r = r;
  }

  public void printAll() {
    if (_r.isPrime()) {
      LinkedList<Region> allRegions = _r.getChildren();
      System.out.println(_r.getId() + ":" + _r.getName());
      _printAll(allRegions, 1);
    }
  }

  private void _printAll(LinkedList<Region> allRegions, int l) {
    if (allRegions == null) {
      return;
    }
    if (allRegions.size() == 0) {
      return;
    }

    Region e = allRegions.removeFirst();

    for (int i = 0; i < l; i++) {
      System.out.print(SPACE_INDENT);
    }
    System.out.println(SPACE_INDENT + "" + e.getId() + ":" + e.getName());

    if (e.isPrime()) {
      _printAll(e.getChildren(), l + 1);
    }
    _printAll(allRegions, l);
  }

  public Region childrenById(String id) {
    if (_r.isPrime()) {
      LinkedList<Region> allRegions = _r.getChildren();
      return _childrenById(allRegions, id);
    }
    return null;
  }

  private Region _childrenById(LinkedList<Region> allRegions, String id) {
    Region r = allRegions.removeFirst();
    if (id.equalsIgnoreCase(r.getId())) {
      return r;
    }

    if (r.isPrime()) {
      LinkedList<Region> children = r.getChildren();
      if (children != null) {
        allRegions.addAll(children);
      }
    }

    if (allRegions.isEmpty()) {
      return null;
    } else {
      return _childrenById(allRegions, id);
    }
  }
}
