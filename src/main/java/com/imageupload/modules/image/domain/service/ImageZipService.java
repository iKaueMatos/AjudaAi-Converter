package com.imageupload.modules.image.domain.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.springframework.stereotype.Service;

@Service
public class ImageZipService {

    /**
     * Adiciona um arquivo temporário ao arquivo ZIP.
     * 
     * @param fileName Nome do arquivo dentro do ZIP
     * @param tempFile Arquivo temporário a ser adicionado
     * @param zipOutputStream OutputStream do arquivo ZIP
     * @throws IOException Caso ocorra erro na leitura ou escrita dos arquivos
     */
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
            System.err.println("Erro ao adicionar arquivo ao ZIP: " + e.getMessage());
            throw e;
        } finally {
            zipOutputStream.closeArchiveEntry();
        }
    }
}
