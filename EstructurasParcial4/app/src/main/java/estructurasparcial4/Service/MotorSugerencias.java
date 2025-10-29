package estructurasparcial4.Service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import estructurasparcial4.Model.Perfil;
import estructurasparcial4.Model.SugerenciaAmigo;
import java.util.PriorityQueue;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import estructurasparcial4.Util.WeightedQuickUnionUF;

// Clase que contiene la lógica para generar lazos y sugerir amigos.
// Se apoya en `AlmacenamientoPerfiles` para leer perfiles y en el arbol
// para representar la red cuando es necesario.
public class MotorSugerencias {
    private final AlmacenamientoPerfiles almacenamiento;
    private static final Logger logger = LogManager.getLogger(MotorSugerencias.class);

    // Constructor: recibe el almacenamiento de perfiles que usará.
    public MotorSugerencias(AlmacenamientoPerfiles almacenamiento) {
        logger.info("Inicializando MotorSugerencias");
        if (almacenamiento == null) {
            logger.error("Almacenamiento nulo en constructor de MotorSugerencias");
            throw new IllegalArgumentException("Almacenamiento no puede ser nulo");
        }
        this.almacenamiento = almacenamiento;
        logger.info("MotorSugerencias inicializado");
    }

    // Crea la relación de amistad entre dos usuarios en el almacenamiento.
    // No toca la estructura UF; solo actualiza los perfiles.
    public void generarAmistad(String idUsuarioA, String idUsuarioB, int calidad) {
        logger.info("Generando amistad entre {} y {} con calidad {}", idUsuarioA, idUsuarioB, calidad);
        try {
            if (idUsuarioA == null || idUsuarioA.trim().isEmpty()) {
                logger.error("ID de usuario A nulo o vacío");
                throw new IllegalArgumentException("ID de usuario A no puede ser nulo o vacío");
            }

            if (idUsuarioB == null || idUsuarioB.trim().isEmpty()) {
                logger.error("ID de usuario B nulo o vacío");
                throw new IllegalArgumentException("ID de usuario B no puede ser nulo o vacío");
            }

            if (idUsuarioA.equals(idUsuarioB)) {
                logger.error("Intento de crear amistad consigo mismo: {}", idUsuarioA);
                throw new IllegalArgumentException("Un usuario no puede ser amigo de sí mismo");
            }

            Perfil perfilA = almacenamiento.buscarPerfil(idUsuarioA);
            Perfil perfilB = almacenamiento.buscarPerfil(idUsuarioB);

            if (perfilA == null || perfilB == null) {
                logger.error("Uno o ambos usuarios no existen. Usuario A: {}, Usuario B: {}", idUsuarioA, idUsuarioB);
                throw new IllegalArgumentException("Uno o ambos usuarios no existen");
            }

            if (calidad < 1 || calidad > 5) {
                logger.error("Calidad de amistad inválida: {}", calidad);
                throw new IllegalArgumentException("Calidad debe estar entre 1 y 5");
            }

            perfilA.agregarAmigo(idUsuarioB, calidad);
            perfilB.agregarAmigo(idUsuarioA, calidad);
            logger.info("Amistad generada exitosamente entre {} y {}", idUsuarioA, idUsuarioB);
        } catch (Exception e) {
            logger.error("Error al generar amistad: {}", e.getMessage());
            throw e;
        }
    }

