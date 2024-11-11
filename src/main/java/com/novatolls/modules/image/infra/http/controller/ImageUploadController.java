package com.novatolls.modules.image.infra.http.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.novatolls.modules.image.application.dto.ImageProcessingDataRequest;
import com.novatolls.modules.image.application.usecases.ImageProcessingUseCase;
import com.novatolls.shared.response.ResponseDTO;

import jakarta.validation.Valid;

@Controller
public class ImageUploadController {
    private final ImageProcessingUseCase imageProcessingUseCase;

    @Autowired
    public ImageUploadController(ImageProcessingUseCase imageProcessingUseCase) {
        this.imageProcessingUseCase = imageProcessingUseCase;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> handleFileUpload(
            @RequestParam("files") MultipartFile[] files,
            @Valid @ModelAttribute ImageProcessingDataRequest requestData,
            BindingResult result) throws IOException {
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            result.getFieldErrors().forEach(error -> {
                errors.put(error.getField(), error.getDefaultMessage());
            });
            return ResponseEntity.badRequest()
                    .body(new ResponseDTO<>(HttpStatus.BAD_REQUEST, errors, "Erro de validação"));
        }

        if (files.length == 0) {
            Map<String, String> error = new HashMap<>();
            return ResponseEntity.badRequest()
                    .body(new ResponseDTO<>(HttpStatus.BAD_REQUEST, error, "Nenhum arquivo selecionado"));
        }

        // Filtra os arquivos removendo os de formato webp e svg
        List<MultipartFile> validFiles = Arrays.stream(files)
                .filter(file -> isValidImage(file))
                .collect(Collectors.toList());

        if (validFiles.isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Nenhuma imagem válida foi enviada (webp e svg são proibidos).");
            return ResponseEntity.badRequest()
                    .body(new ResponseDTO<>(HttpStatus.BAD_REQUEST, error, "Formato de imagem inválido"));
        }

        try {
            byte[] resultResponse = imageProcessingUseCase.execute(validFiles.toArray(new MultipartFile[0]),
                    requestData);
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=\"" + requestData.getTitle() + ".zip\"")
                    .header("Content-Type", "application/zip")
                    .body(resultResponse);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Erro ao processar as imagens. Tente novamente.");
            return ResponseEntity.status(500)
                    .body(new ResponseDTO<>(HttpStatus.INTERNAL_SERVER_ERROR, error, "Erro interno no servidor"));
        }
    }

    private boolean isValidImage(MultipartFile file) {
        String fileName = file.getOriginalFilename().toLowerCase();
        return !(fileName.endsWith(".webp") || fileName.endsWith(".svg"));
    }

}
