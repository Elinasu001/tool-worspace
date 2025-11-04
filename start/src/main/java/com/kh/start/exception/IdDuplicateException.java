package com.kh.start.exception;

// 3-2. 아이디 중복 검사
public class IdDuplicateException extends RuntimeException {
	
	public IdDuplicateException(String message) {
		super(message);
	}
	
}
