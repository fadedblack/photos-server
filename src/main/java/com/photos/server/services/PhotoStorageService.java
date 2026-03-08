package com.photos.server.services;

import com.photos.server.dto.Photo;
import com.photos.server.services.ssh.SshCommandExecutor;
import com.photos.server.services.ssh.SshCommandResult;
import java.io.IOException;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PhotoStorageService {

    private static final Pattern SAFE_PHOTO_NAME = Pattern.compile("[A-Za-z0-9._-]+");
    private final SshCommandExecutor sshCommandExecutor;

    public PhotoStorageService(SshCommandExecutor sshCommandExecutor) {
        this.sshCommandExecutor = sshCommandExecutor;
    }

    public void uploadPhoto(Photo photo) throws IOException {
        if (photo == null || photo.getImageData() == null) {
            throw new IOException("Photo payload is empty");
        }

        String photoName = sanitizePhotoName(photo.getPhotoName());

        if (doesPhotoExist(photoName)) {
            log.info("Photo '{}' already exists. Skipping upload.", photoName);
            return;
        }

        String command = "mkdir -p ~/server-storage && cat > ~/server-storage/" + photoName;
        SshCommandResult result = sshCommandExecutor.execute(command, photo.getImageData());
        if ((result.exitStatus() != null && result.exitStatus() != 0)
                || !result.stderr().isBlank()) {
            throw new IOException("Failed to upload photo '" + photoName + "': "
                    + result.stderr().trim());
        }
    }

    private boolean doesPhotoExist(String photoName) throws IOException {
        String command = " find ./ -type f -name '" + photoName + "'";
        SshCommandResult result = sshCommandExecutor.execute(command);

        return result.stdout().length != 0;
    }

    public byte[] viewPhoto(String photoId) throws IOException {
        String safePhotoId = sanitizePhotoName(photoId);
        String command = "cat ~/server-storage/" + safePhotoId;
        SshCommandResult result = sshCommandExecutor.execute(command, null);
        if ((result.exitStatus() != null && result.exitStatus() != 0)
                || !result.stderr().isBlank()) {
            throw new IOException("Failed to read photo '" + safePhotoId + "': "
                    + result.stderr().trim());
        }
        return result.stdout();
    }

    private String sanitizePhotoName(String photoName) throws IOException {
        if (photoName == null || photoName.isBlank()) {
            throw new IOException("Photo name is required");
        }
        if (!SAFE_PHOTO_NAME.matcher(photoName).matches()) {
            throw new IOException("Invalid photo name. Allowed chars: letters, numbers, dot, underscore, hyphen.");
        }
        return photoName;
    }
}
