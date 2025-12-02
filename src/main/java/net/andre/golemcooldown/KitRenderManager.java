package net.andre.golemcooldown;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

/**
 * Renderiza el contador de cooldown del kit actual
 * Diseño circular estilo canelex: círculo con borde blanco, fondo oscuro/morado, número centrado
 * Solo muestra si hay cooldown activo
 */
public class KitRenderManager {
    
    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        // Renderizar solo cuando se renderiza el texto (después de todo lo demás)
        if (event.type != RenderGameOverlayEvent.ElementType.TEXT) {
            return;
        }
        
        ScoreboardCooldownManager manager = CooldownAnni.scoreboardCooldownManager;
        if (manager == null) return;
        
        // Solo mostrar si hay cooldown activo
        if (!manager.tieneCooldownActivo()) {
            return;
        }
        
        String kit = manager.getKitActual();
        int cooldown = manager.getCooldownActual();
        
        if (kit == null || kit.isEmpty() || cooldown <= 0) {
            return;
        }
        
        // Obtener posición y escala de la configuración
        // IMPORTANTE: Usar el ScaledResolution del evento, no crear uno nuevo
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld == null || mc.thePlayer == null) {
            return; // No renderizar si no hay mundo/jugador
        }
        
        ScaledResolution scaled = event.resolution; // Usar el del evento
        int screenHeight = scaled.getScaledHeight();
        
        int posX = (ConfigManager.hudX < 0) ? 10 : ConfigManager.hudX;
        int posY = (ConfigManager.hudY < 0) ? screenHeight - 50 : ConfigManager.hudY;
        float scale = ConfigManager.hudScale;
        
        // Renderizar diseño circular estilo canelex
        dibujarContadorCircular(mc, posX, posY, scale, cooldown, manager.getCooldownMaximo());
    }
    
    /**
     * Dibuja el contador circular estilo canelex
     * Diseño: Círculo con borde blanco, fondo oscuro/morado, número centrado en amarillo
     * IMPORTANTE: Usa pushMatrix/popMatrix simple como el mod original - NO toca estados manualmente
     */
    private void dibujarContadorCircular(Minecraft mc, int x, int y, float escala, 
                                         int cooldown, int cooldownMax) {
        // Validar escala para evitar división por cero
        if (escala <= 0.0f) {
            escala = 1.0f;
        }
        
        // SIMPLE: Solo push/pop matrix - GlStateManager maneja el resto internamente
        GlStateManager.pushMatrix();
        
        try {
            // Aplicar transformaciones solo para nuestro renderizado
            GlStateManager.scale(escala, escala, 1.0f);
            
            int scaledX = (int)(x / escala);
            int scaledY = (int)(y / escala);
            
            // Tamaño del círculo (radio)
            int radio = 25; // Radio del círculo
            
            // Habilitar blend para transparencia
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.disableTexture2D();
            GlStateManager.disableAlpha();
            
            // Dibujar fondo del círculo (oscuro/morado)
            // Color: RGB(75, 0, 130) = morado oscuro, con alpha
            int alphaFondo = 220;
            int colorFondo = (alphaFondo << 24) | 0x4B0082; // Morado oscuro con transparencia
            
            dibujarCirculoRelleno(scaledX, scaledY, radio, colorFondo);
            
            // Dibujar borde blanco del círculo
            int grosorBorde = 2;
            int colorBorde = 0xFFFFFFFF; // Blanco
            dibujarCirculoBorde(scaledX, scaledY, radio, grosorBorde, colorBorde);
            
            // Restaurar estados de OpenGL antes del texto
            GlStateManager.enableTexture2D();
            GlStateManager.enableAlpha();
            
            // Texto del cooldown centrado (amarillo pixelado)
            // Formato: "10.0" con un decimal
            String tiempoTexto;
            if (cooldownMax > 0 && cooldown < cooldownMax) {
                // Mostrar con decimal si hay cooldown máximo (ej: 10.0, 9.5, etc.)
                float tiempoDecimal = (float)cooldown;
                tiempoTexto = String.format("%.1f", tiempoDecimal);
            } else {
                // Mostrar entero si no hay máximo (ej: 30, 29, etc.)
                tiempoTexto = String.valueOf(cooldown);
            }
            
            // Renderizar texto centrado en amarillo (estilo pixelado)
            int textoWidth = mc.fontRendererObj.getStringWidth(tiempoTexto);
            int textoX = scaledX - textoWidth / 2;
            int textoY = scaledY - 4; // Centrado verticalmente
            
            // Texto amarillo con sombra (estilo pixelado)
            mc.fontRendererObj.drawStringWithShadow(
                EnumChatFormatting.YELLOW + tiempoTexto,
                textoX, textoY,
                0xFFFFFF
            );
            
        } catch (Exception e) {
            // Si hay error, no hacer nada que pueda afectar otros mods
            System.err.println("[CooldownAnni] Error al renderizar círculo: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // SIMPLE: Solo popMatrix - GlStateManager restaura todo automáticamente
            // NO intentar restaurar estados manualmente - eso causa problemas
            GlStateManager.popMatrix();
        }
    }
    
    /**
     * Dibuja un círculo relleno usando OpenGL
     * NO restaura estados aquí - se hace en el método principal para compatibilidad
     */
    private void dibujarCirculoRelleno(int x, int y, int radio, int color) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        
        // NO modificar estados aquí - ya están configurados en el método principal
        // Solo dibujar el círculo
        
        // Extraer componentes de color
        int alpha = (color >> 24) & 0xFF;
        int red = (color >> 16) & 0xFF;
        int green = (color >> 8) & 0xFF;
        int blue = color & 0xFF;
        
        float r = red / 255.0f;
        float g = green / 255.0f;
        float b = blue / 255.0f;
        float a = alpha / 255.0f;
        
        worldrenderer.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);
        worldrenderer.pos(x, y, 0).color(r, g, b, a).endVertex();
        
        // Dibujar círculo usando múltiples puntos
        int segmentos = 32; // Número de segmentos para el círculo (más = más suave)
        for (int i = 0; i <= segmentos; i++) {
            double angulo = (i * 2 * Math.PI) / segmentos;
            double px = x + radio * Math.cos(angulo);
            double py = y + radio * Math.sin(angulo);
            worldrenderer.pos(px, py, 0).color(r, g, b, a).endVertex();
        }
        
        tessellator.draw();
    }
    
    /**
     * Dibuja el borde de un círculo usando OpenGL
     * NO restaura estados aquí - se hace en el método principal para compatibilidad
     */
    private void dibujarCirculoBorde(int x, int y, int radio, int grosor, int color) {
        // Validar parámetros
        if (radio <= 0 || grosor <= 0 || grosor >= radio) {
            return; // No dibujar si los parámetros son inválidos
        }
        
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        
        // NO modificar estados aquí - ya están configurados en el método principal
        // Solo dibujar el borde
        
        // Extraer componentes de color
        int alpha = (color >> 24) & 0xFF;
        int red = (color >> 16) & 0xFF;
        int green = (color >> 8) & 0xFF;
        int blue = color & 0xFF;
        
        float r = red / 255.0f;
        float g = green / 255.0f;
        float b = blue / 255.0f;
        float a = alpha / 255.0f;
        
        // Dibujar borde como un anillo (círculo exterior menos círculo interior)
        int radioExterior = radio;
        int radioInterior = Math.max(1, radio - grosor); // Asegurar que sea al menos 1
        
        int segmentos = 32;
        worldrenderer.begin(GL11.GL_QUAD_STRIP, DefaultVertexFormats.POSITION_COLOR);
        
        for (int i = 0; i <= segmentos; i++) {
            double angulo = (i * 2 * Math.PI) / segmentos;
            double cos = Math.cos(angulo);
            double sin = Math.sin(angulo);
            
            // Punto exterior
            double px1 = x + radioExterior * cos;
            double py1 = y + radioExterior * sin;
            worldrenderer.pos(px1, py1, 0).color(r, g, b, a).endVertex();
            
            // Punto interior
            double px2 = x + radioInterior * cos;
            double py2 = y + radioInterior * sin;
            worldrenderer.pos(px2, py2, 0).color(r, g, b, a).endVertex();
        }
        
        tessellator.draw();
    }
}

