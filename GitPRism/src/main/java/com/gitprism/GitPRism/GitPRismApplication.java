package com.gitprism.GitPRism;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync // 비동기 메서드 활성화
@SpringBootApplication
public class GitPRismApplication {
	public static void main(String[] args) {
		// Spring Boot 실행
		SpringApplication.run(GitPRismApplication.class, args);
	}
}
