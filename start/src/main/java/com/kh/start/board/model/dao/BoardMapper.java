package com.kh.start.board.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.web.multipart.MultipartFile;

import com.kh.start.board.model.dto.BoardDTO;
import com.kh.start.board.model.vo.BoardVO;

@Mapper
public interface BoardMapper {
	
	int save(BoardVO board);
	
	List<BoardDTO> findAll();
	
	BoardDTO findByBoardNo(Long boardNo);
	 
	void update(BoardDTO board);
	
	void deleteByBoardNo(Long boardNo);
	
}
