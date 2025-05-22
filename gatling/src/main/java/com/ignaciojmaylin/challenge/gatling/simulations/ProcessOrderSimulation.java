package com.ignaciojmaylin.challenge.gatling.simulations;

import com.github.javafaker.Faker;
import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import java.time.Duration;
import java.util.*;
import java.util.stream.Stream;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class ProcessOrderSimulation extends Simulation {

    private static final Properties props = new Properties();

    static {
        String profile = System.getProperty("gatling.profile", "default");
        String fileName = profile.equals("default")
                ? "gatling.properties"
                : String.format("gatling-%s.properties", profile);

        try (var input = ProcessOrderSimulation.class
                .getClassLoader()
                .getResourceAsStream(fileName)) {

            if (input == null) {
                System.err.println("🚫 Archivo '" + fileName + "' no encontrado en classpath.");
            } else {
                props.load(input);
                System.out.println("✅ " + fileName + " cargado correctamente.");
            }

        } catch (Exception e) {
            System.err.println("❌ Error al cargar " + fileName + ": " + e.getMessage());
        }
    }


    private static String prop(String key, String defaultValue) {
        return props.getProperty(key, defaultValue);
    }

    private static final HttpProtocolBuilder HTTP_PROTOCOL = http
            .baseUrl(prop("baseUrl", "http://127.0.0.1:8080"))
            .acceptHeader("application/json")
            .contentTypeHeader("application/json");

    private static final Faker faker = new Faker();

    private static final Iterator<Map<String, Object>> FEED_DATA = Stream.generate(() -> {
        Map<String, Object> map = new HashMap<>();
        map.put("orderId", UUID.randomUUID().toString());
        map.put("customerId", UUID.randomUUID().toString());
        map.put("orderAmount", String.format("%.2f", faker.number().randomDouble(2, 10, 1000)));
        map.put("orderItems", "[\"item1\",\"item2\"]");
        return map;
    }).iterator();

    private static final int rampUsersPerSec = Integer.parseInt(prop("rampUsersPerSec", "100"));
    private static final int targetUsersPerSec = Integer.parseInt(prop("targetUsersPerSec", "1000"));
    private static final int rampDurationSec = Integer.parseInt(prop("rampDurationSec", "60"));
    private static final int steadyDurationSec = Integer.parseInt(prop("steadyDurationSec", "120"));
    private static final int assertionMaxMeanMs = Integer.parseInt(prop("assertionMeanMs", "500"));
    private static final double assertionSuccessRate = Double.parseDouble(prop("assertionSuccessRate", "99.0"));

    private static final ScenarioBuilder SCENARIO = scenario("Cargar pedidos concurrentes")
            .feed(FEED_DATA)
            .exec(http("Enviar pedido")
                    .post("/processOrder")
                    .body(StringBody(session ->
                            String.format("""
                                {
                                  "orderId": "%s",
                                  "customerId": "%s",
                                  "orderAmount": %s,
                                  "orderItems": %s
                                }
                                """,
                                    session.getString("orderId"),
                                    session.getString("customerId"),
                                    session.getString("orderAmount"),
                                    session.getString("orderItems")
                            )))
                    .asJson()
                    .check(status().is(200))
            );

    public ProcessOrderSimulation() {
        setUp(
                SCENARIO.injectOpen(
                        rampUsersPerSec(rampUsersPerSec).to(targetUsersPerSec).during(Duration.ofSeconds(rampDurationSec)),
                        constantUsersPerSec(targetUsersPerSec).during(Duration.ofSeconds(steadyDurationSec))
                ).protocols(HTTP_PROTOCOL)
        ).assertions(
                global().successfulRequests().percent().gt(assertionSuccessRate),
                global().responseTime().mean().lte(assertionMaxMeanMs)
        );
    }
}