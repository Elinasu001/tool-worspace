package com.kh.start.token.model.service;


import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.kh.start.token.model.dao.TokenMapper;
import com.kh.start.token.model.vo.RefreshToken;
import com.kh.start.token.util.JwtUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {
	
	private final JwtUtil tokenUtil;
	private final TokenMapper tokenMapper;
	
	/**
	 * 인증에 성공했을 때 
	 * JWTUtil 에 정의해놓은
	 * getAccessToken메소드 호출
	 * getRefreshToken메소드 호출
	 * RefreshToken의 경우 DB의 Token테이블에 만들어진 Token을 INSERT
	 * 두개를 어디다 고이 예쁘게 담아서 AuthServiceImpl의 login메서드로 반환
	 **/
	
	// 토큰을 받아서 반환해주는 메서드
	public Map<String, String> generateToken(String username) {
		
		/*
		String accessToken = tokenUtil.getAccessToken(username);
		String refreshToken = tokenUtil.getRefreshToken(username); 
		// 일단 AuthServiceImpl에 가서 의존성 주입을 해준다.
		  
		// 확인
		log.info("액세스토큰 : {}, 리프레시토큰 : {}", accessToken, refreshToken);
		*/
		
		Map<String, String> tokens = createTokens(username);
		
		/*
		// 리플래시 토큰
		// 36000000L == 1시간 // TokenMapper 의존성 주입하기
		RefreshToken token = RefreshToken.builder().token(refreshToken).username(username).expiration(System.currentTimeMillis() + 3600000L * 72).build();
		tokenMapper.saveToken(token);
		*/
		
		saveToken(tokens.get("refreshToken"), username);
		
		/*
		// 이 두개를 AuthService에다가 반환해줘야된다.
		Map<String, String> tokens = new HashMap();
		tokens.put("accessToken", accessToken);
		tokens.put("refreshToken", refreshToken);
		*/
		
		return tokens;
	}
	
	/*책임분리*/
	private Map<String, String> createTokens(String username){
		String accessToken = tokenUtil.getAccessToken(username);
		String refreshToken = tokenUtil.getRefreshToken(username); 
		//log.info("액세스토큰 : {}, 리프레시토큰 : {}", accessToken, refreshToken);
		Map<String, String> tokens = new HashMap();
		tokens.put("accessToken", accessToken);
		tokens.put("refreshToken", refreshToken);
		return tokens;
	}
	
	/*책임분리*/
	private void saveToken(String refreshToken, String username) {
		RefreshToken token = RefreshToken.builder().token(refreshToken).username(username).expiration(System.currentTimeMillis() + 3600000L * 72).build();
		tokenMapper.saveToken(token);
	}
	
	
	/**
	 * 추후 AccessToken의 만료기간이 지나서 토큰 갱신 요청이 들어왔을 때
	 * 사용자에게 전달받은 RefreshToken의 DB에 존재하면서 만료기간이 지나지 않았는지를 검증하는 메소드
	 * 
	 **/
	
	
}
