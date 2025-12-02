package net.andre.golemcooldown;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.io.IOException;

/**
 * Interfaz para configurar el cooldown del kit actual
 * Muestra el item del kit detectado (o ? si no se detecta)
 * Permite mover y escalar el contador
 */
public class GuiCreateCooldown extends GuiScreen {
    private GuiScreen parent;
    
    // Estado de edición
    private boolean isDragging = false;
    private int lastMouseX = 0;
    private int lastMouseY = 0;
    
    // Información del kit actual
    private String kitDetectado = "";
    
    // Posición y escala temporal (para edición)
    private int tempX = 0;
    private int tempY = 0;
    private float tempScale = 1.0f;
    
    public GuiCreateCooldown(GuiScreen parent) {
        this.parent = parent;
        detectarKitActual();
    }
    
    /**
     * Detecta el kit actual desde el scoreboard
     */
    private void detectarKitActual() {
        ScoreboardCooldownManager manager = CooldownAnni.scoreboardCooldownManager;
        if (manager != null) {
            // El manager se actualiza automáticamente cada tick
            kitDetectado = manager.getKitActual();
        }
        
        // Cargar posición y escala actual
        tempX = ConfigManager.hudX;
        tempY = ConfigManager.hudY;
        tempScale = ConfigManager.hudScale;
    }
    
