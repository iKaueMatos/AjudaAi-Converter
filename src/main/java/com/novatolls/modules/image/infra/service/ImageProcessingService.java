package com.novatolls.modules.image.infra.service;

import java.awt.List;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.novatolls.modules.image.application.dto.ImageProcessingDataRequest;
import com.novatolls.modules.image.application.service.IImageProcessingService;
import com.novatolls.modules.image.domain.service.impl.ImageResizerService;
import com.novatolls.modules.image.domain.service.impl.ImageZipService;
import com.novatolls.modules.image.domain.service.impl.TempFileService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ImageProcessingService implements IImageProcessingService {
    private final ImageResizerService imageResizerService;
    private final ImageZipService zipService;
    private final TempFileService tempFileService;

    public ImageProcessingService(ImageResizerService imageResizerService, ImageZipService zipService,
            TempFileService tempFileService) {
        this.imageResizerService = imageResizerService;
        this.zipService = zipService;
        this.tempFileService = tempFileService;
    }

    @Override
    public byte[] processImages(MultipartFile[] files, ImageProcessingDataRequest requestData) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        try (ZipArchiveOutputStream zipOutputStream = new ZipArchiveOutputStream(byteArrayOutputStream)) {
            processEachFile(files, requestData, zipOutputStream);
            zipOutputStream.finish();
        } catch (IOException e) {
            log.error("Erro ao processar as imagens: {}", e.getMessage(), e);
            throw new IOException("Erro ao processar as imagens", e);
        }

        return prepareResponse(byteArrayOutputStream, requestData.getTitle());
    }

    private void processEachFile(
            MultipartFile[] files,
            ImageProcessingDataRequest requestData,
            ZipArchiveOutputStream zipOutputStream) throws IOException {

        java.util.List<String> erroredFiles = new ArrayList<>();

        if (files.length == 1) {
            MultipartFile file = files[0];
            String imageTitle = generateImageTitle(requestData.getTitle(), 0, requestData.getFormat());

            try {
                BufferedImage resizedImage = imageResizerService.resizeImage(
                        file.getInputStream(),
                        requestData.getWidth(), requestData.getHeight(),
                        requestData.getCompression(), requestData.getFormat(),
                        requestData.getConvertTo());

                File tempFile = tempFileService.createTempFile(requestData.getFormat(), resizedImage,
                        requestData.getConvertTo());
                zipService.addToZip(imageTitle, tempFile, zipOutputStream);

                boolean isDeleted = tempFile.delete();
                if (!isDeleted) {
                    log.warn("Não foi possível deletar o arquivo temporário: {}", tempFile.getName());
                }
            } catch (Exception e) {
                erroredFiles.add(file.getOriginalFilename());
                log.error("Erro ao processar a imagem: {}", file.getOriginalFilename(), e);
            }

            return;
        }

        for (int i = 0; i < files.length; i++) {
            MultipartFile file = files[i];
            String imageTitle = generateImageTitle(requestData.getTitle(), i + 1, requestData.getFormat());

            try {
                BufferedImage resizedImage = imageResizerService.resizeImage(
                        file.getInputStream(),
                        requestData.getWidth(), requestData.getHeight(),
                        requestData.getCompression(), requestData.getFormat(),
                        requestData.getConvertTo());

                File tempFile = tempFileService.createTempFile(requestData.getFormat(), resizedImage,
                        requestData.getConvertTo());
                zipService.addToZip(imageTitle, tempFile, zipOutputStream);

                boolean isDeleted = tempFile.delete();
                if (!isDeleted) {
                    log.warn("Não foi possível deletar o arquivo temporário: {}", tempFile.getName());
                }
            } catch (Exception e) {
                erroredFiles.add(file.getOriginalFilename());
                log.error("Erro ao processar a imagem: {}", file.getOriginalFilename(), e);
            }
        }

        if (!erroredFiles.isEmpty()) {
            String erroMessage = "As seguintes imagens não foram processadas corretamente: "
                    + String.join(", ", erroredFiles);
            throw new IOException(erroMessage);
        }
    }

    private String generateImageTitle(String baseTitle, int index, String format) {
        if (index == 0) {
            return baseTitle + "." + format;
        }

        return baseTitle + " - " + index + "." + format;
    }

    private byte[] prepareResponse(ByteArrayOutputStream byteArrayOutputStream, String baseTitle) {
        byte[] zipBytes = byteArrayOutputStream.toByteArray();
        return zipBytes;
    }
}
