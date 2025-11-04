package com.kh.start.member.model.service;


import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.start.auth.model.vo.CustomUserDetails;
import com.kh.start.exception.CustomAuthenticationException;
import com.kh.start.exception.IdDuplicateException;
import com.kh.start.member.model.dao.MemberMapper;
import com.kh.start.member.model.dto.ChangePasswordDTO;
import com.kh.start.member.model.dto.MemberDTO;
import com.kh.start.member.model.vo.MemberVO;
import com.kh.start.token.model.dao.TokenMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
	
	private final MemberMapper memberMapper;
	private final PasswordEncoder passwordEncoder; // // 3-3. ROLE 추가 -> 이런식이면 유지보수가 안좋다 BCryptPasswordEncoder 이 아닌 PasswordEncoder 상위 타입으로 잡아줘야 코드의 변경이 일어나지 않는다.
	private final TokenMapper tokenEncoder; // 토큰 
	// 회원가입
	@Override
	public void signUp(MemberDTO member) {
		/**--- 할게 많으니 SLACK ~ implementation 'org.springframework.boot:spring-boot-starter-validation' build.gradle : 직접추가
		 * 3.
		 * 3-1. 유효성 검사 ==> Validator에게 위임
		 * 3-2. 아이디 중복 검사 : 중복된 ID를 허용하면, 데이터베이스 내 식별자(Primary Key) 규칙이 깨집니다 / 보안(Security) 측면
		 * 3-3. 비밀번호 암호화
		 * 3-4. ROLE 추가
		 * 3-5. 매퍼 호출
		 * 
		 * **/
		
		// 3-1 유효성 검사
		// 3-2 아이디 중복 검사
		int count = memberMapper.countByMemberId(member.getMemberId());
		
		// primary key니깐 0 아니면 1 
		if(1 == count) {
			// 이미 있는 아이디라고 알려주기
			throw new IdDuplicateException("이미 존재하는 아이디입니다.");
		}
		
		// 3-3. 비밀번호 암호화
		String encodedPwd = passwordEncoder.encode(member.getMemberPwd());
		
		
		// 3-4. ROLE 추가
		/*
		 * 생성자로 밖에 못만드는데 매개 변수의 타입과 순서를 알아야 사용할 수있다. // 만약에 필드가 변경될 경우 없어져버림 ... // 그래서 우리는 
			MemberVO signUpMember = new MemberVO(member.getMemberId(), encodedPwd, member.getMemberName(), "ROLE_USER"); 
			MemberVO 에서 @builder 추가 시 필드가 추가되도 문제가 생기지 않는다.
		*/
		MemberVO memberBuilder = MemberVO.builder().memberId(member.getMemberId()).memberPwd(passwordEncoder.encode(member.getMemberPwd())).memberName(member.getMemberName()).role("ROLE_USER").build();
		
		// 3-5.매퍼 호출
		memberMapper.signUp(memberBuilder);
		log.info("사용자 등록 성공 : {} ", memberBuilder);
		
	}
	@Override
	public void changedPassword(ChangePasswordDTO password) {
		
		// 현재 비밀번호가 맞는지 검증 => passwordEncoder.matches(평문, 암호문);(맞으면 암호문, 아니며 평문 반환)
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication(); // Authentication에서 현재 인증된 사용자의 정보 뽑아오기
		// 문제는 Authentication 다이렉트로 할수 없고 security > hodler > securitycontext > authentication 에서 받아낼 수 있다.
		
		// authentication principal에 담아놈 (CusomUserDetails)
		CustomUserDetails user = (CustomUserDetails)auth.getPrincipal();
		
		//  비밀번호가 맞는지 검증  맞다면 암호문 필요(BOOT_MEMBER MEMBER PWD) // 중요한건 여기까지 도착하는 시점에 JWT 거쳐서옴 > DB에서 사용자 정보를 AUTHENTICATION에 담아놈 -> 그럼 요청 전까지는 SESS~ 사용할 수 있음 그럼 이 시점에서 뽑아오자!
		String currentPassword = password.getCurrentPassword();
		String encodePassword = user.getPassword();
		if(!passwordEncoder.matches(encodePassword, currentPassword)) {
			throw new CustomAuthenticationException ("일치하지 않는 비밀번호");
		}
		
		// 현재 비밀번호가 맞다면 새 비밀번호를 암호화
		String newPassword = passwordEncoder.encode(password.getNewPassword());
		// UPDATE BOOT_MEMBER MEMBER_PWD = "newpassword" WHERE MEMBER_ID = "사용자ID" (담아가야됨)
		
		// 담아갈때 vo가 제일 좋지만 일단 map
		Map<String, String> changeRequest = Map.of("memberId", user.getUsername(),
													"newPassword", newPassword);
		
		memberMapper.changePassword(changeRequest); // 바꿔 보기 ~ localhost:8080/members : current~ BODY : Encoded password does not look like BCrypt
		
		
	}
	
	

	@Override
	@Transactional // spring aop:  transaction
	public void deleteByPassword(String password) {
		
		// 사용자가 입력한 비밀번호가 DB에 저장된 비밀번호 암호문이 쿵카짜짜 이게 맞는지 검증
		//Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		//CustomUserDetails user = (CustomUserDetails)auth.getPrincipal();
		// 검증이 맞다면
		//if(!passwordEncoder.matches(password, user.getPassword())) {
			//throw new CustomAuthenticationException("비밀번호가 일치하지 않습니다.");
		//}
		
		log.info("{}",password);
		CustomUserDetails user = validatePassword(password);
		tokenEncoder.deleteToken(user.getUsername());
		memberMapper.deleteByPassword(validatePassword(password).getUsername());// // DELETE FROM BOOT_MEMBER WHERE MEMBER_ID = 사용자아이디
		// 단일 트랜잭션 두개 => 문제는 성공하면 처음에 커밋을 해버리니 이 두개를 하나의 트랜잭션으로 처리 해야된다.
		// 스프링 트랜잭션은 ? @Transactional try transaction catch rollback ==> 다중 트랜잭션을 처리 해야되는데 예외 처리가 일어나야 된느데 여기서spring에서는 알아서 진행
	
	}
	
	
	// 비밀번호 검증
	private CustomUserDetails validatePassword(String password) {
		// 사용자가 입력한 비밀번호가 DB에 저장된 비밀번호 암호문이 쿵카짜짜 이게 맞는지 검증
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CustomUserDetails user = (CustomUserDetails)auth.getPrincipal();
		// 검증이 맞다면
		if(!passwordEncoder.matches(password, user.getPassword())) {
			throw new CustomAuthenticationException("비밀번호가 일치하지 않습니다.");
		}
		// DELETE FROM BOOT_MEMBER WHERE MEMBER_ID = 사용자아이디
		return user;
	}
	
	
	
}
