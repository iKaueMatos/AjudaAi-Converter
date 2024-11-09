package com.imageupload.modules.image.domain.service;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import org.springframework.stereotype.Service;
import net.coobird.thumbnailator.Thumbnails;

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

        return resizedImage;
    }
}
