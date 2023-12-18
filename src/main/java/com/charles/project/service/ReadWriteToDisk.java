package com.charles.project.service;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.util.ArrayList;
import java.util.List;

import com.charles.project.model.Product;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

public class ReadWriteToDisk {

	//Reminder: Use Path class instead of String
	final static private String destinationPath = System.getProperty("user.home") + "/Products";
	
	
	static {
		File createProductDir = new File(destinationPath);

		if (!createProductDir.exists()) {
			createProductDir.mkdir();
			System.out.println("Products directory created on path: " + destinationPath);
		}else System.out.println("Products will be saved on path " + destinationPath);
		
	}

	
	public ReadWriteToDisk() {
		super();
	}

	
	
	public void createProduct(Product product) throws Exception {

		File createDir = new File(destinationPath + "/" + product.getName());

		if (createDir.exists()) {
			throw new FileAlreadyExistsException(createDir.getAbsolutePath());
		} else {
			createDir.mkdirs();

			File createFile = new File(destinationPath + "/" + product.getName() + "/info.txt");
			createFile.createNewFile();

			Writer writer = new FileWriter(createFile.getAbsolutePath());
			Gson gson = new GsonBuilder().create();
			gson.toJson(product, writer);
			writer.flush(); // flush data to file <---
			writer.close(); // close write <---
		}

	}

	public List<Product> getAllProdcuts() {
		List<Product> productList = new ArrayList<>();
		Gson gson = new Gson();
		JsonReader reader;
		File file = new File(destinationPath);
		File[] fileList = file.listFiles();
		for (File fl : fileList) {

			try {
				reader = new JsonReader(new FileReader(fl.getAbsolutePath() + "/info.txt"));
				Product product = gson.fromJson(reader, Product.class);
				productList.add(product);

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

		}

		return productList;

	}

	
	public void deleteProduct(String productName) throws FileNotFoundException {
		File deleteDir = new File(destinationPath + "/" + productName);
		if (deleteDir.exists()) {
			File[] allContents = deleteDir.listFiles();
			if (allContents != null) {
				for (File file : allContents) {
					file.delete();
				}
			}
			deleteDir.delete();

		} else {
			throw new FileNotFoundException();
		}

	}
}
