/**
 * Copyright 2014 Cisco Systems, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cisco.oss.foundation.directory.connect;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * The remote Directory Servers.
 *
 * It used to save all Directory Server address in the remote cluster.
 * It supply getNextDirectoryServer() method to get next Directory Server.
 *
 *
 */
public class DirectoryServers {

    /**
     * The server list.
     */
    private List<InetSocketAddress> servers ;

    /**
     * The server index.
     */
    private int index = -1;

    /**
     * Constructor.
     */
    public DirectoryServers(){
        servers = new ArrayList<InetSocketAddress>();
        servers.add(InetSocketAddress.createUnresolved("vcsdirsvc", 2013));
    }

    /**
     * Constructor.
     *
     * @param servers
     *         the Server list.
     */
    public DirectoryServers(List<String> servers){
        parseServers(servers);
        if(servers.size() == 0){
            throw new IllegalArgumentException("Servers list is invalid.");
        }
    }

    /**
     * Set the Servers.
     *
     * @param servers
     *         the server list.
     */
    public void setServers(List<String> servers){

        parseServers(servers);
        if(servers.size() == 0){
            throw new IllegalArgumentException("Servers list is invalid.");
        }
    }

    /**
     * Get the next directory server.
     *
     * @return
     *         the Directory Server InetSocketAddress.
     */
    public InetSocketAddress getNextDirectoryServer(){

        index = index + 1;
        if(index == servers.size()){
            index = 0;
        }
        InetSocketAddress unresolved = servers.get(index);

//        List<InetSocketAddress> resolved = null;
//        try {
//            InetAddress[] addresses = InetAddress.getAllByName(unresolved.getHostName());
//            for(InetAddress address : addresses){
//                if(resolved == null){
//                    resolved = new ArrayList<InetSocketAddress>();
//                }
//                resolved.add(new InetSocketAddress(address.getHostAddress(), unresolved.getPort()));
//            }
//        } catch (UnknownHostException e) {
//            LOGGER.error("Resolve the Directory Server failed, host=" + unresolved.getAddress().toString() + " - " + e.getMessage());
//        }
//
//        if(resolved == null){
//            return Collections.emptyList();
//        }
        return new InetSocketAddress(unresolved.getHostName(), unresolved.getPort());
    }

    /**
     * Parser the server list.
     *
     * @param servers
     *         the server list.
     */
    private void parseServers(List<String> servers){
        if(servers != null && servers.size() > 0){
            this.servers = new ArrayList<InetSocketAddress>();
            for(String server : servers){
                String[] ss = server.split(":");
                if(ss.length == 2){
                    String host = ss[0];
                    int port = 0;
                    try{
                        port = Integer.valueOf(ss[1]);
                    } catch(NumberFormatException e){
                        // do nothing.
                    }
                    if(validateServer(host, port)){
                        this.servers.add(InetSocketAddress.createUnresolved(host, port));
                    }
                }
            }
        }
    }

    /**
     * Validate the host and port.
     *
     * @param host
     *         the host string.
     * @param port
     *         the port number.
     * @return
     *         true for success.
     */
    private boolean validateServer(String host, int port){
        if(host==null || host.isEmpty()){
            return false;
        }
        if(port <=0 || port > 65535){
            return false;
        }
        return true;
    }

}
