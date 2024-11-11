package com.novatolls.modules.pdf.application.usecases;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;

@Service
public class PDFMergeUseCase {

    public byte[] execute(
        MultipartFile[] files,
        String title,
        String outputFilename,
        boolean addWatermark,
        String watermarkText,
        int[] pageRanges
    ) throws IOException, DocumentException
    {
        Document document = new Document();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, outputStream);
        document.open();

        for (MultipartFile file : files) {
            InputStream inputStream = file.getInputStream();
            PdfReader reader = new PdfReader(inputStream);

            // Adicionando apenas as páginas selecionadas (se pageRanges for fornecido)
            int startPage = (pageRanges != null && pageRanges.length > 0) ? pageRanges[0] : 1;
            int endPage = (pageRanges != null && pageRanges.length > 1) ? pageRanges[1] : reader.getNumberOfPages();

            for (int pageNum = startPage; pageNum <= endPage; pageNum++) {
                PdfImportedPage page = writer.getImportedPage(reader, pageNum);
                document.add(Image.getInstance(page));
                
                if (addWatermark) {
                    Phrase watermark = new Phrase(watermarkText, new Font(Font.FontFamily.HELVETICA, 30));
                    document.add(watermark);
                }
            }
        }

        document.close();
        return outputStream.toByteArray();
    }
}
