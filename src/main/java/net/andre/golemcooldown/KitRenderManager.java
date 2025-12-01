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

/**
 * Renderiza el contador de cooldown del kit actual
 * Solo muestra si hay cooldown activo
 */
public class KitRenderManager {
    private static final RenderItem itemRenderer = new RenderItem();
    
    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }
        
        ScoreboardCooldownManager manager = GolemCooldownMod.scoreboardCooldownManager;
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
        
        // Obtener Item ID del kit
        int itemId = KitMapper.getItemId(kit);
        if (itemId <= 0) {
            return; // Kit no mapeado
        }
        
        // Crear ItemStack
        ItemStack itemStack = new ItemStack(net.minecraft.item.Item.getItemById(itemId));
        if (itemStack.getItem() == null) {
            return;
        }
        
        // Obtener posición y escala de la configuración
        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution scaled = new ScaledResolution(mc);
        int screenWidth = scaled.getScaledWidth();
        int screenHeight = scaled.getScaledHeight();
        
        int posX = (ConfigManager.hudX < 0) ? 10 : ConfigManager.hudX;
        int posY = (ConfigManager.hudY < 0) ? screenHeight - 50 : ConfigManager.hudY;
        float scale = ConfigManager.hudScale;
        
        // Renderizar
        dibujarContador(mc, posX, posY, scale, itemStack, cooldown, manager.getCooldownMaximo());
    }
    
    /**
     * Dibuja el contador en pantalla
     * IMPORTANTE: Restaura completamente el estado de OpenGL para no afectar otros mods
     * NO modifica configuraciones de video, zoom, controles u otros mods
     */
    private void dibujarContador(Minecraft mc, int x, int y, float escala, 
                                 ItemStack itemStack, int cooldown, int cooldownMax) {
        // Guardar estado de OpenGL (CRÍTICO para no afectar otros mods)
        GlStateManager.pushMatrix();
        
        try {
            // Aplicar transformaciones solo para nuestro renderizado
            GlStateManager.scale(escala, escala, 1.0f);
            
            int scaledX = (int)(x / escala);
            int scaledY = (int)(y / escala);
            
            int itemSize = 32;
            int itemX = scaledX - itemSize / 2;
            int itemY = scaledY - itemSize / 2;
            
            // Fondo semi-transparente
            int alpha = 200;
            int colorFondo = (alpha << 24) | 0x000000;
            Gui.drawRect(itemX - 5, itemY - 5, itemX + itemSize + 5, itemY + itemSize + 5, colorFondo);
            
            // Borde
            Gui.drawRect(itemX - 5, itemY - 5, itemX + itemSize + 5, itemY - 4, 0xFF000000);
            Gui.drawRect(itemX - 5, itemY + itemSize + 4, itemX + itemSize + 5, itemY + itemSize + 5, 0xFF000000);
            Gui.drawRect(itemX - 5, itemY - 5, itemX - 4, itemY + itemSize + 5, 0xFF000000);
            Gui.drawRect(itemX + itemSize + 4, itemY - 5, itemX + itemSize + 5, itemY + itemSize + 5, 0xFF000000);
            
            // Renderizar item - guardar y restaurar estados críticos
            // Estos estados pueden afectar otros mods si no se restauran
            RenderHelper.enableGUIStandardItemLighting();
            GlStateManager.enableRescaleNormal();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            
            itemRenderer.renderItemIntoGUI(itemStack, itemX, itemY);
            itemRenderer.renderItemOverlayIntoGUI(mc.fontRendererObj, itemStack, itemX, itemY, null);
            
            // RESTAURAR estados (CRÍTICO)
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableRescaleNormal();
            // NO deshabilitar blend aquí, puede estar usado por otros mods
            // Solo restaurar la función de blend si es necesario
            
            // Texto del cooldown
            String tiempoTexto = cooldown + "s";
            int textoWidth = mc.fontRendererObj.getStringWidth(tiempoTexto);
            int textoX = scaledX - textoWidth / 2;
            int textoY = scaledY + itemSize / 2 + 5;
            
            // Fondo para el texto
            Gui.drawRect(textoX - 2, textoY - 1, textoX + textoWidth + 2, textoY + 9, (200 << 24) | 0x000000);
            
            // Texto
            mc.fontRendererObj.drawStringWithShadow(
                EnumChatFormatting.YELLOW + tiempoTexto,
                textoX, textoY,
                0xFFFFFF
            );
            
            // Barra de progreso (opcional)
            if (cooldownMax > 0) {
                float progreso = (float)cooldown / (float)cooldownMax;
                int barraWidth = itemSize;
                int barraHeight = 2;
                int barraX = itemX;
                int barraY = itemY + itemSize - barraHeight;
                
                // Fondo de la barra
                Gui.drawRect(barraX, barraY, barraX + barraWidth, barraY + barraHeight, 0xFF000000);
                
                // Barra de progreso (verde -> amarillo -> rojo)
                int colorBarra;
                if (progreso > 0.5f) {
                    colorBarra = 0xFF00FF00; // Verde
                } else if (progreso > 0.25f) {
                    colorBarra = 0xFFFFFF00; // Amarillo
                } else {
                    colorBarra = 0xFFFF0000; // Rojo
                }
                
                Gui.drawRect(barraX, barraY, barraX + (int)(barraWidth * progreso), barraY + barraHeight, colorBarra);
            }
            
        } catch (Exception e) {
            // Si hay error, no hacer nada que pueda afectar otros mods
            System.err.println("[GolemCooldown] Error al renderizar: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // SIEMPRE restaurar el estado de OpenGL (CRÍTICO)
            // Esto previene que otros mods o configuraciones se vean afectadas
            GlStateManager.popMatrix();
        }
    }
}

