package com.photos.server.services.ssh;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.apache.sshd.client.channel.ClientChannel;
import org.apache.sshd.client.channel.ClientChannelEvent;
import org.apache.sshd.client.session.ClientSession;
import org.springframework.stereotype.Service;

@Service
public class SshCommandExecutor {

    private static final int TIMEOUT_SECONDS = 60;
    private final SshSessionManager sshSessionManager;

    public SshCommandExecutor(SshSessionManager sshSessionManager) {
        this.sshSessionManager = sshSessionManager;
    }

    public SshCommandResult execute(String command) throws IOException {
        return execute(command, null);
    }

    public synchronized SshCommandResult execute(String command, byte[] stdin) throws IOException {
        ClientSession session = sshSessionManager.getActiveSession();

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
                ClientChannel channel = session.createExecChannel(command)) {
            channel.setOut(outputStream);
            channel.setErr(errorStream);
            channel.open().verify(TIMEOUT_SECONDS, TimeUnit.SECONDS);

            if (stdin != null && stdin.length > 0) {
                try (OutputStream remoteInput = channel.getInvertedIn()) {
                    remoteInput.write(stdin);
                    remoteInput.flush();
                }
            }

            Set<ClientChannelEvent> events = channel.waitFor(
                    EnumSet.of(ClientChannelEvent.CLOSED, ClientChannelEvent.TIMEOUT),
                    TimeUnit.SECONDS.toMillis(TIMEOUT_SECONDS));

            if (events.contains(ClientChannelEvent.TIMEOUT)) {
                throw new IOException("Timed out while executing command: " + command);
            }

            return new SshCommandResult(
                    outputStream.toByteArray(), errorStream.toString(StandardCharsets.UTF_8), channel.getExitStatus());
        }
    }
}
