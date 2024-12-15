package services;

import org.springframework.lang.NonNull;

import model.originalUrl;

public interface UrlServices {

	public originalUrl shortenUrl(@NonNull String url);

	public String getUrlByKey(@NonNull String key);
	

}
