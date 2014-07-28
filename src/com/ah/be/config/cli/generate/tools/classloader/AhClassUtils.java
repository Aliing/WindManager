package com.ah.be.config.cli.generate.tools.classloader;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import com.ah.be.config.cli.generate.CLIGenerateAutoAdaptive;

public class AhClassUtils {
	
	public static List<Class<?>> getClassesByPackageName(String packageName, AhClassFilterCallBack callBack){
		List<Class<?>> allClasses = null;
		try{
			allClasses = getClasses(packageName);
		}catch(Throwable t){
			t.printStackTrace();
		}
		
		if(allClasses == null){
			return null;
		}
		
		List<Class<?>> resultClassList = new ArrayList<Class<?>>();
		for(Class<?> clazz : allClasses){
			if(callBack.isValid(clazz)){
				resultClassList.add(clazz);
			}
		}
		
		return resultClassList;
	}
	
	private static List<Class<?>> getClasses(String packageName) throws IOException, ClassNotFoundException{
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		String filePath = packageName.replace(".", "/");
		
		Enumeration<URL> resources = loader.getResources(filePath);
		List<File> dirs = new ArrayList<File>();
		URL resObj = null;
		while(resources.hasMoreElements()){
			resObj = resources.nextElement();
			dirs.add(new File(resObj.getFile()));
		}
		
		List<Class<?>> resultList = new ArrayList<Class<?>>();
		for(File dir : dirs){
			resultList.addAll(findClasses(dir, packageName));
		}
		return resultList;
	}
	
	private static List<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException{
		List<Class<?>> resList = new ArrayList<Class<?>>();
		if(!directory.exists()){
			return resList;
		}
		
		File[] files = directory.listFiles();
		String fileName = null;
		for(File file : files){
			fileName = file.getName();
			if(file.isDirectory()){
				resList.addAll(
						findClasses(file, packageName + "." + fileName)
				);
			}else if(fileName.endsWith(".class")){
				resList.add(
						Class.forName(packageName + "." + fileName.substring(0, fileName.length()-6))
				);
			}
		}
		
		return resList;
	}
	
	public static void main(String[] args){
		List<Class<?>> allClasses = getClassesByPackageName("com.ah.be.config.cli.generate.impl", 
				new AhClassFilterCallBack(){

					@Override
					public boolean isValid(Class<?> clazz) {
						//class extends from CLIGenerateAutoAdaptive
						if(!CLIGenerateAutoAdaptive.class.isAssignableFrom(clazz)){
							return false;
						}
						
						//get constructor without parameters;
						Constructor<?> constructorNoParams = null;
						try{
							constructorNoParams = clazz.getDeclaredConstructor(new Class<?>[]{});
						}catch(Exception e){
							constructorNoParams = null;
						}
						
						return constructorNoParams != null;
					}
		});
		
		for(Class<?> clazz : allClasses){
			System.out.println(clazz.getSimpleName());
		}
	}
}
