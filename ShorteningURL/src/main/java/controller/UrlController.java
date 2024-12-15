package controller;

import java.net.URL;
import model.originalUrl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import services.UrlServices;

@RestController
@RequestMapping(value="/urlShortner")
public class UrlController {
	
	@Autowired
	private UrlServices urlservice;
	
	@PostMapping(value= "/{url}")
	public ResponseEntity shortenUrl(@PathVariable String url) {
		
		originalUrl shortenUrlentry= urlservice.shortenUrl(url);
		return ResponseEntity.ok(shortenUrlentry);
		
	}
	
	@GetMapping(value="/{key}")
	public ResponseEntity getUrlByKey(@PathVariable String key) {
		String url= urlservice.getUrlByKey(key);
		if (url!=null) {
		return ResponseEntity.ok(url);
	}
		return ResponseEntity.notFound().build();
	
	}	

}
