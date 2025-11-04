package com.kh.start.board.model.dto;

import java.sql.Date;

import org.hibernate.validator.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BoardDTO {
	
	private Long boardNo;
	private String boardTitle;
	private String boardContent;
	private String boardWriter;
	private String fileUrl;
	private String status;
	private Date createDate;
	
}

/**
 * 전제조건 로그인 + 토큰
 **/
