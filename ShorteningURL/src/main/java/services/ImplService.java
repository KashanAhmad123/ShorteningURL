package services;

import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.google.common.hash.Hashing;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import model.originalUrl;

@Service
@Slf4j
public class ImplService implements UrlServices {

	@Autowired
	private RedisTemplate<String,originalUrl> redisTemplate;
	
	public originalUrl shortenUrl(@NotNull String url) {
		// TODO Auto-generated method stub
		String key= Hashing.murmur3_32().hashString(url,Charset.defaultCharset()).toString();
		
		originalUrl shortEntryurl= originalUrl.builder().key(key).createdAt(LocalDateTime.now()).gUrl(url).build();
		
		
		redisTemplate.opsForValue().set(key,shortEntryurl,36000L, TimeUnit.SECONDS);
		
		return shortEntryurl;
	}

	public String getUrlByKey(@NotNull String key) {
		// TODO Auto-generated method stub
		originalUrl urlEntry= (originalUrl) redisTemplate.opsForValue().get(key);
		if(urlEntry!=null) {
			return urlEntry.getGUrl();
		}
		return null;
	}
	
	

}
