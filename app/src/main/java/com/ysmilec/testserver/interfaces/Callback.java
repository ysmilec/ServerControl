package com.ysmilec.testserver.interfaces;

import com.ysmilec.testserver.ssh.ServerManager;

public interface Callback {
    public void onConnect(ServerManager sm);
    public void onDisconnect();
}
