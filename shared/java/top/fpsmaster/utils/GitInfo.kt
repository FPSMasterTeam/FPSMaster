package top.fpsmaster.utils;

import top.fpsmaster.modules.logger.Logger;

import java.io.IOException;
import java.util.Properties;
import java.io.InputStream;
  
public class GitInfo {

    private static final Properties properties;
    static {
        properties = new Properties();
        InputStream inputStream = GitInfo.class.getResourceAsStream("/git.properties");
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            Logger.error("Failed to load git.properties file");
        }
    }

    public static String getCommitId(){
        return properties.getProperty("git.commit.id");
    }

    public static String getCommitIdAbbrev(){
        return properties.getProperty("git.commit.id.abbrev");
    }

    public static String getCommitTime(){
        return properties.getProperty("git.commit.time");
    }

    public static String getBranch(){
        return properties.getProperty("git.branch");
    }
}