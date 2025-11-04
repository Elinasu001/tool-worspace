package com.example.demo.model.service;

import java.util.List;
import com.example.demo.model.dto.Comment;

public interface ApiService {
    String requestBeef();
    String requestBlog(String query);
    String requestBusan(int pageNo);
    String requestBusanDetail(int num);
    void saveComment(Comment comment);
    List<Comment> selectAll(Long seq);
}
