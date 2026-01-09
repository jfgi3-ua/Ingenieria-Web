package com.fitgym.backend.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * Transaccion de pago asociada al alta de un socio.
 *
 * Se crea en estado PENDING al iniciar el pago y se actualiza tras verificar con TPVV.
 */
@Entity
@Table(name = "pago_registro")
public class PagoRegistro {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, length = 120)
  private String token;

  @Enumerated(EnumType.STRING)
  @JdbcTypeCode(SqlTypes.NAMED_ENUM)
  @Column(nullable = false, columnDefinition = "tpvv_pago_estado")
  private PagoRegistroEstado estado = PagoRegistroEstado.PENDING;

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal importe;

  @Column(name = "external_reference", length = 120)
  private String externalReference;

  @Column(name = "callback_url", nullable = false)
  private String callbackUrl;

  @Column(name = "payment_url")
  private String paymentUrl;

  @Column(name = "provider_status", length = 40)
  private String providerStatus;

  @Column(name = "failure_reason")
  private String failureReason;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  @Column(name = "completed_at")
  private Instant completedAt;

  @Column(name = "failed_at")
  private Instant failedAt;

  @PrePersist
  void onCreate() {
    Instant now = Instant.now();
    createdAt = now;
    updatedAt = now;
  }

  @PreUpdate
  void onUpdate() {
    updatedAt = Instant.now();
  }

  public Long getId() { return id; }

  public String getToken() { return token; }
  public void setToken(String token) { this.token = token; }

  public PagoRegistroEstado getEstado() { return estado; }
  public void setEstado(PagoRegistroEstado estado) { this.estado = estado; }

  public BigDecimal getImporte() { return importe; }
  public void setImporte(BigDecimal importe) { this.importe = importe; }

  public String getExternalReference() { return externalReference; }
  public void setExternalReference(String externalReference) { this.externalReference = externalReference; }

  public String getCallbackUrl() { return callbackUrl; }
  public void setCallbackUrl(String callbackUrl) { this.callbackUrl = callbackUrl; }

  public String getPaymentUrl() { return paymentUrl; }
  public void setPaymentUrl(String paymentUrl) { this.paymentUrl = paymentUrl; }

  public String getProviderStatus() { return providerStatus; }
  public void setProviderStatus(String providerStatus) { this.providerStatus = providerStatus; }

  public String getFailureReason() { return failureReason; }
  public void setFailureReason(String failureReason) { this.failureReason = failureReason; }

  public Instant getCreatedAt() { return createdAt; }
  public Instant getUpdatedAt() { return updatedAt; }

  public Instant getCompletedAt() { return completedAt; }
  public void setCompletedAt(Instant completedAt) { this.completedAt = completedAt; }

  public Instant getFailedAt() { return failedAt; }
  public void setFailedAt(Instant failedAt) { this.failedAt = failedAt; }
}
