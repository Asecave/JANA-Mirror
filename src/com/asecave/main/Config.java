package com.asecave.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {
	
	private JanaMirror jana;
	
	Properties prop = new Properties();

	public Config(JanaMirror jana) {
		
		this.jana = jana;
		
		String thisPath = new File(Config.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParent();
		
		try {
			jana.log("Loading config...");
			prop.load(new FileInputStream(new File("config.properties")));
			jana.log("Success");
		} catch (IOException e) {
			jana.log("Failed. Creating new config file...");
			prop.setProperty("sourceDir", thisPath);
			prop.setProperty("targetDir", thisPath);
			save();
			jana.log("Success");
		}
	}
	
	public String get(String key) {
		return prop.getProperty(key);
	}
	
	public void set(String key, String value) {
		prop.setProperty(key, value);
	}
	
	public void save() {
		try {
			prop.store(new FileOutputStream("config.properties"), null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
