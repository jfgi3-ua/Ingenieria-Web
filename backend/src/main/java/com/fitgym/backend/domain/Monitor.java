package com.fitgym.backend.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "monitor")
public class Monitor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String nombre;

    @Column(nullable = false, length = 20)
    private String dni;

    @Column(name="correo_electronico", nullable = false, length = 120)
    private String correoElectronico;

    @Column(name = "contrasena", nullable = false, length = 120)
    private String contrasenya;

    @Column(nullable = false, length = 20)
    private String telefono;

    @Column(nullable = false, length = 80)
    private String ciudad;

    @Column(nullable = false, length = 200)
    private String direccion;

    @Column(name = "codigo_postal", nullable = false, length = 10)
    private String codigoPostal;

    // Getters y Setters
    public Long getId() { return id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public String getCorreoElectronico() { return correoElectronico; }
    public void setCorreoElectronico(String correoElectronico) { this.correoElectronico = correoElectronico; }

    public String getContrasenya() { return contrasenya; }
    public void setContrasenya(String contrasenya) { this.contrasenya = contrasenya; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getCodigoPostal() { return codigoPostal; }
    public void setCodigoPostal(String codigoPostal) { this.codigoPostal = codigoPostal; }
}
