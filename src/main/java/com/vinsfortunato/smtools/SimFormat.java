package com.vinsfortunato.smtools;

import java.io.File;

public enum SimFormat {
    SM("sm"),
    SSC("ssc"),
    DWI("dwi");

    public final String extension;

    SimFormat(String extension) {
        this.extension = extension;
    }

    public static SimFormat fromFile(File file) {
        return fromFile(file.getName());
    }

    public static SimFormat fromFile(String fileName) {
        fileName = fileName.toLowerCase().trim();
        for(SimFormat format : values()) {
            if(fileName.endsWith("." + format.extension)) {
                return format;
            }
        }
        return null;
    }
}
