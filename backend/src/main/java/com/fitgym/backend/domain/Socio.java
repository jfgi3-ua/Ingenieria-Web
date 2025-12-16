package com.fitgym.backend.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;

/**
 * La clase Socio representa a un miembro del gimnasio.
 *
 * Esta entidad está mapeada a la tabla "socio" en la base de datos y contiene
 * información relevante sobre el socio, incluyendo su identificación, nombre,
 * correo electrónico, contraseña, teléfono, tarifa asociada, estado, y otros
 * detalles de contacto.
 *
 * Atributos:
 * @param id: Identificador único del socio (clave primaria).
 * @param nombre: Nombre del socio, no puede ser nulo y tiene un máximo de 80 caracteres.
 * @param correoElectronico: Correo electrónico del socio, debe ser único, no puede ser nulo y tiene un máximo de 120 caracteres.
 * @param contrasenaHash: Contraseña del socio almacenada como un hash, no puede ser nula y tiene un máximo de 255 caracteres.
 * @param telefono: Número de teléfono del socio, puede ser nulo y tiene un máximo de 20 caracteres.
 * @param tarifa: Relación Many-to-One con la entidad Tarifa, representa la tarifa asociada al socio. Esta relación es obligatoria.
 * @param estado: Estado del socio, representado como un enum, no puede ser nulo.
 * @param pagoDomiciliado: Indica si el pago del socio está domiciliado, valor booleano que por defecto es falso.
 * @param saldoMonedero: Saldo del monedero del socio, no puede ser nulo y tiene precisión de 12 y escala de 2.
 * @param direccion: Dirección del socio, puede ser nula y tiene un máximo de 200 caracteres.
 * @param ciudad: Ciudad del socio, puede ser nula y tiene un máximo de 80 caracteres.
 * @param codigoPostal: Código postal del socio, puede ser nulo y tiene un máximo de 10 caracteres.
 *
 * Métodos:
 * - Getters y Setters para acceder y modificar los atributos de la clase.
 *
 * Notas:
 * - La relación con la entidad Tarifa se carga de forma perezosa (Lazy Loading).
 * - El estado del socio se almacena como un enum en la base de datos.
 */
@Entity
@Table(name = "socio")
public class Socio {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 80)
  private String nombre;

  @Column(name = "correo_electronico", nullable = false, length = 120, unique = true)
  private String correoElectronico;

  @Column(name = "contrasena", nullable = false, length = 255)
  private String contrasenaHash;

  @Column(length = 20)
  private String telefono;

 /**
  * Relación Many-to-One con Tarifa.
  * La tarifa asociada a este socio.
  *
  * Esta es una relación obligatoria muchos-a-uno con la entidad Tarifa.
  * La relación se carga de forma perezosa y la clave foránea se almacena en la
  * columna 'id_tarifa'. Un socio siempre debe tener una tarifa asignada.
  */
  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "id_tarifa", nullable = false)
  private Tarifa tarifa;

  // Postgres enum socio_estado
  @Enumerated(EnumType.STRING)
  @JdbcTypeCode(SqlTypes.NAMED_ENUM)
  @Column(nullable = false, columnDefinition = "socio_estado")
  private SocioEstado estado;

  @Column(name = "pago_domiciliado", nullable = false)
  private boolean pagoDomiciliado = false;

  @Column(name = "saldo_monedero", nullable = false, precision = 12, scale = 2)
  private BigDecimal saldoMonedero = BigDecimal.ZERO;

  @Column(length = 200)
  private String direccion;

  @Column(length = 80)
  private String ciudad;

  @Column(name = "codigo_postal", length = 10)
  private String codigoPostal;

  // Getters y Setters
  public Long getId() { return id; }

  public String getNombre() { return nombre; }
  public void setNombre(String nombre) { this.nombre = nombre; }

  public String getCorreoElectronico() { return correoElectronico; }
  public void setCorreoElectronico(String correoElectronico) { this.correoElectronico = correoElectronico; }

  public String getContrasenaHash() { return contrasenaHash; }
  public void setContrasenaHash(String contrasenaHash) { this.contrasenaHash = contrasenaHash; }

  public String getTelefono() { return telefono; }
  public void setTelefono(String telefono) { this.telefono = telefono; }

  public Tarifa getTarifa() { return tarifa; }
  public void setTarifa(Tarifa tarifa) { this.tarifa = tarifa; }

  public SocioEstado getEstado() { return estado; }
  public void setEstado(SocioEstado estado) { this.estado = estado; }

  public boolean isPagoDomiciliado() { return pagoDomiciliado; }
  public void setPagoDomiciliado(boolean pagoDomiciliado) { this.pagoDomiciliado = pagoDomiciliado; }

  public BigDecimal getSaldoMonedero() { return saldoMonedero; }
  public void setSaldoMonedero(BigDecimal saldoMonedero) { this.saldoMonedero = saldoMonedero; }

  public String getDireccion() { return direccion; }
  public void setDireccion(String direccion) { this.direccion = direccion; }

  public String getCiudad() { return ciudad; }
  public void setCiudad(String ciudad) { this.ciudad = ciudad; }

  public String getCodigoPostal() { return codigoPostal; }
  public void setCodigoPostal(String codigoPostal) { this.codigoPostal = codigoPostal; }
}
