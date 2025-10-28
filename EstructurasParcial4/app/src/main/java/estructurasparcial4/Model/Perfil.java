package estructurasparcial4.Model;

import java.util.HashMap;

// Clase que representa a una persona de la red social. Utilizamos un HashMap para almacenar amigos directos y la calidad de amistad porque resulta más sencillo relacionar el id del amigo
// con la calidad de amistad
public class Perfil {
    private String id;
    private String nombre;
    private short edad;
    private String genero;
    private HashMap<String, Integer> amigosDirectos;

    public Perfil(String id, String nombre, short edad, String genero) {
        this.id = id;
        this.nombre = nombre;
        this.edad = edad;
        this.genero = genero;
        this.amigosDirectos = new HashMap<>();
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public short getEdad() {
        return edad;
    }

    public String getGenero() {
        return genero;
    }

    public HashMap<String, Integer> getAmigosDirectos() {
        return amigosDirectos;
    }

    public boolean esAmigo(String idAmigo) {
        return amigosDirectos.containsKey(idAmigo);
    }

    public void agregarAmigo(String idAmigo, int calidadAmistad) {
        if (calidadAmistad < 1 || calidadAmistad > 5) {
            throw new IllegalArgumentException("Calidad debe estar entre 1 y 5");
        }
        amigosDirectos.put(idAmigo, calidadAmistad);
    }

    public void eliminarAmigo(String idAmigo) {
        amigosDirectos.remove(idAmigo);
    }

    public Integer obtenerCalidadAmistad(String idAmigo) {
        return amigosDirectos.get(idAmigo);
    }
    
    @Override
    public String toString() {
        return "Perfil{" +
                "id='" + id + '\'' +
                ", nombre='" + nombre + '\'' +
                ", edad=" + edad +
                ", genero='" + genero + '\'' +
                ", amigosDirectos=" + amigosDirectos +
                '}';
    }
}
