package com.novatolls.modules.pdf.application.dto;

import org.springframework.web.multipart.MultipartFile;

import com.novatolls.shared.anotation.ValidPdf;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PDFMergeRequest {
    @NotNull(message = "Os arquivos não podem ser nulos.")
    @ValidPdf(message = "Todos os arquivos devem ser PDFs válidos.")
    @Size(min = 2, message = "É necessário fornecer pelo menos dois arquivos PDF.")
    private MultipartFile[] files;
    @NotNull(message = "O título não pode ser nulo.")
    private String title;
    private String outputFilename;
    private boolean addWatermark = false;
    private String watermarkText;
    private int[] pageRanges;
}

