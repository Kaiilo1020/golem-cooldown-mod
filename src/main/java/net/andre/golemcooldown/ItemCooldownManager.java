package net.andre.golemcooldown;

import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import java.util.HashMap;
import java.util.Map;

/**
 * Gestiona cooldowns por item específico (no por kit)
 * Similar al mod de canelex - muestra items y sus cooldowns
 */
public class ItemCooldownManager {
    // Mapa: Item ID -> Tiempo restante en segundos
    private Map<Integer, Integer> cooldownsActivos = new HashMap<Integer, Integer>();
    
    // Mapa: Item ID -> Tiempo máximo de cooldown (para mostrar progreso)
    private Map<Integer, Integer> cooldownsMaximos = new HashMap<Integer, Integer>();
    
    // Timestamp de última actualización por item
    private Map<Integer, Long> ultimaActualizacion = new HashMap<Integer, Long>();
    
    // Cooldowns conocidos por ITEM ID (configurables)
    // Formato: Item ID -> segundos de cooldown
    // Ver lista de IDs en: https://minecraft-ids.grahamedgecombe.com/
    private static final Map<Integer, Integer> COOLDOWNS_POR_ITEM_ID = new HashMap<Integer, Integer>();
    
    // Cooldowns conocidos por nombre de habilidad (para detección desde chat)
    // Formato: Nombre de habilidad (lowercase) -> segundos de cooldown
    private static final Map<String, Integer> COOLDOWNS_POR_NOMBRE = new HashMap<String, Integer>();
    
    static {
        // ============================================
        // CONFIGURACIÓN DE COOLDOWNS POR ITEM ID
        // ============================================
        // Formato: COOLDOWNS_POR_ITEM_ID.put(ITEM_ID, segundos);
        // Ver lista completa: https://minecraft-ids.grahamedgecombe.com/
        
        // Ejemplos (ajustar según los items de tu servidor):
        // COOLDOWNS_POR_ITEM_ID.put(267, 30);  // Iron Sword (ID 267) - 30 segundos
        // COOLDOWNS_POR_ITEM_ID.put(268, 25);  // Wooden Sword (ID 268) - 25 segundos
        // COOLDOWNS_POR_ITEM_ID.put(272, 20);  // Stone Sword (ID 272) - 20 segundos
        // COOLDOWNS_POR_ITEM_ID.put(276, 35);  // Diamond Sword (ID 276) - 35 segundos
        
        // Agregar más items aquí según necesites:
        // COOLDOWNS_POR_ITEM_ID.put(ITEM_ID, segundos);
        
        // ============================================
        // CONFIGURACIÓN DE COOLDOWNS POR NOMBRE (chat)
        // ============================================
        // Para cuando se detecta desde el chat
        COOLDOWNS_POR_NOMBRE.put("pisotón", 30);
        COOLDOWNS_POR_NOMBRE.put("pisoton", 30);
    }
    
    /**
     * Obtiene el cooldown conocido para un item por su ID
     */
    public static int getCooldownPorItemId(int itemId) {
        return COOLDOWNS_POR_ITEM_ID.getOrDefault(itemId, 0);
    }
    
    /**
     * Obtiene el cooldown conocido para una habilidad por nombre
     */
    public static int getCooldownPorNombre(String nombreHabilidad) {
        if (nombreHabilidad == null) return 0;
        String nombreLower = nombreHabilidad.toLowerCase().trim();
        return COOLDOWNS_POR_NOMBRE.getOrDefault(nombreLower, 0);
    }
    
    /**
     * Agrega o actualiza un cooldown para un item ID
     */
    public static void setCooldownPorItemId(int itemId, int segundos) {
        COOLDOWNS_POR_ITEM_ID.put(itemId, segundos);
    }
    
    /**
     * Inicia un cooldown para un item específico
     */
    public void iniciarCooldown(ItemStack itemStack, int segundos) {
        if (itemStack == null || itemStack.getItem() == null) return;
        
        int itemId = Item.getIdFromItem(itemStack.getItem());
        cooldownsActivos.put(itemId, segundos);
        cooldownsMaximos.put(itemId, segundos);
        ultimaActualizacion.put(itemId, System.currentTimeMillis());
    }
    
