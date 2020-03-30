package com.vinsfortunato.smtools;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

public final class Version {
    public static final String getVersion() {
        try {
            Enumeration<URL> resources = Version.class.getClassLoader().getResources("META-INF/MANIFEST.MF");
            while(resources.hasMoreElements()) {
                Manifest manifest = new Manifest(resources.nextElement().openStream());
                Attributes attributes = manifest.getMainAttributes();
                String value = attributes.getValue("App-Version");
                if(value != null) return value;
            }
        } catch (IOException e) {
            throw new RuntimeException("Cannot read manifest!", e);
        }
        return "?";
    }
}