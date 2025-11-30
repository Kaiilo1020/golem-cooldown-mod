package net.andre.golemcooldown;

import net.minecraft.client.Minecraft;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CooldownManager {
    private Map<String, Integer> cooldownsGuardados = new HashMap<String, Integer>();
    private Map<String, Integer> cooldownsMaximos = new HashMap<String, Integer>(); // Tiempos máximos por kit
    private String kitActual = "";
    private int cooldownActual = 0;
    private long ultimaLectura = 0;
    private long ultimoUsoKit = 0; // Timestamp del último uso del kit
    private static final Pattern KIT_PATTERN = Pattern.compile("Kit:\\s*(\\w+)\\s*\\(?(\\d+)s?\\)?", Pattern.CASE_INSENSITIVE);
    private static final Pattern KIT_SIMPLE_PATTERN = Pattern.compile("Kit:\\s*(\\w+)", Pattern.CASE_INSENSITIVE);
    
    // Tiempos de cooldown conocidos por kit (en segundos)
    static {
        // Estos valores se pueden ajustar según los cooldowns reales del servidor
    }

    public void actualizar() {
        long ahora = System.currentTimeMillis();
        
        // Decrementar cooldown cada segundo si está activo
        if (cooldownActual > 0) {
            long tiempoTranscurrido = ahora - ultimaLectura;
            if (tiempoTranscurrido >= 1000) {
                int segundosTranscurridos = (int)(tiempoTranscurrido / 1000);
                cooldownActual = Math.max(0, cooldownActual - segundosTranscurridos);
                if (kitActual != null && !kitActual.isEmpty()) {
                    cooldownsGuardados.put(kitActual, cooldownActual);
                }
                ultimaLectura = ahora - (tiempoTranscurrido % 1000);
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
            
            // Debug: imprimir en consola (solo para desarrollo)
            // System.out.println("[GolemCooldown] Scoreboard línea: " + texto);

            // Intentar patrón completo primero (con tiempo)
            // Ejemplos: "Kit: Golem (30s)", "Kit: Golem 30s", "Kit Golem (30s)"
            Matcher matcher = KIT_PATTERN.matcher(texto);
            if (matcher.find()) {
                kitEncontrado = matcher.group(1);
                try {
                    cooldownEncontrado = Integer.parseInt(matcher.group(2));
                } catch (NumberFormatException e) {
                    cooldownEncontrado = -1;
                }
                break;
            }
            
            // Si no tiene tiempo, buscar solo el nombre del kit
            // Ejemplo: "Kit: Golem"
            Matcher matcherSimple = KIT_SIMPLE_PATTERN.matcher(texto);
            if (matcherSimple.find()) {
                kitEncontrado = matcherSimple.group(1);
                // No hay cooldown en el scoreboard, usar el guardado o continuar con el actual
                break;
            }
        }
        
        if (kitEncontrado != null) {
            // Si cambió el kit
            if (!kitEncontrado.equals(kitActual)) {
                // Guardar cooldown del kit anterior
                if (kitActual != null && !kitActual.isEmpty() && cooldownActual > 0) {
                    cooldownsGuardados.put(kitActual, cooldownActual);
                }

                // Cargar cooldown del nuevo kit si existe
                kitActual = kitEncontrado;
                if (cooldownsGuardados.containsKey(kitEncontrado)) {
                    cooldownActual = cooldownsGuardados.get(kitEncontrado);
                } else if (cooldownEncontrado > 0) {
                    cooldownActual = cooldownEncontrado;
                } else {
                    // Si no hay cooldown en el scoreboard y no hay uno guardado, no mostrar nada
                    cooldownActual = 0;
                }
            } else {
                // Mismo kit
                if (cooldownEncontrado > 0) {
                    // Si el scoreboard muestra el cooldown, usarlo
                    cooldownActual = cooldownEncontrado;
                    cooldownsGuardados.put(kitEncontrado, cooldownActual);
                }
                // Si no hay cooldown en el scoreboard, continuar con el guardado
            }
        }
    }
    
    // Método para iniciar cooldown manualmente (útil para testing)
    public void iniciarCooldown(String kit, int segundos) {
        kitActual = kit;
        cooldownActual = segundos;
        cooldownsGuardados.put(kit, segundos);
        ultimoUsoKit = System.currentTimeMillis();
        ultimaLectura = System.currentTimeMillis(); // Resetear el contador de lectura
    }

    public int getCooldownActual() {
        return cooldownActual;
    }

    public String getKitActual() {
        return kitActual;
    }

    public void resetear() {
        cooldownActual = 0;
        kitActual = "";
        cooldownsGuardados.clear();
    }

    public void resetearPorMuerte() {
        // No resetear, mantener el estado
    }
}

