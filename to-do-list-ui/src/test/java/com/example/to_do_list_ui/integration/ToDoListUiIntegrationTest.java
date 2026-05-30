package com.example.to_do_list_ui.integration;

import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.to_do_list_ui.config.WireMockConfig;
import com.example.to_do_list_ui.constants.UiElementConstants;
import com.example.to_do_list_ui.util.WireMockStubUtil;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.stubbing.Scenario;
import com.microsoft.playwright.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
    properties = "server.port=8080")
@ActiveProfiles("test")
@Execution(ExecutionMode.SAME_THREAD)
@Import({WireMockConfig.class, WireMockStubUtil.class, UiElementConstants.class})
public class ToDoListUiIntegrationTest {

  @Autowired private WireMockServer wireMockListService;

  @Autowired private WireMockServer wireMockTaskService;

  @Autowired private WireMockServer wireMockWeatherService;

  private boolean headless = true; // Set to true for headless mode (no browser UI)

  @BeforeEach
  void setUp() {
    // Reset WireMock before each test to ensure clean state
    wireMockListService.resetAll();
    wireMockTaskService.resetAll();
    wireMockWeatherService.resetAll();

    WireMockStubUtil.addCorsHeadersForPreflight(wireMockListService);
    WireMockStubUtil.addCorsHeadersForPreflight(wireMockTaskService);
    WireMockStubUtil.addCorsHeadersForPreflight(wireMockWeatherService);
  }

  @Test
  public void showTestPageIntegrationTest() {

    wireMockListService.stubFor(
        WireMock.get(WireMock.urlEqualTo("/lists/test"))
            .willReturn(
                WireMock.aResponse()
                    .withHeader("Content-Type", "text/plain")
                    .withBody("testEndpoint ListService")));

    wireMockTaskService.stubFor(
        WireMock.get(WireMock.urlEqualTo("/tasks/test"))
            .willReturn(
                WireMock.aResponse()
                    .withHeader("Content-Type", "text/plain")
                    .withBody("testEndpoint TaskService")));

    wireMockWeatherService.stubFor(
        WireMock.get(WireMock.urlEqualTo("/forecast/test"))
            .willReturn(
                WireMock.aResponse()
                    .withHeader("Content-Type", "text/plain")
                    .withBody("testEndpoint WeatherService")));

    // Playwright Test Execution
    try (com.microsoft.playwright.Playwright playwright =
        com.microsoft.playwright.Playwright.create()) {
      com.microsoft.playwright.Browser browser =
          playwright
              .chromium()
              .launch(
                  new com.microsoft.playwright.BrowserType.LaunchOptions()
                      .setHeadless(headless)); // Set to true for headless mode (no browser UI)
      com.microsoft.playwright.Page page = browser.newPage();

      // Navigate to a specific page
      page.navigate("http://localhost:8080/to-do-list/test");
      page.waitForLoadState();

      // Locate the <p> elements that display the connection messages
      String listServiceText =
          page.locator(UiElementConstants.TEST_PAGE_LIST_SERVICE_TEST_MESSAGE).textContent();
      String taskServiceText =
          page.locator(UiElementConstants.TEST_PAGE_TASK_SERVICE_TEST_MESSAGE).textContent();
      String weatherServiceText =
          page.locator(UiElementConstants.TEST_PAGE_WEATHER_SERVICE_TEST_MESSAGE).textContent();

      // Assert the service connection messages are correct
      assertThat(listServiceText).isEqualTo("List Service Connection: testEndpoint ListService");
      assertThat(taskServiceText).isEqualTo("Task Service Connection: testEndpoint TaskService");
      assertThat(weatherServiceText)
          .isEqualTo("Weather Service Connection: testEndpoint WeatherService");

      // Close browser after test
      browser.close();
    }
  }

