package com.novatolls.modules.image.domain.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.springframework.stereotype.Service;

@Service
public class ImageZipService {
    public void addToZip(String fileName, File tempFile, ZipArchiveOutputStream zipOutputStream) throws IOException {
        ZipArchiveEntry zipEntry = new ZipArchiveEntry(fileName);
        zipOutputStream.putArchiveEntry(zipEntry);

        try (FileInputStream tempFileInputStream = new FileInputStream(tempFile)) {
            byte[] buffer = new byte[8192];
            int len;
            while ((len = tempFileInputStream.read(buffer)) > 0) {
                zipOutputStream.write(buffer, 0, len);
            }
        } catch (IOException e) {
            throw e;
        } finally {
            zipOutputStream.closeArchiveEntry();
        }
    }
}
