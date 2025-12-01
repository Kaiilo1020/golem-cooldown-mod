package net.andre.golemcooldown;

import java.util.HashMap;
import java.util.Map;

/**
 * Mapea nombres de kits a Item IDs de Minecraft
 * Basado en: https://minecraft-ids.grahamedgecombe.com/
 */
public class KitMapper {
    // Mapa: Nombre del kit (lowercase) -> Item ID
    private static final Map<String, Integer> KIT_TO_ITEM_ID = new HashMap<String, Integer>();
    
    // Mapa: Nombre del kit (lowercase) -> Tiene cooldown (true/false)
    private static final Map<String, Boolean> KIT_HAS_COOLDOWN = new HashMap<String, Boolean>();
    
    static {
        // ============================================
        // CONFIGURACIÓN DE KITS
        // ============================================
        // Formato: KIT_TO_ITEM_ID.put("nombre_kit", ITEM_ID);
        //          KIT_HAS_COOLDOWN.put("nombre_kit", true/false);
        
        // Kits con cooldown (ajustar según tu servidor)
        registerKit("golem", 265, true);      // Golem - Item ID 265 - Tiene cooldown
        registerKit("pisotón", 265, true);    // Pisotón (con acento) - Mismo item
        registerKit("pisoton", 265, true);     // Pisotón (sin acento)
        
        // Agregar más kits aquí:
        // registerKit("nombre_kit", ITEM_ID, tiene_cooldown);
        // Ejemplos:
        // registerKit("archer", 261, true);    // Bow - Tiene cooldown
        // registerKit("warrior", 276, false);  // Diamond Sword - No tiene cooldown
    }
    
    /**
     * Registra un kit con su Item ID y si tiene cooldown
     */
    private static void registerKit(String nombre, int itemId, boolean tieneCooldown) {
        KIT_TO_ITEM_ID.put(nombre.toLowerCase(), itemId);
        KIT_HAS_COOLDOWN.put(nombre.toLowerCase(), tieneCooldown);
    }
    
    /**
     * Obtiene el Item ID de un kit por su nombre
     * @param nombreKit Nombre del kit (ej: "Golem", "golem", "GOLEM")
     * @return Item ID o -1 si no se encuentra
     */
    public static int getItemId(String nombreKit) {
        if (nombreKit == null) return -1;
        return KIT_TO_ITEM_ID.getOrDefault(nombreKit.toLowerCase().trim(), -1);
    }
    
    /**
     * Verifica si un kit tiene cooldown
     * @param nombreKit Nombre del kit
     * @return true si tiene cooldown, false si no
     */
    public static boolean hasCooldown(String nombreKit) {
        if (nombreKit == null) return false;
        return KIT_HAS_COOLDOWN.getOrDefault(nombreKit.toLowerCase().trim(), false);
    }
    
    /**
     * Verifica si un kit está registrado
     * @param nombreKit Nombre del kit
     * @return true si está registrado
     */
    public static boolean isKitRegistered(String nombreKit) {
        if (nombreKit == null) return false;
        return KIT_TO_ITEM_ID.containsKey(nombreKit.toLowerCase().trim());
    }
}

