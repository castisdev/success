package com.example.success.service.impl;


import com.example.success.service.DashEncodingService;
import com.example.success.utils.AtemeFilenameUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@Slf4j
public class DashEncodingServiceImpl implements DashEncodingService {




    public static final String[] EXT_LIST = {"mpd", "mp4"};
    public static final String EXT_MPD = "mpd";

    @Override
    public void encode(String sourceFileName, String resource) throws Exception {

        log.info(String.format("Start DASH encoding {%s}...", FilenameUtils.getName(sourceFileName)));

        String encodedFileName = FilenameUtils.getBaseName(sourceFileName);

        // 1: get .mpd file name
        File resourceFile = new File(resource);
        if (!resourceFile.exists()) {
            throw new Exception(String.format("Resource file: %s is not exist", resource));
        }
        String destDir = FilenameUtils.getFullPath(sourceFileName) + encodedFileName + File.separator;
        File destDirFile = new File(destDir);
        if (!destDirFile.exists()) {
            destDirFile.mkdirs();
        }

        // 2. copy resource dir to dest dir
        FileUtils.copyDirectory(resourceFile, destDirFile);
        log.info(String.format("Copy Directory from %s to %s successfully" , resource, destDir));

        // 3 rename all files in destination dir
        // 3.1. get name (oldFileName) of encoded response folder (destDir)

        File[] fileList = destDirFile.listFiles();
        String ext = "";
        String oldFileName = "";
        if (fileList.length != 0) {
            for (File file : fileList) {
                ext = FilenameUtils.getExtension(file.getName());
                if (ext.contains(EXT_MPD)) {
                    oldFileName = FilenameUtils.getBaseName(file.getName());
                    break;
                }
            }
        } else {
            throw new Exception(String.format("None of .mpd file from: %s", destDir));
        }

        // rename
        String newDir = "";
        String newFileName = "";
        for (File file : fileList) {
             if (file.isFile()) {
                 String absolutePath = file.getAbsolutePath();
                  newFileName = FilenameUtils.getBaseName(sourceFileName);
                 if (absolutePath.contains(oldFileName)) {
                     newDir = absolutePath.replace(oldFileName, newFileName);
                     file.renameTo(new File(newDir));
                 }
             }
         }

        // replace all old  file names  to new file names in content mpd file: resource 29815 -> new filename: 330345_4BC
        log.info(String.format("DASH encoded folder here: %s", destDir));
        AtemeFilenameUtils.modifyFile(destDir + File.separator + newFileName + ".mpd", oldFileName, newFileName);

        log.info(String.format("DASH encoding successfully!, dash file is in {%s}", destDir));
    }
}
