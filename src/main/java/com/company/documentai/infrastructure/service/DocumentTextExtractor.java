package com.company.documentai.infrastructure.service;

//TODO behövs interface?
public interface DocumentTextExtractor {

    String extract(
            String fileType,
            byte[] content
    );
}
