package Tpi_Metodologia.API.mappers;

import Tpi_Metodologia.API.dtos.registrar.DomicilioRegistroDto;
import Tpi_Metodologia.API.dtos.response.DomicilioResponseDto;
import Tpi_Metodologia.API.models.Domicilio;

public class DomicilioMapper {

    public static Domicilio toEntity(DomicilioRegistroDto dto) {
        Domicilio domicilio = new Domicilio();
        domicilio.setPais(dto.getPais());
        domicilio.setProvincia(dto.getProvincia());
        domicilio.setCiudad(dto.getCiudad());
        domicilio.setCalle(dto.getCalle());
        domicilio.setNro(dto.getNro());
        domicilio.setDepartamento(dto.getDepartamento());
        domicilio.setNroDepartamento(dto.getNroDepartamento());
        domicilio.setPiso(dto.getPiso());
        return domicilio;
    }

    public static DomicilioResponseDto toDto(Domicilio domicilio) {
        DomicilioResponseDto dto = new DomicilioResponseDto();
        dto.setDomicilioID(domicilio.getDomicilioID());
        dto.setPais(domicilio.getPais());
        dto.setProvincia(domicilio.getProvincia());
        dto.setCiudad(domicilio.getCiudad());
        dto.setCalle(domicilio.getCalle());
        dto.setNro(domicilio.getNro());
        dto.setDepartamento(domicilio.getDepartamento());
        dto.setNroDepartamento(domicilio.getNroDepartamento());
        dto.setPiso(domicilio.getPiso());
        return dto;
    }
    
}
