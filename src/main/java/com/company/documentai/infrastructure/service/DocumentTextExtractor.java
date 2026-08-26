package com.company.documentai.infrastructure.service;

public interface DocumentTextExtractor {

    String extract(
            String fileType,
            byte[] content
    );
}