  @Test
  public void showHomePageIntegrationTest() throws InterruptedException {

    wireMockListService.stubFor(
        WireMock.get(WireMock.urlEqualTo("/lists"))
            .willReturn(
                WireMock.aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withBody("[{\"listId\":1,\"listName\":\"Groceries\"}]")));

    wireMockTaskService.stubFor(
        WireMock.get(WireMock.urlEqualTo("/tasks/list/1"))
            .willReturn(
                WireMock.aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        "[{\"taskId\":1,\"listId\":1,\"taskName\":\"Buy milk\",\"taskDate\":\"2025-03-23\",\"taskStatus\":\"TO_DO\"}]")));

    wireMockWeatherService.stubFor(
        WireMock.get(WireMock.urlEqualTo("/forecast"))
            .willReturn(
                WireMock.aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        "{\"name\":\"Tonight\",\"temperature\":19,\"windSpeed\":\"5 to 15 mph\",\"detailedForecast\":\"Mostly clear, with a low around 19. North northwest wind 5 to 15 mph, with gusts as high as 30 mph.\"}")));

    try (com.microsoft.playwright.Playwright playwright =
        com.microsoft.playwright.Playwright.create()) {
      com.microsoft.playwright.Browser browser =
          playwright
              .chromium()
              .launch(
                  new com.microsoft.playwright.BrowserType.LaunchOptions()
                      .setHeadless(headless)); // Set to true for headless mode (no browser UI)
      com.microsoft.playwright.Page page = browser.newPage();

      // Navigate to a specific page
      page.navigate("http://localhost:8080/to-do-list/home");
      page.waitForLoadState();

      // page.pause();

      // Verify the correct Headers for each section exist
      assertEquals(
          "Lists", page.querySelector(UiElementConstants.HOME_PAGE_LIST_HEADER).innerText());
      assertEquals(
          "Tasks", page.querySelector(UiElementConstants.HOME_PAGE_TASK_HEADER).innerText());
      assertEquals(
          "Weather Forecast",
          page.querySelector(UiElementConstants.HOME_PAGE_WEATHER_HEADER).innerText());

      // Verify the name of the list is "Groceries"
      assertEquals(
          "Groceries",
          page.locator(UiElementConstants.HOME_PAGE_LIST_TABLE_FIRST_LIST_NAME).innerText());

      // Verify the heads of the Task Table all exist
      assertEquals(
          "Task Name",
          page.locator(UiElementConstants.HOME_PAGE_TASK_TABLE_TASK_NAME_HEADER).innerText());
      assertEquals(
          "Task Date",
          page.locator(UiElementConstants.HOME_PAGE_TASK_TABLE_TASK_DATE_HEADER).innerText());
      assertEquals(
          "Task Status",
          page.locator(UiElementConstants.HOME_PAGE_TASK_TABLE_TASK_STATUS_HEADER).innerText());
      assertEquals(
          "Action",
          page.locator(UiElementConstants.HOME_PAGE_TASK_TABLE_ACTION_HEADER).innerText());

      // Verify the first task in the Task Table is correct
      assertEquals(
          "Buy milk",
          page.locator(UiElementConstants.HOME_PAGE_TASK_TABLE_TASK_NAME_1_ROW)
              .innerText()); // Check if the task name is "Buy milk"
      assertEquals(
          "2025-03-23",
          page.locator(UiElementConstants.HOME_PAGE_TASK_TABLE_TASK_DATE_1_ROW)
              .innerText()); // Check if the task date is "2025-03-23"
      assertEquals(
          "TO_DO",
          page.locator(UiElementConstants.HOME_PAGE_TASK_TABLE_TASK_STATUS_1_ROW)
              .innerText()); // Check if the task status is "TO_DO"
      assertEquals(
          "Delete",
          page.locator(UiElementConstants.HOME_PAGE_TASK_TABLE_DELETE_TASK_BUTTON_1_ROW)
              .innerText()); // Verify the Delete button text if needed

      // Validate the weather forecast table
      assertEquals(
          "Temperature: 19°C",
          page.locator(UiElementConstants.HOME_PAGE_WEATHER_TABLE_TEMPERATURE).textContent(),
          "Temperature is incorrect");
      assertEquals(
          "Wind: 5 to 15 mph",
          page.locator(UiElementConstants.HOME_PAGE_WEATHER_TABLE_WIND).textContent(),
          "Wind information is incorrect");
      assertEquals(
          "Description: Mostly clear, with a low around 19. North northwest wind 5 to 15 mph, with gusts as high as 30 mph.",
          page.locator(UiElementConstants.HOME_PAGE_WEATHER_TABLE_DESCRIPTION).textContent(),
          "Description is incorrect");

      browser.close();
    }
  }

  @Test
  public void createListIntegrationTest() throws InterruptedException {
    // WireMockUtil.addCorsHeadersForPreflight(wireMockListService);

    // 1. Initial GET request to /lists - returns empty list
    wireMockListService.stubFor(
        WireMock.get(WireMock.urlEqualTo("/lists"))
            .inScenario("Create List Scenario")
            .whenScenarioStateIs(Scenario.STARTED)
            .willReturn(
                WireMock.aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withBody("[]"))); // No lists exist yet

    wireMockWeatherService.stubFor(
        WireMock.get(WireMock.urlEqualTo("/forecast"))
            .inScenario("Create List Scenario")
            .whenScenarioStateIs(Scenario.STARTED)
            .willReturn(
                WireMock.aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        "{\"name\":\"Tonight\",\"temperature\":19,\"windSpeed\":\"5 to 15 mph\",\"detailedForecast\":\"Mostly clear, with a low around 19. North northwest wind 5 to 15 mph, with gusts as high as 30 mph.\"}")));

    // 2. Stub the POST /lists request to simulate creating a new list and change state
    wireMockListService.stubFor(
        WireMock.post(WireMock.urlEqualTo("/lists"))
            .inScenario("Create List Scenario")
            .whenScenarioStateIs(Scenario.STARTED)
            .withRequestBody(equalToJson("{\"listName\": \"Work Tasks\"}"))
            .willReturn(
                WireMock.aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withStatus(201)
                    .withBody("{\"listId\":1,\"listName\":\"Work Tasks\"}")) // Simulated response
            .willSetStateTo("List Created")); // Changes scenario state

    // 3. GET /lists after list creation - returns the new list
    wireMockListService.stubFor(
        WireMock.get(WireMock.urlEqualTo("/lists"))
            .inScenario("Create List Scenario")
            .whenScenarioStateIs("List Created")
            .willReturn(
                WireMock.aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withBody("[{\"listId\":1,\"listName\":\"Work Tasks\"}]"))); // List now exists

    wireMockWeatherService.stubFor(
        WireMock.get(WireMock.urlEqualTo("/forecast"))
            .inScenario("Create List Scenario")
            .whenScenarioStateIs("List Created")
            .willReturn(
                WireMock.aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        "{\"name\":\"Tonight\",\"temperature\":19,\"windSpeed\":\"5 to 15 mph\",\"detailedForecast\":\"Mostly clear, with a low around 19. North northwest wind 5 to 15 mph, with gusts as high as 30 mph.\"}")));

    // 4. Use Playwright to test UI behavior
    try (Playwright playwright = Playwright.create()) {
      Browser browser =
          playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(headless));
      Page page = browser.newPage();

      // Navigate to the home page
      page.navigate("http://localhost:8080/to-do-list/home");

      // Wait for the page to load
      page.waitForLoadState();
      page.waitForTimeout(1000);

      // Verify the lists table is empty initially
      assertThat(page.locator(UiElementConstants.HOME_PAGE_LIST_TABLE_ROWS).count()).isEqualTo(0);

      // Create a new list
      page.locator(UiElementConstants.HOME_PAGE_LIST_TABLE_CREATE_LIST_BUTTON).click();
      page.locator(UiElementConstants.HOME_PAGE_CREATE_LIST_MODAL_LIST_NAME_INPUT)
          .fill("Work Tasks");
      page.locator(UiElementConstants.HOME_PAGE_CREATE_LIST_MODAL_SUBMIT_FROM_BUTTON).click();

      // Wait for the UI to update
      page.waitForLoadState();
      page.waitForTimeout(1000);

      // Verify the lists table now has 1 list and the name is correct
      assertThat(page.locator(UiElementConstants.HOME_PAGE_LIST_TABLE_ROWS).count()).isEqualTo(1);
      assertThat(
              page.locator(UiElementConstants.HOME_PAGE_LIST_TABLE_FIRST_LIST_NAME).textContent())
          .isEqualTo("Work Tasks");

      browser.close();
    }
  }

  @Test
  public void createTaskIntegrationTest() throws InterruptedException {
    // 1. Initial GET request to /lists - returns empty list
    wireMockListService.stubFor(
        WireMock.get(WireMock.urlEqualTo("/lists"))
            .inScenario("Create Task Scenario")
            .whenScenarioStateIs(Scenario.STARTED)
            .willReturn(
                WireMock.aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withBody("[{\"listId\":1,\"listName\":\"Work Tasks\"}]"))); // List now exists

    wireMockTaskService.stubFor(
        WireMock.get(WireMock.urlEqualTo("/tasks/status-options"))
            .inScenario("Create Task Scenario")
            .whenScenarioStateIs(Scenario.STARTED)
            .willReturn(
                WireMock.aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withStatus(200)
                    .withBody(
                        """
                            [
                                "TO_DO",
                                "IN_PROGRESS",
                                "DONE"
                            ]
                            """)));

    wireMockWeatherService.stubFor(
        WireMock.get(WireMock.urlEqualTo("/forecast"))
            .inScenario("Create Task Scenario")
            .whenScenarioStateIs(Scenario.STARTED)
            .willReturn(
                WireMock.aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        "{\"name\":\"Tonight\",\"temperature\":19,\"windSpeed\":\"5 to 15 mph\",\"detailedForecast\":\"Mostly clear, with a low around 19. North northwest wind 5 to 15 mph, with gusts as high as 30 mph.\"}")));

    wireMockTaskService.stubFor(
        WireMock.post(WireMock.urlEqualTo("/tasks"))
            .inScenario("Create Task Scenario")
            .whenScenarioStateIs(Scenario.STARTED)
            .withRequestBody(
                WireMock.equalToJson(
                    """
                    {
                        "listId": 1,
                        "taskName": "Test Task",
                        "taskDate": "2024-10-15",
                        "taskStatus": "TO_DO"
                    }
                """,
                    true,
                    true))
            .willReturn(
                WireMock.aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withStatus(201)
                    .withBody(
                        """
                            {
                                "taskId": 2,
                                "listId": 1,
                                "taskName": "Test Task",
                                "taskDate": "2024-10-15",
                                "taskStatus": "TO_DO"
                            }
                        """))
            .willSetStateTo("Task Created"));

    wireMockListService.stubFor(
        WireMock.get(WireMock.urlEqualTo("/lists"))
            .inScenario("Create Task Scenario")
            .whenScenarioStateIs("Task Created")
            .willReturn(
                WireMock.aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withBody("[{\"listId\":1,\"listName\":\"Work Tasks\"}]"))); // List now exists

    wireMockTaskService.stubFor(
        WireMock.get(WireMock.urlEqualTo("/tasks/list/1"))
            .inScenario("Create Task Scenario")
            .whenScenarioStateIs("Task Created")
            .willReturn(
                WireMock.aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withStatus(200)
                    .withBody(
                        """
                            [
                                {
                                    "taskId": 2,
                                    "listId": 1,
                                    "taskName": "Test Task",
                                    "taskDate": "2024-10-15",
                                    "taskStatus": "TO_DO"
                                }
                            ]
                            """)));

    wireMockWeatherService.stubFor(
        WireMock.get(WireMock.urlEqualTo("/forecast"))
            .inScenario("Create Task Scenario")
            .whenScenarioStateIs("Task Created")
            .willReturn(
                WireMock.aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        "{\"name\":\"Tonight\",\"temperature\":19,\"windSpeed\":\"5 to 15 mph\",\"detailedForecast\":\"Mostly clear, with a low around 19. North northwest wind 5 to 15 mph, with gusts as high as 30 mph.\"}")));

    try (Playwright playwright = Playwright.create()) {
      Browser browser =
          playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(headless));
      Page page = browser.newPage();

      // Navigate to the home page
      page.navigate("http://localhost:8080/to-do-list/home");
      page.waitForLoadState();
      page.waitForTimeout(1000);

      page.locator(UiElementConstants.HOME_PAGE_LIST_TABLE_FIRST_LIST_NAME).click();

      // Verify the tasks table is empty initially
      assertThat(page.locator(UiElementConstants.HOME_PAGE_TASK_TABLE_ROWS).count()).isEqualTo(0);

      // Click the "Create Task" button, fill in the form, and submit
      page.locator(UiElementConstants.HOME_PAGE_TASK_TABLE_CREATE_TASK_BUTTON).click();
      page.locator(UiElementConstants.HOME_PAGE_CREATE_TASK_MODAL_TASK_NAME_INPUT)
          .fill("Test Task");
      page.locator(UiElementConstants.HOME_PAGE_CREATE_TASK_MODAL_TASK_DATE_INPUT)
          .fill("2024-10-15");
      page.locator(UiElementConstants.HOME_PAGE_CREATE_TASK_MODAL_TASK_STATUS_INPUT)
          .selectOption("TO_DO");
      page.locator(UiElementConstants.HOME_PAGE_CREATE_TASK_MODAL_SUBMIT_FORM_BUTTON).click();

      page.waitForLoadState();
      page.waitForTimeout(3000);

      // Verify the tasks table contains the new Task
      assertThat(page.locator(UiElementConstants.HOME_PAGE_TASK_TABLE_ROWS).count()).isEqualTo(1);

      browser.close();
    }
  }

  @Test
  public void deleteListIntegrationTest() throws InterruptedException {
    // 1. Initial GET request to /lists - returns a list with "Work Tasks"
    wireMockListService.stubFor(
        WireMock.get(WireMock.urlEqualTo("/lists"))
            .inScenario("Delete List Scenario")
            .whenScenarioStateIs(Scenario.STARTED)
            .willReturn(
                WireMock.aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withBody("[{\"listId\":1,\"listName\":\"Work Tasks\"}]"))); // List exists

    // Stub the weather service before deletion
    wireMockWeatherService.stubFor(
        WireMock.get(WireMock.urlEqualTo("/forecast"))
            .inScenario("Delete List Scenario")
            .whenScenarioStateIs(Scenario.STARTED)
            .willReturn(
                WireMock.aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        "{\"name\":\"Tonight\",\"temperature\":19,\"windSpeed\":\"5 to 15 mph\",\"detailedForecast\":\"Mostly clear, with a low around 19. North northwest wind 5 to 15 mph, with gusts as high as 30 mph.\"}")));

    // 2. Stub the DELETE /lists/{listId} request to simulate deleting the list
    wireMockTaskService.stubFor(
        WireMock.delete(WireMock.urlEqualTo("/tasks/list/1"))
            .inScenario("Delete List Scenario")
            .whenScenarioStateIs(Scenario.STARTED)
            .willReturn(WireMock.aResponse().withStatus(204))); // Successful delete

    wireMockListService.stubFor(
        WireMock.delete(WireMock.urlEqualTo("/lists/1"))
            .inScenario("Delete List Scenario")
            .whenScenarioStateIs(Scenario.STARTED)
            .willReturn(WireMock.aResponse().withStatus(204)) // Successful delete
            .willSetStateTo("List Deleted")); // Changes scenario state

    // 3. GET /lists after deletion - returns an empty list
    wireMockListService.stubFor(
        WireMock.get(WireMock.urlEqualTo("/lists"))
            .inScenario("Delete List Scenario")
            .whenScenarioStateIs("List Deleted")
            .willReturn(
                WireMock.aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withBody("[]"))); // No lists exist after deletion

    wireMockTaskService.stubFor(
        WireMock.get(WireMock.urlEqualTo("/tasks/list/1"))
            .inScenario("Delete List Scenario")
            .whenScenarioStateIs("List Deleted")
            .willReturn(
                WireMock.aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withBody("[]"))); // No lists exist after deletion

    // Stub the weather service after deletion
    wireMockWeatherService.stubFor(
        WireMock.get(WireMock.urlEqualTo("/forecast"))
            .inScenario("Delete List Scenario")
            .whenScenarioStateIs("List Deleted")
            .willReturn(
                WireMock.aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        "{\"name\":\"Tonight\",\"temperature\":19,\"windSpeed\":\"5 to 15 mph\",\"detailedForecast\":\"Mostly clear, with a low around 19. North northwest wind 5 to 15 mph, with gusts as high as 30 mph.\"}")));

    // 4. Use Playwright to test UI behavior
    try (Playwright playwright = Playwright.create()) {
      Browser browser =
          playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(headless));
      Page page = browser.newPage();

      // Navigate to the home page
      page.navigate("http://localhost:8080/to-do-list/home");

      // Wait for the page to load
      page.waitForLoadState();
      page.waitForTimeout(1000);

      // Verify the lists table initially has 1 list
      assertThat(page.locator(UiElementConstants.HOME_PAGE_LIST_TABLE_ROWS).count()).isEqualTo(1);
      assertThat(
              page.locator(UiElementConstants.HOME_PAGE_LIST_TABLE_FIRST_LIST_NAME).textContent())
          .isEqualTo("Work Tasks");

      // Click the delete button for "Work Tasks"
      page.locator(UiElementConstants.HOME_PAGE_LIST_TABLE_DELETE_LIST_BUTTON).click();

      // page.pause();

      // Wait for UI update
      page.waitForLoadState();
      page.waitForTimeout(1000);

      // Verify the lists table is now empty
      assertThat(page.locator(UiElementConstants.HOME_PAGE_LIST_TABLE_ROWS).count()).isEqualTo(0);

      // page.pause();
      browser.close();
    }
  }

  @Test
  public void deleteTaskIntegrationTest() throws InterruptedException {
    // 1. Initial GET request to /tasks - returns a task in "Work Tasks"
    wireMockListService.stubFor(
        WireMock.get(WireMock.urlEqualTo("/lists"))
            .inScenario("Delete List Scenario")
            .whenScenarioStateIs(Scenario.STARTED)
            .willReturn(
                WireMock.aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withBody("[{\"listId\":1,\"listName\":\"Work Tasks\"}]"))); // List exists

    wireMockTaskService.stubFor(
        WireMock.get(WireMock.urlEqualTo("/tasks/list/1"))
            .inScenario("Delete Task Scenario")
            .whenScenarioStateIs(Scenario.STARTED)
            .willReturn(
                WireMock.aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        """
                            [
                                {
                                    "taskId": 2,
                                    "listId": 1,
                                    "taskName": "Test Task",
                                    "taskDate": "2024-10-15",
                                    "taskStatus": "TO_DO"
                                }
                            ]
                            """))); // Task exists

    // Stub the weather service before task deletion
    wireMockWeatherService.stubFor(
        WireMock.get(WireMock.urlEqualTo("/forecast"))
            .inScenario("Delete Task Scenario")
            .whenScenarioStateIs(Scenario.STARTED)
            .willReturn(
                WireMock.aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        "{\"name\":\"Tonight\",\"temperature\":19,\"windSpeed\":\"5 to 15 mph\",\"detailedForecast\":\"Mostly clear, with a low around 19. North northwest wind 5 to 15 mph, with gusts as high as 30 mph.\"}")));

    // 2. Stub the DELETE /tasks/{taskId} request to simulate deleting the task
    wireMockTaskService.stubFor(
        WireMock.delete(WireMock.urlEqualTo("/tasks/2"))
            .inScenario("Delete Task Scenario")
            .whenScenarioStateIs(Scenario.STARTED)
            .willReturn(WireMock.aResponse().withStatus(204))
            .willSetStateTo("Task Deleted")); // Successful task delete

    // 3. GET /tasks/list/1 after task deletion - returns an empty list of tasks
    wireMockListService.stubFor(
        WireMock.get(WireMock.urlEqualTo("/lists"))
            .inScenario("Delete List Scenario")
            .whenScenarioStateIs("Task Deleted")
            .willReturn(
                WireMock.aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withBody("[{\"listId\":1,\"listName\":\"Work Tasks\"}]"))); // List exists

    wireMockTaskService.stubFor(
        WireMock.get(WireMock.urlEqualTo("/tasks/list/1"))
            .inScenario("Delete Task Scenario")
            .whenScenarioStateIs("Task Deleted")
            .willReturn(
                WireMock.aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withBody("[]"))); // No tasks exist after deletion

    // Stub the weather service after task deletion
    wireMockWeatherService.stubFor(
        WireMock.get(WireMock.urlEqualTo("/forecast"))
            .inScenario("Delete Task Scenario")
            .whenScenarioStateIs("Task Deleted")
            .willReturn(
                WireMock.aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        "{\"name\":\"Tonight\",\"temperature\":19,\"windSpeed\":\"5 to 15 mph\",\"detailedForecast\":\"Mostly clear, with a low around 19. North northwest wind 5 to 15 mph, with gusts as high as 30 mph.\"}")));

    // 4. Use Playwright to test UI behavior
    try (Playwright playwright = Playwright.create()) {
      Browser browser =
          playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(headless));
      Page page = browser.newPage();

      // Navigate to the home page
      page.navigate("http://localhost:8080/to-do-list/home");

      // Wait for the page to load
      page.waitForLoadState();
      page.waitForTimeout(1000);

      // Click on "Work Tasks" list
      page.locator(UiElementConstants.HOME_PAGE_LIST_TABLE_FIRST_LIST_NAME).click();

      page.waitForLoadState();
      page.waitForTimeout(1000);

      // Verify the tasks table initially has 1 task
      assertThat(page.locator(UiElementConstants.HOME_PAGE_TASK_TABLE_ROWS).count()).isEqualTo(1);
      assertThat(
              page.locator(UiElementConstants.HOME_PAGE_TASK_TABLE_TASK_NAME_1_ROW).textContent())
          .isEqualTo("Test Task");

      // Click the delete button for "Test Task"
      page.locator(UiElementConstants.HOME_PAGE_TASK_TABLE_DELETE_TASK_BUTTON_1_ROW).click();

      // Wait for UI update
      page.waitForLoadState();
      page.waitForTimeout(1000);

      // Verify the list still exists
      assertThat(
              page.locator(UiElementConstants.HOME_PAGE_LIST_TABLE_FIRST_LIST_NAME).textContent())
          .isEqualTo("Work Tasks");
      // Verify the tasks table is now empty
      assertThat(page.locator(UiElementConstants.HOME_PAGE_TASK_TABLE_ROWS).count()).isEqualTo(0);

      // Close the browser after the test
      browser.close();
    }
  }
}
