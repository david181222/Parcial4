package estructurasparcial4.Service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import estructurasparcial4.Model.Perfil;
import java.util.HashMap;

// Clase que guarda y gestiona los perfiles de la red social simulando una base de datos con hashmap
public class AlmacenamientoPerfiles {
    private final HashMap<String, Perfil> tablaPerfiles;
    private static final Logger logger = LogManager.getLogger(AlmacenamientoPerfiles.class);

    public AlmacenamientoPerfiles() {
        logger.info("Inicializando AlmacenamientoPerfiles");
        this.tablaPerfiles = new HashMap<>();
        logger.info("AlmacenamientoPerfiles inicializado");
    }

    public void crearPerfil(Perfil perfil) {
        logger.info("Intentando crear perfil");
        try {
            if (perfil == null) {
                logger.error("Intento de crear perfil nulo");
                throw new IllegalArgumentException("Perfil no puede ser null");
            }
            
            if (perfil.getId() == null || perfil.getId().trim().isEmpty()) {
                logger.error("Intento de crear perfil con ID nulo o vacío");
                throw new IllegalArgumentException("ID de perfil no puede ser nulo o vacío");
            }
            
            if (tablaPerfiles.containsKey(perfil.getId())) {
                logger.error("UserID ya existe: {}", perfil.getId());
                throw new IllegalArgumentException("UserID ya existe: " + perfil.getId());
            }
            
            tablaPerfiles.put(perfil.getId(), perfil);
            logger.info("Perfil creado exitosamente: {}", perfil.getId());
        } catch (Exception e) {
            logger.error("Error al crear perfil: {}", e.getMessage());
            throw e;
        }
    }

    public Perfil buscarPerfil(String idUsuario) {
        logger.info("Buscando perfil: {}", idUsuario);
        try {
            if (idUsuario == null || idUsuario.trim().isEmpty()) {
                logger.error("ID de usuario nulo o vacío en búsqueda");
                return null;
            }
            
            Perfil perfil = tablaPerfiles.get(idUsuario);
            if (perfil == null) {
                logger.warn("Perfil no encontrado: {}", idUsuario);
            } else {
                logger.info("Perfil encontrado: {}", idUsuario);
            }
            return perfil;
        } catch (Exception e) {
            logger.error("Error al buscar perfil {}: {}", idUsuario, e.getMessage());
            return null;
        }
    }

    public boolean existePerfil(String idUsuario) {
        logger.info("Verificando existencia de perfil: {}", idUsuario);
        try {
            if (idUsuario == null || idUsuario.trim().isEmpty()) {
                logger.error("ID de usuario nulo o vacío en verificación");
                return false;
            }
            boolean existe = tablaPerfiles.containsKey(idUsuario);
            logger.info("Perfil {} existe: {}", idUsuario, existe);
            return existe;
        } catch (Exception e) {
            logger.error("Error al verificar existencia de perfil {}: {}", idUsuario, e.getMessage());
            return false;
        }
    }

    public void eliminarPerfil(String idUsuario) {
        logger.info("Intentando eliminar perfil: {}", idUsuario);
        try {
            if (idUsuario == null || idUsuario.trim().isEmpty()) {
                logger.error("ID de usuario nulo o vacío en eliminación");
                throw new IllegalArgumentException("ID de usuario no puede ser nulo o vacío");
            }
            
            if (!tablaPerfiles.containsKey(idUsuario)) {
                logger.warn("Perfil no encontrado para eliminar: {}", idUsuario);
            } else {
                tablaPerfiles.remove(idUsuario);
                logger.info("Perfil eliminado exitosamente: {}", idUsuario);
            }
        } catch (Exception e) {
            logger.error("Error al eliminar perfil {}: {}", idUsuario, e.getMessage());
            throw e;
        }
    }

    public int obtenerTotalPerfiles() {
        logger.info("Obteniendo total de perfiles");
        try {
            int total = tablaPerfiles.size();
            logger.info("Total de perfiles: {}", total);
            return total;
        } catch (Exception e) {
            logger.error("Error al obtener total de perfiles: {}", e.getMessage());
            return 0;
        }
    }

    public HashMap<String, Perfil> obtenerTodosPerfiles() {
        logger.info("Obteniendo todos los perfiles");
        try {
            logger.info("Total de perfiles retornados: {}", tablaPerfiles.size());
            return tablaPerfiles;
        } catch (Exception e) {
            logger.error("Error al obtener todos los perfiles: {}", e.getMessage());
            return new HashMap<>();
        }
    }

    // Método para obtener la calidad de amistad entre dos perfiles
    public Integer obtenerCalidadAmistad(String idPerfilA, String idPerfilB) {
        logger.info("Obteniendo calidad de amistad entre {} y {}", idPerfilA, idPerfilB);
        try {
            if (idPerfilA == null || idPerfilB == null) {
                logger.error("IDs de perfiles nulos en obtención de calidad de amistad");
                return null;
            }

            Perfil perfilA = buscarPerfil(idPerfilA);
            if (perfilA != null) {
                Integer q = perfilA.obtenerCalidadAmistad(idPerfilB);
                if (q != null) {
                    logger.info("Calidad de amistad encontrada desde perfil {}: {}", idPerfilA, q);
                    return q;
                }
            }
            
            Perfil perfilB = buscarPerfil(idPerfilB);
            if (perfilB != null) {
                Integer q = perfilB.obtenerCalidadAmistad(idPerfilA);
                if (q != null) {
                    logger.info("Calidad de amistad encontrada desde perfil {}: {}", idPerfilB, q);
                    return q;
                }
            }

            logger.warn("No se encontró calidad de amistad entre {} y {}", idPerfilA, idPerfilB);
            return null;
        } catch (Exception e) {
            logger.error("Error al obtener calidad de amistad entre {} y {}: {}", idPerfilA, idPerfilB, e.getMessage());
            return null;
        }
    }

}
