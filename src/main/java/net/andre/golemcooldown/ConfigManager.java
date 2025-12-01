package net.andre.golemcooldown;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import java.io.File;

/**
 * Gestiona la configuración del mod (posición, escala, etc.)
 * Todos los kits comparten la misma configuración
 * 
 * IMPORTANTE: Este mod SOLO modifica su propio archivo de configuración.
 * NO modifica configuraciones de Minecraft, Feather Client u otros mods.
 */
public class ConfigManager {
    private static Configuration config;
    private static File configFile;
    private static boolean initialized = false;
    
    // Valores por defecto
    private static int defaultX = 10;
    private static int defaultY = -1; // -1 = calcular automáticamente
    private static float defaultScale = 1.0f;
    
    // Valores actuales
    public static int hudX = defaultX;
    public static int hudY = defaultY;
    public static float hudScale = defaultScale;
    
    /**
     * Inicializa el sistema de configuración
     * SOLO crea/modifica el archivo golemcooldown.cfg
     * NO toca ningún otro archivo de configuración
     */
    public static void init(File configDir) {
        try {
            // Asegurarse de que solo usamos nuestro propio archivo
            configFile = new File(configDir, "golemcooldown.cfg");
            
            // Crear el archivo si no existe (solo nuestro archivo)
            if (!configFile.exists()) {
                configFile.getParentFile().mkdirs();
                configFile.createNewFile();
            }
            
            // Inicializar Configuration SOLO para nuestro archivo
            config = new Configuration(configFile);
            
            // Cargar configuración
            loadConfig();
            
            initialized = true;
        } catch (Exception e) {
            // Si hay error, usar valores por defecto
            // NO intentar modificar nada más
            System.err.println("[GolemCooldown] Error al inicializar configuración: " + e.getMessage());
            e.printStackTrace();
            hudX = defaultX;
            hudY = defaultY;
            hudScale = defaultScale;
        }
    }
    
    /**
     * Carga la configuración desde el archivo
     * SOLO lee de golemcooldown.cfg
     */
    public static void loadConfig() {
        if (config == null || !initialized) return;
        
        try {
            config.load();
            
            // Solo leer nuestras propiedades específicas
            Property propX = config.get("hud", "positionX", defaultX, 
                "Posición X del contador (en píxeles). -1 para usar posición por defecto.");
            Property propY = config.get("hud", "positionY", defaultY, 
                "Posición Y del contador (en píxeles). -1 para usar posición por defecto.");
            Property propScale = config.get("hud", "scale", defaultScale, 
                "Escala del contador (1.0 = tamaño normal, 0.5 = mitad, 2.0 = doble).");
            
            hudX = propX.getInt();
            hudY = propY.getInt();
            hudScale = (float) propScale.getDouble();
            
            // Solo guardar si realmente cambió algo
            if (config.hasChanged()) {
                config.save();
            }
        } catch (Exception e) {
            // Si hay error, usar valores por defecto
            System.err.println("[GolemCooldown] Error al cargar configuración: " + e.getMessage());
            hudX = defaultX;
            hudY = defaultY;
            hudScale = defaultScale;
        }
    }
    
    /**
     * Guarda la configuración actual
     * SOLO guarda en golemcooldown.cfg
     */
    public static void saveConfig() {
        if (config == null || !initialized) return;
        
        try {
            // Solo modificar nuestras propiedades
            config.get("hud", "positionX", defaultX).set(hudX);
            config.get("hud", "positionY", defaultY).set(hudY);
            config.get("hud", "scale", defaultScale).set(hudScale);
            
            // Guardar SOLO nuestro archivo
            config.save();
        } catch (Exception e) {
            // Si hay error, no hacer nada
            // NO intentar modificar otros archivos
            System.err.println("[GolemCooldown] Error al guardar configuración: " + e.getMessage());
        }
    }
    
    /**
     * Establece la posición del HUD
     */
    public static void setPosition(int x, int y) {
        hudX = x;
        hudY = y;
        saveConfig();
    }
    
    /**
     * Establece la escala del HUD
     */
    public static void setScale(float scale) {
        hudScale = scale;
        saveConfig();
    }
    
    /**
     * Resetea la configuración a los valores por defecto
     */
    public static void reset() {
        hudX = defaultX;
        hudY = defaultY;
        hudScale = defaultScale;
        saveConfig();
    }
}

