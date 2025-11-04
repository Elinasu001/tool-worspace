package com.kh.start.board.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kh.start.auth.model.vo.CustomUserDetails;
import com.kh.start.board.model.dto.BoardDTO;
import com.kh.start.board.model.service.BoardService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@Validated
@RequestMapping("/boards")
@RequiredArgsConstructor
public class BoardController {
	
	private final BoardService boardService;
	
	// 게시글 작성 + 첨부파일 있는 @Authentication의 principal 에 담아놓은 UserDetails를 매개변수로 받을 수 있음 ==> session 에 getAttribute 필요 없음
	// ※ 권장방법임. 
	// @@AuthenticationPrincipal로 
	@PostMapping
	public ResponseEntity<?> save(@Valid BoardDTO board,
								@RequestParam(name="file", required=false) MultipartFile file,
								@AuthenticationPrincipal CustomUserDetails userDetails){
		
		//log.info("게시글 정보 : {}, 파일 정보 : {} ", board, file.getOriginalFilename()); // boards > post : body : form-data : key: value (token 설정 후) 
		//log.info("이게뭔데 : {}", userDetails.getUsername());  // boards > post : body : form-data : key: value (token 설정 후) // admin 필요한 값 확인
		
		boardService.save(board, file, userDetails.getUsername());
		return ResponseEntity.status(HttpStatus.CREATED).build();
		
	}
	
	
	// 전체조회
	// Get boards
	@GetMapping
	public ResponseEntity<List<BoardDTO>> findAll(
	    
	    @RequestParam(name = "page", defaultValue = "0") int pageNo) {

	    //if (pageNo < 0) pageNo = 0; // 음수 방지

	    List<BoardDTO> boards = boardService.findAll(pageNo);
	    return ResponseEntity.ok(boards);
	}
	
	
	
	//@GET/boards/PrimaryKey
	// 단일 조회
	@GetMapping("/{boardNo}")
	public ResponseEntity<BoardDTO> findByBoardNo(@PathVariable(name="boardNo") 
													@Min(value=1, message="넘작아요") Long boardNo){
		BoardDTO board = boardService.findByBoardNo(boardNo);
		return ResponseEntity.ok(board); // localhost:8080/boards/1 검색
	}
	
	
	@PutMapping("/{boardNo}")
	public ResponseEntity<BoardDTO> update(@PathVariable(name="boardNo") Long boardNo,
											BoardDTO board,
											@RequestParam(name="file", required=false)
											MultipartFile file,
											@AuthenticationPrincipal CustomUserDetails userDetails){
		BoardDTO b = boardService.update(board, file, boardNo, userDetails); // vo로 돌리는게 좋음
		log.info("b: {}", b);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
	
	
	@DeleteMapping("/{boardNo}")
	public ResponseEntity<?> deleteByBoardNo(@PathVariable(name="boardNo") Long boardNo,
											@AuthenticationPrincipal CustomUserDetails userDetails){
		boardService.deleteByBoardNo(boardNo, userDetails);
		return ResponseEntity.ok().build();
	}
	
	
	
	
	
}
