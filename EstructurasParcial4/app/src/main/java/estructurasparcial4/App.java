package estructurasparcial4;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import estructurasparcial4.Model.Perfil;
import estructurasparcial4.Model.SugerenciaAmigo;
import estructurasparcial4.Service.AlmacenamientoPerfiles;
import estructurasparcial4.Service.LeerPerfiles;
import estructurasparcial4.Service.MotorSugerencias;
import estructurasparcial4.Util.WeightedQuickUnionUF;

import java.util.List;
import java.util.Scanner;

// En esta clase se halla el método main que orquesta la aplicación por medio de un menú
public class App {
    private static AlmacenamientoPerfiles almacenamiento;
    private static MotorSugerencias motorSugerencias;
    private static WeightedQuickUnionUF redSocial;
    private static LeerPerfiles lectorPerfiles;
    private static Scanner sc;
    private static final Logger logger = LogManager.getLogger(App.class);


    public static void main(String[] args) {
        logger.info("Iniciando aplicación de Red Social");
        try {
            System.setProperty("org.graphstream.ui", "swing"); // Esta línea se emplea para garantizar que GraphStream use Swing,
                                                                  // lo cual es necesario para la visualización segura. Pues si no se establece, el hace una detección automática.
                                                                  // Por tanto, esto evita problemas en ese apartado
            almacenamiento = new AlmacenamientoPerfiles();
            motorSugerencias = new MotorSugerencias(almacenamiento);
            redSocial = new WeightedQuickUnionUF(1000);
            sc = new Scanner(System.in);

            lectorPerfiles = new LeerPerfiles();
            lectorPerfiles.cargarPerfilesEnAlmacenamiento(almacenamiento);

            for (Perfil perfil : almacenamiento.obtenerTodosPerfiles().values()) {
                redSocial.agregarUsuario(perfil.getId());
            }

            motorSugerencias.cargarLazosDesdePerfiles(redSocial);

            System.out.println("Sistema inicializado con " + almacenamiento.obtenerTotalPerfiles() + " perfiles");
            logger.info("Sistema inicializado con {} perfiles", almacenamiento.obtenerTotalPerfiles());

            mostrarMenu();
        } catch (Exception e) {
            logger.error("Error crítico al iniciar la aplicación: {}", e.getMessage());
            System.err.println("Error al iniciar la aplicación: " + e.getMessage());
        }
    }

   
    private static void mostrarMenu() {
        logger.info("Mostrando menú principal");
        boolean continuar = true;

        while (continuar) {
            try {
                System.out.println();
                System.out.println("RED SOCIAL - MENU PRINCIPAL");
                System.out.println("1. Crear perfil");
                System.out.println("2. Generar lazo de amistad");
                System.out.println("3. Visualizar red social");
                System.out.println("4. Sugerir amigos");
                System.out.println("5. Listar todos los perfiles");
                System.out.println("0. Salir");
                System.out.print("Seleccione una opción: ");

                int opcion = -1;
                try {
                    opcion = Integer.parseInt(sc.nextLine().trim());
                    logger.info("Opción seleccionada: {}", opcion);
                } catch (NumberFormatException e) {
                    logger.error("Opción inválida ingresada: {}", e.getMessage());
                    System.out.println("Opción inválida");
                    continue;
                }

                switch (opcion) {
                    case 1:
                        crearPerfil();
                        break;
                    case 2:
                        generarAmistad();
                        break;
                    case 3:
                        visualizarRedSocial();
                        break;
                    case 4:
                        sugerirAmigos();
                        break;
                    case 5:
                        listarPerfiles();
                        break;
                    case 0:
                        continuar = false;
                        logger.info("Usuario saliendo del sistema");
                        System.out.println("Saliendo del sistema...");
                        break;
                    default:
                        logger.warn("Opción inválida seleccionada: {}", opcion);
                        System.out.println("Opción inválida");
                }
            } catch (Exception e) {
                logger.error("Error en el menú principal: {}", e.getMessage());
                System.out.println("Error: " + e.getMessage());
            }
        }

        try {
            sc.close();
            logger.info("Scanner cerrado correctamente");
        } catch (Exception e) {
            logger.error("Error al cerrar scanner: {}", e.getMessage());
        }
    }

    private static void crearPerfil() {
        logger.info("Iniciando creación de perfil");
        System.out.println("\n--- CREAR PERFIL ---");
        try {
            System.out.print("User ID: ");
            String userId = sc.nextLine();

            if (userId == null || userId.trim().isEmpty()) {
                logger.error("User ID vacío ingresado");
                System.out.println("Error: User ID no puede estar vacío");
                return;
            }

            System.out.print("Nombre Completo: ");
            String nombre = sc.nextLine();

            if (nombre == null || nombre.trim().isEmpty()) {
                logger.error("Nombre vacío ingresado");
                System.out.println("Error: Nombre no puede estar vacío");
                return;
            }

            System.out.print("Edad: ");
            short edad;
            try {
                edad = (short) sc.nextInt();
                sc.nextLine();
                
                if (edad <= 0 || edad > 120) {
                    logger.error("Edad inválida ingresada: {}", edad);
                    System.out.println("Error: Edad debe estar entre 1 y 120");
                    return;
                }
            } catch (Exception e) {
                logger.error("Error al leer edad: {}", e.getMessage());
                System.out.println("Error: Edad inválida");
                sc.nextLine();
                return;
            }

            System.out.print("Género (M/F/Otro): ");
            String genero = sc.nextLine();

            if (genero == null || genero.trim().isEmpty()) {
                logger.error("Género vacío ingresado");
                System.out.println("Error: Género no puede estar vacío");
                return;
            }

            try {
                Perfil nuevoPerfil = new Perfil(userId, nombre, edad, genero);
                almacenamiento.crearPerfil(nuevoPerfil);
                redSocial.agregarUsuario(userId);
                lectorPerfiles.guardarPerfiles(almacenamiento);
                System.out.println("Perfil creado exitosamente");
                logger.info("Perfil creado exitosamente: {}", userId);
            } catch (IllegalArgumentException e) {
                logger.error("Error al crear perfil: {}", e.getMessage());
                System.out.println("Error: " + e.getMessage());
            }
        } catch (Exception e) {
            logger.error("Error inesperado en creación de perfil: {}", e.getMessage());
            System.out.println("Error inesperado: " + e.getMessage());
        }
    }

