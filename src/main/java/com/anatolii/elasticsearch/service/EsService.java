package com.anatolii.elasticsearch.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class EsService {
    public static class Article{
        private String title;
        private String text;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
    private final static String INDEX_NAME = "articles";
    private final ObjectMapper mapper = new ObjectMapper();
    private final RestHighLevelClient esClient;

    public EsService(RestHighLevelClient esClient) {
        this.esClient = esClient;
    }

    public void updateArticle(String id, String title, String text) throws IOException {
        Article article = new Article();
        article.setTitle(title);
        article.setText(text);

        IndexRequest indexRequest = new IndexRequest(INDEX_NAME);
        indexRequest.id(id);
        indexRequest.source(mapper.writeValueAsString(article), XContentType.JSON);

        esClient.index(indexRequest, RequestOptions.DEFAULT);
    }

    public List<Article> search(String searchString) throws IOException {
        SearchRequest searchRequest = new SearchRequest(INDEX_NAME);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("text", searchString));
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = esClient.search(searchRequest, RequestOptions.DEFAULT);
        List<Article> articles = new ArrayList<>();
        for(SearchHit hit : searchResponse.getHits().getHits()){
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();

            Article article = new Article();
            article.setTitle((String) sourceAsMap.get("title"));
            article.setText((String) sourceAsMap.get("text"));
            articles.add(article);
        }
        return articles;
    }
}
