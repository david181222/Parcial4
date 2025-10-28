package estructurasparcial4.Util;

import java.util.HashMap;
import java.util.Map;

import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

import com.google.common.graph.Graph;
import com.google.errorprone.annotations.OverridingMethodsMustInvokeSuper;

import estructurasparcial4.Model.Perfil;
import estructurasparcial4.Service.AlmacenamientoPerfiles;

public class WeightedQuickUnionUF {

    private int[] parent; // parent[i] = padre de i
    private int[] size; // size[i] = tamaño del árbol raíz i
    private int count; // número de componentes

    // Para el mapa bidireccional que nos permite mapear usuarios a índices y
    // viceversa
    private final HashMap<String, Integer> usuarioAindex; // Mapa de usuario a índice
    private final HashMap<Integer, String> indiceAusuario; // Mapa de índice a usuario

    private int siguienteIndice; // Siguiente índice del usuario a añadir

    public WeightedQuickUnionUF(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacidad debe ser mayor que 0");
        }
        count = 0;
        parent = new int[capacity];
        size = new int[capacity];
        usuarioAindex = new HashMap<>();
        indiceAusuario = new HashMap<>();
        siguienteIndice = 0;

        // Preinicializa los arrays
        for (int i = 0; i < capacity; i++) {
            parent[i] = i;
            size[i] = 1;
        }
    }

    public int getCount() {
        return count;
    }

    // El método find se le añadio path compression para optimizar las búsquedas
    public int find(int userIndex) {
        validate(userIndex);

        // Primera pasada que encuentra la raíz
        int root = userIndex;
        while (root != parent[root]) {
            root = parent[root];
        }

        // Segunda pasada para hacer path compression
        int current = userIndex;
        int next;
        while (current != root) {
            next = parent[current];
            parent[current] = root;
            current = next;
        }
        return root;
    }

   
    @Deprecated
    public boolean connected(int p, int q) {
        return find(p) == find(q);
    }

    // Valida que el índice esté dentro del rango
    private void validate(int userIndex) {
        if (userIndex < 0 || userIndex >= siguienteIndice) {
            throw new IllegalArgumentException("Índice " + userIndex + " no es válido (0.." + (siguienteIndice - 1) + ")");
        }
    }

    // Método para unir dos perfiles
    public void union(int p, int q) {
        int rootP = find(p);
        int rootQ = find(q);
        if (rootP == rootQ)
            return;

        if (size[rootP] < size[rootQ]) {
            parent[rootP] = rootQ;
            size[rootQ] += size[rootP];
        } else {
            parent[rootQ] = rootP;
            size[rootP] += size[rootQ];
        }
        count--;
    }

    // Método que registra a un perfil a la red social en ambos mapas de manera bidireccional
    public void agregarUsuario(String userId) {
        if (usuarioAindex.containsKey(userId)) {
            return; 
        }

        if (siguienteIndice >= parent.length) {
            throw new IllegalStateException("Capacidad máxima alcanzada");
        }

        int index = siguienteIndice;
        usuarioAindex.put(userId, index);
        indiceAusuario.put(index, userId);
        parent[index] = index;
        size[index] = 1;
        siguienteIndice++;
        count++;  
    }

    // Método que verifica si dos perfiles están conectados mediante su id
    public boolean conectados(String idPerfil1, String idPerfil2) {
        Integer indiceA = usuarioAindex.get(idPerfil1);
        Integer indiceB = usuarioAindex.get(idPerfil2);

        if (indiceA == null || indiceB == null) {
            return false;
        }
        return find(indiceA) == find(indiceB);
    }

     // Método que genera una amistad entre dos usuarios con una calidad dada empleando union()
    public void generarAmistad(String idPerfil1, String idPerfil2, int calidad) {
        if (calidad < 1 || calidad > 5) {
            throw new IllegalArgumentException("Calidad debe estar entre 1 y 5");
        }

        Integer indiceA = usuarioAindex.get(idPerfil1);
        Integer indiceB = usuarioAindex.get(idPerfil2);

        if (indiceA == null || indiceB == null) {
            throw new IllegalArgumentException("Uno o ambos usuarios no existen");
        }

        union(indiceA, indiceB);  
    }

    // Método que visualiza la red social basado del método imprimirArbol()
     public String visualizarRedSocial(AlmacenamientoPerfiles almacenamiento) {
        Node nodo = null;
        String estadoActual = toString();
        SingleGraph grafo = new SingleGraph("Red Social");
        String idPerfil;
        
        grafo.setAttribute("ui.stylesheet",
                "node { fill-color: lightblue; size: 25px; text-size: 14px; text-alignment: under; }" +
                "edge { fill-color: gray; size: 2px; text-size: 12px; }");

        for (int i = 0; i < siguienteIndice; i++) {
            idPerfil = indiceAusuario.get(i);
            nodo = grafo.addNode(idPerfil);
            nodo.setAttribute("ui.label", idPerfil);
        }

        // Iteramos los perfiles en el almacenamiento y extraemos los lazos.
        // Para evitar duplicados (amistad A-B y B-A) sólo añadimos la arista
        // cuando userId <= friendId según orden lexicográfico.

        // Debemos iterar los perfiles para obtener las amistades, luego añadimos las aristas al árbol
        String a;
        String b;
        String idExtremo;
        for (Perfil perfil : almacenamiento.obtenerTodosPerfiles().values()) {
            a = perfil.getId();
            for (Map.Entry<String, Integer> entrada : perfil.getAmigosDirectos().entrySet()) {
                b = entrada.getKey();
                if (a.compareTo(b) <= 0) {
                    idExtremo = a + "-" + b;
                    grafo.addEdge(idExtremo, a, b, false);
        
                }
            }
        }

        grafo.display();
        return estadoActual;
    }

    @Override
    public String toString() {
        String estado = "-".repeat(10) + "\n";
        estado += "Parent [ ] = " + java.util.Arrays.toString(java.util.Arrays.copyOf(parent, siguienteIndice)) + "\n";
        estado += "Size   [ ] = " + java.util.Arrays.toString(java.util.Arrays.copyOf(size, siguienteIndice)) + "\n";
        estado += "Usuarios: " + siguienteIndice + ", Componentes: " + count + "\n";
        return estado;
    }
}