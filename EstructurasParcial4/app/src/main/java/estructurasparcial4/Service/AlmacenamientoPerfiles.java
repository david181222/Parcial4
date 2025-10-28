package estructurasparcial4.Service;

import estructurasparcial4.Model.Perfil;
import java.util.HashMap;

// Clase que guarda y gestiona los perfiles de la red social simulando una base de datos con hashmap
public class AlmacenamientoPerfiles {
    private final HashMap<String, Perfil> tablaPerfiles;

    public AlmacenamientoPerfiles() {
        this.tablaPerfiles = new HashMap<>();
    }

    public void crearPerfil(Perfil perfil) {
        if (perfil == null) {
            throw new IllegalArgumentException("Perfil no puede ser null");
        }
        if (tablaPerfiles.containsKey(perfil.getId())) {
            throw new IllegalArgumentException("UserID ya existe: " + perfil.getId());
        }
        tablaPerfiles.put(perfil.getId(), perfil);
    }

    public Perfil buscarPerfil(String idUsuario) {
        return tablaPerfiles.get(idUsuario);
    }

    public boolean existePerfil(String idUsuario) {
        return tablaPerfiles.containsKey(idUsuario);
    }

    public void eliminarPerfil(String idUsuario) {
        tablaPerfiles.remove(idUsuario);
    }

    public int obtenerTotalPerfiles() {
        return tablaPerfiles.size();
    }

    public HashMap<String, Perfil> obtenerTodosPerfiles() {
        return tablaPerfiles;
    }

    // Método para obtener la calidad de amistad entre dos perfiles
    public Integer obtenerCalidadAmistad(String idPerfilA, String idPerfilB) {
        if (idPerfilA == null || idPerfilB == null)
            return null;

        Perfil perfilA = buscarPerfil(idPerfilA);
        if (perfilA != null) {
            Integer q = perfilA.obtenerCalidadAmistad(idPerfilB);
            if (q != null)
                return q;
        }
        
        Perfil perfilB = buscarPerfil(idPerfilB);
        if (perfilB != null) {
            Integer q = perfilB.obtenerCalidadAmistad(idPerfilA);
            if (q != null)
                return q;
        }

        return null;
    }

}
