package com.bofa.microservices.client;

import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;

import com.bofa.microservices.model.Product;
import com.bofa.microservices.model.ProductEvent;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class WebClientAPI {

	private WebClient webClient;
	
	WebClientAPI() {
		this.webClient = WebClient.builder().baseUrl("http://localhost:8080/products").build();
	}
	
	
	private Mono<ResponseEntity<Product>> postNewProduct(){
		return webClient
				.post()
				.body(Mono.just(new Product(null, "Latte Coffee", 1.99)), Product.class)
				.exchange()
				.flatMap(response->response.toEntity(Product.class))
				.doOnSuccess(res->System.out.println("*********************Post Request:"+res));		
	}
	
	private Flux<Product> getAllProducts(){
		return webClient
				.get()
				.retrieve()
				.bodyToFlux(Product.class)
				.doOnNext(res->System.out.println("*********************Get Request:"+res));
	}
	
	private Mono<Product> updateProduct(String id, String name, Double price){
		return webClient
				.put()
				.uri("/{id}", id)
				.body(Mono.just(new Product(null, name, price)), Product.class)
				.retrieve()
				.bodyToMono(Product.class)
				.doOnSuccess(res->System.out.println("*********************Put Request:"+res));		
	}
	
	private Mono<Void> deleteProduct(String id){
		return webClient
				.delete()
				.uri("/{id}",id)
				.retrieve()
				.bodyToMono(Void.class)
				.doOnSuccess(res->System.out.println("*********************Delete Request:"+res));		
	}
	
	private Flux<ProductEvent> getAllEvents(){
		return webClient
				.get()
				.uri("/events")
				.retrieve()
				.bodyToFlux(ProductEvent.class);
				
	}
	
	public static void main(String[] args) {
		WebClientAPI api = new WebClientAPI();
		
		api.postNewProduct()
		   .thenMany(api.getAllProducts())
		   .take(1)
		   .flatMap(p->api.updateProduct(p.getId(), "Cold Coffee", 3.33))
		   .thenMany(api.getAllProducts())
		   .take(1)
		   .flatMap(p->api.deleteProduct(p.getId()))
		   .thenMany(api.getAllProducts())
		   .thenMany(api.getAllEvents())
		   .subscribe(System.out::println);
	}
	
	
	
}
