# Requirements

Requirements for a Java Spring Boot Microservices App (To Do List App) using Java, Spring Boot,
Thymeleaf, Postgres, Docker, Playwright, Openshift. 

## User Requirements
1. As a user I can add a new List or Task. After clicking the "Add List" button the user will be
   taken to a form to create a new list. After clicking the "Add Task" button the user will be taken
   to a form to create a new Task.
2. "Add List Form". List Name is an input parameter. Cancel Button (upper right corner of form):
   Takes the user back to the main page. Submit Button: Creates the list and takes the user back to
   the main page.
3. "Add Task Form". Task Name and Date are input parameters. Status defaults to: "To Do". Cancel
   Button (upper right corner of form): Takes the user back to the main page. Submit Button: Creates
   the task and takes the user back to the main page.
4. List data operations.
    - A user can click / select different lists and view each of the tasks in the corresponding
      list.
    - A user can update the name of a list.
    - A user delete a list by clicking the delete button.
5. Task data operations. 
    - A user can update the Task Name, Date, and Status. 
      - Name: Can be any String of Characters.
      - Date: Any valid date.
      - Status: Status is a dropdown with the following options: To Do, In Progress, Done. Styling:
      To Do should be Grey. In Progress should be Yellow. Done should be Green. 
    - A user can delete a task from a list.
6. A User can see the weather forecast for 14 number of periods. The user can scroll through the
   periods on the right side of the screen.

## Future User Requirement ideas: 
- When a user sets a date to a task, a calendar event is created. The event be created on the date
specified and contains the name of the task. Allow the user to click on a task and show more details
like a description, AC, tags, etc. 

## Tech Stack:
- Front End: Spring Boot MVC with Thymeleaf.
- Backend: Java Spring Boot Gateway and Java Spring Boot Rest APIs.
- Database: Postgres Docker Container.
- Testing: Show how you could use an excel sheet to enter in data for tests.
    - End to End test UI: Playwright
    - End to End tests Backend: 
    - Integration: 
    - Unit: JUnit5: 
    - Contract: Pact
- Infrastructure: Openshift using Docker Containers

## Requests UI Sends to Backend Services: 
- List Service: 
  - GET /lists/test: Done
  - GET /lists: Done
  - POST /lists: Done
  - DELETE /lists/{id}: Done
- Task Service:
  - Task Service: /tasks/test: Done
  - GET /tasks/list/{id}: Done
  - GET tasks/status-options: Done
  - POST /tasks: Done
  - DELETE /tasks/{id}: Done
  - DELETE /tasks/list/{id}: Done
- Weather Service: 
  - GET /forecast/test: Done
  - GET /forecast: Done