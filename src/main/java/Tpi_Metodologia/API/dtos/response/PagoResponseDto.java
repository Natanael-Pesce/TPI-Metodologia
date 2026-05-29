package Tpi_Metodologia.API.dtos.response;

import lombok.Data;

import java.time.LocalDate;

import Tpi_Metodologia.API.utility.EstadoPago;
import Tpi_Metodologia.API.utility.TipoPago;

@Data
public class PagoResponseDto {
    private int pagoID;
    private TipoPago tipoPago;
    private EstadoPago estadoPago;
    private LocalDate fechaPago;
    private double monto;
}