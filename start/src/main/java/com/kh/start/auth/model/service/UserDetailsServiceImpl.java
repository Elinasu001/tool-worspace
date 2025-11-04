package com.kh.start.auth.model.service;

import java.util.Collections;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.kh.start.auth.model.vo.CustomUserDetails;
import com.kh.start.exception.UserNameNotFoundException;
import com.kh.start.member.model.dao.MemberMapper;
import com.kh.start.member.model.dto.MemberDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
	
	// AuthenticationManager가 실질적으로 사용자의 정보를 조회할 때 메소드를 호출하는 클래스
	
	private final MemberMapper mapper;
	
	@Override							//AuthServiceImpl에서 사용자가 평문으로 입력한 아이디  userId를 매개변수로 들어옴
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		MemberDTO user = mapper.loadUser(username); // username 존재하는가
		
		//log.info("이거 나옴? : {} ", user);
		if(user == null) {
			throw new UserNameNotFoundException("DB에 사용자 정보가 없을 때 예외");
		}
		
		// UserDetailsServiceImpl → AuthenticationManager → AuthServiceImpl
		// UserDetails 객체(CustomUserDetails)를 빌더 패턴으로 반환
		// 반환된 UserDetails의 password와 입력 암호화된 password 비교, 일치하면 인증 성공 → 이걸 담은 Authentication 객체 반환((principal, authorities 포함))  true면 build에 authentication 타입을 담아서 반환 어디에 ? authserviceimpl쪽으로 반환
		return CustomUserDetails.builder().username(user.getMemberId()).password(user.getMemberPwd()).memberName(user.getMemberName()).authorities(Collections.singletonList(new SimpleGrantedAuthority(user.getRole()))).build();
	}

}
