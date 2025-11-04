package com.kh.start.configuration.filter;



import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.kh.start.auth.model.vo.CustomUserDetails;
import com.kh.start.token.util.JwtUtil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component // bean 등록
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
	/**
	 * OncePerRequestFilter
	 * 추상클래스 ( 단일 클래스 ) 
	 * 
	 **/
	
	private final JwtUtil jwtUtil;
	private final UserDetailsService userDetailsService;
	
	// 필터링으로 인해서 토큰 학인이 어려우니 이걸로 확인해보기
	
	// 필터의 주요 로직을 구현하는 메서드, 요청이 들어올떄마다 호출됨
	@Override 
	protected void doFilterInternal(
			HttpServletRequest request, 
			HttpServletResponse response,  
			FilterChain filterChain) 
			throws ServletException, IOException{
		//log.info("1. 진짜로 요청이 들어올때마다 요친구가 호출되는지 확인");
		
		
		String uri = request.getRequestURI(); //  필터링으로 인해서 어떻게 요청이 들어왔는지 확인
		//log.info("2. 요청 어떻게 들어옴 ? {}", uri);
		
		
		//토큰 검즘 : members : Headers :key( authorization ) : value( Baearer 내토큰 )토큰 보내기 
		String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
		if(authorization == null || uri.equals("/auth/login")){
			filterChain.doFilter(request, response);
			return; // 밑으로 가지 않도록 위로 올리기~
		}
		//log.info("헤더에 포함시킨 Authorization : {} ", authorization); // 토큰 넘어오는지 확인 : 헤더에 포함시킨 Authorization : Bearer ~ 
		
		String token = authorization.split(" ")[1]; // 토큰 뺴오기
		log.info("토큰 값 : {} : ", token); // 이걸로 이제 검증
		
		// 1. 서버에서 관리하는 시크릿키로 만든게 맞는가?
		// 2. 유효기간이 지나지 않았는가?
		// ==> JwtUtil Claims 반환하는 메소드
		
		try {
			Claims claims = jwtUtil.parseJwt(token);// 뽑은 토큰 값을 넘겨준다.
			
			
			String username = claims.getSubject();	// JWT 토큰 안에 들어 있는 사용자 식별 정보(subject) 를 꺼내오는 코드
			log.info("토큰 소유주의 아이디 값 : {}", username); // 토큰 소유주의 아이디 값 : admin
			
			
			//이제 사용자의 아이디를 가지고 디비 가서 조회를 할 예정 
			//UserDetailsService 이미 만들어놈
			CustomUserDetails user = (CustomUserDetails)userDetailsService.loadUserByUsername(username);
			log.info("DB에서 조회해온 user의 정보 : {}", user); //  DB에서 조회해온 user의 정보 : CustomUserDetails(username=admin, password= ~)
			
			// SESSION에 담아야지 ~
			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
			authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			// 세부 설정관련 사용자의 IP주소, MAC주소, sessionID 등등을 포함시켜서 셋팅
			
			SecurityContextHolder.getContext().setAuthentication(authentication);
			// 요롷게 담아주면 현재 요청이 만료될때까지  Authentication에 담겨져있는 사용자의 정보를 사용할 수 있음.
			
			
		} catch(ExpiredJwtException e	) {
			log.info("토큰의 유효기간 말료");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getWriter().write("토큰 만료");
			
			return;
		} catch(JwtException e	) {
			log.info("서버에서 만들어진 토큰이 아님");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().write("유효하지 않은 토큰입니다.");
			
			return;
		}
		
		filterChain.doFilter(request, response);
		
		
		
		
		
		/**
		 * 장점 : 왜 이렇게 해야하는가 ?
		 * - 큐레이션(curation)
		 * - 세션이나 쿠키로도 로그인 유지할 수 있는데, 왜 굳이 토큰을 쓰는가?
		 * 	서비스 규모, 분산 구조, 보안 방식이 바뀌면서 필연적으로 토큰 기반 인증이 등장하게 된 이유
		 *  즉, 서버가 사용자를 인증한 뒤 “이 사람이 인증된 사용자임”을 증명하는 정보 조각을 발급
		 * 
		 * - Stateless(무상태) 인증
		 *   서버가 사용자 세션을 저장하지 않아도, 토큰 자체가 “내가 로그인한 사람임”을 증명
		 * - 확장성과 분산성
		 *   서버가 여러 대여도(로드밸런싱 환경), 토큰만 있으면 인증 가능 — 세션 공유 불필요
		 * - 모바일 / 외부 서비스 연동에 유리
		 *   브라우저 쿠키 없이도 API 통신으로 로그인 상태 유지 가능
		 * 
		 **/
		
		
	
	};
	
}
