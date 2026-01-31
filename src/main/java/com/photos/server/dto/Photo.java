package com.photos.server.dto;

import com.photos.server.dto.request.PhotoRequestBody;
import java.io.IOException;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Photo {
    private String photoName;

    private byte[] imageData;

    public static Photo from(PhotoRequestBody requestBody) throws IOException {
        return Photo.builder()
                .photoName(requestBody.getPhotoName())
                .imageData(requestBody.getFile().getBytes())
                .build();
    }
}
