package com.example.e2e;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.e2e.constants.UiElementConstants;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import java.io.File;
import java.nio.file.Paths;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;

public class EndToEndTests {

  private static final String DOCKER_COMPOSE_FILE =
      Paths.get("").toAbsolutePath().getParent().toString() + "/docker-compose.yml";

  private static DockerComposeContainer<?> composeContainer;

  static Playwright playwright;
  static Browser browser;
  static Page page;

  @BeforeAll
  public static void setUp() {
    // Start the services with Docker Compose
    composeContainer =
        new DockerComposeContainer<>(new File(DOCKER_COMPOSE_FILE))
            .withExposedService("to-do-list-db", 5432, Wait.forListeningPort())
            .withEnv("SKIP_INIT", "true")
            .withExposedService("list-service", 8082, Wait.forListeningPort())
            .withExposedService("task-service", 8083, Wait.forListeningPort())
            .withExposedService("weather-service", 8085, Wait.forListeningPort())
            .withExposedService("to-do-list-ui", 8080, Wait.forListeningPort());
    composeContainer.start();

    playwright = Playwright.create();
    browser =
        playwright
            .chromium()
            .launch(new BrowserType.LaunchOptions().setHeadless(true)); // Set to true for headless
    page = browser.newPage();
  }

  @AfterAll
  public static void tearDown() {
    if (composeContainer != null) {
      composeContainer.stop();
    }
    if (browser != null) {
      browser.close();
    }
    if (playwright != null) {
      playwright.close();
    }
  }

  @Test
  public void testPageEndToEndTest() {
    page.navigate("http://localhost:8080/to-do-list/test");
    page.waitForLoadState();

    // page.pause();

    String listServiceText =
        page.locator(UiElementConstants.TEST_PAGE_LIST_SERVICE_TEST_MESSAGE).textContent();
    String taskServiceText =
        page.locator(UiElementConstants.TEST_PAGE_TASK_SERVICE_TEST_MESSAGE).textContent();
    String weatherServiceText =
        page.locator(UiElementConstants.TEST_PAGE_WEATHER_SERVICE_TEST_MESSAGE).textContent();

    assertThat(listServiceText).isEqualTo("List Service Connection: testEndpoint ListService");
    assertThat(taskServiceText).isEqualTo("Task Service Connection: testEndpoint TaskService");
    assertThat(weatherServiceText)
        .isEqualTo("Weather Service Connection: testEndpoint WeatherService");
  }

  @Test
  public void homePageEndToEndTest() throws InterruptedException {
    page.navigate("http://localhost:8080/to-do-list/home");
    page.waitForLoadState();

    /* Verify Headers */
    assertEquals("Lists", page.querySelector(UiElementConstants.HOME_PAGE_LIST_HEADER).innerText());
    assertEquals("Tasks", page.querySelector(UiElementConstants.HOME_PAGE_TASK_HEADER).innerText());
    assertEquals(
        "Weather Forecast",
        page.querySelector(UiElementConstants.HOME_PAGE_WEATHER_HEADER).innerText());

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
        "Action", page.locator(UiElementConstants.HOME_PAGE_TASK_TABLE_ACTION_HEADER).innerText());

    /* Create List */
    assertThat(page.locator(UiElementConstants.HOME_PAGE_LIST_TABLE_ROWS).count()).isEqualTo(0);

    page.locator(UiElementConstants.HOME_PAGE_LIST_TABLE_CREATE_LIST_BUTTON).click();
    page.locator(UiElementConstants.HOME_PAGE_CREATE_LIST_MODAL_LIST_NAME_INPUT).fill("Work Tasks");
    page.locator(UiElementConstants.HOME_PAGE_CREATE_LIST_MODAL_SUBMIT_FROM_BUTTON).click();

    page.waitForLoadState();
    page.waitForTimeout(3000);

    assertThat(page.locator(UiElementConstants.HOME_PAGE_LIST_TABLE_ROWS).count()).isEqualTo(1);
    assertThat(page.locator(UiElementConstants.HOME_PAGE_LIST_TABLE_FIRST_LIST_NAME).textContent())
        .isEqualTo("Work Tasks");

    page.locator(UiElementConstants.HOME_PAGE_LIST_TABLE_FIRST_LIST_NAME).click();

    /* Create Task */
    assertThat(page.locator(UiElementConstants.HOME_PAGE_TASK_TABLE_ROWS).count()).isEqualTo(0);

    page.locator(UiElementConstants.HOME_PAGE_TASK_TABLE_CREATE_TASK_BUTTON).click();
    page.locator(UiElementConstants.HOME_PAGE_CREATE_TASK_MODAL_TASK_NAME_INPUT).fill("Test Task");
    page.locator(UiElementConstants.HOME_PAGE_CREATE_TASK_MODAL_TASK_DATE_INPUT).fill("2024-10-15");
    page.locator(UiElementConstants.HOME_PAGE_CREATE_TASK_MODAL_TASK_STATUS_INPUT)
        .selectOption("TO_DO");
    page.locator(UiElementConstants.HOME_PAGE_CREATE_TASK_MODAL_SUBMIT_FORM_BUTTON).click();

    page.waitForLoadState();
    page.waitForTimeout(3000);

    assertThat(page.locator(UiElementConstants.HOME_PAGE_TASK_TABLE_ROWS).count()).isEqualTo(1);

    /* Delete Task */
    page.waitForLoadState();
    page.waitForTimeout(3000);

    page.locator(UiElementConstants.HOME_PAGE_LIST_TABLE_FIRST_LIST_NAME).click();

    page.waitForLoadState();
    page.waitForTimeout(3000);

    assertThat(page.locator(UiElementConstants.HOME_PAGE_TASK_TABLE_ROWS).count()).isEqualTo(1);
    assertThat(page.locator(UiElementConstants.HOME_PAGE_TASK_TABLE_TASK_NAME_1_ROW).textContent())
        .isEqualTo("Test Task");

    page.locator(UiElementConstants.HOME_PAGE_TASK_TABLE_DELETE_TASK_BUTTON_1_ROW).click();

    page.waitForLoadState();
    page.waitForTimeout(3000);

    assertThat(page.locator(UiElementConstants.HOME_PAGE_LIST_TABLE_FIRST_LIST_NAME).textContent())
        .isEqualTo("Work Tasks");
    assertThat(page.locator(UiElementConstants.HOME_PAGE_TASK_TABLE_ROWS).count()).isEqualTo(0);

    /* Delete List */
    page.waitForLoadState();
    page.waitForTimeout(3000);

    assertThat(page.locator(UiElementConstants.HOME_PAGE_LIST_TABLE_ROWS).count()).isEqualTo(1);
    assertThat(page.locator(UiElementConstants.HOME_PAGE_LIST_TABLE_FIRST_LIST_NAME).textContent())
        .isEqualTo("Work Tasks");

    page.locator(UiElementConstants.HOME_PAGE_LIST_TABLE_DELETE_LIST_BUTTON).click();

    page.waitForLoadState();
    page.waitForTimeout(3000);

    assertThat(page.locator(UiElementConstants.HOME_PAGE_LIST_TABLE_ROWS).count()).isEqualTo(0);
  }
}
