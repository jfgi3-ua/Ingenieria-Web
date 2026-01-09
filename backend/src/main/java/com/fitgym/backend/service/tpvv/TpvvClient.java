package com.fitgym.backend.service.tpvv;

import com.fitgym.backend.service.TpvvCommunicationException;
import com.fitgym.backend.service.tpvv.dto.TpvvPaymentInitRequest;
import com.fitgym.backend.service.tpvv.dto.TpvvPaymentInitResponse;
import com.fitgym.backend.service.tpvv.dto.TpvvPaymentVerifyResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

/**
 * Cliente HTTP para comunicar con la API de TPVV.
 */
@Component
public class TpvvClient {

  private final RestClient restClient;
  private final TpvvProperties props;

  public TpvvClient(RestClient tpvvRestClient, TpvvProperties props) {
    this.restClient = tpvvRestClient;
    this.props = props;
  }

  public TpvvPaymentInitResponse initPayment(TpvvPaymentInitRequest payload) {
    validateApiKey();
    try {
      return restClient.post()
          .uri("/api/v1/payments/init")
          .contentType(MediaType.APPLICATION_JSON)
          .header("X-API-KEY", props.apiKey())
          .body(payload)
          .retrieve()
          .body(TpvvPaymentInitResponse.class);
    } catch (RestClientResponseException ex) {
      throw new TpvvCommunicationException("TPVV rechazo la solicitud de init. Codigo: " + ex.getStatusCode().value());
    } catch (Exception ex) {
      throw new TpvvCommunicationException("No se pudo iniciar el pago en TPVV.");
    }
  }

  public TpvvPaymentVerifyResponse verifyPayment(String token) {
    validateApiKey();
    try {
      return restClient.get()
          .uri("/api/v1/payments/verify/{token}", token)
          .header("X-API-KEY", props.apiKey())
          .retrieve()
          .body(TpvvPaymentVerifyResponse.class);
    } catch (RestClientResponseException ex) {
      throw new TpvvCommunicationException("TPVV rechazo la verificacion. Codigo: " + ex.getStatusCode().value());
    } catch (Exception ex) {
      throw new TpvvCommunicationException("No se pudo verificar el pago en TPVV.");
    }
  }

  private void validateApiKey() {
    if (props.apiKey() == null || props.apiKey().isBlank()) {
      throw new TpvvCommunicationException("TPVV API key no configurada.");
    }
  }
}
