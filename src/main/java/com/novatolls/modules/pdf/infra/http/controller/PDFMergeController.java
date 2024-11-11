package com.novatolls.modules.pdf.infra.http.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.novatolls.modules.pdf.application.dto.PDFMergeRequest;
import com.novatolls.modules.pdf.application.usecases.PDFMergeUseCase;

import jakarta.validation.Valid;

@Controller
public class PDFMergeController {
    private final PDFMergeUseCase pdfMergeUseCase;

    @Autowired
    public PDFMergeController(PDFMergeUseCase pdfMergeUseCase) {
        this.pdfMergeUseCase = pdfMergeUseCase;
    }

    @PostMapping("/merge")
    public ResponseEntity<?> mergePdfs(@Valid @RequestBody PDFMergeRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        try {
            byte[] mergedPdf = pdfMergeUseCase.execute(
                request.getFiles(),              // Arquivos a serem mesclados
                request.getTitle(),              // Título do PDF
                request.getOutputFilename(),     // Nome do arquivo de saída
                request.isAddWatermark(),        // Se deve adicionar marca d'água
                request.getWatermarkText(),      // Texto da marca d'água
                request.getPageRanges()          // Intervalo de páginas
            );
            return ResponseEntity.ok()
                .header("Content-Type", "application/pdf")
                .header("Content-Disposition", "attachment; filename=\"" + request.getOutputFilename() + "\"")
                .body(mergedPdf);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao combinar os PDFs.");
        }
    }
}
