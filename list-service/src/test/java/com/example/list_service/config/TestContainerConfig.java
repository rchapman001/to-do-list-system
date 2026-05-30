package com.example.list_service.config;

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
    String schemaScriptPath =
        Paths.get("").toAbsolutePath().getParent().toString() + "/db/01-list-schema.sql";

    return new PostgreSQLContainer<>("postgres:15")
        .withCopyFileToContainer(
            MountableFile.forHostPath(schemaScriptPath), "/docker-entrypoint-initdb.d/");
  }
}
