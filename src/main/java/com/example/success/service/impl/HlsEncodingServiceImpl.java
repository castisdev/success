package com.example.success.service.impl;


import com.example.success.service.HlsEncodingService;
import com.example.success.utils.AtemeFilenameUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;

@Service
@Slf4j
public class HlsEncodingServiceImpl implements HlsEncodingService {

    @Override
    public void encode(String sourceFileName, String m3u8FileRootResource) throws Exception {
        log.info("Start HLS encoding ...");

        // load a .m3u8 file and folder and rename it follow
        // there is only one .m3u8 file and a folder with the same .m3u8 file name
        // check exist files in hlsResource (as the path)

        // copy from resource to dest:
        // copy File:
        File m3u8SourceRootFile = new File(m3u8FileRootResource);
        String newFileName = FilenameUtils.getBaseName(sourceFileName); // same folder name

        // fake encoded file
        String destPathStr = FilenameUtils.removeExtension(sourceFileName) + ".m3u8";
        File destFile = new File(destPathStr);
        FileUtils.copyFile(m3u8SourceRootFile, destFile);


        // copy directory:
        String hlsFolderStr = FilenameUtils.removeExtension(m3u8FileRootResource);
        File hlsFolderFile = new File(hlsFolderStr);
        if (!hlsFolderFile.exists()) {
            throw new Exception(String.format("Not exist HLS folder resource: %s, need to config folder resource to %s", hlsFolderStr, hlsFolderStr));

        }


        String destFolderStr = FilenameUtils.getFullPath(sourceFileName) + newFileName;
        File destFolderFile = new File(destFolderStr);
        AtemeFilenameUtils.copyDirectory(hlsFolderFile, destFolderFile);



        ///////////////////////////
        // replace all folder names and its files
        File fileDir = new File(destFolderStr);
        if (!fileDir.isDirectory()) {
            log.info("Encoded HLS folder %s doesn't not exist", fileDir.getPath());
            return;
        }

        String[] hlsEncodedFolderList = fileDir.list(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name) {
                return new File(current, name).isDirectory();
            }
        });
        if (hlsEncodedFolderList.length == 0) {
            return;
        }
        log.info(String.format("HLS encoded folders in (%s) >> %s", destFolderStr, Arrays.toString(hlsEncodedFolderList)));

        String oldM3u8RootBaseName = FilenameUtils.getBaseName(m3u8SourceRootFile.getName());
        for (String m3u8Folder : hlsEncodedFolderList) {
            String oldSubFolderDir = destFolderStr + File.separator + m3u8Folder; // current <=> old name
            // rename folder:
            String newFolderName = m3u8Folder.replaceAll(oldM3u8RootBaseName, newFileName); // 102285_7_1.2M -> 331078_4BC_1.2M
            String newFolderDir = destFolderStr + File.separator + newFolderName;
            AtemeFilenameUtils.renameFolder(oldSubFolderDir, newFolderDir);

            File newFolderDirFile = new File(newFolderDir); // new folder dir
            if (!newFolderDirFile.isDirectory()) {
                throw new Exception(String.format("HLS encoded folder (%s) doesn't exists" +
                        "\n rename folder from (%s) to (%s) failed", newFolderDir, m3u8Folder, newFolderName));
            }

            // rename files in renamed folder
            // get File list from new folder to rename:
            String oldFileName = FilenameUtils.getBaseName(m3u8SourceRootFile.getName());
            File[] originFileList = newFolderDirFile.listFiles();
            AtemeFilenameUtils.renameAllFilesInFolder(originFileList, oldFileName, newFileName);
        }

        log.info(String.format("Encoded by HLS successfully, hls encoded files in: %s", destFolderStr));
    }

}
