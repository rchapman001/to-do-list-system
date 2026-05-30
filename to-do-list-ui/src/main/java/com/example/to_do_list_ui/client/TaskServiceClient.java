package com.example.to_do_list_ui.client;

import com.example.to_do_list_ui.dto.external.response.TaskApiResponse;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class TaskServiceClient {

  @Autowired private WebClient taskServiceWebClient;

  public String testEndpointTask() {
    return taskServiceWebClient
        .get()
        .uri("/tasks/test")
        .retrieve()
        .bodyToMono(String.class)
        .block();
  }

  public List<TaskApiResponse> getTasksByListId(Long listId) {
    return taskServiceWebClient
        .get()
        .uri("/tasks/list/{id}", listId)
        .retrieve()
        .bodyToFlux(TaskApiResponse.class)
        .collectList()
        .block();
  }
}
