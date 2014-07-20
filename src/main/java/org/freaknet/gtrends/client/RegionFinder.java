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

import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.freaknet.gtrends.client.exceptions.RegionFinderException;
import org.freaknet.gtrends.client.json.Region;
import org.freaknet.gtrends.client.json.RegionParser;

/**
 *
 * @author elibus
 */
public class RegionFinder {

  private RegionFinder() {
  }

  public static Region find(String region) throws RegionFinderException {
    try {
      RegionParser p = RegionParser.getInstance();
      Region r = p.childrenById(region);
      if (r != null) {
        return r;
      } else {
        throw new RegionFinderException("Region with id " + region + " is not available!");

      }
    } catch (FileNotFoundException ex) {
      Logger.getLogger(RegionFinder.class.getName()).log(Level.SEVERE, null, ex);
    }
    return null;
  }
}
