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
package org.freaknet.gtrends.client.writers;

import java.io.File;
import org.freaknet.gtrends.api.GoogleTrendsRequest;

/**
 * Build the full path to the file.
 *
 * @author Marco Tizzoni <marco.tizzoni@gmail.com>
 */
public class FullPathBuilder {

 /**
  * @param baseDir Where to start writing
  * @param r Google Trend request
  */
  static String build(File baseDir, GoogleTrendsRequest r) {
    String q = r.getParam("q");
    String geo = r.getParam("geo");
    if (geo == null){
      geo = "";
    }
    
    return baseDir + File.pathSeparator + geo + File.pathSeparator + q + ".csv";
  }
  
}
