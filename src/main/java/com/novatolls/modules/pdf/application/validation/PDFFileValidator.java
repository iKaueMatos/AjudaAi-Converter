package com.novatolls.modules.pdf.application.validation;

import org.springframework.web.multipart.MultipartFile;

import com.novatolls.shared.anotation.ValidPdf;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PDFFileValidator implements ConstraintValidator<ValidPdf, MultipartFile> {
    @Override
    public void initialize(ValidPdf constraintAnnotation) {}

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null || file.isEmpty()) {
            return false;
        }

        try {
            String contentType = file.getContentType();
            return contentType != null && contentType.equals("application/pdf");
        } catch (Exception e) {
            return false;
        }
    }
}