    // Calcula y devuelve una lista de sugerencias de amigos para un usuario.
    // Solo se usa la calidad de amistad del amigo directo como prioridad.
    public List<SugerenciaAmigo> sugerirAmigos(String idUsuario) {
        logger.info("Generando sugerencias de amigos para usuario: {}", idUsuario);
        try {
            if (idUsuario == null || idUsuario.trim().isEmpty()) {
                logger.error("ID de usuario nulo o vacío en sugerencias");
                throw new IllegalArgumentException("ID de usuario no puede ser nulo o vacío");
            }

            Perfil perfilUsuario = almacenamiento.buscarPerfil(idUsuario);
            if (perfilUsuario == null) {
                logger.error("Usuario no existe: {}", idUsuario);
                throw new IllegalArgumentException("Usuario no existe: " + idUsuario);
            }

            HashMap<String, SugerenciaAmigo> candidatosPorId = new HashMap<>();
            HashMap<String, Integer> amigosUsuario = perfilUsuario.getAmigosDirectos();

            String amigoDirectoId = null;
            int calidadConAmigoDirecto = 0;
            Perfil perfilAmigoDirecto = null;
            HashMap<String, Integer> amigosDelAmigoDirecto = null;
            String candidatoId = null;
            Perfil perfilCandidato = null;
            SugerenciaAmigo candidatoExistente = null;

            for (HashMap.Entry<String, Integer> entradaAmigo : amigosUsuario.entrySet()) {
                amigoDirectoId = entradaAmigo.getKey();
                calidadConAmigoDirecto = entradaAmigo.getValue();

                perfilAmigoDirecto = almacenamiento.buscarPerfil(amigoDirectoId);
                if (perfilAmigoDirecto == null)
                    continue;

                amigosDelAmigoDirecto = perfilAmigoDirecto.getAmigosDirectos();
                if (amigosDelAmigoDirecto == null)
                    continue;

                for (java.util.Iterator<String> it = amigosDelAmigoDirecto.keySet().iterator(); it.hasNext();) {
                    candidatoId = it.next();
                    if (candidatoId.equals(idUsuario))
                        continue;
                    if (amigosUsuario.containsKey(candidatoId))
                        continue;

                    perfilCandidato = almacenamiento.buscarPerfil(candidatoId);
                    if (perfilCandidato == null)
                        continue;

                    candidatoExistente = candidatosPorId.get(candidatoId);
                    if (candidatoExistente == null || calidadConAmigoDirecto > candidatoExistente.getPrioridad()) {
                        candidatosPorId.put(candidatoId,
                                new SugerenciaAmigo(candidatoId, calidadConAmigoDirecto, perfilCandidato));
                    }
                }
            }

            PriorityQueue<SugerenciaAmigo> colaPriorizada = new PriorityQueue<>(candidatosPorId.values());

            List<SugerenciaAmigo> resultados = new ArrayList<>();
            while (!colaPriorizada.isEmpty()) {
                resultados.add(colaPriorizada.poll());
            }

            resultados = ordenarPorPrioridadYNombre(resultados);
            logger.info("Sugerencias generadas para {}: {} sugerencias encontradas", idUsuario, resultados.size());

            return resultados;
        } catch (Exception e) {
            logger.error("Error al sugerir amigos para {}: {}", idUsuario, e.getMessage());
            throw e;
        }
    }

    // Carga los lazos de amistad almacenados en los perfiles hacia la
    // estructura de red (WeightedQuickUnionUF). Esta operación centraliza
    // la lógica de creación de lazos en el motor de sugerencias
    public String cargarLazosDesdePerfiles(WeightedQuickUnionUF redSocial) {
        logger.info("Cargando lazos desde perfiles a la red social");
        try {
            if (redSocial == null) {
                logger.error("Red social nula en carga de lazos");
                throw new IllegalArgumentException("Red social no puede ser nula");
            }

            StringBuilder resultado = new StringBuilder();
            int lazosCreados = 0;

            HashMap<String, Integer> amigos;
            java.util.Iterator<Map.Entry<String, Integer>> iterador;
            Map.Entry<String, Integer> entrada;
            String idDeAmigo;
            int calidad;

            for (Perfil perfil : almacenamiento.obtenerTodosPerfiles().values()) {
                amigos = perfil.getAmigosDirectos();
                if (amigos == null)
                    continue;
                iterador = amigos.entrySet().iterator();
                while (iterador.hasNext()) {
                    entrada = iterador.next();
                    idDeAmigo = entrada.getKey();
                    calidad = entrada.getValue();

                    if (!redSocial.conectados(perfil.getId(), idDeAmigo)) {
                        try {
                            redSocial.generarAmistad(perfil.getId(), idDeAmigo, calidad);
                            lazosCreados++;
                        } catch (Exception e) {
                            logger.error("Error al crear lazo entre {} y {}: {}", perfil.getId(), idDeAmigo,
                                    e.getMessage());
                            throw new IllegalArgumentException("Error al crear lazo: " + e.getMessage());
                        }
                    }
                }
            }
            resultado.append("Lazos de amistad cargados: ").append(lazosCreados).append(System.lineSeparator());
            logger.info("Lazos cargados exitosamente: {}", lazosCreados);
            return resultado.toString();
        } catch (Exception e) {
            logger.error("Error al cargar lazos desde perfiles: {}", e.getMessage());
            throw e;
        }
    }

