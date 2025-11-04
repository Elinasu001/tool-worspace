package com.kh.start.board.model.service;

import java.security.InvalidParameterException;
import java.util.List;

import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.kh.start.auth.model.vo.CustomUserDetails;
import com.kh.start.board.model.dao.BoardMapper;
import com.kh.start.board.model.dto.BoardDTO;
import com.kh.start.board.model.vo.BoardVO;
import com.kh.start.exception.CustomAuthenticationException;
import com.kh.start.file.service.FileService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

	private final BoardMapper boardMapper;
	private final FileService fileService;
	
	@Override
	public void save(BoardDTO board, MultipartFile file, String username) {
		
		// 유효성 검증 valid로 퉁
		// 권한검증 -> ROLE로 함
		
		BoardVO b = null;
		// 첨부파일 관련 값
		if(file != null && !file.isEmpty()) {
			// 이름 바꾸기~
			// 원본파일명에서 확장자 뽑기~
			// 저장위치 정해야함~
			// 파일 올리는 메소드 호출~
			String filePath = fileService.store(file);
			
			 b = BoardVO.builder()
							   .boardTitle(board.getBoardTitle())
							   .boardContent(board.getBoardContent())
							   .boardWriter(username)
							   .fileUrl(filePath)
							   .build();
			// title, content, writer, file INSERT => 변하지 않은 값을 넣어야됨.
			
		} else {
			
			 b = BoardVO.builder()
					   .boardTitle(board.getBoardTitle())
					   .boardContent(board.getBoardContent())
					   .boardWriter(username)
					   .build();
		}
		
		boardMapper.save(b);
	}
	


	@Override
	public List<BoardDTO> findAll(int pageNo) {
		
		if(pageNo < 0) {
			throw new InvalidParameterException("유효하지 않은 접근입니다.");
		}
		
		RowBounds rb = new RowBounds(pageNo * 3, 3);
		
		return boardMapper.findAll();
	}

	/**
	 *	단일조회
	 */
	@Override
	public BoardDTO findByBoardNo(Long boardNo) {
		return getBoardOrThrow(boardNo);
		
	}
	
	// 내부 공통 : 유효성검증
	private BoardDTO getBoardOrThrow(Long boardNo) {
		BoardDTO board = boardMapper.findByBoardNo(boardNo);
		if(board == null) {
			throw new InvalidParameterException("유효하지 않은 접근입니다."); // ConstraintViolationException 이거 잡아주기
		}
		return board;
	}

	/**
	 * 수정
	 */
	@Override // update BoardDTO 말고 BoardVO로 가져오는게 맞음
	public BoardDTO update(BoardDTO board, MultipartFile file
											, Long boardNo, CustomUserDetails userDetails) {
		
		// 1. 게시글번호가 존재하는 게시글인가
		// 2. 현재 토큰 소유주가 게시글작성자와 일치하는가
		// 3. 새로운 파일이 첨부되었는가
		// 4. 만약 첨부되었다면 새롭게 파일을 업로드 한 후 새로운 패스로 변경
		// 5. 전부 오카이다 그러면 UPDATE
		/*
		BoardDTO b = getBoardOrThrow(boardNo);

		if(!b.getBoardWriter().equals(userDetails.getUsername())) {
			throw new CustomAuthenticationException("게시글 작성자가 아닙니다");
		}
		*/
		
		validateBoard(boardNo, userDetails);
		
		board.setBoardNo(boardNo);
		if(file != null && !file.isEmpty()) {		// 따로빼기
			String filePath = fileService.store(file);
			board.setFileUrl(filePath);
		}
		
		boardMapper.update(board);
		
		return board; // inesert 도 이 처럼 하는게 좋음
	}
	
	private void validateBoard(Long boardNo, CustomUserDetails userDetails) {
		BoardDTO b = getBoardOrThrow(boardNo);

		if(!b.getBoardWriter().equals(userDetails.getUsername())) {
			throw new CustomAuthenticationException("게시글 작성자가 아닙니다");
		}
	}
	
	
	@Override
	public void deleteByBoardNo(Long boardNo, CustomUserDetails userDetails) {
		/*
		BoardDTO board = getBoardOrThrow(boardNo);
		if(!board.getBoardWriter().equals(userDetails.getUsername())) {
			throw new CustomAuthenticationException("게시글작성자가 아닙니다.");
		}
		*/
		validateBoard(boardNo, userDetails);
		boardMapper.deleteByBoardNo(boardNo);
	}

}

/**boardservice는 crud 유효성검사는 class로 따로 빼서 bean으로 주입받아서 사용하기**/