    private static void generarAmistad() {
        logger.info("Iniciando generación de amistad");
        System.out.println("\n--- GENERAR LAZO DE AMISTAD ---");
        try {
            System.out.print("User ID A: ");
            String userIdA = sc.nextLine();

            if (userIdA == null || userIdA.trim().isEmpty()) {
                logger.error("User ID A vacío ingresado");
                System.out.println("Error: User ID A no puede estar vacío");
                return;
            }

            System.out.print("User ID B: ");
            String userIdB = sc.nextLine();

            if (userIdB == null || userIdB.trim().isEmpty()) {
                logger.error("User ID B vacío ingresado");
                System.out.println("Error: User ID B no puede estar vacío");
                return;
            }

            System.out.print("Calidad de amistad (1-5): ");
            int calidad;
            try {
                calidad = sc.nextInt();
                sc.nextLine();
                
                if (calidad < 1 || calidad > 5) {
                    logger.error("Calidad inválida ingresada: {}", calidad);
                    System.out.println("Error: Calidad debe estar entre 1 y 5");
                    return;
                }
            } catch (Exception e) {
                logger.error("Error al leer calidad: {}", e.getMessage());
                System.out.println("Error: Calidad inválida");
                sc.nextLine();
                return;
            }

            try {
                motorSugerencias.generarAmistad(userIdA, userIdB, calidad);
                redSocial.generarAmistad(userIdA, userIdB, calidad);
                lectorPerfiles.guardarPerfiles(almacenamiento);
                System.out.println("Lazo de amistad creado exitosamente");
                logger.info("Lazo de amistad creado exitosamente entre {} y {}", userIdA, userIdB);
            } catch (IllegalArgumentException e) {
                logger.error("Error al generar amistad: {}", e.getMessage());
                System.out.println("Error: " + e.getMessage());
            }
        } catch (Exception e) {
            logger.error("Error inesperado en generación de amistad: {}", e.getMessage());
            System.out.println("Error inesperado: " + e.getMessage());
        }
    }


    private static void visualizarRedSocial() {
        logger.info("Visualizando red social completa");
        System.out.println("\n--- VISUALIZAR RED SOCIAL COMPLETA ---");
        try {
            System.out.println(redSocial.toString());
            redSocial.visualizarRedSocial(almacenamiento);
            logger.info("Red social visualizada exitosamente");
        } catch (Exception e) {
            logger.error("Error al visualizar red social: {}", e.getMessage());
            System.out.println("Error al visualizar red social: " + e.getMessage());
        }
    }

    private static void sugerirAmigos() {
        logger.info("Iniciando sugerencia de amigos");
        System.out.println("\n--- SUGERIR AMIGOS ---");
        try {
            System.out.print("User ID: ");
            String userId = sc.nextLine();

            if (userId == null || userId.trim().isEmpty()) {
                logger.error("User ID vacío ingresado en sugerencias");
                System.out.println("Error: User ID no puede estar vacío");
                return;
            }

            try {
                List<SugerenciaAmigo> sugerencias = motorSugerencias.sugerirAmigos(userId);

                if (sugerencias.isEmpty()) {
                    System.out.println("No hay sugerencias disponibles");
                    logger.info("No hay sugerencias disponibles para usuario: {}", userId);
                } else {
                    System.out.println("SUGERENCIAS (ordenadas por prioridad):");
                    int contador = 1;
                    Perfil perfil;
                    for (SugerenciaAmigo sugerencia : sugerencias) {
                        perfil = sugerencia.getPerfil();
                        System.out.println(contador + ". " + perfil.getNombre() + 
                                         " [" + sugerencia.getUserIdSugerido() + "]");
                        System.out.println("  Prioridad: " + sugerencia.getPrioridad());
                        contador++;
                    }
                    logger.info("Se mostraron {} sugerencias para usuario: {}", sugerencias.size(), userId);
                }
            } catch (IllegalArgumentException e) {
                logger.error("Error al sugerir amigos: {}", e.getMessage());
                System.out.println("Error: " + e.getMessage());
            }
        } catch (Exception e) {
            logger.error("Error inesperado en sugerencia de amigos: {}", e.getMessage());
            System.out.println("Error inesperado: " + e.getMessage());
        }
    }

    
    private static void listarPerfiles() {
        logger.info("Listando todos los perfiles");
        System.out.println("TODOS LOS PERFILES:");
        try {
            int contador = 1;
            for (Perfil perfil : almacenamiento.obtenerTodosPerfiles().values()) {
                System.out.println(contador + ". " + perfil.getNombre() + " [" + perfil.getId() + "]");
                System.out.println("   Edad: " + perfil.getEdad() + " | Género: " + perfil.getGenero());
                System.out.println("   Amigos: " + perfil.getAmigosDirectos().size());
                contador++;
            }
            logger.info("Se listaron {} perfiles", contador - 1);
        } catch (Exception e) {
            logger.error("Error al listar perfiles: {}", e.getMessage());
            System.out.println("Error al listar perfiles: " + e.getMessage());
        }
    }
}