package com.kh.start.comment.model.service;

import java.util.List;

import com.kh.start.auth.model.vo.CustomUserDetails;
import com.kh.start.comment.model.dto.CommentDTO;
import com.kh.start.comment.model.vo.CommentVO;

public interface CommentService {
	
	// 인서트 : insert -	CONTROLLER에서 DTO로 넘어와야된다.
	CommentVO save(CommentDTO comment, CustomUserDetails userDetails);
	
	
	// 조회 : select
	List<CommentDTO> findAll(Long boardNo);
}
