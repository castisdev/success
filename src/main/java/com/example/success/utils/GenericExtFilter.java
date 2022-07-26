package com.example.success.utils;

import org.apache.logging.log4j.util.Strings;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Objects;

public class GenericExtFilter implements FilenameFilter {
    private String ext;

    private String[] extList;

    public GenericExtFilter(String[] extList) {
        this.extList = extList;
    }

    public GenericExtFilter(String ext) {
        this.ext = ext;
    }

    @Override
    public boolean accept(File dir, String name) {
        if (!Strings.isEmpty(this.ext) && Objects.nonNull(this.extList)) {
            boolean result = false;
            for(String extension : extList){
                result= (name.endsWith("." + extension));
                if (result) {
                    return result;
                }
            }
            return result;
        }

        return (name.endsWith("." + ext));
    }
}