    @Override
    public void initGui() {
        super.initGui();
        
        // Invalidar cache de ScaledResolution cuando cambia el tamaño de la GUI
        cachedScaledResolution = null;
        
        // Botón "Save"
        this.buttonList.add(new GuiButton(
            1,
            this.width / 2 - 100,
            this.height - 60,
            200,
            20,
            EnumChatFormatting.GREEN + "Save"
        ));
        
        // Botón "Back"
        this.buttonList.add(new GuiButton(
            2,
            this.width / 2 - 100,
            this.height - 30,
            200,
            20,
            "Back"
        ));
    }
    
    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 1) {
            // Save - Guardar configuración
            ConfigManager.setPosition(tempX, tempY);
            ConfigManager.setScale(tempScale);
            if (this.mc.thePlayer != null) {
                this.mc.thePlayer.addChatMessage(
                    new net.minecraft.util.ChatComponentText(
                        EnumChatFormatting.GREEN + "[CooldownAnni] " + 
                        EnumChatFormatting.YELLOW + "Configuración guardada"
                    )
                );
            }
            this.mc.displayGuiScreen(this.parent);
        } else if (button.id == 2) {
            // Back
            this.mc.displayGuiScreen(this.parent);
        }
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        
        // Título
        String title = EnumChatFormatting.GOLD + EnumChatFormatting.BOLD.toString() + 
                      "CREATE COOLDOWN";
        int titleWidth = this.fontRendererObj.getStringWidth(title);
        this.fontRendererObj.drawStringWithShadow(
            title,
            (this.width - titleWidth) / 2,
            20,
            0xFFFFFF
        );
        
        // Información del kit
        String kitInfo;
        if (kitDetectado == null || kitDetectado.isEmpty()) {
            kitInfo = EnumChatFormatting.GRAY + "Kit: " + EnumChatFormatting.WHITE + "No detectado (únete a una partida)";
        } else {
            kitInfo = EnumChatFormatting.GREEN + "Kit: " + EnumChatFormatting.WHITE + kitDetectado;
        }
        int infoWidth = this.fontRendererObj.getStringWidth(kitInfo);
        this.fontRendererObj.drawStringWithShadow(
            kitInfo,
            (this.width - infoWidth) / 2,
            40,
            0xFFFFFF
        );
        
        // Instrucciones
        String inst1 = EnumChatFormatting.GRAY + "Arrastra para mover | Rueda del mouse para escalar";
        int inst1Width = this.fontRendererObj.getStringWidth(inst1);
        this.fontRendererObj.drawStringWithShadow(
            inst1,
            (this.width - inst1Width) / 2,
            this.height - 100,
            0xFFFFFF
        );
        
        // Dibujar el contador de ejemplo
        dibujarContadorEjemplo(mouseX, mouseY);
        
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
    
    // Cache de ScaledResolution para evitar crear múltiples instancias
    private net.minecraft.client.gui.ScaledResolution cachedScaledResolution = null;
    
    /**
     * Obtiene ScaledResolution de forma segura (reutiliza instancia si es posible)
     */
    private net.minecraft.client.gui.ScaledResolution getScaledResolution() {
        Minecraft mc = Minecraft.getMinecraft();
        // Solo crear nueva instancia si es necesario (cambió el tamaño de ventana)
        if (cachedScaledResolution == null) {
            cachedScaledResolution = new net.minecraft.client.gui.ScaledResolution(mc);
        }
        return cachedScaledResolution;
    }
    
    /**
     * Dibuja el contador de ejemplo (para edición)
     */
    private void dibujarContadorEjemplo(int mouseX, int mouseY) {
        Minecraft mc = Minecraft.getMinecraft();
        net.minecraft.client.gui.ScaledResolution scaled = getScaledResolution();
        int screenHeight = scaled.getScaledHeight();
        
        // Calcular posición
        int posX = (tempX < 0) ? 10 : tempX;
        int posY = (tempY < 0) ? screenHeight - 50 : tempY;
        
        // Ajustar para el overlay (la GUI tiene su propia escala)
        int guiPosX = (int)((posX / (double)scaled.getScaledWidth()) * this.width);
        int guiPosY = (int)((posY / (double)scaled.getScaledHeight()) * this.height);
        
        GlStateManager.pushMatrix();
        GlStateManager.scale(tempScale, tempScale, 1.0f);
        
        int scaledX = (int)(guiPosX / tempScale);
        int scaledY = (int)(guiPosY / tempScale);
        
        // Dibujar círculo estilo canelex
        int radio = 25;
        
        // Habilitar blend para transparencia
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.disableTexture2D();
        GlStateManager.disableAlpha();
        
        // Dibujar fondo del círculo (oscuro/morado)
        int alphaFondo = 220;
        int colorFondo = (alphaFondo << 24) | 0x4B0082; // Morado oscuro
        dibujarCirculoRelleno(scaledX, scaledY, radio, colorFondo);
        
        // Dibujar borde blanco del círculo
        int grosorBorde = 2;
        int colorBorde = 0xFFFFFFFF; // Blanco
        dibujarCirculoBorde(scaledX, scaledY, radio, grosorBorde, colorBorde);
        
        // Restaurar estados
        GlStateManager.enableTexture2D();
        GlStateManager.enableAlpha();
        
        // Texto de ejemplo "30.0" centrado en amarillo
        String tiempoEjemplo = EnumChatFormatting.YELLOW + "30.0";
        int tiempoWidth = this.fontRendererObj.getStringWidth(tiempoEjemplo);
        int textoX = scaledX - tiempoWidth / 2;
        int textoY = scaledY - 4; // Centrado verticalmente
        
        this.fontRendererObj.drawStringWithShadow(
            tiempoEjemplo,
            textoX, textoY,
            0xFFFFFF
        );
        
        // SIEMPRE restaurar el estado de OpenGL
        GlStateManager.popMatrix();
        
        // Indicador de que se puede arrastrar
        int radioDiametro = radio * 2;
        if (isHoveringOverCounter(mouseX, mouseY, guiPosX, guiPosY, radioDiametro)) {
            String dragHint = EnumChatFormatting.GRAY + "Arrastra para mover";
            int hintWidth = this.fontRendererObj.getStringWidth(dragHint);
            this.fontRendererObj.drawStringWithShadow(
                dragHint,
                mouseX - hintWidth / 2,
                mouseY - 15,
                0xFFFFFF
            );
        }
    }
    
    /**
     * Verifica si el mouse está sobre el contador circular
     */
    private boolean isHoveringOverCounter(int mouseX, int mouseY, int counterX, int counterY, int diametro) {
        int radio = diametro / 2;
        int dx = mouseX - counterX;
        int dy = mouseY - counterY;
        double distancia = Math.sqrt(dx * dx + dy * dy);
        return distancia <= radio;
    }
    
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        
        if (mouseButton == 0) { // Click izquierdo
            net.minecraft.client.gui.ScaledResolution scaled = getScaledResolution();
            int screenHeight = scaled.getScaledHeight();
            
            int posX = (tempX < 0) ? 10 : tempX;
            int posY = (tempY < 0) ? screenHeight - 50 : tempY;
            
            int guiPosX = (int)((posX / (double)scaled.getScaledWidth()) * this.width);
            int guiPosY = (int)((posY / (double)scaled.getScaledHeight()) * this.height);
            
            int radio = 25;
            if (isHoveringOverCounter(mouseX, mouseY, guiPosX, guiPosY, radio * 2)) {
                isDragging = true;
                lastMouseX = mouseX;
                lastMouseY = mouseY;
            }
        }
    }
    
    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        isDragging = false;
    }
    
    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
        
        if (isDragging && clickedMouseButton == 0) {
            net.minecraft.client.gui.ScaledResolution scaled = getScaledResolution();
            
            // Calcular nueva posición
            int deltaX = mouseX - lastMouseX;
            int deltaY = mouseY - lastMouseY;
            
            int newGuiX = (int)((tempX < 0 ? 10 : tempX) / (double)scaled.getScaledWidth() * this.width) + deltaX;
            int newGuiY = (int)((tempY < 0 ? scaled.getScaledHeight() - 50 : tempY) / (double)scaled.getScaledHeight() * this.height) + deltaY;
            
            // Convertir de vuelta a coordenadas de pantalla
            tempX = (int)((newGuiX / (double)this.width) * scaled.getScaledWidth());
            tempY = (int)((newGuiY / (double)this.height) * scaled.getScaledHeight());
            
            lastMouseX = mouseX;
            lastMouseY = mouseY;
        }
    }
    
    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        
        int wheel = Mouse.getEventDWheel();
        if (wheel != 0) {
            if (wheel > 0) {
                // Scroll up - aumentar escala
                tempScale = Math.min(5.0f, tempScale + 0.1f);
            } else {
                // Scroll down - disminuir escala
                tempScale = Math.max(0.1f, tempScale - 0.1f);
            }
        }
    }
    
    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            this.mc.displayGuiScreen(this.parent);
        } else {
            super.keyTyped(typedChar, keyCode);
        }
    }
    
    @Override
    public boolean doesGuiPauseGame() {
        // Retornar true para evitar problemas al minimizar la ventana
        return true;
    }
    
    /**
     * Dibuja un círculo relleno usando OpenGL (mismo método que KitRenderManager)
     */
    private void dibujarCirculoRelleno(int x, int y, int radio, int color) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        
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
        int segmentos = 32;
        for (int i = 0; i <= segmentos; i++) {
            double angulo = (i * 2 * Math.PI) / segmentos;
            double px = x + radio * Math.cos(angulo);
            double py = y + radio * Math.sin(angulo);
            worldrenderer.pos(px, py, 0).color(r, g, b, a).endVertex();
        }
        
        tessellator.draw();
        GlStateManager.enableTexture2D();
    }
    
    /**
     * Dibuja el borde de un círculo usando OpenGL (mismo método que KitRenderManager)
     */
    private void dibujarCirculoBorde(int x, int y, int radio, int grosor, int color) {
        // Validar parámetros
        if (radio <= 0 || grosor <= 0 || grosor >= radio) {
            return; // No dibujar si los parámetros son inválidos
        }
        
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        
        // Extraer componentes de color
        int alpha = (color >> 24) & 0xFF;
        int red = (color >> 16) & 0xFF;
        int green = (color >> 8) & 0xFF;
        int blue = color & 0xFF;
        
        float r = red / 255.0f;
        float g = green / 255.0f;
        float b = blue / 255.0f;
        float a = alpha / 255.0f;
        
        // Dibujar borde como un anillo
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
        GlStateManager.enableTexture2D();
    }
}

