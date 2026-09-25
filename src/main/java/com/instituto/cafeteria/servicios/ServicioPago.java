package com.instituto.cafeteria.servicios;

import org.springframework.stereotype.Service;
import java.time.LocalDate;

@Service
public class ServicioPago {

    public static class ResultadoPago {
        private final boolean exito;
        private final String mensaje;

        public ResultadoPago(boolean exito, String mensaje) {
            this.exito = exito;
            this.mensaje = mensaje;
        }

        public boolean isExito() { return exito; }
        public String getMensaje() { return mensaje; }
    }

    public ResultadoPago procesarPago(String metodo, String numeroTarjeta,
                                       String caducidad, String cvv, String telefono) {
        if ("TARJETA".equalsIgnoreCase(metodo)) {
            String num = numeroTarjeta != null ? numeroTarjeta.replaceAll("\\s+", "") : "";
            
            if (!num.matches("\\d{16}")) {
                return new ResultadoPago(false, "Número de tarjeta inválido");
            }
            if (num.endsWith("0000")) {
                return new ResultadoPago(false, "Tarjeta rechazada por el banco");
            }
            if (num.endsWith("1111")) {
                return new ResultadoPago(false, "Saldo insuficiente");
            }
            if (caducidad == null || !caducidad.matches("^(0[1-9]|1[0-2])/\\d{2}$")) {
                return new ResultadoPago(false, "Tarjeta caducada");
            }
            
            try {
                String[] parts = caducidad.split("/");
                int mes = Integer.parseInt(parts[0]);
                int anio = Integer.parseInt(parts[1]) + 2000;
                
                LocalDate hoy = LocalDate.now();
                int anioActual = hoy.getYear();
                int mesActual = hoy.getMonthValue();
                
                if (anio < anioActual || (anio == anioActual && mes < mesActual)) {
                    return new ResultadoPago(false, "Tarjeta caducada");
                }
            } catch (Exception e) {
                return new ResultadoPago(false, "Tarjeta caducada");
            }
            
            return new ResultadoPago(true, "Pago con tarjeta aprobado");
            
        } else if ("BIZUM".equalsIgnoreCase(metodo)) {
            String tel = telefono != null ? telefono.replaceAll("\\s+", "") : "";
            
            if (!tel.matches("\\d{9}")) {
                return new ResultadoPago(false, "Número de teléfono inválido");
            }
            if (tel.startsWith("000")) {
                return new ResultadoPago(false, "Número de Bizum no registrado");
            }
            if (tel.startsWith("999")) {
                return new ResultadoPago(false, "Límite diario de Bizum superado");
            }
            
            return new ResultadoPago(true, "Pago con Bizum aprobado");
        }
        
        return new ResultadoPago(false, "Método de pago no soportado");
    }
}
