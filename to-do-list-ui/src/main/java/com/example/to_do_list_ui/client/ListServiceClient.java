package com.example.to_do_list_ui.client;

import com.example.to_do_list_ui.dto.external.response.ToDoListApiResponse;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class ListServiceClient {

  @Autowired private WebClient listServiceWebClient;

  public String testEndpointList() {
    return listServiceWebClient
        .get()
        .uri("/lists/test")
        .retrieve()
        .bodyToMono(String.class)
        .block();
  }

  public List<ToDoListApiResponse> getAllToDoLists() {
    return listServiceWebClient
        .get()
        .uri("/lists")
        .retrieve()
        .bodyToFlux(ToDoListApiResponse.class)
        .collectList()
        .block();
  }
}
