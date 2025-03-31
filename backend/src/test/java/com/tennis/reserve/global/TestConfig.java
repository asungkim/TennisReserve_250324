package com.tennis.reserve.global;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.testcontainers.containers.GenericContainer;

@TestConfiguration
public class TestConfig {

    /**
     * Redis TestContainer 설정
     */
    private static final GenericContainer<?> redisTestContainer =
            new GenericContainer<>("redis:7.2.4-alpine")
                    .withExposedPorts(6379)
                    .withReuse(true);

    static {
        redisTestContainer.start();
        System.setProperty("spring.data.redis.host", redisTestContainer.getHost());
        System.setProperty("spring.data.redis.port", redisTestContainer.getMappedPort(6379).toString());
    }

    @Bean
    public GenericContainer<?> redisContainer() {
        return redisTestContainer;
    }


    @Bean
    @Primary
    public RedisConnectionFactory redisConnectionFactory() {
        LettuceConnectionFactory factory = new LettuceConnectionFactory(
                redisTestContainer.getHost(), redisTestContainer.getMappedPort(6379)
        );
        factory.setValidateConnection(true);
        factory.afterPropertiesSet();
        return factory;
    }

    @Bean
    @Primary
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringSerializer);
        template.setValueSerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);
        template.setHashValueSerializer(stringSerializer);

        template.afterPropertiesSet();
        return template;
    }

}
