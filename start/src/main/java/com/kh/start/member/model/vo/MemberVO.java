package com.kh.start.member.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

// 불변객체로 SETTER X , NoArgsConstructor X
@Getter
@AllArgsConstructor
@Builder // 3-3. ROLE 추가
@ToString
public class MemberVO {
	
	private String memberId;
	private String memberPwd;
	private String memberName;
	private String role;
	
}


// lombak 사용하면 ... ? 