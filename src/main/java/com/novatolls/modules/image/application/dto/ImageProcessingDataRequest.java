package com.novatolls.modules.image.application.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ImageProcessingDataRequest {

    @NotEmpty(message = "O título não pode ser vazio.")
    private String title;

    @Min(value = 1, message = "A largura deve ser maior que zero.")
    @Max(value = 10000, message = "A largura máxima permitida é 10000.")
    private int width;

    @Min(value = 1, message = "A altura deve ser maior que zero.")
    @Max(value = 10000, message = "A altura máxima permitida é 10000.")
    private int height;

    @NotEmpty(message = "O formato da imagem não pode ser vazio.")
    private String format;

    @Min(value = 0, message = "A compressão deve ser maior ou igual a zero.")
    @Max(value = 1, message = "A compressão deve ser menor ou igual a 1.")
    private double compression;

    private String convertTo;
}
