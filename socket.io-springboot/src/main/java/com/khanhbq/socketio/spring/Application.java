package com.khanhbq.socketio.spring;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.util.StringUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

@Slf4j
@SpringBootApplication(scanBasePackages = {"com.khanhbq.socketio.spring"})
@PropertySources({
        @PropertySource("classpath:application.yaml"),
})
@ConfigurationPropertiesScan(basePackages = "com.khanhbq.socketio.spring")
@RequiredArgsConstructor
@EnableScheduling
public class Application implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    private final Environment environment;

    @Override
    public void run(String... args) {
        String protocol = StringUtils.hasText(environment.getProperty("server.ssl.key-store")) ? "https" : "http";
        Integer serverPort = environment.getProperty("server.port", Integer.class);
        String hostAddress = "localhost";
        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.warn("The host name could not be determined, using `localhost` as fallback");
        }
        log.info("\n----------------------------------------------------------\n\t" +
                        "Application '{}' is running! Access URLs:\n\t" +
                        "Local: \t\t{}://localhost:{}\n\t" +
                        "External: \t{}://{}:{}\n\t" +
                        "Date time: \t{}\n\t" +
                        "Default timezone: \t{}\n\t" +
                        "Profile(s): \t{}\n----------------------------------------------------------",
                environment.getProperty("spring.application.name"),
                protocol, serverPort,
                protocol, hostAddress, serverPort,
                ZonedDateTime.now().format(DateTimeFormatter.ISO_ZONED_DATE_TIME),
                TimeZone.getDefault().getDisplayName(),
                environment.getActiveProfiles());
    }
}