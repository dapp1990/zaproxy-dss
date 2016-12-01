package org.parosproxy.paros.extension.filter;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class ConfigurationReader {
//	private static final String CONFIG_FILE = "resources/config.txt";
//	private FileReader fileReader;
//	private Class[] types;
	private String fileLocation;
	private ArrayList<Object> instances;
	private String[] classNames;
	
	public ConfigurationReader (String fileLocation, String[] classNames) {
		this.fileLocation = fileLocation;
//		this.types = types;
		this.classNames = classNames;
		instances = new ArrayList<Object>();
	}
	
	public void execute() throws IOException {
		FileReader fr = new FileReader(fileLocation);
    	BufferedReader textReader = new BufferedReader(fr);
    	            	
    	while(textReader.ready()){
    		String nextString = textReader.readLine();
    		for(String name : classNames) {
    			if(name.equals(nextString)) {
//    				instances.add(createI)
    				try {
						Class<?> clazz = Class.forName(name);
						Constructor<?> ctor = clazz.getConstructor(String.class);
						instances.add(ctor.newInstance(new Object[] {  }));	//static, no arguments for constructors
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NoSuchMethodException nsme) {
						nsme.printStackTrace();
					} catch (InvocationTargetException ite) {
						
					} catch (InstantiationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
    			}
    		}
    	}
    	textReader.close();
	}
	
	public ArrayList<Object> getInstances() {
		return this.instances;
	}
	
	/*public Initialized {
		FileReader fileReader = new FileReader(CONFIG_FILE);
	}*/
}
