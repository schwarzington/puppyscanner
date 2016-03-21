package com.puppyscanner.main.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class ScannerUtil {
	public Properties initializeProperties(){
		Properties properties = null;
		try {
			File file = new File("classes/pupperties.properties");
			FileInputStream fileInput = new FileInputStream(file);
			properties = new Properties();
			properties.load(fileInput);
			fileInput.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return properties;
	}
}
