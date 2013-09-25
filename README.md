Java Google Trends Client (unofficial)
======================================

j-google-trends-client is a Java based implementation of Unofficial Google Trends cli based client.

Help
====
    [ elibus@teresa ~/dev/j-google-trends-client/target ] java -jar j-google-trends-client-1.1.1-jar-with-dependencies.jar
    Sep 24, 2013 10:25:34 PM org.freaknet.gtrends.client.CmdLineParser parse
    SEVERE: Missing required options: q, u, p
    usage: gtclient.sh [-C <proxy>] [-d <dir>] [-m <maxRequests>] [-o
           <queryOptions>] [-P <proxy>] -p <password> -q <query> [-s
           <section>] [-S <sleep>] -u <username>
    This is a client for Google Trends. It allows to:
    - Download the entire CSV file (the same file that can be downloaded
    through the browser)
    - Download a single section of the same CSV file
    - Iterate over the hierarchical tree of "Top Searches" and download
    section/CSV file
     -C,--credentials <proxy>           Proxy Credentials in the form
                                        "[DOMAIN/]username:password". DOMAIN
                                        is required only for NTLM
                                        authentication
     -d,--dir <dir>                     Output directory (default: "./out")
     -m,--maxRequests <maxRequests>     Maximum number of requests to perform.
     -o,--queryOptions <queryOptions>   Query options.
     -P,--proxy <proxy>                 Proxy host in the form
                                        "protocol://host:port" (example:
                                        http://proxy.domain.com:8080)
     -p,--password <password>           Password
     -q,--query <query>                 Google query string
     -s,--section <section>             CSV section to retrieve
     -S,--sleep <sleep>                 Sleep in ms between two different
                                        requests (might help in case the QoS
                                        threshold is exceeded - default: 0)
     -u,--username <username>           Username (example: user@google.com)
    EXAMPLE: gtclient.sh -u user@google.com - p passwd -d ./outdir -q "jobs
    -'steve jobs'"

Example
=======
    java -jar j-google-trends-client-1.1.1.jar -u myUser -p myPass -q "jobs -'steve jobs'" -o 'geo=IT-RM'

Download binaries
=================
Binaries can be downloaded here: https://github.com/elibus/j-google-trends-client/tree/master/releases/org/freaknet/gtrends/client/j-google-trends-client/
Jar files with "with-dependencies" suffix can be used straightaway as standalone application. No other dependency is needed as everything is included in the bundle.

LICENSE
=======
j-google-trends-api
Java based implementation of Unofficial Google Trends API
Copyright (C) 2013  Marco Tizzoni

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.

