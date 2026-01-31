package com.photos.server.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@AllArgsConstructor
public class PhotoRequestBody {
    private String photoName;

    private MultipartFile file;
}
