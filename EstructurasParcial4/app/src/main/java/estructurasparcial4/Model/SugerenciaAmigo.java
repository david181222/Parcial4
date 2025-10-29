package estructurasparcial4.Model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// Clase que representa una sugerencia de amigo para un usuario.
// Contiene el id sugerido, su prioridad y el perfil asociado.
public class SugerenciaAmigo implements Comparable<SugerenciaAmigo> {
    private final String idUsuarioSugerido;
    private final int prioridad;
    private final Perfil perfil;

    private static final Logger logger = LogManager.getLogger(SugerenciaAmigo.class);

    public SugerenciaAmigo(String idUsuarioSugerido, int prioridad, Perfil perfil) {
        logger.info("Creando sugerencia de amigo: {}", idUsuarioSugerido);
        
        if (idUsuarioSugerido == null || idUsuarioSugerido.trim().isEmpty()) {
            logger.error("ID de usuario sugerido es nulo o vacío");
            throw new IllegalArgumentException("ID de usuario sugerido no puede ser nulo o vacío");
        }
        
        if (prioridad < 1 || prioridad > 5) {
            logger.error("Prioridad inválida: {} para usuario sugerido: {}", prioridad, idUsuarioSugerido);
            throw new IllegalArgumentException("Prioridad debe estar entre 1 y 5");
        }
        
        if (perfil == null) {
            logger.error("Perfil es nulo para usuario sugerido: {}", idUsuarioSugerido);
            throw new IllegalArgumentException("Perfil no puede ser nulo");
        }
        
        this.idUsuarioSugerido = idUsuarioSugerido;
        this.prioridad = prioridad;
        this.perfil = perfil;
        logger.info("Sugerencia de amigo creada: {} con prioridad: {}", idUsuarioSugerido, prioridad);
    }

    public String getUserIdSugerido() {
        return idUsuarioSugerido;
    }

    public int getPrioridad() {
        return prioridad;
    }

    public Perfil getPerfil() {
        return perfil;
    }

    // Ordena por prioridad (mayor prioridad primero).
    @Override
    public int compareTo(SugerenciaAmigo otra) {
        try {
            if (otra == null) {
                logger.error("Intentando comparar con sugerencia nula");
                throw new IllegalArgumentException("No se puede comparar con una sugerencia nula");
            }
            return Integer.compare(otra.prioridad, this.prioridad);
        } catch (Exception e) {
            logger.error("Error al comparar sugerencias: {}", e.getMessage());
            throw e;
        }
    }
}
