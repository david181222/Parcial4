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

    public Perfil buscarPerfil(String userId) {
        return tablaPerfiles.get(userId);
    }

    public boolean existePerfil(String userId) {
        return tablaPerfiles.containsKey(userId);
    }

    public void eliminarPerfil(String userId) {
        tablaPerfiles.remove(userId);
    }

    public int obtenerTotalPerfiles() {
        return tablaPerfiles.size();
    }

    public HashMap<String, Perfil> obtenerTodosPerfiles() {
        return tablaPerfiles;
    }

}

