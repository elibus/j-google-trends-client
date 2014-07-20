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
package org.freaknet.gtrends.client;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.freaknet.gtrends.api.GoogleTrendsClient;
import org.freaknet.gtrends.client.exceptions.CmdLineParserException;
import org.freaknet.gtrends.client.exceptions.GoogleTrendsClientRunException;
import org.freaknet.gtrends.client.exceptions.HierarchicalDownloaderException;
import org.freaknet.gtrends.client.exceptions.RegionFinderException;
import org.freaknet.gtrends.client.json.RegionParser;
import org.freaknet.gtrends.client.writers.MultipleFileWriter;

/**
 *
 * @author Marco Tizzoni <marco.tizzoni@gmail.com>
 */
public class App {
    public static void main(String[] args) throws GoogleTrendsClientRunException, HierarchicalDownloaderException, IOException, FileNotFoundException, CmdLineParserException, RegionFinderException {
    CmdLineParser cmdLine = new CmdLineParser().parse(args);
    
    GoogleTrendsClient client = GoogleTrendsClientFactory.buildClient(cmdLine);
    MultipleFileWriter writer = new MultipleFileWriter(cmdLine.getOutputDir());
    //writer.setSectionFilter(cmdLine.getSection());
    
    GoogleTrendsRequestBuilder b = new GoogleTrendsRequestBuilder(cmdLine.getQuery(), cmdLine.getRegions(), cmdLine.getmaxRequests());

    HierarchicalDownloader csvDownloader = new HierarchicalDownloader(client, b, writer);
    csvDownloader.start(1000);
    
    //csvDownloader.setSection(cmdLine.getSection());
    //csvDownloader.setRegions(cmdLine.getRegions());
    //csvDownloader.setDate(cmdLine.getDateSince());
    //csvDownloader.setDateWindow(cmdLine.getDateWindow());
  }
}
