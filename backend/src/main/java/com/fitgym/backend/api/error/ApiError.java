package com.fitgym.backend.api.error;

import java.time.Instant;

/**
 * Clase que representa un error de la API.
 * 
 * Esta clase se utiliza para encapsular la información sobre un error que ocurre en la API,
 * incluyendo detalles como la marca de tiempo del error, el estado HTTP, el tipo de error,
 * un mensaje descriptivo y la ruta de la solicitud que causó el error.
 * 
 * @param timestamp La marca de tiempo en la que ocurrió el error.
 * @param status El código de estado HTTP asociado con el error.
 * @param error Una descripción breve del tipo de error.
 * @param message Un mensaje más detallado que describe el error.
 * @param path La ruta de la solicitud que causó el error.
 */
public record ApiError(
    Instant timestamp,
    int status,
    String error,
    String message,
    String path
) {}
