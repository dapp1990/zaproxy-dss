package org.parosproxy.paros.extension.filter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class ConfigurationReader<T> {
	private String fileLocation;
	private ArrayList<T> instances;
	private String path;

	public ConfigurationReader (String fileLocation, String packagePath) {
		this.fileLocation = fileLocation;
		this.instances = new ArrayList<T>();
		this.path = packagePath;
	}

	private void execute() throws IOException {
		FileReader fr = new FileReader(fileLocation);
		BufferedReader textReader = new BufferedReader(fr);

		while(textReader.ready()){
			String nextString = textReader.readLine();
			try {
				Class<?> clazz = Class.forName(path+"."+nextString);	//Total programming: instantiation will only work if the typing is correct
				Constructor<?> ctor = clazz.getConstructor();
				instances.add((T) ctor.newInstance(new Object[] {  }));	//static, no arguments for constructors
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException nsme) {
				nsme.printStackTrace();
			} catch (InvocationTargetException ite) {

			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		}
		textReader.close();
	}

	public ArrayList<T> getInstances() {
		try {
			execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return this.instances;
	}
}
