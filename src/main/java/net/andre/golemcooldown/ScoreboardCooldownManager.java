package net.andre.golemcooldown;

import net.minecraft.client.Minecraft;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Gestiona cooldowns detectados desde el scoreboard
 * Detecta: "Kit: [nombre] (Xs)" y extrae el kit y el tiempo
 */
public class ScoreboardCooldownManager {
    private String kitActual = "";
    private int cooldownActual = 0;
    private int cooldownMaximo = 0;
    private long ultimaLectura = 0;
    private long ultimaActualizacion = 0;
    
    // Patrón para detectar "Kit: Golem (30s)" o "Kit: Golem 30s"
    private static final Pattern KIT_COOLDOWN_PATTERN = Pattern.compile(
        "Kit:\\s*([A-Za-zÁÉÍÓÚáéíóúÑñ\\s]+?)\\s*\\(?(\\d+)s?\\)?", 
        Pattern.CASE_INSENSITIVE
    );
    
    // Patrón para detectar solo "Kit: Golem" (sin tiempo)
    private static final Pattern KIT_SIMPLE_PATTERN = Pattern.compile(
        "Kit:\\s*([A-Za-zÁÉÍÓÚáéíóúÑñ\\s]+?)(?:\\s|$)", 
        Pattern.CASE_INSENSITIVE
    );
    
