package Tpi_Metodologia.API.services.interfaces;

import java.util.List;

import Tpi_Metodologia.API.dtos.registrar.DomicilioRegistroDto;
import Tpi_Metodologia.API.dtos.registrar.UsuarioRegistroDto;
import Tpi_Metodologia.API.dtos.response.DomicilioResponseDto;
import Tpi_Metodologia.API.dtos.response.UsuarioResponseDto;
import Tpi_Metodologia.API.dtos.update.UsuarioUpdateDto;

public interface IUsuarioService {

    UsuarioResponseDto registrar(UsuarioRegistroDto dto);

    UsuarioResponseDto obtenerporId(int id);

    List<UsuarioResponseDto> listarTodos();

    UsuarioResponseDto actualizar(int id, UsuarioUpdateDto dto);

    void eliminar(int id);

    DomicilioResponseDto agregarDomicilio(int usuarioID, DomicilioRegistroDto dto);

    List<DomicilioResponseDto> listarDomicilios(int usuarioID);

    void eliminarDomicilio(int usuarioID,int domicilioID);

    UsuarioResponseDto aplicarCupon(int usuarioID,String codigoCupon);

    UsuarioResponseDto login(String correo,String contrasena);
}
