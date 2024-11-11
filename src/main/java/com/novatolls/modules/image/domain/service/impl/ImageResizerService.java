package com.novatolls.modules.image.domain.service.impl;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;

@Slf4j
@Service
public class ImageResizerService {
    public BufferedImage resizeImage(InputStream inputStream, int width, int height, double compression, String format,
            String convertTo) throws IOException {

        BufferedImage originalImage = ImageIO.read(inputStream);
        if (originalImage == null) {
            throw new IllegalArgumentException(
                    "Não foi possível ler a imagem a partir do InputStream. Verifique se o arquivo é uma imagem válida.");
        }

        BufferedImage resizedImage = Thumbnails.of(originalImage)
                .size(width, height)
                .outputQuality(compression)
                .asBufferedImage();

        if (convertTo != null && !convertTo.equalsIgnoreCase(format)) {
            resizedImage = convertImageFormat(resizedImage, convertTo);
        }

        return resizedImage;
    }

    private BufferedImage convertImageFormat(BufferedImage image, String convertTo) {
        try {
            String imageFormat = convertTo.toUpperCase();
            if (imageFormat.equals("PNG") || imageFormat.equals("JPG") || imageFormat.equals("JPEG") || imageFormat.equals("GIF")) {
                return image;
            }
    
            if (imageFormat.equals("WEBP")) {
                return convertToWebP(image);
            } else {
                throw new UnsupportedOperationException("Formato de conversão não suportado: " + convertTo);
            }
    
        } catch (IOException e) {
            log.error("Erro ao converter a imagem para o formato " + convertTo, e);
            throw new RuntimeException("Erro ao converter a imagem", e);
        } catch (UnsupportedOperationException e) {
            log.error("Formato de conversão não suportado: " + convertTo, e);
            throw e;  
        } catch (Exception e) {
            log.error("Erro inesperado ao converter a imagem", e);
            throw new RuntimeException("Erro inesperado ao converter a imagem", e);
        }
    }

    private BufferedImage convertToWebP(BufferedImage image) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageWriter writer = ImageIO.getImageWritersByFormatName("WEBP").next();
        ImageOutputStream ios = ImageIO.createImageOutputStream(outputStream);
        writer.setOutput(ios);
        writer.write(image);
        ios.close();
        return ImageIO.read(new ByteArrayInputStream(outputStream.toByteArray()));
    }
}
