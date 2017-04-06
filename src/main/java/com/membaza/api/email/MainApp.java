package com.membaza.api.email;

import com.membaza.api.email.controller.KeysController;
import com.membaza.api.email.persistence.Privilege;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.EnumSet;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

/**
 * @author Emil Forslund
 * @since 1.0.0
 */
@SpringBootApplication
public class MainApp implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(MainApp.class, args);
    }

    private final KeysController keysController;

    MainApp(KeysController keysController) {
        this.keysController = requireNonNull(keysController);
    }

    @Override
    public void run(String... args) throws Exception {
        if (Stream.of(args).anyMatch("--init"::equals)) {
            System.out.println("Creating API Key...");

            String username = DEFAULT_USERNAME;
            String password = DEFAULT_PASSWORD;

            for (int i = 0; i < args.length - 1; i++) {
                if ("--u".equals(args[i])) {
                    username = args[++i];
                } else if ("--p".equals(args[i])) {
                    password = args[++i];
                } else if (isHelp(args[i])) {
                    printHelp();
                } else {
                    throw new IllegalArgumentException(
                        "Unknown parameter '" + args[i] + "'."
                    );
                }
            }

            final KeysController.PostedKey key = new KeysController.PostedKey();
            key.setName(username);
            key.setPassword(password);
            key.setAuthorities(EnumSet.allOf(Privilege.class));

            keysController.createApiKey(key);

            System.out.println("API Key " + username + ":" + password + " created.");
        } else if (Stream.of(args).anyMatch(this::isHelp)) {
            printHelp();
        }
    }

    private boolean isHelp(String arg) {
        return "--h".equals(arg) || "--help".equals(arg);
    }

    private void printHelp() {
        System.out.println("The following parameters can be specified:");
        System.out.println("  --init (-u [name]) (-p [password])");
        System.out.println("      Default name: " + DEFAULT_USERNAME);
        System.out.println("      Default password: " + DEFAULT_PASSWORD);
        System.exit(0);
    }

    private static String DEFAULT_USERNAME = "admin",
                          DEFAULT_PASSWORD = "password";
}