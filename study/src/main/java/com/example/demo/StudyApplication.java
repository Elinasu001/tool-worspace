package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StudyApplication {
	/*
	 * 1. AutoConfiguration(자동구성)
	 * 
	 * 스프링부트의 핵심 기능
	 * 애플리케이션의 클래스패스에 존재하는 라이브러리 및 설정을 기반으로 Bean을 자동으로 구성해줌
	 * 
	 * 개발자가 복잡한 설정을 건너뛰고 바로 개발을 시작할 수 있음
	 * 
	 * @EnableAutoConfiguration
	 * 스프링부트의 자동구성을 활성화해주는 애노테이션
	 * 
	 * 동작순서
	 * 애플리케이션 시작 -> @SpringBootApplication 애노테이션이 붙은 클래스의 main 메서드가 호출됨
	 * 자동구성 활성화 -> @EnableAutoConfiguration을 통해 자동구성을 활성화
	 * 모든 자동구성이 활성화가 끝나면 애플리케이션 실행
	 * 
	 * 자동구성을 해주지만 직접적으로 가져와야 하는 경우가 있다 - ex ) sercurity까지는 자동 등록 안에 bean등록은 개발자가 자동으로 등록해야됨
	 * ==> @Configuration
	 * 
	 */
	public static void main(String[] args) {
		SpringApplication.run(StudyApplication.class, args);
	}

}
