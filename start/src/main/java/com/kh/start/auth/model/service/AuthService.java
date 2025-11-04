package com.kh.start.auth.model.service;

import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.kh.start.member.model.dto.MemberDTO;

public interface AuthService {
	
	Map<String, String> login(MemberDTO member);
}
