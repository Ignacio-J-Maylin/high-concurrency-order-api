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
    private static final Properties props = loadProperties();
    private static final Faker faker = new Faker();

    private static Properties loadProperties() {
        Properties properties = new Properties();
        String profile = System.getProperty("gatling.profile", "default");
        String fileName = profile.equals("default") ? "gatling.properties" : String.format("gatling-%s.properties", profile);

        try (var input = ProcessOrderSimulation.class.getClassLoader().getResourceAsStream(fileName)) {
            if (input != null) {
                properties.load(input);
                System.out.println("✅ " + fileName + " cargado correctamente.");
            } else {
                System.err.println("🚫 Archivo '" + fileName + "' no encontrado en classpath.");
            }
        } catch (Exception e) {
            System.err.println("❌ Error al cargar " + fileName + ": " + e.getMessage());
        }
        return properties;
    }

    private static String prop(String key, String defaultValue) {
        return props.getProperty(key, defaultValue);
    }

    private static final HttpProtocolBuilder HTTP_PROTOCOL = http
            .baseUrl(prop("baseUrl", "http://127.0.0.1:8080"))
            .acceptHeader("application/json")
            .contentTypeHeader("application/json");

    private static final Iterator<Map<String, Object>> FEED_DATA = Stream.generate(() -> {
        Map<String, Object> map = new HashMap<>();
        map.put("orderId", UUID.randomUUID().toString());
        map.put("customerId", UUID.randomUUID().toString());
        map.put("orderAmount", String.format("%.2f", faker.number().randomDouble(2, 10, 1000)));
        map.put("orderItems", "[\"item1\",\"item2\"]");
        return map;
    }).iterator();

    private static final int rampUsersPerSec = Integer.parseInt(prop("rampUsersPerSec", "50"));
    private static final int targetUsersPerSec = Integer.parseInt(prop("targetUsersPerSec", "100"));
    private static final int rampDurationSec = Integer.parseInt(prop("rampDurationSec", "30"));
    private static final int steadyDurationSec = Integer.parseInt(prop("steadyDurationSec", "60"));
    private static final int assertionMaxMeanMs = Integer.parseInt(prop("assertionMeanMs", "500"));
    private static final double assertionSuccessRate = Double.parseDouble(prop("assertionSuccessRate", "98.0"));

    private static ChainBuilder sendOrder() {
        return exec(http("Enviar pedido")
                .post("/processOrder")
                .body(StringBody(session -> String.format("""
                    {
                      \"orderId\": \"%s\",
                      \"customerId\": \"%s\",
                      \"orderAmount\": %s,
                      \"orderItems\": %s
                    }
                    """,
                        session.getString("orderId"),
                        session.getString("customerId"),
                        session.getString("orderAmount"),
                        session.getString("orderItems")
                )))
                .asJson()
                .check(status().is(202))
        );
    }

    private static ChainBuilder pollOrderStatus() {
        return repeat(10, "pollCount").on(
                exec(http("Consultar estado")
                        .get(session -> "/processOrder/status/" + session.getString("orderId"))
                        .check(bodyString().saveAs("orderStatus"))
                )
                        .exec(session -> {
                            String orderId = session.getString("orderId");
                            String status = session.getString("orderStatus");
                            System.out.printf("🔄 Estado actual: %s | orderId: %s%n", status, orderId);
                            return session;
                        })
                        .doIf(session -> {
                            String s = session.getString("orderStatus");
                            return !("APPROVED".equals(s) || "REJECTED".equals(s));
                        }).then(pause(1))
        );
    }

    private static ChainBuilder logFinalStatus() {
        return exec(session -> {
            String status = session.getString("orderStatus");
            String orderId = session.getString("orderId");
            if ("APPROVED".equals(status)) {
                System.out.printf("✅ Pedido procesado correctamente | orderId: %s%n", orderId);
            } else if ("REJECTED".equals(status)) {
                System.err.printf("❌ Pedido rechazado | orderId: %s%n", orderId);
            } else {
                System.err.printf("⏰ Timeout esperando estado final | orderId: %s (último estado: %s)%n", orderId, status);
            }
            return session;
        });
    }

    private static ChainBuilder sendBadRequest() {
        return exec(http("Pedido inválido - falta de campos")
                .post("/processOrder")
                .body(StringBody("""
                    {
                      \"orderId\": null,
                      \"customerId\": \"\",
                      \"orderAmount\": 0,
                      \"orderItems\": []
                    }
                    """))
                .asJson()
                .check(status().is(400))
        );
    }

    private static ChainBuilder sendDuplicateOrder(String orderId) {
        return exec(http("Pedido duplicado")
                .post("/processOrder")
                .body(StringBody(String.format("""
                    {
                      \"orderId\": \"%s\",
                      \"customerId\": \"duplicated-user\",
                      \"orderAmount\": 123.45,
                      \"orderItems\": [\"item1\"]
                    }
                    """, orderId)))
                .asJson()
                .check(status().in(200,202, 409))
        );
    }

    private static final ScenarioBuilder SCENARIO = scenario("Carga completa de pedidos")
            .feed(FEED_DATA)
            .exec(sendOrder())
            .pause(2)
            .exec(pollOrderStatus())
            .exec(logFinalStatus())
            .exec(sendBadRequest())
            .exec(session -> {
                String reuseId = UUID.randomUUID().toString();
                return session.set("reuseId", reuseId);
            })
            .exec(sendDuplicateOrder("DUPLICATE_ORDER_ID"));

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