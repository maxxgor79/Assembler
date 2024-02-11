package ru.zxspectrum.converter.settings;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Slf4j
public class ConvSettings {
    @Getter
    @NonNull
    private String majorVersion;

    @Getter
    @NonNull
    private String minorVersion;

    @Getter
    @NonNull
    private String appName;

    public void load(@NonNull InputStream is) throws IOException {
        final Properties props = new Properties();
        props.load(is);
        appName = props.getProperty("app_name", "app");
        majorVersion = props.getProperty("major_version", "1");
        minorVersion = props.getProperty("minor_version", "0");
    }

}
