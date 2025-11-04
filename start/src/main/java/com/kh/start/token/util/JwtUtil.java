package com.kh.start.token.util;

import java.util.Base64;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtUtil {
//@Configuration은 자신이 가진 설정(Bean 정보 등)을 반환한다” 이건 @Component
	
	// 토큰을 만들어내는 기능들을 가지고 있을 클래스
	// String key = "fejiwejfiowe123123"; => 보통 yml 따로 빼서 작업하지만 일단 application.yml에서 진행
	// 절대절대 노출 되지 말아야됨
	// 애플리케이션 설정파일에 정의된 속성의 값들을 클래스 내부에서 불러서 사용하고 싶다!
	@Value("${jwt.secret}")  // 문자열 데이터 넣기
	private String secretKey;
	private SecretKey key; // 서명할 수 있다. // 타입 객체(SecretKey)는 변수명(secreKey)으로 만들어지는데 "시점"이 중요하다.
	
	// "시점" : 빈 만들어지고 의존성 주입 끝나고 value값이 들어 온 시점에 key를 만든다.
	
	@PostConstruct // 이것이 이 시점을 다루는 애노테이션이다.
	public void init() {
		log.info("{}", secretKey); // com.kh.start.token.util.JwtUtil : 시크릿키 확인
		byte[] arr =  Base64.getDecoder().decode(secretKey); // java.util
		this.key = Keys.hmacShaKeyFor(arr);
		log.info("JWT 서명용 SecretKey 생성 완료");
	}
	
	public String getAccessToken(String username) { // 여기다가는 하루
		// sub 은 식별자 : 우리는 username으로 넣을거임 
		return Jwts.builder()
				.subject(username) // 사용자 아이디
				.issuedAt(new Date())	// java.util.Date 발급일		
				.expiration(new Date(System.currentTimeMillis() + (1000 * 60 * 60 * 24)))	// acesstoken은 보통 3일을 넘지 않는데 보편적으로 하루, 3일 // currentTimeMillis 현재시간을 mills 단위로 만든 것 
				.signWith(key) // 서명
				.compact();

	}		
	
	public String getRefreshToken(String username) { // 여기다가는 3일
		return Jwts.builder()
				.subject(username) // 사용자 아이디
				.issuedAt(new Date())	// java.util.Date 발급일		
				//.expiration(new Date(System.currentTimeMillis() + (1000 * 60 * 60 * 24 * 3))) /// 현재 시간을 밀리초(ms) 단위로 반환
				//.expiration(new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(10)));	// 10일을 밀리초로 변환
				.expiration(Date.from(Instant.now().plus(Duration.ofDays(3)))) // 현재 시간으로부터 3일 뒤의 시각을 Instant 기반으로 계산해서 Date로 변환
				.signWith(key) // 서명
				.compact();
	}
	
	public Claims parseJwt(String token) {
		return Jwts.parser()
				.verifyWith(key)
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}
	
}
