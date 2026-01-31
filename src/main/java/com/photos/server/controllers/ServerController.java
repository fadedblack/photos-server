package com.photos.server.controllers;

import com.photos.server.dto.Photo;
import com.photos.server.dto.request.MetadataRequestBody;
import com.photos.server.dto.request.PhotoRequestBody;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(("/inkeet/server/photos"))
public class ServerController {

    @PostMapping("/upload-photo")
    public ResponseEntity<String> uploadPhoto(@ModelAttribute @Validated PhotoRequestBody requestBody)
            throws IOException {
        Photo photo = Photo.from(requestBody);
        log.info("Received photo upload request for photo: {}", photo.getPhotoName());
        return ResponseEntity.ok("Photo uploaded successfully for photo: " + requestBody.getPhotoName());
    }

    @PostMapping("/upload-metadata")
    public ResponseEntity<String> uploadMetadata(@RequestBody @Validated MetadataRequestBody request) {
        log.info("Received metadata upload request for photoId: {}", request.getTitle());
        return ResponseEntity.ok("Metadata uploaded successfully for photoId: " + request.getTitle());
    }
}
