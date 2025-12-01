package net.andre.golemcooldown;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Detecta cuando se usa un item y extrae el cooldown del chat/action bar
 */
public class ItemCooldownEventHandler {
    private ItemCooldownManager itemManager;
    
    // Patrón principal: "¡Se ha activado la Habilidad Pisotón!"
    private static final Pattern HABILIDAD_ACTIVADA_PATTERN = Pattern.compile(
        "(?:¡|!)?\\s*(?:se\\s+ha\\s+activado|activado|activaste|has\\s+activado|usaste|usado)\\s+(?:la\\s+)?(?:habilidad|ability|skill)\\s+([A-Za-zÁÉÍÓÚáéíóúÑñ\\s]+?)(?:!|¡|\\s|$)",
        Pattern.CASE_INSENSITIVE
    );
    
    // Patrón alternativo: "Habilidad X activada"
    private static final Pattern HABILIDAD_ALTERNATIVA_PATTERN = Pattern.compile(
        "(?:habilidad|ability|skill)\\s+([A-Za-zÁÉÍÓÚáéíóúÑñ\\s]+?)\\s+(?:activada|activado|usada|usado)",
        Pattern.CASE_INSENSITIVE
    );
    
    // Patrones para detectar cooldowns en mensajes (por si el servidor los muestra)
    private static final Pattern COOLDOWN_PATTERN = Pattern.compile(
        "(?:cooldown|enfriamiento|enfriar|recarga).*?(\\d+).*?(?:segundo|s|second)",
        Pattern.CASE_INSENSITIVE
    );
    
    // Patrón para detectar tiempos (30s, 30 segundos, etc)
    private static final Pattern TIEMPO_PATTERN = Pattern.compile(
        "\\b(\\d+)\\s*(?:s|segundo|segundos|second|seconds)\\b",
        Pattern.CASE_INSENSITIVE
    );
    
    public ItemCooldownEventHandler(ItemCooldownManager manager) {
        this.itemManager = manager;
    }
    
    /**
     * Detecta cuando el jugador hace click derecho con un item
     * Este es el método principal para detectar el uso de habilidades
     */
    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.entityPlayer != Minecraft.getMinecraft().thePlayer) return;
        if (event.action != PlayerInteractEvent.Action.RIGHT_CLICK_AIR && 
            event.action != PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) return;
        
        ItemStack itemStack = event.entityPlayer.getHeldItem();
        if (itemStack == null || itemStack.getItem() == null) return;
        
        // Obtener el ID numérico del item
        int itemId = net.minecraft.item.Item.getIdFromItem(itemStack.getItem());
        
        // Buscar si este item tiene un cooldown configurado
        int cooldown = ItemCooldownManager.getCooldownPorItemId(itemId);
        
        if (cooldown > 0) {
            // Iniciar el cooldown automáticamente cuando se hace click derecho
            itemManager.iniciarCooldown(itemStack, cooldown);
        }
    }
    
    /**
     * Detecta mensajes del chat que contengan información de habilidad activada
     * Ejemplo: "¡Se ha activado la Habilidad Pisotón!"
     */
    @SubscribeEvent
    public void onChatReceived(ClientChatReceivedEvent event) {
        if (event.message == null) return;
        
        String texto = event.message.getUnformattedText();
        String textoLimpio = texto.trim();
        
        // PRIORIDAD 1: Detectar "¡Se ha activado la Habilidad Pisotón!"
        Matcher habilidadActivadaMatcher = HABILIDAD_ACTIVADA_PATTERN.matcher(textoLimpio);
        if (habilidadActivadaMatcher.find()) {
            String nombreHabilidad = habilidadActivadaMatcher.group(1).trim();
            
            // Buscar el cooldown predefinido para esta habilidad (por nombre)
            int cooldown = ItemCooldownManager.getCooldownPorNombre(nombreHabilidad);
            
            if (cooldown > 0) {
                // Iniciar cooldown usando el tiempo predefinido
                itemManager.iniciarCooldownPorNombre(nombreHabilidad, cooldown);
                return;
            } else {
                // Si no hay cooldown predefinido, intentar usar el item en la mano
                Minecraft mc = Minecraft.getMinecraft();
                if (mc.thePlayer != null) {
                    ItemStack itemEnMano = mc.thePlayer.getHeldItem();
                    if (itemEnMano != null && itemEnMano.getItem() != null) {
                        // Usar cooldown por defecto (30s) si no está configurado
                        itemManager.iniciarCooldown(itemEnMano, 30);
                    }
                }
            }
        }
        
        // PRIORIDAD 2: Detectar patrón alternativo "Habilidad X activada"
        Matcher habilidadAlternativaMatcher = HABILIDAD_ALTERNATIVA_PATTERN.matcher(textoLimpio);
        if (habilidadAlternativaMatcher.find()) {
            String nombreHabilidad = habilidadAlternativaMatcher.group(1).trim();
            int cooldown = ItemCooldownManager.getCooldownPorNombre(nombreHabilidad);
            
            if (cooldown > 0) {
                itemManager.iniciarCooldownPorNombre(nombreHabilidad, cooldown);
                return;
            }
        }
        
        // PRIORIDAD 3: Si el mensaje contiene un tiempo de cooldown explícito
        Matcher cooldownMatcher = COOLDOWN_PATTERN.matcher(textoLimpio);
        if (cooldownMatcher.find()) {
            try {
                int segundos = Integer.parseInt(cooldownMatcher.group(1));
                
                // Intentar obtener el item de la mano del jugador
                Minecraft mc = Minecraft.getMinecraft();
                if (mc.thePlayer != null) {
                    ItemStack itemEnMano = mc.thePlayer.getHeldItem();
                    if (itemEnMano != null && itemEnMano.getItem() != null) {
                        itemManager.iniciarCooldown(itemEnMano, segundos);
                        return;
                    }
                }
            } catch (NumberFormatException e) {
                // Ignorar
            }
        }
    }
    
    /**
     * Actualiza los cooldowns cada tick
     */
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (itemManager != null) {
            itemManager.actualizar();
        }
    }
}

