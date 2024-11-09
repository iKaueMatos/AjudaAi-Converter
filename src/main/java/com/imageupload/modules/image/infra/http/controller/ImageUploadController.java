package com.imageupload.modules.image.infra.http.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.imageupload.modules.image.application.dto.ImageProcessingDataRequest;
import com.imageupload.modules.image.application.useCases.ImageProcessingUseCase;
import com.imageupload.shared.response.ResponseDTO;

import jakarta.validation.Valid;

@Controller
public class ImageUploadController {
    private static final Logger logger = LoggerFactory.getLogger(ImageUploadController.class);

    @Autowired
    private ImageProcessingUseCase imageProcessingUseCase;

    @PostMapping("/upload")
    public ResponseEntity<?> handleFileUpload(
        @RequestParam("files") MultipartFile[] files,
        @Valid @ModelAttribute ImageProcessingDataRequest requestData,
        BindingResult result
    ) throws IOException {
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            result.getFieldErrors().forEach(error -> {
                logger.error("Erro no campo {}: {}", error.getField(), error.getDefaultMessage());
                errors.put(error.getField(), error.getDefaultMessage());
            });
            return ResponseEntity.badRequest().body(new ResponseDTO<>(HttpStatus.BAD_REQUEST, errors, "Erro de validação"));
        }

        if (files.length == 0) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Nenhum arquivo selecionado!");
            return ResponseEntity.badRequest().body(new ResponseDTO<>(HttpStatus.BAD_REQUEST, error, "Nenhum arquivo selecionado"));
        }

        try {
            byte[] resultResponse = imageProcessingUseCase.execute(files, requestData);
            return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"" + requestData.getTitle() + ".zip\"")
                .header("Content-Type", "application/zip")
                .body(resultResponse);
        } catch (Exception e) {
            logger.error("Erro ao processar as imagens: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("message", "Erro ao processar as imagens. Tente novamente.");
            return ResponseEntity.status(500).body(new ResponseDTO<>(HttpStatus.INTERNAL_SERVER_ERROR, error, "Erro interno no servidor"));
        }
    }
}
