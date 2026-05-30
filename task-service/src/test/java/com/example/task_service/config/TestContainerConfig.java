package com.example.task_service.config;

import java.nio.file.Paths;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.MountableFile;

@TestConfiguration(proxyBeanMethods = false)
public class TestContainerConfig {

  @Bean
  @ServiceConnection
  public PostgreSQLContainer<?> postgreSQLContainer() {
    String listSchemaScriptPath =
        Paths.get("").toAbsolutePath().getParent().toString() + "/db/01-list-schema.sql";
    String taskSchemaScriptPath =
        Paths.get("").toAbsolutePath().getParent().toString() + "/db/02-task-schema.sql";
    String listDataScriptPath =
        Paths.get("").toAbsolutePath().getParent().toString() + "/db/03-list-data.sql";

    return new PostgreSQLContainer<>("postgres:15")
        .withCopyFileToContainer(
            MountableFile.forHostPath(listSchemaScriptPath), "/docker-entrypoint-initdb.d/")
        .withCopyFileToContainer(
            MountableFile.forHostPath(taskSchemaScriptPath), "/docker-entrypoint-initdb.d/")
        .withCopyFileToContainer(
            MountableFile.forHostPath(listDataScriptPath), "/docker-entrypoint-initdb.d/");
  }
}
