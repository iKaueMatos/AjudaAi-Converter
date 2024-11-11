package com.novatolls.modules.image.domain.service.impl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Service;

@Service
public class TempFileService {
    public File createTempFile(String format, BufferedImage image, String convertTo) throws IOException {
        String fileExtension = (convertTo != null) ? convertTo.toLowerCase() : format.toLowerCase();
        File tempFile = File.createTempFile("image", "." + fileExtension);

        try (FileOutputStream tempFileOutputStream = new FileOutputStream(tempFile)) {
            ImageIO.write(image, fileExtension, tempFileOutputStream);
        }

        return tempFile;
    }
}
