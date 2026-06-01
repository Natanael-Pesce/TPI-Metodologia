package Tpi_Metodologia.API.mappers;

import Tpi_Metodologia.API.dtos.registrar.CuponRegistroDto;
import Tpi_Metodologia.API.dtos.response.CuponResponseDto;
import Tpi_Metodologia.API.models.Cupon;

public class CuponMapper {
        public static Cupon toEntity(CuponRegistroDto dto) {
        Cupon cupon = new Cupon();
        cupon.setCodigo(dto.getCodigo());
        cupon.setDescuento(dto.getDescuento());
        cupon.setFechaInicio(dto.getFechaInicio());
        cupon.setFechaFin(dto.getFechaFin());
        cupon.setEstado(true);
        return cupon;
    }
 
    public static CuponResponseDto toDto(Cupon cupon) {
        CuponResponseDto dto = new CuponResponseDto();
        dto.setCuponID(cupon.getCuponID());
        dto.setCodigo(cupon.getCodigo());
        dto.setDescuento(cupon.getDescuento());
        dto.setEstado(cupon.isEstado());
        dto.setFechaInicio(cupon.getFechaInicio());
        dto.setFechaFin(cupon.getFechaFin());
        return dto;
    }
    
}
