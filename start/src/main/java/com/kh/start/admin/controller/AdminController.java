package com.kh.start.admin.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("admin")
public class AdminController {
	// 한 후 db에 role admin 한 후 request
	// ROLE VARCHAR2(20) DEFAULT 'ROLE_USER' CHECK (ROLE IN ('ROLE_USER', 'ROLE_ADMIN')),
}
