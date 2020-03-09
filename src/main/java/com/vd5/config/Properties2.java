package com.vd5.config;

import com.vd5.dcs2.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Properties2 extends Properties {
    private static final long serialVersionUID = -6046620895071681613L;

    public static final String INCLUDES = "includes";

    public synchronized void load(String file) throws IOException {
        InputStream is = getInputStream(file);
        if (is != null) {
            this.load(is);
        }
    }

    @Override
    public synchronized void load(InputStream inStream) throws IOException {
        super.load(inStream);
        String includedFile = super.getProperty(INCLUDES);
        super.remove(INCLUDES); //remove after get values to prevent StackOverflowError
        if (includedFile != null && includedFile.length() > 0) {
            String[] fileNames = includedFile.split(",");
            for (String file : fileNames) {
                if (file.trim().length() > 0) {
                    this.load(file.trim());
                }
            }
        }
    }

    private InputStream getInputStream(String file) {
        Log.info("Loading file: {}", file);
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(file);;

        if (inputStream == null) {
            try {
                inputStream = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return inputStream;
    }
}
