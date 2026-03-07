package com.photos.server.services.ssh;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.session.ClientSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SshSessionManager {

    private static final int TIMEOUT_SECONDS = 60;

    private SshClient sshClient;
    private ClientSession session;

    @Value("${ssh.username}")
    private String username;

    @Value("${ssh.port}")
    private int port;

    @Value("${ssh.password}")
    private String password;

    @Value("${ssh.host}")
    private String host;

    public synchronized void connect() throws IOException {
        if (isConnected()) {
            log.info("SSH session already active for host: {}", host);
            return;
        }

        closeConnection();
        sshClient = SshClient.setUpDefaultClient();
        sshClient.start();
        try {
            session = sshClient.connect(username, host, port).verify(TIMEOUT_SECONDS, TimeUnit.SECONDS).getSession();
            session.addPasswordIdentity(password);
            session.auth().verify(TIMEOUT_SECONDS, TimeUnit.SECONDS);
            log.info("SSH connection established successfully to host: {}", host);
        } catch (IOException e) {
            closeConnection();
            log.error("SSH connection failed to host: {}", host, e);
            throw e;
        }
    }

    public synchronized ClientSession getActiveSession() throws IOException {
        if (!isConnected()) {
            connect();
        }
        return session;
    }

    public synchronized boolean isConnected() {
        return session != null && session.isOpen() && sshClient != null && sshClient.isStarted();
    }

    @PreDestroy
    public synchronized void closeConnection() {
        if (session != null) {
            try {
                session.close(false);
            } catch (RuntimeException e) {
                log.warn("Error while closing SSH session", e);
            } finally {
                session = null;
            }
        }

        if (sshClient != null) {
            sshClient.stop();
            sshClient = null;
        }
    }
}
