package com.project.medic.config;

import com.google.gson.Gson;
import com.project.medic.entity.ConfigEntity;
import com.project.medic.utils.ReadFile;
import com.project.medic.utils.WriteFile;

import java.io.File;

public class Config {
    public static String url = "http://";
    public static String host = "localhost";
    public static int port = 8745;

    private File file;

    public Config(){
        this.file = new File("config.json");
    }

    public ConfigEntity getData(){
        ReadFile readFile = new ReadFile(this.file);
        String result = readFile.get();

        if (result == null) {
            return null;
        }

        return new Gson().fromJson(readFile.get(), ConfigEntity.class);
    }

    public boolean setData(ConfigEntity configEntity){
        WriteFile writeFile = new WriteFile(this.file);
        return writeFile.write(new Gson().toJson(configEntity));
    }
}
