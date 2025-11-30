package net.andre.golemcooldown;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class RenderManager {
    private int hudX = -1;
    private int hudY = -1;
    private float hudScale = 1.0f;
    private boolean modoEdicion = false;

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }

        CooldownManager manager = GolemCooldownMod.cooldownManager;
        if (manager == null) return;

        manager.actualizar();

        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution scaled = new ScaledResolution(mc);
        int screenWidth = scaled.getScaledWidth();
        int screenHeight = scaled.getScaledHeight();

        int posX = (hudX < 0) ? 10 : hudX;
        int posY = (hudY < 0) ? screenHeight - 50 : hudY;

        int cooldown = manager.getCooldownActual();
        String kit = manager.getKitActual();

        // Dibujar contador si hay cooldown activo
        if (cooldown > 0 && kit != null && !kit.isEmpty()) {
            String texto = "§e§lKit " + kit + " §f§l" + cooldown + "s";
            dibujarContador(mc, posX, posY, texto, false);
        }

        // Modo edición
        if (modoEdicion) {
            String texto = "§e§lKit " + (kit != null && !kit.isEmpty() ? kit : "Ninguno") + " §f§l" + (cooldown > 0 ? cooldown : "0") + "s";
            if (cooldown <= 0 && (kit == null || kit.isEmpty())) {
                texto = "§e§l[EDITAR] §7Arrastra para mover";
            }
            dibujarContador(mc, posX, posY, texto, true);
            dibujarPanelAyuda(mc, screenWidth, screenHeight, posX, posY);
        }
    }

    private void dibujarContador(Minecraft mc, int x, int y, String texto, boolean conFondo) {
        GlStateManager.pushMatrix();
        GlStateManager.scale(hudScale, hudScale, 1.0f);
        
        int scaledX = (int)(x / hudScale);
        int scaledY = (int)(y / hudScale);
        
        String textoLimpio = texto.replaceAll("§[0-9a-fk-or]", "");
        int textoWidth = mc.fontRendererObj.getStringWidth(textoLimpio);
        int textoHeight = 12;

        if (conFondo) {
            // Fondo amarillo (con transparencia)
            int alpha = 200; // 0-255
            int color = (alpha << 24) | 0xFFFF00; // Amarillo con alpha
            Gui.drawRect((int)(scaledX - 10 / hudScale), (int)(scaledY - 10 / hudScale),
                    (int)((scaledX + textoWidth + 10) / hudScale), (int)((scaledY + textoHeight + 10) / hudScale),
                    color);
            
            // Bordes negros
            Gui.drawRect((int)(scaledX - 10 / hudScale), (int)(scaledY - 10 / hudScale),
                    (int)((scaledX + textoWidth + 10) / hudScale), (int)(scaledY - 7 / hudScale), 0xFF000000);
            Gui.drawRect((int)(scaledX - 10 / hudScale), (int)((scaledY + textoHeight + 7) / hudScale),
                    (int)((scaledX + textoWidth + 10) / hudScale), (int)((scaledY + textoHeight + 10) / hudScale), 0xFF000000);
            Gui.drawRect((int)(scaledX - 10 / hudScale), (int)(scaledY - 10 / hudScale),
                    (int)(scaledX - 7 / hudScale), (int)((scaledY + textoHeight + 10) / hudScale), 0xFF000000);
            Gui.drawRect((int)((scaledX + textoWidth + 7) / hudScale), (int)(scaledY - 10 / hudScale),
                    (int)((scaledX + textoWidth + 10) / hudScale), (int)((scaledY + textoHeight + 10) / hudScale), 0xFF000000);
            
            // Esquinas rojas
            Gui.drawRect((int)(scaledX - 10 / hudScale), (int)(scaledY - 10 / hudScale),
                    (int)(scaledX - 2 / hudScale), (int)(scaledY - 2 / hudScale), 0xFFFF0000);
            Gui.drawRect((int)((scaledX + textoWidth + 2) / hudScale), (int)(scaledY - 10 / hudScale),
                    (int)((scaledX + textoWidth + 10) / hudScale), (int)(scaledY - 2 / hudScale), 0xFFFF0000);
            Gui.drawRect((int)(scaledX - 10 / hudScale), (int)((scaledY + textoHeight + 2) / hudScale),
                    (int)(scaledX - 2 / hudScale), (int)((scaledY + textoHeight + 10) / hudScale), 0xFFFF0000);
            Gui.drawRect((int)((scaledX + textoWidth + 2) / hudScale), (int)((scaledY + textoHeight + 2) / hudScale),
                    (int)((scaledX + textoWidth + 10) / hudScale), (int)((scaledY + textoHeight + 10) / hudScale), 0xFFFF0000);
        }

        mc.fontRendererObj.drawStringWithShadow(texto, scaledX, scaledY, 0xFFFFFF);
        
        GlStateManager.popMatrix();
    }

    private void dibujarPanelAyuda(Minecraft mc, int screenWidth, int screenHeight, int posX, int posY) {
        int ayudaX = screenWidth / 2 - 150;
        int ayudaY = 80;

        // Fondo del panel
        Gui.drawRect(ayudaX - 10, ayudaY - 10, ayudaX + 330, ayudaY + 80, 0xDC000000);
        Gui.drawRect(ayudaX - 8, ayudaY - 8, ayudaX + 328, ayudaY + 78, 0x78FFFF00);

        mc.fontRendererObj.drawStringWithShadow("§a§l=== MODO EDICIÓN ACTIVO ===", ayudaX, ayudaY, 0xFFFFFF);
        mc.fontRendererObj.drawStringWithShadow("§eBusca el contador AMARILLO con esquinas ROJAS", ayudaX, ayudaY + 15, 0xFFFFFF);
        mc.fontRendererObj.drawStringWithShadow("§eArrastra el contador para moverlo", ayudaX, ayudaY + 25, 0xFFFFFF);
        mc.fontRendererObj.drawStringWithShadow("§eUsa la rueda del mouse para cambiar el tamaño", ayudaX, ayudaY + 35, 0xFFFFFF);
        mc.fontRendererObj.drawStringWithShadow("§7O usa: /golemmove <x> <y> para moverlo manualmente", ayudaX, ayudaY + 45, 0xFFFFFF);
        mc.fontRendererObj.drawStringWithShadow("§7Posición: " + (hudX < 0 ? "Por defecto (" + posX + ", " + posY + ")" : hudX + ", " + hudY), ayudaX, ayudaY + 55, 0xFFFFFF);
        mc.fontRendererObj.drawStringWithShadow("§7Tamaño: " + String.format("%.1f", hudScale) + "x | §c/golemhud para cerrar", ayudaX, ayudaY + 65, 0xFFFFFF);
    }

    public void setPosicion(int x, int y) {
        this.hudX = x;
        this.hudY = y;
    }

    public void setEscala(float escala) {
        this.hudScale = escala;
    }

    public void toggleModoEdicion() {
        this.modoEdicion = !this.modoEdicion;
    }
}

