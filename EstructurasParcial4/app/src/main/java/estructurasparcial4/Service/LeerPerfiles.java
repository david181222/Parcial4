package estructurasparcial4.Service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.google.gson.reflect.TypeToken;
import com.google.gson.GsonBuilder;

import estructurasparcial4.Model.Perfil;

import java.lang.reflect.Type;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

public class LeerPerfiles {
    // Clase encargada de leer y escribir el archivo JSON de perfiles.
    // Se usa para cargar perfiles al inicio y guardarlos cuando cambian.
    private final Gson gson;
    private static final String RUTA_ARCHIVO = "C:\\Users\\david\\Documents\\E.Datos\\EstructurasParcial4\\app\\src\\main\\resources\\Perfiles.json";
    private List<Perfil> perfilesCargados;
    private boolean archivoLeido;
    private static final Logger logger = LogManager.getLogger(LeerPerfiles.class);

    // Inicializa el lector con Gson y lista vacía.
    public LeerPerfiles() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.perfilesCargados = new ArrayList<>();
        this.archivoLeido = false;
    }

    // Lee el archivo JSON una sola vez y guarda los perfiles en memoria.
    public void leerArchivo() {
        if (archivoLeido) return;

        try (FileReader fileReader = new FileReader(RUTA_ARCHIVO)) {
            logger.info("Leyendo el archivo de perfiles: {}", RUTA_ARCHIVO);

            Type listType = new TypeToken<List<Perfil>>() {}.getType();
            List<Perfil> perfilesLeidos = gson.fromJson(fileReader, listType);
            
            if (perfilesLeidos != null) {
                perfilesCargados = perfilesLeidos;
            } else {
                perfilesCargados = new ArrayList<>();
                logger.warn("Archivo JSON vacío o inválido, inicializando lista vacía");
            }
            
            archivoLeido = true;
            logger.info("Archivo de perfiles leído exitosamente. Perfiles encontrados: {}", perfilesCargados.size());
        } catch (IOException e) {
            logger.error("Error al leer el archivo: {}", e.getMessage());
            perfilesCargados = new ArrayList<>();
            archivoLeido = true;
            logger.warn("Inicializando con lista vacía debido al error");
        }
    }

    // Devuelve la lista leída del archivo (o vacía si no hay datos).
    public List<Perfil> obtenerPerfilesLeidos() {
        return perfilesCargados;
    }

    // Carga los perfiles leídos en el Almacenamiento (ignora duplicados).
    public void cargarPerfilesEnAlmacenamiento(AlmacenamientoPerfiles almacenamiento) {
        if (!archivoLeido) {
            leerArchivo();
        }

        if (perfilesCargados == null || perfilesCargados.isEmpty()) {
            logger.info("No hay perfiles para cargar desde el archivo");
            return;
        }

        for (Perfil perfil : perfilesCargados) {
            try {
                almacenamiento.crearPerfil(perfil);
            } catch (IllegalArgumentException e) {
                logger.warn("No se pudo cargar perfil {}: {}", perfil.getId(), e.getMessage());
            }
        }

        logger.info("Perfiles cargados en almacenamiento: {}", perfilesCargados.size());
    }

    // Guarda todos los perfiles del almacenamiento en el archivo JSON.
    public void guardarPerfiles(AlmacenamientoPerfiles almacenamiento) {
        try (FileWriter fileWriter = new FileWriter(RUTA_ARCHIVO)) {
            List<Perfil> todosPerfiles = new ArrayList<>(almacenamiento.obtenerTodosPerfiles().values());
            gson.toJson(todosPerfiles, fileWriter);
            logger.info("Perfiles guardados exitosamente en: {}", RUTA_ARCHIVO);
        } catch (IOException e) {
            logger.error("Error al guardar perfiles: {}", e.getMessage());
        }
    }
}


