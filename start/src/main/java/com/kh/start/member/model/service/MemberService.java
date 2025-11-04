package com.kh.start.member.model.service;

import com.kh.start.member.model.dto.ChangePasswordDTO;
import com.kh.start.member.model.dto.MemberDTO;

public interface MemberService {
	
	// 회원가입
	void signUp(MemberDTO member);
	
	// 비밀번호 변경
	void changedPassword(ChangePasswordDTO password);
	
	// 비밀번호 삭제
	void deleteByPassword(String password);
	
}
