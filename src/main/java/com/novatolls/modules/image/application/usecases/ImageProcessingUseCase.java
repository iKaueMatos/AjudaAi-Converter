package com.novatolls.modules.image.application.usecases;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.novatolls.modules.image.application.dto.ImageProcessingDataRequest;
import com.novatolls.modules.image.infra.service.ImageProcessingService;

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