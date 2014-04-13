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

import org.freaknet.gtrends.client.json.RegionsParser;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import junit.framework.TestCase;
import org.freaknet.gtrends.client.json.Region;

/**
 *
 * @author elibus
 */
public class RegionsParserTest extends TestCase {
  
  private RegionsParser instance = null;
  
  public RegionsParserTest(String testName) {
    super(testName);
  }
  
  @Override
  protected void setUp() throws Exception {
    instance = RegionsParser.getInstance();
    super.setUp();
  }
  
  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }

  /**
   * Test of exists method, of class RegionsParser.
   * @throws java.io.FileNotFoundException
   */
  public void testExists() throws FileNotFoundException {
    System.out.println("exists");
    String id = "US";
    Region result = instance.find(id);
    assertEquals(result.getId(), id);
  }

    /**
   * Test of exists method, of class RegionsParser.
   * @throws java.io.FileNotFoundException
   */
  public void testExistsMustFail() throws FileNotFoundException {
    System.out.println("exists");
    String id = "ZZ";
    Region result = instance.find(id);
    assertNull(result);
  }

}
