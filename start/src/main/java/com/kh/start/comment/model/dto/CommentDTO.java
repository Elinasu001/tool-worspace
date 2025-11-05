package com.kh.start.comment.model.dto;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {
	
	private Long commentNo;
	private String commentContent;
	private String commentWriter;
	private Date createDate;
	private Long refBoardNo;
	
}
