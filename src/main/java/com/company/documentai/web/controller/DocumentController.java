package com.company.documentai.web.controller;


import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


import java.util.Map;


@RestController
@RequestMapping("/api/v1/documents")
public class DocumentController {


    @PostMapping(
            value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> uploadFile(
            @RequestParam(value = "file", required = true) final MultipartFile file) {


        final String contentType = file.getContentType();
        final String fileName = file.getOriginalFilename();


        if (MediaType.APPLICATION_JSON_VALUE.equals(contentType)) {
            return ResponseEntity.badRequest().build();
        }


        return ResponseEntity.ok(Map.of(
                "fileName", StringUtils.hasText(fileName) ? fileName : "unknown",
                "contentType", StringUtils.hasText(contentType) ? contentType : "unknown"
        ));
    }
}