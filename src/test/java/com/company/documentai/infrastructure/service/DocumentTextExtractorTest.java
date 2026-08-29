package com.company.documentai.infrastructure.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

//TODO duplicerat test, finns ju även för DocumentTextExtractorImplTest, slå ihop dem så allt testas 1 gång
class DocumentTextExtractorTest {

    private DocumentTextExtractor extractor;

    @BeforeEach
    void setUp() {
        extractor = new DocumentTextExtractorImpl();
    }

    @Test
    void shouldExtractTextFromTxt() {

        final String text = """
                Detta är ett testdokument.

                Dokumentet handlar om AI och RAG.
                """;

        final byte[] content =
                text.getBytes(StandardCharsets.UTF_8);

        final String result =
                extractor.extract(
                        "text/plain",
                        content
                );

        assertThat(result)
                .isEqualTo(text);
    }

    @Test
    void shouldExtractTextFromPdf() throws Exception {

        final byte[] pdf = createPdf(
                "Detta är text från ett PDF-dokument."
        );

        final String result =
                extractor.extract(
                        "application/pdf",
                        pdf
                );

        assertThat(result)
                .contains(
                        "Detta är text från ett PDF-dokument."
                );
    }

    @Test
    void shouldExtractTextFromDocx() throws Exception {

        final byte[] docx = createDocx(
                "Detta är text från ett Word-dokument."
        );

        final String result =
                extractor.extract(
                        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                        docx
                );

        assertThat(result)
                .contains(
                        "Detta är text från ett Word-dokument."
                );
    }

    @Test
    void shouldThrowExceptionForUnsupportedFileType() {

        final byte[] content =
                "test".getBytes(StandardCharsets.UTF_8);

        assertThatThrownBy(
                () -> extractor.extract(
                        "application/json",
                        content
                )
        )
                .isInstanceOf(
                        IllegalArgumentException.class
                )
                .hasMessage(
                        "Unsupported document type: application/json"
                );
    }

    @Test
    void shouldThrowExceptionWhenContentIsEmpty() {

        assertThatThrownBy(
                () -> extractor.extract(
                        "text/plain",
                        new byte[0]
                )
        )
                .isInstanceOf(
                        IllegalArgumentException.class
                )
                .hasMessage(
                        "Document content is empty"
                );
    }

    private byte[] createPdf(
            final String text
    ) throws Exception {

        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream output =
                     new ByteArrayOutputStream()) {

            final PDPage page = new PDPage();

            document.addPage(page);

            try (PDPageContentStream content =
                         new PDPageContentStream(
                                 document,
                                 page
                         )) {

                content.beginText();
                content.setFont(
                        new PDType1Font(Standard14Fonts.FontName.HELVETICA),
                        12
                );
                content.newLineAtOffset(
                        50,
                        700
                );
                content.showText(text);
                content.endText();
            }

            document.save(output);

            return output.toByteArray();
        }
    }

    private byte[] createDocx(
            final String text
    ) throws Exception {

        try (
                XWPFDocument document =
                        new XWPFDocument();

                ByteArrayOutputStream output =
                        new ByteArrayOutputStream()
        ) {

            final XWPFParagraph paragraph =
                    document.createParagraph();

            paragraph
                    .createRun()
                    .setText(text);

            document.write(output);

            return output.toByteArray();
        }
    }
}
