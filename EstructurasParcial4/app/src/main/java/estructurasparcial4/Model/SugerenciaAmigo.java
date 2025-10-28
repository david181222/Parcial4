package estructurasparcial4.Model;

// Clase que representa una sugerencia de amigo para un usuario.
// Contiene el id sugerido, su prioridad y el perfil asociado.
public class SugerenciaAmigo implements Comparable<SugerenciaAmigo> {
    private final String idUsuarioSugerido;
    private final int prioridad;
    private final Perfil perfil;

    public SugerenciaAmigo(String idUsuarioSugerido, int prioridad, Perfil perfil) {
        this.idUsuarioSugerido = idUsuarioSugerido;
        this.prioridad = prioridad;
        this.perfil = perfil;
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
        return Integer.compare(otra.prioridad, this.prioridad);
    }
}
