package config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;

import model.originalUrl;

@Configuration
public class RedisConfiguration {
	
	@Autowired
	ObjectMapper objectmapper;
	
	@Autowired
	JedisConnectionFactory connectionfactory;
	
	@SuppressWarnings("removal")
	RedisTemplate<String ,model.originalUrl> redisTemplate(){
		final RedisTemplate<String, originalUrl> redisTemplate = new RedisTemplate<>();
        Jackson2JsonRedisSerializer<originalUrl> valueSerializer = new Jackson2JsonRedisSerializer(originalUrl.class);
        valueSerializer.setObjectMapper(objectmapper);
        redisTemplate.setConnectionFactory(connectionfactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(valueSerializer);
        return redisTemplate;
	}
	

}
