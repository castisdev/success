package com.example.success.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@Slf4j
public class HybridEncodingServiceImpl implements HybridEncodingService{
    @Override
    public void encode(String sourceFileName, String tsResource) {
        log.info("Start encoding HYBRID ...");
        File tsSourceFile = new File(tsResource); // fake encoded file
        String destPathStr = FilenameUtils.removeExtension(sourceFileName) + ".ts";
        File destFile = new File(destPathStr);
        try {
            FileUtils.copyFile(tsSourceFile, destFile);
        } catch (Exception e) {
            log.error(String.format("[HYBRID encode error] copy file from (%s) to (%s)", tsResource, destPathStr));
    }
        log.info(String.format("Encoded by HYBRID successfully, hybrid encoded files (.ts file) in: %s", destPathStr));

    }
}
