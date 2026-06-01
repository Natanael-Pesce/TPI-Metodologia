package Tpi_Metodologia.API.mappers;

import Tpi_Metodologia.API.dtos.response.EnvioResponseDto;
import Tpi_Metodologia.API.models.Domicilio;
import Tpi_Metodologia.API.models.Envio;
import Tpi_Metodologia.API.repositories.DomicilioRepository;
import Tpi_Metodologia.API.utility.EstadoEnvio;

public class EnvioMapper {

    public static Envio toEntity(int domicilioID, DomicilioRepository domicilioRepository) {
        Envio envio = new Envio();
        envio.setEstadoEnvio(EstadoEnvio.PREPARANDO);
 
        Domicilio domicilio = domicilioRepository.findById(domicilioID)
            .orElseThrow(() -> new RuntimeException("Domicilio no encontrado con ID: " + domicilioID));
        envio.setDomicilio(domicilio);
 
        return envio;
    }
 
    public static EnvioResponseDto toDto(Envio envio) {
        EnvioResponseDto dto = new EnvioResponseDto();
        dto.setEnvioID(envio.getEnvioID());
        dto.setTracking(envio.getTracking());
        dto.setEstadoEnvio(envio.getEstadoEnvio());
        dto.setFechaEntrega(envio.getFechaEntrega());
 
        if (envio.getDomicilio() != null) {
            dto.setDomicilio(DomicilioMapper.toDto(envio.getDomicilio()));
        }
        return dto;
    }
    
}
