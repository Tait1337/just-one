package de.clique.westwood.justone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * The entry point of the Application.
 */
@SpringBootApplication
public class App extends SpringBootServletInitializer {

    /**
     * Run the App
     * @param args program arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

}
