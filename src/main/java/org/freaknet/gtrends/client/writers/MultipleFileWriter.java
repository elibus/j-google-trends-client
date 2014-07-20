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
import java.io.FileOutputStream;
import java.io.IOException;
import org.freaknet.gtrends.api.GoogleTrendsRequest;
import org.freaknet.gtrends.client.writers.exceptions.DataWriterException;

/**
 *
 * @author Marco Tizzoni <marco.tizzoni@gmail.com>
 */
public class MultipleFileWriter implements DataWriter {

  private final File outputDir;

  // TODO - comments
  public MultipleFileWriter(String outputDir) throws IOException {
    this.outputDir = new File(outputDir);
  }

  // TODO - comments
  @Override
  public void write(GoogleTrendsRequest r, String content) throws DataWriterException {
    try {
      String fullPath = FullPathBuilder.build(outputDir, r);
      File f = new File(fullPath);
      (new File(f.getParent())).mkdirs();
      FileOutputStream out = new FileOutputStream(f);
      out.write(content.getBytes());
      out.close();
    } catch (IOException ex) {
      throw new DataWriterException(ex);
    }
  }
}
