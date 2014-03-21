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
package org.freaknet.gtrends.client.writers.exceptions;

/**
 *
 * @author Marco Tizzoni <marco.tizzoni@gmail.com>
 */
public class DataWriterException extends Exception {

  /**
   * Creates a new instance of <code>DataWriterException</code> without detail
   * message.
   */
  public DataWriterException() {
  }

  /**
   * Constructs an instance of <code>DataWriterException</code> with the
   * specified detail message.
   *
   * @param msg the detail message.
   */
  public DataWriterException(String msg) {
    super(msg);
  }

  /**
   * Constructs an instance of <code>DataWriterException</code> with the
   * specified exception.
   *
   * @param e the exception.
   */
  public DataWriterException(Exception e) {
    super(e);
  }
}
