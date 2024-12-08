package ca.bazlur.workshop.jakarta.llm.config;


import jakarta.inject.Inject;
import lombok.Data;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.enterprise.context.ApplicationScoped;

@Data
@ApplicationScoped
public class DatabaseProp {
    @Inject
    @ConfigProperty(name = "database.name")
    private String database;

    @Inject
    @ConfigProperty(name = "database.user")
    private String user;

    @Inject
    @ConfigProperty(name = "database.password")
    private String password;

    @Inject
    @ConfigProperty(name = "database.driver")
    private String driver;

    @Inject
    @ConfigProperty(name = "database.host")
    private String host;

    @Inject
    @ConfigProperty(name = "database.port")
    private int port;
}