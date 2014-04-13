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

package org.freaknet.gtrends.client.json;

import java.util.LinkedList;

/**
 *
 * @author elibus
 */
public class Region {
  private boolean prime;
  private String id;
  private String name;
  private LinkedList<Region> children;
  private boolean favorite;

  /**
   * @return the prime
   */
  public boolean isPrime() {
    return prime;
  }

  /**
   * @param prime the prime to set
   */
  public void setPrime(boolean prime) {
    this.prime = prime;
  }

  /**
   * @return the id
   */
  public String getId() {
    return id;
  }

  /**
   * @param id the id to set
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * @return the favorite
   */
  public boolean isFavorite() {
    return favorite;
  }

  /**
   * @param favorite the favorite to set
   */
  public void setFavorite(boolean favorite) {
    this.favorite = favorite;
  }

  /**
   * @return the children
   */
  public LinkedList<Region> getChildren() {
    return children;
  }

  /**
   * @param children the children to set
   */
  public void setChildren(LinkedList<Region> children) {
    this.children = children;
  }
  
}
