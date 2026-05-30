package Tpi_Metodologia.API.services.impl;

import Tpi_Metodologia.API.services.interfaces.IEmailService;
import Tpi_Metodologia.API.utility.EstadoEnvio;
import Tpi_Metodologia.API.utility.EstadoPedido;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements IEmailService {

    private final JavaMailSender mailSender;

    private static final String FROM = "noreply@tienda.com";

    @Override
    public void enviarConfirmacionPedido(String destinatario, String nombreCliente,int pedidoID, double total) {
        String asunto = "Confirmación de tu pedido #" + pedidoID;
        String cuerpo = String.format(
            "Hola %s,%n%n" +
            "Hemos recibido tu pedido con éxito.%n%n" +
            "   Pedido N°: %d%n" +
            "   Total:     $%.2f%n%n" +
            "Pronto recibirás más novedades sobre el estado de tu pedido.%n%n" +
            "¡Gracias por tu compra!%n" +
            "El equipo de la tienda",
            nombreCliente, pedidoID, total
        );
        enviar(destinatario, asunto, cuerpo);
    }

    @Override
    public void enviarNumeroTracking(String destinatario, String nombreCliente,int pedidoID, String numeroTracking) {
        String asunto = "Tu pedido #" + pedidoID + " está en camino";
        String cuerpo = String.format(
            "Hola %s,%n%n" +
            "Tu pedido ha sido confirmado y está siendo preparado para envío.%n%n" +
            "   Pedido N°:       %d%n" +
            "   Número Tracking: %s%n%n" +
            "Podés seguir tu envío en todo momento consultando el estado con " +
            "tu número de tracking.%n%n" +
            "¡Gracias por tu compra!%n" +
            "El equipo de la tienda",
            nombreCliente, pedidoID, numeroTracking
        );
        enviar(destinatario, asunto, cuerpo);
    }

    @Override
    public void enviarNotificacionEstadoPedido(String destinatario, String nombreCliente,int pedidoID, EstadoPedido nuevoEstado) {
        String asunto = "Actualización de tu pedido #" + pedidoID;
        String descripcion = descripcionEstadoPedido(nuevoEstado);
        String cuerpo = String.format(
            "Hola %s,%n%n" +
            "El estado de tu pedido ha cambiado.%n%n" +
            "   Pedido N°:    %d%n" +
            "   Nuevo estado: %s%n" +
            "   %s%n%n" +
            "Si tenés alguna consulta, no dudes en contactarnos.%n%n" +
            "El equipo de la tienda",
            nombreCliente, pedidoID, nuevoEstado.name(), descripcion
        );
        enviar(destinatario, asunto, cuerpo);
    }

    @Override
    public void enviarNotificacionEstadoEnvio(String destinatario, String nombreCliente,int pedidoID, String tracking,EstadoEnvio nuevoEstado) {
        String asunto = "Estado de envío actualizado — Pedido #" + pedidoID;
        String descripcion = descripcionEstadoEnvio(nuevoEstado);
        String cuerpo = String.format(
            "Hola %s,%n%n" +
            "Tu envío ha sido actualizado.%n%n" +
            "   Pedido N°:       %d%n" +
            "   Tracking:        %s%n" +
            "   Estado del envío: %s%n" +
            "   %s%n%n" +
            "El equipo de la tienda",
            nombreCliente, pedidoID,
            (tracking != null ? tracking : "pendiente de asignación"),
            nuevoEstado.name(), descripcion
        );
        enviar(destinatario, asunto, cuerpo);
    }

    private void enviar(String destinatario, String asunto, String cuerpo) {
        try {
            SimpleMailMessage mensaje = new SimpleMailMessage();
            mensaje.setFrom(FROM);
            mensaje.setTo(destinatario);
            mensaje.setSubject(asunto);
            mensaje.setText(cuerpo);
            mailSender.send(mensaje);
            log.info("Email enviado a {} — Asunto: {}", destinatario, asunto);
        } catch (Exception ex) {
            log.error("Error al enviar email a {}: {}", destinatario, ex.getMessage());
        }
    }

    private String descripcionEstadoPedido(EstadoPedido estado) {
        return switch (estado) {
            case PENDIENTE      -> "Tu pedido fue recibido y está pendiente de confirmación.";
            case CONFIRMADO     -> "Tu pedido fue confirmado y comenzará a prepararse.";
            case EN_PREPARACION -> "Estamos preparando tu pedido para despacharlo.";
            case ENVIO          -> "Tu pedido fue despachado y está en camino.";
            case ENTREGADO      -> "¡Tu pedido fue entregado con éxito!";
            case CANCELADO      -> "Tu pedido fue cancelado. El stock ha sido restituido.";
        };
    }

    private String descripcionEstadoEnvio(EstadoEnvio estado) {
        return switch (estado) {
            case PENDIENTE  -> "Tu envío está pendiente de preparación.";
            case PREPARANDO -> "Estamos preparando el paquete para su despacho.";
            case DESPACHADO -> "El paquete fue despachado desde nuestras instalaciones.";
            case EN_CAMINO  -> "El paquete está en camino a tu domicilio.";
            case ENTREGADO  -> "¡El paquete fue entregado en tu domicilio!";
            case DEVUELTO   -> "El paquete fue devuelto. Nos pondremos en contacto contigo.";
        };
    }
}