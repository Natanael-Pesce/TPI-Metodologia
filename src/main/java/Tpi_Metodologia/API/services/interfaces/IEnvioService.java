package Tpi_Metodologia.API.services.interfaces;

import Tpi_Metodologia.API.dtos.response.EnvioResponseDto;
import Tpi_Metodologia.API.utility.EstadoEnvio;

import java.util.List;

public interface IEnvioService {

    EnvioResponseDto obtenerPorId(int id);

    EnvioResponseDto obtenerPorTracking(String tracking);

    List<EnvioResponseDto> listarTodos();

    List<EnvioResponseDto> listarPorEstado(EstadoEnvio estado);

    /** Actualiza el estado del envío y opcionalmente el tracking */
    EnvioResponseDto actualizarEstado(int envioID, EstadoEnvio nuevoEstado, String tracking);
}
