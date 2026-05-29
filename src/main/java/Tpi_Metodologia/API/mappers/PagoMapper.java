package Tpi_Metodologia.API.mappers;

import Tpi_Metodologia.API.dtos.registrar.PagoRegistroDto;
import Tpi_Metodologia.API.dtos.response.PagoResponseDto;
import Tpi_Metodologia.API.models.Pago;
import Tpi_Metodologia.API.utility.EstadoPago;
import Tpi_Metodologia.API.utility.TipoPago;

public class PagoMapper {
    public static Pago toEntity(PagoRegistroDto dto) {
        Pago pago = new Pago();
        pago.setTipoPago(TipoPago.valueOf(dto.getTipoPago().toUpperCase()));
        pago.setMonto(dto.getMonto());
        pago.setEstadoPago(EstadoPago.PENDIENTE);
        return pago;
    }

    public static PagoResponseDto toDto(Pago pago) {
        PagoResponseDto dto = new PagoResponseDto();
        dto.setPagoID(pago.getPagoID());
        dto.setTipoPago(pago.getTipoPago());
        dto.setEstadoPago(pago.getEstadoPago());
        dto.setFechaPago(pago.getFechaPago());
        dto.setMonto(pago.getMonto());
        return dto;
    }
}
