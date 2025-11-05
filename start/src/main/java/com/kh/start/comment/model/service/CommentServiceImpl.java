package com.kh.start.comment.model.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kh.start.auth.model.vo.CustomUserDetails;
import com.kh.start.board.model.service.BoardService;
import com.kh.start.comment.model.dao.CommentMapper;
import com.kh.start.comment.model.dto.CommentDTO;
import com.kh.start.comment.model.vo.CommentVO;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
	
	private final BoardService boardService;
	private final CommentMapper commentMapper;
	
	@Override
	public CommentVO save(CommentDTO comment, CustomUserDetails userDetails) {
		
		// 1. 게시글 있는가? ( 없는데 댓글 달려고 하면 sqlexception 발생)
		boardService.findByBoardNo(comment.getRefBoardNo()); // private 못불러오니 외부에 노출된 메소드로 호출한다.
		
		String memberId = userDetails.getUsername();
		
		CommentVO c = CommentVO.builder()
								.commentWriter(memberId)
								.commentContent(comment.getCommentContent())
								.refBoardNo(comment.getRefBoardNo())
								.build(); // 당연히 commentNo, createDate null 이 나옴 인서트 안받았음
		commentMapper.save(c);
		
		return c;
	}
	
	// 게시글이 없을 수도 있다!
	@Override
	public List<CommentDTO> findAll(Long boardNo) {
		
		boardService.findByBoardNo(boardNo);
		
		return commentMapper.findAll(boardNo);
		
	}

}
