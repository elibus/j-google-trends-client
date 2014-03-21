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
import org.freaknet.gtrends.client.writers.exceptions.DataWriterException;

/**
 *
 * @author Marco Tizzoni <marco.tizzoni@gmail.com>
 */
public class MultipleFileWriter implements DataWriter {

  private final File outputDir;
  private String ext = "csv";

  public MultipleFileWriter(String outputDir) throws IOException {
    this.outputDir = new File(outputDir);
    if (!this.outputDir.canWrite()) {
      throw new IOException("Directory: \"" + outputDir + "\" is not writable!");
    }
  }

  public MultipleFileWriter(String outputDir, String ext) throws IOException {
    this.ext = ext;
    this.outputDir = new File(outputDir);
    if (!this.outputDir.canWrite()) {
      throw new IOException("Directory: \"" + outputDir + "\" is not writable!");
    }
  }

  @Override
  public void write(String name, String text) throws DataWriterException {
    try {
      byte[] toWrite = text.getBytes();
      FileOutputStream out = new FileOutputStream(outputDir + File.separator + name + "." + ext);
      out.write(toWrite);
      out.close();
    } catch (IOException ex) {
      throw new DataWriterException(ex);
    }
  }
}
