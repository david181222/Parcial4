package estructurasparcial4;

import estructurasparcial4.Model.Perfil;
import estructurasparcial4.Model.SugerenciaAmigo;
import estructurasparcial4.Service.AlmacenamientoPerfiles;
import estructurasparcial4.Service.LeerPerfiles;
import estructurasparcial4.Service.MotorSugerencias;
import estructurasparcial4.Util.WeightedQuickUnionUF;

import java.util.List;
import java.util.Scanner;

public class App {
    private static AlmacenamientoPerfiles almacenamiento;
    private static MotorSugerencias motorSugerencias;
    private static WeightedQuickUnionUF redSocial;
    private static LeerPerfiles lectorPerfiles;
    private static Scanner scanner;


    public static void main(String[] args) {
        System.setProperty("org.graphstream.ui", "swing");
        almacenamiento = new AlmacenamientoPerfiles();
        motorSugerencias = new MotorSugerencias(almacenamiento);
        redSocial = new WeightedQuickUnionUF(1000);
        scanner = new Scanner(System.in);

        lectorPerfiles = new LeerPerfiles();
        lectorPerfiles.cargarPerfilesEnAlmacenamiento(almacenamiento);

        for (Perfil perfil : almacenamiento.obtenerTodosPerfiles().values()) {
            redSocial.agregarUsuario(perfil.getId());
        }

        motorSugerencias.cargarLazosDesdePerfiles(redSocial);

        System.out.println("Sistema inicializado con " + almacenamiento.obtenerTotalPerfiles() + " perfiles");

        mostrarMenu();
    }

   
    private static void mostrarMenu() {
        boolean continuar = true;

        while (continuar) {
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
                opcion = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
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
                    System.out.println("Saliendo del sistema...");
                    break;
                default:
                    System.out.println("Opción inválida");
            }
        }

        scanner.close();
    }

    private static void crearPerfil() {
        System.out.println("\n--- CREAR PERFIL ---");
        System.out.print("User ID: ");
        String userId = scanner.nextLine();

        System.out.print("Nombre Completo: ");
        String nombre = scanner.nextLine();

        System.out.print("Edad: ");
        short edad = (short) scanner.nextInt();
        scanner.nextLine();

        System.out.print("Género (M/F/Otro): ");
        String genero = scanner.nextLine();

        try {
            Perfil nuevoPerfil = new Perfil(userId, nombre, edad, genero);
            almacenamiento.crearPerfil(nuevoPerfil);
            redSocial.agregarUsuario(userId);
            lectorPerfiles.guardarPerfiles(almacenamiento);
            System.out.println("Perfil creado exitosamente");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void generarAmistad() {
        System.out.println("\n--- GENERAR LAZO DE AMISTAD ---");
        System.out.print("User ID A: ");
        String userIdA = scanner.nextLine();

        System.out.print("User ID B: ");
        String userIdB = scanner.nextLine();

        System.out.print("Calidad de amistad (1-5): ");
        int calidad = scanner.nextInt();
        scanner.nextLine();

        try {
            motorSugerencias.generarAmistad(userIdA, userIdB, calidad);
            redSocial.generarAmistad(userIdA, userIdB, calidad);
            lectorPerfiles.guardarPerfiles(almacenamiento);
            System.out.println("Lazo de amistad creado exitosamente");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }


    private static void visualizarRedSocial() {
        System.out.println("\n--- VISUALIZAR RED SOCIAL COMPLETA ---");
        System.out.println(redSocial.toString());
        redSocial.visualizarRedSocial(almacenamiento);
        
    }

    private static void sugerirAmigos() {
        System.out.println("\n--- SUGERIR AMIGOS ---");
        System.out.print("User ID: ");
        String userId = scanner.nextLine();

        try {
            List<SugerenciaAmigo> sugerencias = motorSugerencias.sugerirAmigos(userId);

            if (sugerencias.isEmpty()) {
                System.out.println("No hay sugerencias disponibles");
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
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    
    private static void listarPerfiles() {
    System.out.println("TODOS LOS PERFILES:");
        int contador = 1;
        for (Perfil perfil : almacenamiento.obtenerTodosPerfiles().values()) {
            System.out.println(contador + ". " + perfil.getNombre() + " [" + perfil.getId() + "]");
            System.out.println("   Edad: " + perfil.getEdad() + " | Género: " + perfil.getGenero());
            System.out.println("   Amigos: " + perfil.getAmigosDirectos().size());
            contador++;
        }
    }

    
}
