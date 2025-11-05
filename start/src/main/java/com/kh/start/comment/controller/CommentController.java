package com.kh.start.comment.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kh.start.auth.model.vo.CustomUserDetails;
import com.kh.start.comment.model.dto.CommentDTO;
import com.kh.start.comment.model.service.CommentService;
import com.kh.start.comment.model.vo.CommentVO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {
	
	private final CommentService commentService;
	
	
	
	@PostMapping
	public ResponseEntity<CommentVO> save(@RequestBody CommentDTO comment,
								 @AuthenticationPrincipal CustomUserDetails userDetails){
		
		CommentVO c = commentService.save(comment, userDetails);
		return ResponseEntity.status(HttpStatus.CREATED).body(c);
		
	}
	
	
	@GetMapping	// PathVariable : id값 가조올 경우,  RequestParam 구분하기
	public ResponseEntity<List<CommentDTO>> findAll(@RequestParam(name="boardNo") Long boardNo){
		return ResponseEntity.ok(commentService.findAll(boardNo));
	}
	
	
	
	
}

/**
 * 
 * @PathVariable @RequestParam
 * - HTTP 요청에서 값을 추출한다.
 * 
 * @PathVariable
 * - URL에서 값을 추출한다.
 * - @RequestParam과 마찬가지로 Variable 이름과 Argument 이름이 동일하면 값 속성을 생략
 * - 차이점 : 
 *   URL에서 바로 데이터를 추출
 *   
 *   
 *   
 * @RequestParam
 * - URL에서 요청 매개 변수를 가져온다.
 * - 만약 Query Parameter 변수의 이름과 Argument 이름이 동일한 경우,
 *   Query Parameter의 이름을 지정하지 않고 @RequestParam을 사용할 수 있다.
 * - 차이점 :
 *   Query Parameter를 추출하는데 사용,
 *   required 속성 및 defaultValue 속성을 사용하여 비어있는 경우 기본값을 지정할 수 있다.
 *   
 * - RESTful Web Service에서는 @PathVariable이 더 적합
 * 
 * **/
