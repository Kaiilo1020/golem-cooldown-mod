package net.andre.golemcooldown;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ModEventHandler {
    // Patrones para detectar cuando se usa un kit o habilidad
    private static final Pattern KIT_USADO_PATTERN = Pattern.compile(
        "(?:has usado|usaste|usado|activado|activaste).*?kit.*?(\\w+)", 
        Pattern.CASE_INSENSITIVE
    );
    private static final Pattern COOLDOWN_INICIADO_PATTERN = Pattern.compile(
        "(?:cooldown|enfriamiento|enfriar).*?(\\d+).*?segundo", 
        Pattern.CASE_INSENSITIVE
    );
    // Detectar mensajes como "Pisotón" u otros que indiquen uso de habilidad
    private static final Pattern HABILIDAD_USADA_PATTERN = Pattern.compile(
        "(?:pisotón|pisotón|habilidad|ability|skill)", 
        Pattern.CASE_INSENSITIVE
    );
    // Detectar números seguidos de "s" que puedan ser cooldowns
    private static final Pattern TIEMPO_PATTERN = Pattern.compile(
        "\\b(\\d+)\\s*s\\b", 
        Pattern.CASE_INSENSITIVE
    );

    @SubscribeEvent
    public void onChatReceived(ClientChatReceivedEvent event) {
        if (event.message == null) return;
        
        String texto = event.message.getUnformattedText();
        
        // Detectar si se menciona un cooldown en el chat
        Matcher cooldownMatcher = COOLDOWN_INICIADO_PATTERN.matcher(texto);
        if (cooldownMatcher.find()) {
            try {
                int segundos = Integer.parseInt(cooldownMatcher.group(1));
                // Si hay un kit actual, iniciar el cooldown
                if (GolemCooldownMod.cooldownManager != null) {
                    String kit = GolemCooldownMod.cooldownManager.getKitActual();
                    if (kit != null && !kit.isEmpty()) {
                        GolemCooldownMod.cooldownManager.iniciarCooldown(kit, segundos);
                    }
                }
            } catch (NumberFormatException e) {
                // Ignorar
            }
        }
        
        // Detectar uso de habilidad (como "Pisotón")
        if (HABILIDAD_USADA_PATTERN.matcher(texto).find()) {
            // Buscar tiempo en el mismo mensaje
            Matcher tiempoMatcher = TIEMPO_PATTERN.matcher(texto);
            if (tiempoMatcher.find()) {
                try {
                    int segundos = Integer.parseInt(tiempoMatcher.group(1));
                    if (GolemCooldownMod.cooldownManager != null) {
                        String kit = GolemCooldownMod.cooldownManager.getKitActual();
                        if (kit != null && !kit.isEmpty()) {
                            GolemCooldownMod.cooldownManager.iniciarCooldown(kit, segundos);
                        }
                    }
                } catch (NumberFormatException e) {
                    // Ignorar
                }
            } else {
                // Si no hay tiempo en el chat, usar cooldown por defecto según el kit
                if (GolemCooldownMod.cooldownManager != null) {
                    String kit = GolemCooldownMod.cooldownManager.getKitActual();
                    if (kit != null && !kit.isEmpty()) {
                        // Cooldowns por defecto según el kit (ajustar según necesidad)
                        int cooldownDefault = obtenerCooldownDefault(kit);
                        if (cooldownDefault > 0) {
                            GolemCooldownMod.cooldownManager.iniciarCooldown(kit, cooldownDefault);
                        }
                    }
                }
            }
        }
        
        // Detectar mensajes de fin de partida
        if (texto.toLowerCase().contains("victoria") || 
            texto.toLowerCase().contains("derrota") ||
            texto.toLowerCase().contains("game over") ||
            texto.toLowerCase().contains("partida terminada")) {
            if (GolemCooldownMod.cooldownManager != null) {
                GolemCooldownMod.cooldownManager.resetear();
            }
        }
    }
    
    private int obtenerCooldownDefault(String kit) {
        // Cooldowns por defecto según el kit (en segundos)
        // Ajustar estos valores según los cooldowns reales del servidor
        if (kit.equalsIgnoreCase("Golem")) {
            return 30; // 30 segundos para Golem
        }
        // Agregar más kits aquí si es necesario
        return 0; // Si no se conoce el kit, retornar 0
    }

    @SubscribeEvent
    public void onPlayerDeath(LivingDeathEvent event) {
        if (event.entity == Minecraft.getMinecraft().thePlayer) {
            // No resetear, mantener el estado del cooldown
        }
    }

    @SubscribeEvent
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        // El cooldown continúa desde donde se quedó
    }
}

