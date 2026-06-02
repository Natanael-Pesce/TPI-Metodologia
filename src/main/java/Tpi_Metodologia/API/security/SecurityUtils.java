package Tpi_Metodologia.API.security;

import Tpi_Metodologia.API.models.Usuario;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    private SecurityUtils() {}

    public static Usuario getUsuarioAutenticado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof Usuario)) {
            throw new AccessDeniedException("No hay usuario autenticado");
        }
        return (Usuario) auth.getPrincipal();
    }
    
    public static int getUsuarioAutenticadoId() {
        return getUsuarioAutenticado().getUsuarioID();
    }
}