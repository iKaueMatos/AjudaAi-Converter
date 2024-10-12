package com.imageupload.image.application.useCases;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.imageupload.image.application.dto.ImageProcessingDataRequest;
import com.imageupload.image.infra.service.ImageProcessingService;

@Service
public class ImageProcessingUseCase {
    private final ImageProcessingService imageProcessingService;

    public ImageProcessingUseCase(ImageProcessingService imageProcessingService) {
        this.imageProcessingService = imageProcessingService;
    }

    public byte[] execute(
        MultipartFile[] files,
        ImageProcessingDataRequest imageProcessData
    ) throws IOException {
        return imageProcessingService.processImages(files, imageProcessData);
    }
}
