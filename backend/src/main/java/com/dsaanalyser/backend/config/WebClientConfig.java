package com.dsaanalyser.backend.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Configures the WebClient bean used by GeminiApiService to make
 * async HTTP calls to the Gemini REST API.
 *
 * WebClient is the non-blocking alternative to RestTemplate, provided
 * by the spring-boot-starter-webflux dependency listed in pom.xml.
 *
 * Timeouts are configured at the Netty transport layer so that a slow
 * or unresponsive Gemini API does not block application threads indefinitely.
 */
@Configuration
public class WebClientConfig {

    /**
     * Connection timeout: how long to wait for the TCP connection to be established.
     * 5 seconds is generous for an HTTPS API — fail fast if the host is unreachable.
     */
    private static final int CONNECT_TIMEOUT_MS = 5000;

    /**
     * Read timeout: how long to wait for Gemini to start returning a response.
     * 30 seconds allows for slower Gemini responses on complex code analysis
     * without hanging the request thread forever.
     */
    private static final int READ_TIMEOUT_SECONDS = 30;

    /**
     * Write timeout: how long to wait for the full request body to be sent.
     * 10 seconds is more than enough for sending a code submission payload.
     */
    private static final int WRITE_TIMEOUT_SECONDS = 10;

    /**
     * Max in-memory response buffer size.
     * Default WebClient buffer is 256KB — Gemini responses are small JSON,
     * but raising to 1MB avoids buffer overflow if the response grows.
     */
    private static final int MAX_IN_MEMORY_SIZE = 1024 * 1024;

    @Bean
    public WebClient webClient() {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, CONNECT_TIMEOUT_MS)
                .responseTimeout(Duration.ofSeconds(READ_TIMEOUT_SECONDS))
                .doOnConnected(conn ->
                        conn.addHandlerLast(new ReadTimeoutHandler(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS))
                                .addHandlerLast(new WriteTimeoutHandler(WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS))
                );

        ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
                .codecs(configurer ->
                        configurer.defaultCodecs().maxInMemorySize(MAX_IN_MEMORY_SIZE))
                .build();

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .exchangeStrategies(exchangeStrategies)
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("Accept", MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}