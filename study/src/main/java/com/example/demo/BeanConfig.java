package com.example.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {
	/*
	 * Spring mvc에서 빈등로 등록할 때는 어떻게 하나?
	 * 
	 * root-context.xml에가서 
	 * <bean class="풀클래스명" id="식별자">
	 * 	<property 필드값/>
	 * </bean>
	 * ----------------------------------------
	 * 
	 * Sprig boot에서 어떻게 하나?
	 * 
	 * 1. 내가 만든 경우가 아닐 경우 설정 클래스를 만들어준다.
	 * 2. @Configuration 를 단다.
	 * 3. 메서드를 선언한다.
	 * 4. @Bean 를 단다.
	 * 
	 * 
	 * @Configuration란?
	 * 
	 * 스프링에서 설정 클래스를 정의할 때 사용한다.
	 * 
	 * 하나 이상의 @Bean이 달린 메소드를 포함해 스프링컨테이너에 빈으로 등록함
	 * 
	 * @Bean 조건은?
	 *  
	 * 1. @Configuration클래스 내에서 메소드에 달려 스프링 빈을 생성한다.
	 * 2. 메서드의 반환값이 스프링컨테이너에 빈으로 등록된다.
	 * 
	 * 이럴 경우 장점은?
	 * 
	 * (web, settings, security 각각 configuration을 만든다.)
	 * 
	 * - 가독성이 좋아진다.
	 * - xml으로 설정하는 것보다 빠른시점에 오류를 발견할 수 있고,
	 *   코드 기반이기 때문에 자동완성/ 수정이 용잉하고
	 *   설정 클래스내에서 빈의 생성과정을 명확하게 정의할 수 있음
	 * 
	 * 
	 */
	
	@Bean 
	public StudyBean study() {
		return new StudyBean();
	}
	
}
