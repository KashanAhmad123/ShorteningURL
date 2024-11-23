# ShorteningURL
Setting up the project
To set up your project & connect it to your remote repo you can follow the following commands (NOTE: These commands are for Mac. It might be a little bit different for Linux & Windows).

* brew install git
* mkdir urlshortener
* cd urlshortener
* git init
* git remote add origin https://github.com/abinator-1308/urlshortener.git
* git fetch && git checkout main
Setting up Redis
You can install redis using the brew install redis command. Once installed, you can start the server using the redis-server command.

* brew install redis
* redis-server
The server will start. The window should look something like this


startting a redis server
To check if redis-cli is installed, you can run the following command

* redis-cli --version
6.0.8
* redis-cli ping
PONG
Enter interactive mode using the following command:

personal@MacBook-Pro ~ % redis-cli
127.0.0.1:6379>
Configure Redis
We need to define the configuration in order to store our JSON object in redis. Each url entry will be defined as :

{
    "key": 123,
    "url": "www.abc.url"
    "created_at": "2021-08-10T23:59:59Z"
}
In order to define these in our Application, we’ll need to add our configuration in the config package. Start with adding the package and the file RedisConfiguration.java to the application.

The project structure will now look like this:


Project structure after Redis config
The configuration is defined as :

Start with defining the class as a configuration by adding the @Configuration notation.
@Configuration
public class RedisConfiguration {
}
Add a class Url to entry package to define the model. The class looks something like
@Data
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Url {

    @NotNull
    private Integer key;

    @NotBlank
    private String url;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime createdAt;
}
NOTE: I’m using PropertyNamingStrategies as PropertyNamingStrategy SnakeCaseStrategy is deprecated in Java 16

Define your bean for the Redis template
@Bean
RedisTemplate<String, Url> redisTemplate() {
    final RedisTemplate<String, Url> redisTemplate = new RedisTemplate<>();
    Jackson2JsonRedisSerializer valueSerializer = new Jackson2JsonRedisSerializer(Url.class);
    valueSerializer.setObjectMapper(objectMapper);
    redisTemplate.setConnectionFactory(connectionFactory);
    redisTemplate.setKeySerializer(new StringRedisSerializer());
    redisTemplate.setValueSerializer(valueSerializer);
    return redisTemplate;
}
Define the Functionality
Url shortening service has two functionalities: getting the url from the key & generating the key for a given url. We can use murmur3 algorithm provided by Google to generate the key. The manager Interface & Implementation looks something like:

UrlManager Interface:
public interface UrlManager {
    public String getUrlByKey(@NotBlank String key);
    public Url shortenUrl(@NotBlank String url);
}
The methods can be implemented in UrlManagerImpl

public class UrlManagerImpl implements UrlManager {
    @Autowired
    private RedisTemplate<String, Url> redisTemplate;

    @Override
    public String getUrlByKey(@NotBlank String key) {
        Url url = redisTemplate.opsForValue().get(key);
        return url.getUrl();
    }

    @Override
    public Url shortenUrl(@NotBlank String url) {
        // generating murmur3 based hash key as short URL
        String key = Hashing.murmur3_32().hashString(url, Charset.defaultCharset()).toString();
        return Url.builder().key(key).createdAt(LocalDateTime.now()).url(url).build();
    }
}
Adding Rest Controllers to expose the service
RestControllers are required to expose your service to the external world. You can define the operations to get the url from a key & generating a short URL for a given input. The controller will somewhat look like :

@RestController
@RequestMapping(value = "/urlShortener")
public class UrlController {

    @Autowired
    private UrlManager urlManager;

    @RequestMapping(value = "/{url}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity shortenUrl(@PathVariable String url) {
        Url shortUrlEntry = urlManager.shortenUrl(url);
        return ResponseEntity.ok(url);
    }

    @RequestMapping(value = "/{key}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity getUrl(@PathVariable String key) {
        String url = urlManager.getUrlByKey(key);
        return ResponseEntity.ok(url);
    }
}
Postman Test Cases
Postman test cases can be created to test the suite. The collection comprises of two APIs we exposed through the controller.
1. getByKey: This will fetch the shortened url when we pass the key as input. This is a get request wherein the key is passed in the url.


2. urlShortener: This will be used to shorten any url that is passed as input. This will be a post request.


post request to shorten urls
Result
And there you have it! Your url shortener backend application is ready. You can either build on top of it to add further functionalities (url validation, clear cache, advanced/custom hashing algorithms etc.) or add a frontend application that calls your service to shorten urls.

NOTE: MurmurHash is used here as a hashing method. In case of design problems/interviews, it’s expected that you build the hashing algorithm by yourself.
