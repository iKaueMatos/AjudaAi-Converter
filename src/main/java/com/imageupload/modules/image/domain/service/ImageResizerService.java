package com.imageupload.modules.image.domain.service;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import net.coobird.thumbnailator.Thumbnails;

@Service
public class ImageResizerService {
    private static final Logger logger = LoggerFactory.getLogger(ImageResizerService.class);

    /**
     * Redmensiona a imagem.
     * 
     * @param inputStream Imagem original
     * @param width Largura da imagem
     * @param height Altura da imagem
     * @param compression Qualidade da imagem
     * @param format formato da imagem original
     * @param convertTo tipo do formato que sera convertido a imagem caso necessário
     * @throws IOException Caso ocorra erro na leitura ou escrita dos arquivos
     */
    public BufferedImage resizeImage(InputStream inputStream, int width, int height, double compression, String format,
            String convertTo) throws IOException {
        BufferedImage originalImage = ImageIO.read(inputStream);

        if (originalImage == null) {
            throw new IllegalArgumentException(
                    "Não foi possível ler a imagem a partir do InputStream. Verifique se o arquivo é uma imagem válida.");
        }

        logger.info("Imagem original lida com sucesso. Redimensionando para {}x{} com compressão {}.", width, height,
                compression);

        BufferedImage resizedImage = Thumbnails.of(originalImage)
                .size(width, height)
                .outputQuality(compression)
                .asBufferedImage();

        return resizedImage;
    }
}
