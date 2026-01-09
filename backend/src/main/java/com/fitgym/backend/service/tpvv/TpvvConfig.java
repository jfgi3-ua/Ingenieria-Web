package com.fitgym.backend.service.tpvv;

import java.net.http.HttpClient;
import java.time.Duration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

/**
 * Configura el cliente HTTP para comunicacion con TPVV.
 */
@Configuration
@EnableConfigurationProperties(TpvvProperties.class)
public class TpvvConfig {

  @Bean
  public RestClient tpvvRestClient(RestClient.Builder builder, TpvvProperties props) {
    HttpClient httpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(5))
        .build();

    JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
    requestFactory.setReadTimeout(Duration.ofSeconds(10));

    return builder
        .baseUrl(props.baseUrl())
        .requestFactory(requestFactory)
        .build();
  }

  /**
   * Builder base para RestClient (evita dependencia de auto-config).
   */
  @Bean
  public RestClient.Builder restClientBuilder() {
    return RestClient.builder();
  }
}
