package com.example.to_do_list_ui.controller;

import com.example.to_do_list_ui.client.ListServiceClient;
import com.example.to_do_list_ui.client.TaskServiceClient;
import com.example.to_do_list_ui.client.WeatherServiceClient;
import com.example.to_do_list_ui.dto.external.response.ForecastSummaryResponse;
import com.example.to_do_list_ui.dto.external.response.TaskApiResponse;
import com.example.to_do_list_ui.dto.external.response.ToDoListApiResponse;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/to-do-list")
public class ToDoListController {

  private static final Logger logger = LoggerFactory.getLogger(ToDoListController.class);

  @Autowired private ListServiceClient listServiceClient;

  @Autowired private TaskServiceClient taskServiceClient;

  @Autowired private WeatherServiceClient weatherServiceClient;

  @GetMapping("/test")
  public String showTestPage(Model model) {
    String testDataList = listServiceClient.testEndpointList();
    String testDataTask = taskServiceClient.testEndpointTask();
    String testDataWeather = weatherServiceClient.testEndpointWeather();
    model.addAttribute("testDataList", testDataList);
    model.addAttribute("testDataTask", testDataTask);
    model.addAttribute("testDataWeather", testDataWeather);
    return "test";
  }

  @GetMapping("/home")
  public String showHomePage(Model model) {
    List<ToDoListApiResponse> toDoLists = listServiceClient.getAllToDoLists();
    if (!toDoLists.isEmpty()) {
      ToDoListApiResponse toDoList = toDoLists.get(0);
      List<TaskApiResponse> tasks = new ArrayList<>();
      try {
        tasks = taskServiceClient.getTasksByListId(toDoList.getListId());
      } catch (Exception e) {
        logger.info("Tasks not found for listId: " + toDoList.getListId());
      }
      model.addAttribute("toDoLists", toDoLists);
      model.addAttribute("tasks", tasks);
    }
    List<ForecastSummaryResponse> forecastSummaryResponseList =
        weatherServiceClient.getForecastSummary();
    model.addAttribute("forecastSummaryResponseList", forecastSummaryResponseList);
    return "home";
  }
}
