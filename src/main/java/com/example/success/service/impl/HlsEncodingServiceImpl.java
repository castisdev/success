package com.example.success.service.impl;


import com.example.success.service.HlsEncodingService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@Slf4j
public class HlsEncodingServiceImpl implements HlsEncodingService {





/*    @Override
    public AtemeJob getAtemJobByUuid(String uuid) {
        AtemeJob ret = null;
        if (!atemeJobs.isEmpty()) {
            for (int i = 0; i < atemeJobs.size(); i++) {
                AtemeJob atemeJob = atemeJobs.get(i);
                if (Objects.equals(atemeJob.getUuid(), uuid) && !Strings.isEmpty(atemeJob.getState()) && !Strings.isEmpty(atemeJob.getProgress())) {
                    atemeJobs.set(i, atemeJob);
                    ret = atemeJob;
                }
            }
        }
        return ret;
    }*/

    @Override
    public void encode(String sourceFileName, String m3u8FileResource) throws Exception {
        log.info(String.format("Start HLS encoding ..."));

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
       ;
        String destFolderStr = FilenameUtils.getFullPath(sourceFileName) + FilenameUtils.getBaseName(sourceFileName) ;
        File destFolderFile = new File(destFolderStr);
        FileUtils.copyDirectory(hlsFolderFile, destFolderFile);

        log.info(String.format("Encoded by HLS successfully, hls encoded files in: %s", destFolderStr));
    }

}
