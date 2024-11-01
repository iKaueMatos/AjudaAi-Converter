package com.imageupload.modules.image.infra.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.imageupload.modules.image.application.dto.ImageProcessingDataRequest;
import com.imageupload.modules.image.application.service.IImageProcessingService;
import com.imageupload.modules.image.domain.service.ImageResizerService;
import com.imageupload.modules.image.domain.service.ImageZipService;
import com.imageupload.modules.image.domain.service.TempFileService;

@Service
public class ImageProcessingService implements IImageProcessingService {

    private static final Logger logger = LoggerFactory.getLogger(ImageProcessingService.class);

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
            logger.error("Erro ao processar as imagens: {}", e.getMessage(), e);
            throw new IOException("Erro ao processar as imagens", e);
        }

        return prepareResponse(byteArrayOutputStream, requestData.getTitle());
    }

    private void processEachFile(MultipartFile[] files, ImageProcessingDataRequest requestData,
                                 ZipArchiveOutputStream zipOutputStream) throws IOException {

        for (int i = 0; i < files.length; i++) {
            MultipartFile file = files[i];
            String imageTitle = generateImageTitle(requestData.getTitle(), i + 1, requestData.getFormat());

            BufferedImage resizedImage = imageResizerService.resizeImage(file.getInputStream(),
                    requestData.getWidth(), requestData.getHeight(),
                    requestData.getCompression(), requestData.getFormat(),
                    requestData.getConvertTo());

            File tempFile = tempFileService.createTempFile(requestData.getFormat(), resizedImage, requestData.getConvertTo());
            zipService.addToZip(imageTitle, tempFile, zipOutputStream);

            boolean isDeleted = tempFile.delete();
            if (!isDeleted) {
                logger.warn("Não foi possível deletar o arquivo temporário: {}", tempFile.getName());
            }
        }
    }

    private String generateImageTitle(String baseTitle, int index, String format) {
        return baseTitle + " - " + index + "." + format;
    }

    private byte[] prepareResponse(ByteArrayOutputStream byteArrayOutputStream, String baseTitle) {
        byte[] zipBytes = byteArrayOutputStream.toByteArray();
        return zipBytes;
    }
}
