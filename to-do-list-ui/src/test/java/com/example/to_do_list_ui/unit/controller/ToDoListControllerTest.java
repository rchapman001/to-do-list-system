package com.example.to_do_list_ui.unit.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.example.to_do_list_ui.client.ListServiceClient;
import com.example.to_do_list_ui.client.TaskServiceClient;
import com.example.to_do_list_ui.client.WeatherServiceClient;
import com.example.to_do_list_ui.controller.ToDoListController;
import com.example.to_do_list_ui.dto.external.response.ForecastSummaryResponse;
import com.example.to_do_list_ui.dto.external.response.TaskApiResponse;
import com.example.to_do_list_ui.dto.external.response.ToDoListApiResponse;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ExtendedModelMap;

@ExtendWith(MockitoExtension.class)
public class ToDoListControllerTest {

  @Mock private ListServiceClient listServiceClient;
  @Mock private TaskServiceClient taskServiceClient;
  @Mock private WeatherServiceClient weatherServiceClient;

  @InjectMocks private ToDoListController toDoListController;

  @Test
  void givenMockedServices_whenShowTestPage_thenReturnsTestPage() {
    // Arrange
    String testList = "List Service Test Response";
    String testTask = "Task Service Test Response";
    String testWeather = "Weather Service Test Response";

    when(listServiceClient.testEndpointList()).thenReturn(testList);
    when(taskServiceClient.testEndpointTask()).thenReturn(testTask);
    when(weatherServiceClient.testEndpointWeather()).thenReturn(testWeather);

    ExtendedModelMap model = new ExtendedModelMap();

    // Act
    String viewName = toDoListController.showTestPage(model);

    // Assert
    assertEquals("test", viewName);

    assertTrue(model.containsAttribute("testDataList"));
    assertEquals(testList, model.getAttribute("testDataList"));

    assertTrue(model.containsAttribute("testDataTask"));
    assertEquals(testTask, model.getAttribute("testDataTask"));

    assertTrue(model.containsAttribute("testDataWeather"));
    assertEquals(testWeather, model.getAttribute("testDataWeather"));
  }

  @Test
  void givenMockedServices_whenShowHomePage_thenReturnsHomePage() {
    // Arrange
    ToDoListApiResponse toDoList = new ToDoListApiResponse();
    toDoList.setListId(1L);
    toDoList.setListName("Test List");

    TaskApiResponse task = new TaskApiResponse();
    task.setTaskId(1L);
    task.setListId(1L);
    task.setTaskName("Test Task");
    task.setTaskDate(LocalDate.of(2021, 1, 1));
    task.setTaskStatus("TO_DO");

    ForecastSummaryResponse forecastSummary = new ForecastSummaryResponse();
    forecastSummary.setName("Tonight");
    forecastSummary.setTemperature(1);
    forecastSummary.setWindSpeed("5 to 10 mph");
    forecastSummary.setDetailedForecast(
        "Mostly cloudy, with a low around 1. Wind chill values as low as -15. North northwest wind 10 to 15 mph.");

    when(listServiceClient.getAllToDoLists()).thenReturn(List.of(toDoList));
    when(taskServiceClient.getTasksByListId(1L)).thenReturn(List.of(task));
    when(weatherServiceClient.getForecastSummary()).thenReturn(List.of(forecastSummary));

    ExtendedModelMap model = new ExtendedModelMap();

    // Act
    String viewName = toDoListController.showHomePage(model);

    // Assert
    assertEquals("home", viewName);

    // --- toDoLists ---
    List<ToDoListApiResponse> toDoLists =
        (List<ToDoListApiResponse>) model.getAttribute("toDoLists");
    assertNotNull(toDoLists);
    assertEquals(1, toDoLists.size());
    ToDoListApiResponse actualList = toDoLists.get(0);
    assertEquals(1L, actualList.getListId());
    assertEquals("Test List", actualList.getListName());

    // --- tasks ---
    List<TaskApiResponse> tasks = (List<TaskApiResponse>) model.getAttribute("tasks");
    assertNotNull(tasks);
    assertEquals(1, tasks.size());
    TaskApiResponse actualTask = tasks.get(0);
    assertEquals(1L, actualTask.getTaskId());
    assertEquals(1L, actualTask.getListId());
    assertEquals("Test Task", actualTask.getTaskName());
    assertEquals(LocalDate.of(2021, 1, 1), actualTask.getTaskDate());
    assertEquals("TO_DO", actualTask.getTaskStatus());

    // --- forecastSummaryResponseList ---
    List<ForecastSummaryResponse> forecasts =
        (List<ForecastSummaryResponse>) model.getAttribute("forecastSummaryResponseList");
    assertNotNull(forecasts);
    assertEquals(1, forecasts.size());
    ForecastSummaryResponse actualForecast = forecasts.get(0);
    assertEquals("Tonight", actualForecast.getName());
    assertEquals(1, actualForecast.getTemperature());
    assertEquals("5 to 10 mph", actualForecast.getWindSpeed());
    assertEquals(
        "Mostly cloudy, with a low around 1. Wind chill values as low as -15. North northwest wind 10 to 15 mph.",
        actualForecast.getDetailedForecast());
  }
}
