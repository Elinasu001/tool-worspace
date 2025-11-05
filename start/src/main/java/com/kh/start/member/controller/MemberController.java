package com.kh.start.member.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.kh.start.member.model.dto.ChangePasswordDTO;
import com.kh.start.member.model.dto.MemberDTO;
import com.kh.start.member.model.service.MemberService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("members")
@RequiredArgsConstructor // 1. 필드 주입
public class MemberController {
	/*
	 * 회원가입 => 일반회원 => ROLE컬럼에 들어갈 값 필드에 담아주어야함
	 * 				    => 비밀번호 암호화
	 * 					=> VO에 담을것              
	 * VO : ValueObject(값을 담는 역할) => 불변해야한다는 것이 특징
	 * DTO : DataTransferObject(데이터 전송) 
	 * 
	 * dto(담을 떄 유효성 검사 해야됨)
	 */
	
	
	/*
	 * GET
	 * GET(/member/멤버번호)
	 * POST
	 * PUT
	 * DELETE
	 * 
	 * 로그인은 여기다가 구현 안할 거임 ( 분리 )
	 * 
	 */
	
	// 요청받을걸 보내기 위함 필드만들기 1. 필드 주입 2. 생성자 주입 3. setter주입  1번을 권장 : 맨 위 @RequiredArgsConstructor
	private final MemberService memberservice;
	
	/*
	@Autowired
	public MemberController(MemberService memberservice) {
		this.memberservice = memberservice;
	}
	*/
	
	// 회원가입
	@PostMapping					//3-1. @Valid 권한 2.  @RequestBody 요청 보낼때 body에 담아서 보내고 싶을 경우 
	public ResponseEntity<?> signUp(@Valid @RequestBody MemberDTO member){ // dto + vo
		
		log.info("멤버 잘 들어오나: {}", member);
		
		memberservice.signUp(member);
		/**
		 * 1. 다음 postman 에서 postman-start 데이터 등록해보기
		 * 그럼 filter 단에서 막힘 즉, handler 에 도달을 못함.
		 * his generated password is for development use only. Your security configuration must be updated before running your application in production.
		 * **/
		return ResponseEntity.status(201).build();
												  
												   
	}
	
	// 비밀번호 변경기능구현
	@PutMapping
	public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordDTO password){
		
		// 1번 비밀번호 입력값에 대한 유효성 검증
		log.info("비밀번호 정보: {}", password);// put 방식으로  테스트 : (400 Bad Request) : 로그인을 해야됨
		
		// 2번 지금 요청을 보낸 사용자가 입력한 기존의 비밀번호가 잘 맞는지 확인
		// 3번 새로 입력한 비밀번호에 대한 암호화 작업
		// 4번 새 비밀번호로 입력
		
		memberservice.changedPassword(password);
		
		return ResponseEntity.status(HttpStatus.CREATED).build(); // 비밀 번호 잘 바뀌는지 확인해보기 members/body/current~ enw~
	}
	
	@DeleteMapping
	public ResponseEntity<?> deleteBypassword(@RequestBody Map<String, String> request){
		
		log.info("이게오나? : {} ", request);
		memberservice.deleteByPassword(request.get("password"));
		return ResponseEntity.ok("오카카이");
		
	}
	
}
