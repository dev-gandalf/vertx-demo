package com.charles.project;


import java.nio.file.FileAlreadyExistsException;
import java.util.*;
import com.charles.project.model.Product;
import com.charles.project.service.ReadWriteToDisk;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.vertx.config.ConfigRetriever;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;



public class MainVerticle extends AbstractVerticle {

	@Override
	public void start() {

// Initialize router
		Router router = Router.router(vertx);
		router.route().handler(BodyHandler.create());
		

//Create HTTP server with external port configuration		
ConfigRetriever retriever = ConfigRetriever.create(vertx);
retriever.getConfig(config -> {vertx.createHttpServer()
    .requestHandler(router)
    .listen(config.result().getInteger("HTTP_PORT", 8000));});
		



		
		
// Controllers
		
		router.get("/check").handler(this::check);

		router.get("/product/list").handler(this::getProducts);

		router.post("/product/create").handler(this::createProduct);

		router.delete("/product/delete/:name").handler(this::deleteProduct);

		
	}

// Controller methods	
	
	public void getProducts(RoutingContext ctx) {

		ReadWriteToDisk reader = new ReadWriteToDisk();
		List<Product> productList = new ArrayList<>();
		productList = reader.getAllProdcuts();

		ctx
		.response()
		.putHeader("content-type", "application/json; charset=utf-8")
		.setStatusCode(200)
		.end(Json.encodePrettily(productList));
	}

	
	
	public void createProduct(RoutingContext ctx) {

		String json = ctx.body().asString();
		ObjectMapper objectMapper = new ObjectMapper();
		Product product;
		ReadWriteToDisk writer = new ReadWriteToDisk();
		try {
			product = objectMapper.readValue(json, Product.class);
			writer.createProduct(product);
			
			ctx
			.response()
			.putHeader("content-type", "application/json; charset=utf-8")
			.setStatusCode(200)
			.end("Product Successfully created");
		
		}catch (FileAlreadyExistsException e) {
			e.printStackTrace();
			
			ctx
			.response()
			.putHeader("content-type", "application/json; charset=utf-8")
			.setStatusCode(200)
			.end("Product already exists");
		}catch (Exception e) {
			e.printStackTrace();
			
			ctx
			.response()
			.putHeader("content-type", "application/json; charset=utf-8")
			.setStatusCode(200)
			.end("Something went wrong");
		}
	}
	
	
	
	public void deleteProduct(RoutingContext ctx){
		String productName = ctx.pathParam("name");
		ReadWriteToDisk delete = new ReadWriteToDisk();
		try {
			delete.deleteProduct(productName);
			ctx
			.response()
			.putHeader("content-type", "application/json; charset=utf-8")
			.setStatusCode(200)
			.end("Product successfully deleted");
			
		} catch (Exception e) {
			e.printStackTrace();
			
			ctx
			.response()
			.putHeader("content-type", "application/json; charset=utf-8")
			.setStatusCode(200)
			.end("Product not found");
		}
		
		
		
		
	}

	private void check(RoutingContext ctx) {
		ctx
		.response()
		.setStatusCode(200)
		.end("it works");
	}

}
