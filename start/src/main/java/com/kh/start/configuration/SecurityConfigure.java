package com.kh.start.configuration;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// CORS 관련
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

// 사용자 정의 필터
import com.kh.start.configuration.filter.JwtFilter;

import lombok.RequiredArgsConstructor;


/**
 * 2.
 * 우리의 문제점 : 시큐리티의 formLogin필터가 자꾸만 인증이 안됐다고 회원가입도 못하게함
 * 
 * formlogin을 안쓰려면 security filter 에서 빈등록 해주면 securityfilterchain 즉, 끌어다 쓰는
 * => @Configuration 사용한다.
 * 
 * 해결방법 : form 로그인안쓸래하고 filterChain을 빈으로 등록
 * */

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfigure {
	
	
	private final JwtFilter jwtFilter; // 필드로 추가
	
	// (겹치지않게 주의 )메소드 만들어서 httpSecuritys 반환하기
	/*
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
		// filter는 무엇으로 만듦? 매개변수 받아오는 httpSecurity : 우리가 만드는건 쓸것만 정하는 것
		return httpSecurity.formLogin().disable().build(); // postman : 403
	}
	*/
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
		
		// Customizer interface라서 익명 클래스를 만들어서 사용해야된다. 
		/*
		return http.formLogin(new Customizer<FormLoginConfigurer<HttpSecurity>>() {
	        @Override
	        public void customize(FormLoginConfigurer<HttpSecurity> form) {
	            form.disable(); // 폼 로그인 비활성화
	        }
	    }).build();
		*/
		
		/*
		- 너무 기니깐 람다 형식으로 진행
			formLogin필터를 사용안함으로써 401은 지나갔는데 ==> 403이 뜸
			CRSF(Cross-Site Request Forgery)필터가 튀어나옴
			<img src="http://www.naver.com"/> => 만약 악의 적인 사용자가 src를 변경한다면 내가 의도하지 않은 사이트로 갈 수 있으니
			미리 막아주는데
			csrf 토큰이 없기 때문에 csrf를 꺼줘야 한다.
			members로 postman 돌려보기
		*/
		
		
		/*
		 * ex ) 회원가입, 로그인 => 누구나 다 할 수 있어야 함.
		 * 		회원정보수정, 회원탈퇴 => 로그인된 사용자만 할 수 있어야함
		 * 		이거를 구분하기 위해 authorizeHttpRequests 붙여주기
		 */
		return httpSecurity.formLogin(AbstractHttpConfigurer::disable)
				.csrf(AbstractHttpConfigurer::disable)
				.cors(Customizer.withDefaults()) // seruity configuration
				.authorizeHttpRequests(requests -> {
					requests.requestMatchers(HttpMethod.POST, "/auth/login", "/members", "/auth/refresh").permitAll(); // 누구나 다 허용
					requests.requestMatchers(HttpMethod.PUT, "/members", "/boards/**").authenticated(); // 인증된 애만 넘어갈 수 있음 // ** id 달아야된니깐
					requests.requestMatchers(HttpMethod.DELETE, "/members", "/boards/**").authenticated(); // 인증이 된 친구인지 아닌지 체크하기
					requests.requestMatchers(HttpMethod.POST, "/boards", "/comments").authenticated();
					requests.requestMatchers(HttpMethod.GET, "/boards/**", "/comments/**", "/uploads/**").permitAll(); // 누구나 다 볼 수 있음
					requests.requestMatchers("/amin/**").hasRole("ADMIN"); // 데어테엇 ROLE컬럼 가지고 ADIM인지 ROLE MEMBER인지 본다. 
				}) // 람다형식으로 보내주기  
				
				/**
				 * sessionManagement: 세션을 어떻게 관리할 것인지 지정 
				 * sessionCreatePolicy : 세션 사용 정책을 설정 
				 **/
				.sessionManagement(manager -> 
									manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
				.build();// 즉, members로 put요청이 오면 노노
	}
	
	
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
	    CorsConfiguration configuration = new CorsConfiguration();
	    configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
	    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
	    configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type")); //  오타 수정 (Authoriztion → Authorization)
	    configuration.setAllowCredentials(true);// 혹시나 나중에 추가 할경우 ip(domain)등을 추가 해주면 된다.

	    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	    source.registerCorsConfiguration("/**", configuration);
	    return source;// 리액트 단에서 보내는 요청 여기서 다 받음
	}// 이거를 filterChain 포함필요

	
	// 3-3. ROLE 추가
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	/*
	@Bean
	public Argon2PasswordEncoder encoder() {
		return new ~~~; 이런식으로 바꿔 버릴 수 있음
	}
	*/
	
	// AuthenticationManager
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
		return authConfig.getAuthenticationManager();
	}
	
}
