package com.gitprism.GitPRism.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

  @Value("${spring.redis.host}")
  private String redisHost;

  @Value("${spring.redis.port}")
  private int redisPort;

  @Bean
  public LettuceConnectionFactory redisConnectionFactory() {
    RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(redisHost, redisPort);
    return new LettuceConnectionFactory(config);
  }

  @Bean
  public StringRedisTemplate stringRedisTemplate() {
    return new StringRedisTemplate(redisConnectionFactory());
  }

  @Bean
  public RedisTemplate<String, Object> redisTemplate() {
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(redisConnectionFactory());

    // ✅ 직렬화 설정 추가
    StringRedisSerializer keySerializer = new StringRedisSerializer();

    Jackson2JsonRedisSerializer<Object> valueSerializer =
        new Jackson2JsonRedisSerializer<>(Object.class);

    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.activateDefaultTyping(
        BasicPolymorphicTypeValidator.builder().build(),
        ObjectMapper.DefaultTyping.NON_FINAL
    );
    valueSerializer.setObjectMapper(objectMapper);

    template.setKeySerializer(keySerializer);
    template.setValueSerializer(valueSerializer);
    template.setHashKeySerializer(keySerializer);
    template.setHashValueSerializer(valueSerializer);

    return template;
  }
}