    /**
     * Se actualiza automáticamente cada tick del cliente
     */
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        actualizar();
    }
    
    /**
     * Actualiza el cooldown leyendo el scoreboard
     */
    public void actualizar() {
        long ahora = System.currentTimeMillis();
        
        // Decrementar cooldown cada segundo si está activo
        if (cooldownActual > 0) {
            // Si ultimaActualizacion es 0 (primera vez), inicializarlo
            if (ultimaActualizacion == 0) {
                ultimaActualizacion = ahora;
            }
            
            long tiempoTranscurrido = ahora - ultimaActualizacion;
            if (tiempoTranscurrido >= 1000) {
                int segundosTranscurridos = (int)(tiempoTranscurrido / 1000);
                cooldownActual = Math.max(0, cooldownActual - segundosTranscurridos);
                ultimaActualizacion = ahora - (tiempoTranscurrido % 1000);
                
                // Si llegó a 0, limpiar
                if (cooldownActual == 0) {
                    kitActual = "";
                    cooldownMaximo = 0;
                    ultimaActualizacion = 0; // Resetear para la próxima vez
                }
            }
        }
        
        // Leer scoreboard cada 500ms
        if (ahora - ultimaLectura < 500 && cooldownActual > 0) {
            return;
        }
        ultimaLectura = ahora;
        
        // Leer scoreboard
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld == null || mc.theWorld.getScoreboard() == null) {
            return;
        }
        
        Scoreboard scoreboard = mc.theWorld.getScoreboard();
        ScoreObjective objective = scoreboard.getObjectiveInDisplaySlot(1); // Sidebar
        
        if (objective == null) {
            return;
        }
        
        // Buscar la línea que contiene "Kit:"
        String kitEncontrado = null;
        int cooldownEncontrado = -1;
        
        for (Score score : scoreboard.getSortedScores(objective)) {
            ScorePlayerTeam team = scoreboard.getPlayersTeam(score.getPlayerName());
            String line = ScorePlayerTeam.formatPlayerName(team, score.getPlayerName());
            String texto = net.minecraft.util.StringUtils.stripControlCodes(line);
            
            // Intentar patrón completo primero (con tiempo)
            Matcher matcher = KIT_COOLDOWN_PATTERN.matcher(texto);
            if (matcher.find()) {
                kitEncontrado = matcher.group(1).trim();
                try {
                    cooldownEncontrado = Integer.parseInt(matcher.group(2));
                } catch (NumberFormatException e) {
                    cooldownEncontrado = -1;
                }
                break;
            }
            
            // Si no tiene tiempo, buscar solo el nombre del kit
            Matcher matcherSimple = KIT_SIMPLE_PATTERN.matcher(texto);
            if (matcherSimple.find()) {
                kitEncontrado = matcherSimple.group(1).trim();
                break;
            }
        }
        
        if (kitEncontrado != null) {
            // DETECCIÓN AUTOMÁTICA INTELIGENTE:
            // Si el scoreboard muestra un cooldown (ej: "Kit: Golem (30s)") → tiene cooldown
            // Si solo muestra el nombre (ej: "Kit: Golem") → verificar si está registrado manualmente
            // Si está registrado manualmente con hasCooldown=true → mostrar contador cuando aparezca cooldown
            // Si NO está registrado → no mostrar nada hasta que cambie de kit
            
            boolean tieneCooldown = false;
            
            if (cooldownEncontrado > 0) {
                // El scoreboard muestra un cooldown activo → automáticamente tiene cooldown
                // Esto significa que el jugador usó la habilidad y el servidor muestra el cooldown
                tieneCooldown = true;
            } else {
                // No hay cooldown visible en el scoreboard en este momento
                // Verificar si está registrado manualmente en KitMapper
                // Si está registrado con hasCooldown=true, esperará a que aparezca el cooldown
                // Si NO está registrado, no mostrará nada (kit sin cooldown)
                tieneCooldown = KitMapper.hasCooldown(kitEncontrado);
            }
            
            // Si el kit no tiene cooldown (ni detectado automáticamente ni registrado manualmente)
            // No mostrar nada hasta que cambie de kit
            if (!tieneCooldown) {
                // Solo resetear si cambió de kit, no si es el mismo kit sin cooldown
                if (kitActual == null || !kitEncontrado.equals(kitActual)) {
                    kitActual = "";
                    cooldownActual = 0;
                    cooldownMaximo = 0;
                }
                return;
            }
            
            // Verificar si cambió el kit
            boolean kitCambio = (kitActual == null || !kitEncontrado.equals(kitActual));
            
            if (kitCambio) {
                // CAMBIO DE KIT: Resetear cooldown anterior y preparar para el nuevo kit
                kitActual = kitEncontrado;
                cooldownActual = 0;
                cooldownMaximo = 0;
                
                // Si el nuevo kit tiene cooldown activo en el scoreboard, iniciarlo
                if (cooldownEncontrado > 0) {
                    cooldownActual = cooldownEncontrado;
                    cooldownMaximo = cooldownEncontrado;
                    ultimaActualizacion = ahora;
                }
            } else {
                // MISMO KIT: Actualizar cooldown si hay uno nuevo en el scoreboard
                if (cooldownEncontrado > 0) {
                    // Cooldown nuevo detectado en el scoreboard
                    cooldownActual = cooldownEncontrado;
                    cooldownMaximo = cooldownEncontrado;
                    ultimaActualizacion = ahora;
                }
                // Si no hay cooldown en el scoreboard, el cooldown actual sigue decrementando
                // (ya está siendo manejado arriba en el decremento automático)
            }
        } else {
            // No se encontró kit en el scoreboard
            // Si el cooldown llegó a 0, ya se limpió arriba
        }
    }
    
    /**
     * Obtiene el kit actual detectado
     */
    public String getKitActual() {
        return kitActual;
    }
    
    /**
     * Obtiene el cooldown actual en segundos
     */
    public int getCooldownActual() {
        return cooldownActual;
    }
    
    /**
     * Obtiene el cooldown máximo
     */
    public int getCooldownMaximo() {
        return cooldownMaximo;
    }
    
    /**
     * Verifica si hay un cooldown activo
     */
    public boolean tieneCooldownActivo() {
        return cooldownActual > 0 && kitActual != null && !kitActual.isEmpty();
    }
    
    /**
     * Resetea todos los cooldowns
     */
    public void resetear() {
        kitActual = "";
        cooldownActual = 0;
        cooldownMaximo = 0;
        ultimaActualizacion = 0;
        ultimaLectura = 0;
    }
}

