package com.cisco.oss.foundation.directory.impl;

/**
 * Created by alex on 3/25/15.
 */
public class HeartbeatRegistrationManagerImpl extends RegistrationManagerImpl {
    public HeartbeatRegistrationManagerImpl(HeartbeatDirectoryRegistrationService service){
        super(service);
        service.start();
    }
}
