package Tpi_Metodologia.API.services.interfaces;

import Tpi_Metodologia.API.dtos.registrar.DomicilioRegistroDto;
import Tpi_Metodologia.API.dtos.response.DomicilioResponseDto;
import Tpi_Metodologia.API.dtos.update.DomicilioUpdateDto;

import java.util.List;

public interface IDomicilioService {

    DomicilioResponseDto crear(DomicilioRegistroDto dto);

    DomicilioResponseDto obtenerPorId(int id);

    List<DomicilioResponseDto> listarTodos();

    DomicilioResponseDto update(int id, DomicilioUpdateDto dto);

    void eliminar(int id);
}