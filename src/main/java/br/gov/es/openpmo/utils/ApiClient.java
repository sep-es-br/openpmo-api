/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gov.es.openpmo.utils;

import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 *
 * @author gean.carneiro
 */
public class ApiClient {
    
    private final WebClient webClient;

    public ApiClient(String baseUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }
    
    private Mono<JSONObject> execute(String url, String token, HttpMethod method, Object body) {

        WebClient.RequestBodySpec request = webClient
                .method(method)
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token);

        WebClient.ResponseSpec response =
                body != null ? request.bodyValue(body).retrieve()
                             : request.retrieve();

        return response
                .bodyToMono(String.class)
                .map(JSONObject::new);
    }
    
    public Mono<JSONObject> doGetRequest(String url, String token){
        return execute(url, token, HttpMethod.GET, null);
    }
    
    public Mono<JSONObject> doPutRequest(String url, Object body, String token){
        return execute(url, token, HttpMethod.POST, body);
    }
    
    public Mono<JSONObject> doPostRequest(String url, Object body, String token){
        return execute(url, token, HttpMethod.PUT, body);
    }
    
}
