package Tpi_Metodologia.API.services.interfaces;

import Tpi_Metodologia.API.utility.EstadoEnvio;
import Tpi_Metodologia.API.utility.EstadoPedido;

public interface IEmailService {
     
    void enviarConfirmacionPedido(String destinatario, String nombreCliente,int pedidoID, double total);
 
    void enviarNumeroTracking(String destinatario, String nombreCliente,int pedidoID, String numeroTracking);
 
    void enviarNotificacionEstadoPedido(String destinatario, String nombreCliente,int pedidoID, EstadoPedido nuevoEstado);

    void enviarNotificacionEstadoEnvio(String destinatario, String nombreCliente,int pedidoID, String tracking,EstadoEnvio nuevoEstado);
}
