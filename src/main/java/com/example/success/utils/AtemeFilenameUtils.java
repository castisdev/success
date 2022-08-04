package com.example.success.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.*;

@Slf4j
public class AtemeFilenameUtils {
    public static void copyDirectory(File srcDir, File destDir)
            throws IOException {
        String srcPath = srcDir.getPath();
        String destPath = destDir.getPath();
        if (StringUtil.isBlank(srcPath) || StringUtil.equals(srcPath, "/")
                || StringUtil.isBlank(destPath)
                || StringUtil.equals(destPath, "/")) {
            String errorMsg = String
                    .format("copyDirectory Fail, Dir Protection Error(srcDir=%s, destDir=%s)",
                            srcPath, destPath);
            log.error(errorMsg);
            throw new IOException(errorMsg);
        }

        FileUtils.copyDirectory(srcDir, destDir);
    }

    public static void modifyFile(String filePath, String oldString, String newString) {
        File fileToBeModified = new File(filePath);
        String oldContent = "";
        BufferedReader reader = null;
        FileWriter writer = null;

        try{
            reader = new BufferedReader(new FileReader(fileToBeModified));
            //Reading all the lines of input text file into oldContent
            String line = reader.readLine();
            while (line != null) {
                oldContent = oldContent + line + System.lineSeparator();
                line = reader.readLine();
            }

            //Replacing oldString with newString in the oldContent
            String newContent = oldContent.replaceAll(oldString, newString);

            //Rewriting the input text file with newContent
            writer = new FileWriter(fileToBeModified);
            writer.write(newContent);
        } catch (IOException e) {
            e.printStackTrace();

        } finally{
            try{
                //Closing the resources
                reader.close();
                writer.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public static void renameFile(String oldFilePath, String newFilePath) {

        File sourceFile = new File(oldFilePath);
        File destFile = new File(newFilePath);

        if (sourceFile.renameTo(destFile)) {
            log.info(String.format("File renamed successfully: (%s -> %s)", oldFilePath, newFilePath));
        } else {
            log.error(String.format("Fail to rename file: (%s -> %s)", oldFilePath, newFilePath));
        }
    }

    public static void renameFolder(String oldFolderDir, String newFolderDir) {
        File sourceFile = new File(oldFolderDir);
        File destFile = new File(newFolderDir);

        if (sourceFile.renameTo(destFile)) {
            log.info(String.format("Directory renamed successfully: (%s -> %s)", oldFolderDir, newFolderDir));
        } else {
            log.error(String.format("Fail to rename Directory: (%s -> %s)", oldFolderDir, newFolderDir));
        }
    }

     public static void renameAllFilesInFolder(File[] fileList, String oldFileName, String newFileName) {
        String newDir = "";
         for (File file : fileList) {
             if (file.isFile()) {
                 String absolutePath = file.getAbsolutePath();
                 if (absolutePath.contains(oldFileName)) {
                     newDir = absolutePath.replace(oldFileName, newFileName);
                     file.renameTo(new File(newDir));
                 }
             }
         }
     }
}