    // Se le dio a la IA la implementación de un Mergesort para que lo adaptará a nuestro caso: ordena por prioridad (desc) y por nombre
    // (asc)
    // Se eligió este algoritmo por su eficiencia O(n Logn)
    private List<SugerenciaAmigo> ordenarPorPrioridadYNombre(List<SugerenciaAmigo> lista) {
        logger.info("Ordenando sugerencias por prioridad y nombre");
        try {
            if (lista == null || lista.size() <= 1)
                return lista;
            return ordenarRecursivo(lista, 0, lista.size() - 1);
        } catch (Exception e) {
            logger.error("Error al ordenar sugerencias: {}", e.getMessage());
            return lista;
        }
    }

    private List<SugerenciaAmigo> ordenarRecursivo(List<SugerenciaAmigo> lista, int izq, int der) {
        try {
            if (izq == der) {
                List<SugerenciaAmigo> listaUnidad = new ArrayList<>();
                listaUnidad.add(lista.get(izq));
                return listaUnidad;
            }
            int medio = (izq + der) >>> 1;
            List<SugerenciaAmigo> izquierda = ordenarRecursivo(lista, izq, medio);
            List<SugerenciaAmigo> derecha = ordenarRecursivo(lista, medio + 1, der);
            return fusionar(izquierda, derecha);
        } catch (Exception e) {
            logger.error("Error en ordenamiento recursivo: {}", e.getMessage());
            throw e;
        }
    }

    private List<SugerenciaAmigo> fusionar(List<SugerenciaAmigo> izquierda, List<SugerenciaAmigo> derecha) {
        try {
            int indiceIzquierda = 0, indiceDerecha = 0;
            List<SugerenciaAmigo> listaFusionada = new ArrayList<>(izquierda.size() + derecha.size());
            int comparacionPrioridad;
            SugerenciaAmigo candidatoIzquierda;
            SugerenciaAmigo candidatoDerecha;
            String nombreIzquierda;
            String nombreDerecha;
            while (indiceIzquierda < izquierda.size() && indiceDerecha < derecha.size()) {
                candidatoIzquierda = izquierda.get(indiceIzquierda);
                candidatoDerecha = derecha.get(indiceDerecha);

                comparacionPrioridad = Integer.compare(candidatoDerecha.getPrioridad(),
                        candidatoIzquierda.getPrioridad());

                if (comparacionPrioridad == 0) {
                    nombreIzquierda = candidatoIzquierda.getPerfil().getNombre();
                    nombreDerecha = candidatoDerecha.getPerfil().getNombre();
                    comparacionPrioridad = nombreIzquierda.compareToIgnoreCase(nombreDerecha);
                }

                if (comparacionPrioridad < 0) {
                    listaFusionada.add(candidatoIzquierda);
                    indiceIzquierda++;
                } else if (comparacionPrioridad > 0) {
                    listaFusionada.add(candidatoDerecha);
                    indiceDerecha++;
                } else {
                    listaFusionada.add(candidatoIzquierda);
                    indiceIzquierda++;
                }
            }

            while (indiceIzquierda < izquierda.size()) {
                listaFusionada.add(izquierda.get(indiceIzquierda++));
            }
            while (indiceDerecha < derecha.size()) {
                listaFusionada.add(derecha.get(indiceDerecha++));
            }

            return listaFusionada;
        } catch (Exception e) {
            logger.error("Error al fusionar listas: {}", e.getMessage());
            throw e;
        }
    }
}