    /**
     * Inicia cooldown usando el nombre de la habilidad (útil cuando se detecta desde chat)
     * Busca el item relacionado en el inventario o usa el item en la mano
     */
    public void iniciarCooldownPorNombre(String nombreHabilidad, int segundos) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null || mc.thePlayer.inventory == null) return;
        
        // Primero intentar encontrar el item en el inventario por nombre
        for (int i = 0; i < mc.thePlayer.inventory.getSizeInventory(); i++) {
            ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
            if (stack != null && stack.getItem() != null) {
                String nombreItem = stack.getDisplayName();
                String nombreLimpio = net.minecraft.util.StringUtils.stripControlCodes(nombreItem);
                
                // Buscar si el nombre del item contiene el nombre de la habilidad
                if (nombreLimpio.toLowerCase().contains(nombreHabilidad.toLowerCase())) {
                    iniciarCooldown(stack, segundos);
                    return;
                }
            }
        }
        
        // Si no se encuentra, usar el item en la mano (más común)
        ItemStack mano = mc.thePlayer.getHeldItem();
        if (mano != null && mano.getItem() != null) {
            iniciarCooldown(mano, segundos);
        } else {
            // Si no hay item en mano, crear un cooldown "virtual" usando el nombre
            // Esto se puede mejorar más adelante
            iniciarCooldownVirtual(nombreHabilidad, segundos);
        }
    }
    
    /**
     * Inicia un cooldown virtual (sin item físico) usando solo el nombre
     * Útil cuando no se puede encontrar el item pero sabemos el cooldown
     */
    private Map<String, Integer> cooldownsVirtuales = new HashMap<String, Integer>();
    private Map<String, Integer> cooldownsMaximosVirtuales = new HashMap<String, Integer>();
    private Map<String, Long> ultimaActualizacionVirtual = new HashMap<String, Long>();
    
    private void iniciarCooldownVirtual(String nombreHabilidad, int segundos) {
        String nombreLower = nombreHabilidad.toLowerCase();
        cooldownsVirtuales.put(nombreLower, segundos);
        cooldownsMaximosVirtuales.put(nombreLower, segundos);
        ultimaActualizacionVirtual.put(nombreLower, System.currentTimeMillis());
    }
    
    /**
     * Obtiene cooldowns virtuales (por nombre de habilidad)
     */
    public Map<String, Integer> getCooldownsVirtuales() {
        return new HashMap<String, Integer>(cooldownsVirtuales);
    }
    
    /**
     * Actualiza todos los cooldowns activos (tanto items como virtuales)
     */
    public void actualizar() {
        long ahora = System.currentTimeMillis();
        
        // Actualizar cooldowns de items físicos
        Map<Integer, Integer> cooldownsCopy = new HashMap<Integer, Integer>(cooldownsActivos);
        
        for (Map.Entry<Integer, Integer> entry : cooldownsCopy.entrySet()) {
            int itemId = entry.getKey();
            int cooldownActual = entry.getValue();
            
            if (cooldownActual > 0) {
                Long ultimaAct = ultimaActualizacion.get(itemId);
                if (ultimaAct != null) {
                    long tiempoTranscurrido = ahora - ultimaAct;
                    if (tiempoTranscurrido >= 1000) {
                        int segundosTranscurridos = (int)(tiempoTranscurrido / 1000);
                        int nuevoCooldown = Math.max(0, cooldownActual - segundosTranscurridos);
                        cooldownsActivos.put(itemId, nuevoCooldown);
                        ultimaActualizacion.put(itemId, ahora - (tiempoTranscurrido % 1000));
                        
                        // Si llegó a 0, limpiar
                        if (nuevoCooldown == 0) {
                            cooldownsActivos.remove(itemId);
                            cooldownsMaximos.remove(itemId);
                            ultimaActualizacion.remove(itemId);
                        }
                    }
                }
            }
        }
        
        // Actualizar cooldowns virtuales (por nombre de habilidad)
        Map<String, Integer> cooldownsVirtualesCopy = new HashMap<String, Integer>(cooldownsVirtuales);
        
        for (Map.Entry<String, Integer> entry : cooldownsVirtualesCopy.entrySet()) {
            String nombreHabilidad = entry.getKey();
            int cooldownActual = entry.getValue();
            
            if (cooldownActual > 0) {
                Long ultimaAct = ultimaActualizacionVirtual.get(nombreHabilidad);
                if (ultimaAct != null) {
                    long tiempoTranscurrido = ahora - ultimaAct;
                    if (tiempoTranscurrido >= 1000) {
                        int segundosTranscurridos = (int)(tiempoTranscurrido / 1000);
                        int nuevoCooldown = Math.max(0, cooldownActual - segundosTranscurridos);
                        cooldownsVirtuales.put(nombreHabilidad, nuevoCooldown);
                        ultimaActualizacionVirtual.put(nombreHabilidad, ahora - (tiempoTranscurrido % 1000));
                        
                        // Si llegó a 0, limpiar
                        if (nuevoCooldown == 0) {
                            cooldownsVirtuales.remove(nombreHabilidad);
                            cooldownsMaximosVirtuales.remove(nombreHabilidad);
                            ultimaActualizacionVirtual.remove(nombreHabilidad);
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Obtiene todos los items con cooldown activo
     */
    public Map<Integer, Integer> getCooldownsActivos() {
        return new HashMap<Integer, Integer>(cooldownsActivos);
    }
    
    /**
     * Obtiene el cooldown máximo de un item
     */
    public int getCooldownMaximo(int itemId) {
        return cooldownsMaximos.getOrDefault(itemId, 0);
    }
    
    /**
     * Obtiene el cooldown actual de un item
     */
    public int getCooldownActual(int itemId) {
        return cooldownsActivos.getOrDefault(itemId, 0);
    }
    
    /**
     * Resetea todos los cooldowns
     */
    public void resetear() {
        cooldownsActivos.clear();
        cooldownsMaximos.clear();
        ultimaActualizacion.clear();
    }
    
    /**
     * Resetea el cooldown de un item específico
     */
    public void resetearItem(int itemId) {
        cooldownsActivos.remove(itemId);
        cooldownsMaximos.remove(itemId);
        ultimaActualizacion.remove(itemId);
    }
}

