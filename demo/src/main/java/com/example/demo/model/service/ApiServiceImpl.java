package com.example.demo.model.service;

import java.io.*;
import java.net.*;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.example.demo.model.dao.CommentMapper;
import com.example.demo.model.dto.Comment;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApiServiceImpl implements ApiService {

    private final CommentMapper mapper;

    @Override
    public String requestBeef() {
        final String API_KEY = "e7890a0a5df5768248d2ec1be79503b46e6170116a0c6030636eda56d2566ccd";
        StringBuilder sb = new StringBuilder();
        sb.append("http://211.237.50.150:7080/openapi/");
        sb.append(API_KEY);
        sb.append("/json/Grid_20200713000000000605_1/1/5");

        try {
            URI uri = new URI(sb.toString());
            RestTemplate restTemplate = new RestTemplate();
            return restTemplate.getForObject(uri, String.class);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String requestBlog(String query) {
        String clientId = "6SgiAAYZkuHSnSwQARwu";
        String clientSecret = "mFoJw9rUbA";
        try {
            query = URLEncoder.encode(query, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("검색어 인코딩 실패", e);
        }

        String apiURL = "https://openapi.naver.com/v1/search/shop?query=" + query + "&display=10&sort=date";

        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("X-Naver-Client-Id", clientId);
        requestHeaders.put("X-Naver-Client-Secret", clientSecret);

        return get(apiURL, requestHeaders);
    }

    private String get(String apiUrl, Map<String, String> headers) {
        try {
            HttpURLConnection con = (HttpURLConnection) new URL(apiUrl).openConnection();
            con.setRequestMethod("GET");
            for (Map.Entry<String, String> entry : headers.entrySet())
                con.setRequestProperty(entry.getKey(), entry.getValue());

            try (InputStream input = con.getResponseCode() == 200 ? con.getInputStream() : con.getErrorStream()) {
                return readBody(input);
            }
        } catch (IOException e) {
            throw new RuntimeException("API 요청 실패", e);
        }
    }

    private String readBody(InputStream body) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(body, "UTF-8"));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null)
            sb.append(line);
        return sb.toString();
    }

    @Override
    public String requestBusan(int pageNo) {
        final String SERVICE_KEY = "?serviceKey=bafa38a635253c840faba210012e6455cd784e0696c222a50f143cbe54696f39";
        String url = "https://apis.data.go.kr/6260000/FoodService/getFoodKr"
                   + SERVICE_KEY + "&pageNo=" + pageNo + "&numOfRows=6&resultType=json";
        return new RestTemplate().getForObject(url, String.class);
    }

    @Override
    public String requestBusanDetail(int num) {
        final String SERVICE_KEY = "?serviceKey=bafa38a635253c840faba210012e6455cd784e0696c222a50f143cbe54696f39";
        String url = "https://apis.data.go.kr/6260000/FoodService/getFoodKr"
                   + SERVICE_KEY + "&pageNo=1&numOfRows=1&resultType=json&UC_SEQ=" + num;
        return new RestTemplate().getForObject(url, String.class);
    }

    @Override
    public void saveComment(Comment comment) {
        mapper.saveComment(comment);
    }

    @Override
    public List<Comment> selectAll(Long seq) {
        return mapper.selectComment(seq);
    }
}
