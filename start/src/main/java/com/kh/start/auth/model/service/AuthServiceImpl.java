package com.kh.start.auth.model.service;

import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import com.kh.start.auth.model.vo.CustomUserDetails;
import com.kh.start.exception.CustomAuthenticationException;
import com.kh.start.member.model.dto.MemberDTO;
import com.kh.start.token.model.service.TokenService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
	
	// 확인용
	//AuthenticationManager at;
	//UserDetailsService ud; // 유저detail 있는가 없는가 // UserDeatil타입으로 가져옴 // interface : 애플리케이션마다 사용자정보가 다르니, userDetail로 가져오는 메소드를 넣은 것
	
	private final AuthenticationManager authenticationManager;
	private final TokenService tokenService;
	
	
	@Override
	public Map<String, String> login(MemberDTO member) {
		
		// 로그인 구현
		// 1. 유효성 검증(아이디/비밀번호 값이 들어왔는가, 영어숫자인가, 글자수가 괜찮은가)_이미 MemberDTO에 만들어 놓은게 있으니 AuthController에 @vaild 추가
		
		// 2. 아이디가 BOOT_MEMBER테이블에 MEMBER_ID컬럼에 존재하는 아이디인가
		// 3. 조회를 해온 비밀번호 컬럼의 암호문이 사용자가 입력한 평문으로 만들어진 것이 맞는가
		
		// 사용자 인증 -> Security에서 담당 : spring에서 Security 담당하는 이름은? AuthenticationManager; 빈등록하여 사용해야되는데 AuthenticationManager파일에 등록해준다.
		// 일반적으로 authenticationToken을 만들어서 전달을 해준다. (생성자로(사용자가 평문으로 입력한 아이디와 비밀번호를 넣음))/ Username == id 
		// 이 돌리는 과정에서 UserNameNotFoundExceptiondl 발생하는 것 단, 예외가 발생하더라도 클라이언트나 앞단 개발자에게 올바른 메시지를 전달해주기 위해서는 예외처리 진행
		Authentication auth = null;
		try {
			auth =  authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(member.getMemberId(), member.getMemberPwd()));
		} catch(AuthenticationException e) {
			throw new CustomAuthenticationException("아이디 또는 비밀번호를 확인하세요.");
		}
		
		CustomUserDetails user = (CustomUserDetails)auth.getPrincipal(); // 클래스캐스팅
		log.info("로그인성꽁!");
		log.info("인증에 성공한 사용자의 정보 : {}", user);
		
		
		//----------------------------------------------------------------------------------------
		// 토큰 발급
		// JWT라이브러리를 이용해서
		// AccessToken이랑 RefreshToken을 만들어서 발급
		
		Map<String, String> loginResponse = tokenService.generateToken(user.getUsername());
		
		loginResponse.put("memberId", user.getUsername());
		loginResponse.put("memberName", user.getMemberName());
		loginResponse.put("role", user.getAuthorities().toString());
		
		return loginResponse; // 이제 로그인 성공하면 토큰 만들어 사용자에게 보내준다.(라이브러리로)
	}

}
