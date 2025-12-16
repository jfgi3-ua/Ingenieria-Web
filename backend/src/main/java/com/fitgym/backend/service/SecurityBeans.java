package com.fitgym.backend.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Clase de configuración de seguridad para la aplicación.
 *
 * Esta clase se encarga de crear y configurar los beans necesarios para la
 * seguridad de la aplicación, específicamente el codificador de contraseñas.
 *
 * El método {@link #passwordEncoder()} proporciona una instancia de
 * {@link #BCryptPasswordEncoder()}, que es un codificador de contraseñas que
 * utiliza el algoritmo BCrypt. Este algoritmo es conocido por su resistencia
 * a ataques de fuerza bruta, ya que permite ajustar el factor de trabajo,
 * lo que incrementa la complejidad del proceso de hashing.
 *
 * El bean {@link #passwordEncoder()} resultante puede ser inyectado en otros
 * componentes de la aplicación para asegurar que las contraseñas se manejen
 * de manera segura.
 *
 * @return PasswordEncoder una instancia de BCryptPasswordEncoder configurada
 *         y lista para ser utilizada en la aplicación.
 */

@Configuration
public class SecurityBeans {

@Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
