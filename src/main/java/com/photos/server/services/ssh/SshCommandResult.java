package com.photos.server.services.ssh;

public record SshCommandResult(byte[] stdout, String stderr, Integer exitStatus) {}
