package net.andre.golemcooldown;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Map;

/**
 * Renderiza los cooldowns de items en pantalla (estilo canelex)
 * Muestra múltiples items con sus cooldowns
 */
public class ItemRenderManager {
    private static final RenderItem itemRenderer = new RenderItem();
    private int hudX = 10;
    private int hudY = -1; // -1 = calcular automáticamente
    private float hudScale = 1.0f;
    
    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }
        
        ItemCooldownManager manager = GolemCooldownMod.itemCooldownManager;
        if (manager == null) return;
        
        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution scaled = new ScaledResolution(mc);
        int screenWidth = scaled.getScaledWidth();
        int screenHeight = scaled.getScaledHeight();
        
        // Calcular posición Y si no está configurada
        int posY = (hudY < 0) ? screenHeight - 80 : hudY;
        int posX = hudX;
        
        // Obtener todos los items con cooldown activo
        Map<Integer, Integer> cooldowns = manager.getCooldownsActivos();
        
        if (cooldowns.isEmpty()) {
            return; // No hay cooldowns activos
        }
        
        int yOffset = 0;
        int itemSize = 20; // Tamaño del item en píxeles
        int spacing = 25; // Espacio entre items
        
        // Guardar estado de OpenGL (CRÍTICO para no afectar otros mods)
        GlStateManager.pushMatrix();
        
        try {
            GlStateManager.scale(hudScale, hudScale, 1.0f);
            
            for (Map.Entry<Integer, Integer> entry : cooldowns.entrySet()) {
                int itemId = entry.getKey();
                int cooldown = entry.getValue();
                
                // Crear ItemStack temporal para renderizar
                ItemStack itemStack = new ItemStack(net.minecraft.item.Item.getItemById(itemId));
                if (itemStack.getItem() == null) continue;
                
                int scaledX = (int)(posX / hudScale);
                int scaledY = (int)((posY + yOffset) / hudScale);
                
                // Dibujar fondo semi-transparente
                int alpha = 180;
                int colorFondo = (alpha << 24) | 0x000000;
                Gui.drawRect(scaledX - 2, scaledY - 2, 
                            scaledX + itemSize + 2, scaledY + itemSize + 2, 
                            colorFondo);
                
                // Dibujar borde
                Gui.drawRect(scaledX - 2, scaledY - 2, 
                            scaledX + itemSize + 2, scaledY - 1, 
                            0xFF000000);
                Gui.drawRect(scaledX - 2, scaledY + itemSize + 1, 
                            scaledX + itemSize + 2, scaledY + itemSize + 2, 
                            0xFF000000);
                Gui.drawRect(scaledX - 2, scaledY - 2, 
                            scaledX - 1, scaledY + itemSize + 2, 
                            0xFF000000);
                Gui.drawRect(scaledX + itemSize + 1, scaledY - 2, 
                            scaledX + itemSize + 2, scaledY + itemSize + 2, 
                            0xFF000000);
                
                // Renderizar el item - restaurar estados después
                RenderHelper.enableGUIStandardItemLighting();
                GlStateManager.enableRescaleNormal();
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                
                itemRenderer.renderItemIntoGUI(itemStack, scaledX, scaledY);
                itemRenderer.renderItemOverlayIntoGUI(mc.fontRendererObj, itemStack, scaledX, scaledY, null);
                
                // RESTAURAR estados (CRÍTICO)
                RenderHelper.disableStandardItemLighting();
                GlStateManager.disableRescaleNormal();
                
                // Dibujar el tiempo de cooldown
                String tiempoTexto = cooldown + "s";
                int textoWidth = mc.fontRendererObj.getStringWidth(tiempoTexto);
                int textoX = scaledX + (itemSize / 2) - (textoWidth / 2);
                int textoY = scaledY + itemSize + 3;
                
                // Fondo para el texto
                Gui.drawRect(textoX - 2, textoY - 1, 
                            textoX + textoWidth + 2, textoY + 9, 
                            (200 << 24) | 0x000000);
                
                // Texto del cooldown
                mc.fontRendererObj.drawStringWithShadow(
                    EnumChatFormatting.YELLOW + tiempoTexto, 
                    textoX, textoY, 
                    0xFFFFFF
                );
                
                // Dibujar barra de progreso (opcional)
                int cooldownMax = manager.getCooldownMaximo(itemId);
                if (cooldownMax > 0) {
                    float progreso = (float)cooldown / (float)cooldownMax;
                    int barraWidth = itemSize;
                    int barraHeight = 2;
                    int barraX = scaledX;
                    int barraY = scaledY + itemSize - barraHeight;
                    
                    // Fondo de la barra
                    Gui.drawRect(barraX, barraY, 
                                barraX + barraWidth, barraY + barraHeight, 
                                0xFF000000);
                    
                    // Barra de progreso (verde -> amarillo -> rojo)
                    int colorBarra;
                    if (progreso > 0.5f) {
                        colorBarra = 0xFF00FF00; // Verde
                    } else if (progreso > 0.25f) {
                        colorBarra = 0xFFFFFF00; // Amarillo
                    } else {
                        colorBarra = 0xFFFF0000; // Rojo
                    }
                    
                    Gui.drawRect(barraX, barraY, 
                                barraX + (int)(barraWidth * progreso), barraY + barraHeight, 
                                colorBarra);
                }
                
                yOffset += spacing;
            }
        } catch (Exception e) {
            // Si hay error, no hacer nada que pueda afectar otros mods
            System.err.println("[GolemCooldown] Error al renderizar items: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // SIEMPRE restaurar el estado de OpenGL (CRÍTICO)
            // Esto previene que otros mods o configuraciones se vean afectadas
            GlStateManager.popMatrix();
        }
    }
    
    public void setPosicion(int x, int y) {
        this.hudX = x;
        this.hudY = y;
    }
    
    public void setEscala(float escala) {
        this.hudScale = escala;
    }
}

