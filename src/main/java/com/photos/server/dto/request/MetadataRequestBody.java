package com.photos.server.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetadataRequestBody {
    private String title;
    private String description;
    private String imageViews;
    private Timestamp creationTime;
    private Timestamp photoTakenTime;
    private GeoData geoData;
    private String url;
    private GooglePhotosOrigin googlePhotosOrigin;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Timestamp {
        private String timestamp;
        private String formatted;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GeoData {
        private Double latitude;
        private Double longitude;
        private Double altitude;
        private Double latitudeSpan;
        private Double longitudeSpan;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GooglePhotosOrigin {
        private MobileUpload mobileUpload;

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class MobileUpload {
            private DeviceFolder deviceFolder;
            private String deviceType;

            @Data
            @Builder
            @NoArgsConstructor
            @AllArgsConstructor
            public static class DeviceFolder {
                private String localFolderName;
            }
        }
    }
}
