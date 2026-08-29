package com.company.documentai.infrastructure.service;

import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class DocumentTextExtractorImpl
        implements DocumentTextExtractor {

    //TODO lägg till @NoNull på all agument i alla metoder, kolla  if (content == null || content.length == 0) före anropet hit
    @Override
    public String extract(
            final String fileType,
            final byte[] content
    ) {

        if (content == null || content.length == 0) {
            throw new IllegalArgumentException(
                    "Document content is empty"
            );
        }

        return switch (fileType) {

            case "text/plain" -> extractText(content);

            case "application/pdf" -> extractPdf(content);

            case "application/vnd.openxmlformats-officedocument.wordprocessingml.document" -> extractDocx(content);

            case "application/msword" -> extractDoc(content);

            default -> throw new IllegalArgumentException(
                    "Unsupported document type: " + fileType
            );
        };
    }

    private String extractText(
            final byte[] content
    ) {

        return new String(
                content,
                StandardCharsets.UTF_8
        );
    }

    private String extractPdf(
            final byte[] content
    ) {

        try (var document = Loader.loadPDF(content)) {

            final PDFTextStripper stripper =
                    new PDFTextStripper();

            return stripper.getText(document);

        } catch (IOException e) {

            throw new IllegalStateException(
                    "Could not extract text from PDF",
                    e
            );
        }
    }

    private String extractDocx(
            final byte[] content
    ) {

        try (
                XWPFDocument document =
                        new XWPFDocument(
                                new ByteArrayInputStream(content)
                        );

                XWPFWordExtractor extractor =
                        new XWPFWordExtractor(document)
        ) {

            return extractor.getText();

        } catch (IOException e) {

            throw new IllegalStateException(
                    "Could not extract text from DOCX",
                    e
            );
        }
    }

    private String extractDoc(
            final byte[] content
    ) {

        try (
                HWPFDocument document =
                        new HWPFDocument(
                                new ByteArrayInputStream(content)
                        );

                WordExtractor extractor =
                        new WordExtractor(document)
        ) {

            return extractor.getText();

        } catch (IOException e) {

            throw new IllegalStateException(
                    "Could not extract text from DOC",
                    e
            );
        }
    }
}
