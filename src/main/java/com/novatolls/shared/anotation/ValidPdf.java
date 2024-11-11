package com.novatolls.shared.anotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.novatolls.modules.pdf.application.validation.PDFFileValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PDFFileValidator.class)
public @interface ValidPdf {
    String message() default "O arquivo deve ser um PDF válido.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
