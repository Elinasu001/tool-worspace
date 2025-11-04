package com.kh.start.exception;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

//3-1. 유효성 검사 ==> Validator에게 위임
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
	//badRequest 400
	// 401
	private ResponseEntity<Map<String, String>> createResponseEntity(RuntimeException e, HttpStatus status){
		Map<String, String> error = new HashMap();
		error.put("error-message", e.getMessage());
		return ResponseEntity.status(status).body(error); // badRequest는 변경될 수 있으니 status로 처리
	}
	
	@ExceptionHandler(InvalidParameterException.class)
    public ResponseEntity<Map<String, String>> InvalidParameter(InvalidParameterException e){		
		return createResponseEntity(e, HttpStatus.UNAUTHORIZED);
	}
	
	
	@ExceptionHandler(CustomAuthenticationException.class) // HttpStatus 매개변수 추가
	public ResponseEntity<Map<String, String>> handleAuth(CustomAuthenticationException e){
		return createResponseEntity(e, HttpStatus.UNAUTHORIZED);
	}
	
	
	
	@ExceptionHandler(UserNameNotFoundException.class)
	public ResponseEntity<?> handlerUsernameNotFound(UserNameNotFoundException e){
		return createResponseEntity(e, HttpStatus.UNAUTHORIZED);
	}
	
	
	
	// 3-2. 아이디 중복 검사
	@ExceptionHandler(IdDuplicateException.class)
	public ResponseEntity<?> handlerDuplicateId(IdDuplicateException e){
		return createResponseEntity(e, HttpStatus.UNAUTHORIZED);
	} // 400 => 이 경우 json으로 반환
	
	// 3-1. 유효성 검사
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<?> handlerArgumentsNotValid(MethodArgumentNotValidException e){
		
		// 필드가 하나가 틀릴 수도, 여러가지가 틀리 수가 있는데, 필드들의 에러를 리스트로 받을 수 있다.
		List<FieldError> list = e.getBindingResult().getFieldErrors();// 정보들이 많이 들어갈 수 있으니 필드의 에러를 반환해주는 메소드를 사용한다.
		
		/*
		// test
		for(int i = 0; i < list.size(); i++) {
			log.info("예외발생 필드명: {}, 발생한 이유 : {}", list.get(i).getField(), list.get(i).getDefaultMessage());
		}
		*/
											
		Map<String, String> errors = new HashMap();							// key value
		e.getBindingResult().getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
		
		
		return ResponseEntity.badRequest().body(errors);
	}
	
	
}
