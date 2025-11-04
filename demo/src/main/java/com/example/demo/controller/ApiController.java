package com.example.demo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.dto.Comment;
import com.example.demo.model.service.ApiService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@CrossOrigin("*")
@RequestMapping("api")
@RequiredArgsConstructor
public class ApiController {
	
	private final ApiService apiService;
	
	@GetMapping("busans")	// 전체 조회 : pageNo
	public ResponseEntity<String> getFoods(@RequestParam(name="pageNo")int pageNo) {
		String responseData = apiService.requestBusan(pageNo);
		return ResponseEntity.ok(responseData);
	}
	
	@GetMapping("busan/{num}")
	public String getBusanDetail(@PathVariable("num") int num) {
		System.out.println("ApiController");
	    return apiService.requestBusanDetail(num);
	}
	
	@PostMapping("comments") // @RequestBody에서 값을 뽑아야 함
	public ResponseEntity<Integer> saveComment(@RequestBody Comment comment) {
		log.info("코멘트 넘어옴: {}", comment);
		apiService.saveComment(comment); // int result = 
		return ResponseEntity.status(HttpStatus.CREATED).build(); // 객체 데이터 응답할 때 사용 // ok 201 잘 됐다고 돌려보내기
	}
	
	@GetMapping("comments/{id}")  //ResponseEntity 객체를 응답할때 사용하는 클래스 타입
	public ResponseEntity<List<Comment>> selectAll(@PathVariable(value="id") Long seq){
		List<Comment> comments = apiService.selectAll(seq);
		return ResponseEntity.ok(comments); // body에다가 comments 포함시켜준다.
	}
	
}
