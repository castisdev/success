package com.example.success.service.impl;


import com.example.success.service.HlsEncodingService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@Slf4j
public class HlsEncodingServiceImpl implements HlsEncodingService {

    @Override
    public void encode(String sourceFileName, String m3u8FileResource) throws Exception {
        log.info("Start HLS encoding ...");

        // load a .m3u8 file and folder and rename it follow
        // there is only one .m3u8 file and a folder with the same .m3u8 file name
        // check exist files in hlsResource (as the path)

        // copy from resource to dest:
        // copy File:
        File m3u8SourceFile = new File(m3u8FileResource); // fake encoded file
        String destPathStr = FilenameUtils.removeExtension(sourceFileName) + ".m3u8";
        File destFile = new File(destPathStr);
        FileUtils.copyFile(m3u8SourceFile, destFile);

        // copy directory:
        String hlsFolderStr = FilenameUtils.getFullPath(m3u8FileResource);
        File hlsFolderFile = new File(hlsFolderStr);
        if (!hlsFolderFile.exists()) {
            throw new Exception(String.format("Not exist HLS folder resource: %s, need to config folder resource to %s", hlsFolderStr, hlsFolderStr));
        }

        String destFolderStr = FilenameUtils.getFullPath(sourceFileName) + FilenameUtils.getBaseName(sourceFileName) ;
        File destFolderFile = new File(destFolderStr);
        FileUtils.copyDirectory(hlsFolderFile, destFolderFile);

        log.info(String.format("Encoded by HLS successfully, hls encoded files in: %s", destFolderStr));
    }

}
