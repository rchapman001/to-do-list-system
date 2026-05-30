package com.example.e2e.constants;

import org.springframework.boot.test.context.TestConfiguration;

@TestConfiguration
public class UiElementConstants {

  /***********************************************************************************************
   * Test Page Constants
   ***********************************************************************************************/
  public static final String TEST_PAGE_LIST_SERVICE_TEST_MESSAGE = "text=List Service Connection";

  public static final String TEST_PAGE_TASK_SERVICE_TEST_MESSAGE = "text=Task Service Connection";
  public static final String TEST_PAGE_WEATHER_SERVICE_TEST_MESSAGE =
      "text=Weather Service Connection";

  /***********************************************************************************************
   * Home Page Constants
   ***********************************************************************************************/
  // Section Headers
  public static final String HOME_PAGE_LIST_HEADER = "div.col-4 h4";

  public static final String HOME_PAGE_TASK_HEADER = "div.col-8 h4";
  public static final String HOME_PAGE_WEATHER_HEADER = ".weather-scroll-bar h4";

  // List Table
  public static final String HOME_PAGE_LIST_TABLE_ROWS = ".col-4 .table tbody tr";
  public static final String HOME_PAGE_LIST_TABLE_FIRST_LIST_NAME =
      "table.table tbody tr:first-child td button span";
  public static final String HOME_PAGE_LIST_TABLE_CREATE_LIST_BUTTON =
      "button[data-bs-toggle='modal'][data-bs-target='#createListModal']"; // Create List Button
  public static final String HOME_PAGE_LIST_TABLE_DELETE_LIST_BUTTON =
      ".col-4 .table tbody tr td button.btn-light-red";

  // Create List modal
  public static final String HOME_PAGE_CREATE_LIST_MODAL_LIST_NAME_INPUT = "#listName";
  public static final String HOME_PAGE_CREATE_LIST_MODAL_SUBMIT_FROM_BUTTON =
      "button:has-text('Create List')";

  // Task Table
  public static final String HOME_PAGE_TASK_TABLE_ROWS = ".col-8 .table tbody tr";
  public static final String HOME_PAGE_TASK_TABLE_TASK_NAME_HEADER =
      "table.table thead th:nth-child(1)";
  public static final String HOME_PAGE_TASK_TABLE_TASK_DATE_HEADER =
      "table.table thead th:nth-child(2)";
  public static final String HOME_PAGE_TASK_TABLE_TASK_STATUS_HEADER =
      "table.table thead th:nth-child(3)";
  public static final String HOME_PAGE_TASK_TABLE_ACTION_HEADER =
      "table.table thead th:nth-child(4)";
  public static final String HOME_PAGE_TASK_TABLE_TASK_NAME_1_ROW =
      ".col-8 .table tbody tr:first-child td:first-child";
  public static final String HOME_PAGE_TASK_TABLE_TASK_DATE_1_ROW =
      "table.table tbody tr.task-row:first-of-type td:nth-child(2)";

  public static final String HOME_PAGE_TASK_TABLE_TASK_STATUS_1_ROW =
      "table.table tbody tr.task-row:first-of-type td:nth-child(3)";
  // "table.table tbody tr.task-row td:nth-child(3)";

  public static final String HOME_PAGE_TASK_TABLE_DELETE_TASK_BUTTON_1_ROW =
      ".col-8 .table tbody tr:first-of-type td button.btn-light-red";
  // ".col-8 .table tbody tr td button.btn-light-red";

  // Create Task modal
  public static final String HOME_PAGE_TASK_TABLE_CREATE_TASK_BUTTON =
      "button[data-bs-target='#createTaskModal']";
  public static final String HOME_PAGE_CREATE_TASK_MODAL_TASK_NAME_INPUT = "#taskName";
  public static final String HOME_PAGE_CREATE_TASK_MODAL_TASK_DATE_INPUT = "#taskDate";
  public static final String HOME_PAGE_CREATE_TASK_MODAL_TASK_STATUS_INPUT = "#taskStatus";
  public static final String HOME_PAGE_CREATE_TASK_MODAL_SUBMIT_FORM_BUTTON =
      "#createTaskForm button[type='submit']";

  // Weather Table
  public static final String HOME_PAGE_WEATHER_TABLE_TEMPERATURE =
      ".weather-scroll-bar p:nth-child(2)";
  public static final String HOME_PAGE_WEATHER_TABLE_WIND = ".weather-scroll-bar p:nth-child(3)";
  public static final String HOME_PAGE_WEATHER_TABLE_DESCRIPTION =
      ".weather-scroll-bar p:nth-child(4)";
}
