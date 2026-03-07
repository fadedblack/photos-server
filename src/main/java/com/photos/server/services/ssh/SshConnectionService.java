package com.photos.server.services.ssh;

import java.io.IOException;
import org.springframework.stereotype.Service;

@Service
public class SshConnectionService {

    private final SshSessionManager sshSessionManager;

    public SshConnectionService(SshSessionManager sshSessionManager) {
        this.sshSessionManager = sshSessionManager;
    }

    public void connect() throws IOException {
        sshSessionManager.connect();
    }

    public boolean healthStatus() {
        return sshSessionManager.isConnected();
    }
}
