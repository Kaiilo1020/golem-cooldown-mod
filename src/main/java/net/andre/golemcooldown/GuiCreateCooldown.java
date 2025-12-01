package net.andre.golemcooldown;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.io.IOException;

/**
 * Interfaz para configurar el cooldown del kit actual
 * Muestra el item del kit detectado (o ? si no se detecta)
 * Permite mover y escalar el contador
 */
public class GuiCreateCooldown extends GuiScreen {
    private GuiScreen parent;
    private static final RenderItem itemRenderer = new RenderItem();
    
    // Estado de edición
    private boolean isDragging = false;
    private int dragOffsetX = 0;
    private int dragOffsetY = 0;
    private int lastMouseX = 0;
    private int lastMouseY = 0;
    
    // Información del kit actual
    private String kitDetectado = "";
    private int itemId = -1;
    private ItemStack itemStack = null;
    private boolean kitNoDetectado = false;
    
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
        ScoreboardCooldownManager manager = GolemCooldownMod.scoreboardCooldownManager;
        if (manager != null) {
            // El manager se actualiza automáticamente cada tick
            kitDetectado = manager.getKitActual();
            
            if (kitDetectado != null && !kitDetectado.isEmpty()) {
                // Kit detectado, obtener Item ID
                itemId = KitMapper.getItemId(kitDetectado);
                if (itemId > 0) {
                    itemStack = new ItemStack(net.minecraft.item.Item.getItemById(itemId));
                    kitNoDetectado = false;
                } else {
                    kitNoDetectado = true;
                }
            } else {
                kitNoDetectado = true;
            }
        } else {
            kitNoDetectado = true;
        }
        
        // Cargar posición y escala actual
        tempX = ConfigManager.hudX;
        tempY = ConfigManager.hudY;
        tempScale = ConfigManager.hudScale;
    }
    
    @Override
    public void initGui() {
        super.initGui();
        
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
            this.mc.thePlayer.addChatMessage(
                new net.minecraft.util.ChatComponentText(
                    EnumChatFormatting.GREEN + "[GolemCooldown] " + 
                    EnumChatFormatting.YELLOW + "Configuración guardada"
                )
            );
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
        if (kitNoDetectado) {
            kitInfo = EnumChatFormatting.RED + "Kit no detectado";
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
    
    /**
     * Dibuja el contador de ejemplo (para edición)
     */
    private void dibujarContadorEjemplo(int mouseX, int mouseY) {
        Minecraft mc = Minecraft.getMinecraft();
        net.minecraft.client.gui.ScaledResolution scaled = new net.minecraft.client.gui.ScaledResolution(mc);
        int screenWidth = scaled.getScaledWidth();
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
        
        int itemSize = 32;
        int itemX = scaledX - itemSize / 2;
        int itemY = scaledY - itemSize / 2;
        
        // Fondo semi-transparente
        int alpha = 200;
        int colorFondo = (alpha << 24) | 0x000000;
        drawRect(itemX - 5, itemY - 5, itemX + itemSize + 5, itemY + itemSize + 5, colorFondo);
        
        // Borde
        drawRect(itemX - 5, itemY - 5, itemX + itemSize + 5, itemY - 4, 0xFF000000);
        drawRect(itemX - 5, itemY + itemSize + 4, itemX + itemSize + 5, itemY + itemSize + 5, 0xFF000000);
        drawRect(itemX - 5, itemY - 5, itemX - 4, itemY + itemSize + 5, 0xFF000000);
        drawRect(itemX + itemSize + 4, itemY - 5, itemX + itemSize + 5, itemY + itemSize + 5, 0xFF000000);
        
        // Renderizar item o signo de interrogación
        if (kitNoDetectado || itemStack == null) {
            // Signo de interrogación
            String questionMark = EnumChatFormatting.YELLOW + "?";
            int qmWidth = this.fontRendererObj.getStringWidth(questionMark);
            this.fontRendererObj.drawStringWithShadow(
                questionMark,
                scaledX - qmWidth / 2,
                scaledY - 4,
                0xFFFFFF
            );
        } else {
            // Renderizar item - restaurar estados después
            RenderHelper.enableGUIStandardItemLighting();
            GlStateManager.enableRescaleNormal();
            GlStateManager.enableBlend();
            
            itemRenderer.renderItemIntoGUI(itemStack, itemX, itemY);
            itemRenderer.renderItemOverlayIntoGUI(
                this.fontRendererObj,
                itemStack,
                itemX,
                itemY,
                null
            );
            
            // RESTAURAR estados (CRÍTICO para no afectar otros mods)
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableRescaleNormal();
        }
        
        // Texto de ejemplo "30s"
        String tiempoEjemplo = EnumChatFormatting.YELLOW + "30s";
        int tiempoWidth = this.fontRendererObj.getStringWidth(tiempoEjemplo);
        this.fontRendererObj.drawStringWithShadow(
            tiempoEjemplo,
            scaledX - tiempoWidth / 2,
            scaledY + itemSize / 2 + 5,
            0xFFFFFF
        );
        
        // SIEMPRE restaurar el estado de OpenGL
        GlStateManager.popMatrix();
        
        // Indicador de que se puede arrastrar
        if (isHoveringOverCounter(mouseX, mouseY, guiPosX, guiPosY, itemSize)) {
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
     * Verifica si el mouse está sobre el contador
     */
    private boolean isHoveringOverCounter(int mouseX, int mouseY, int counterX, int counterY, int size) {
        int halfSize = size / 2;
        return mouseX >= counterX - halfSize && mouseX <= counterX + halfSize &&
               mouseY >= counterY - halfSize && mouseY <= counterY + halfSize;
    }
    
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        
        if (mouseButton == 0) { // Click izquierdo
            net.minecraft.client.gui.ScaledResolution scaled = new net.minecraft.client.gui.ScaledResolution(mc);
            int screenWidth = scaled.getScaledWidth();
            int screenHeight = scaled.getScaledHeight();
            
            int posX = (tempX < 0) ? 10 : tempX;
            int posY = (tempY < 0) ? screenHeight - 50 : tempY;
            
            int guiPosX = (int)((posX / (double)scaled.getScaledWidth()) * this.width);
            int guiPosY = (int)((posY / (double)scaled.getScaledHeight()) * this.height);
            
            if (isHoveringOverCounter(mouseX, mouseY, guiPosX, guiPosY, 32)) {
                isDragging = true;
                dragOffsetX = mouseX - guiPosX;
                dragOffsetY = mouseY - guiPosY;
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
            net.minecraft.client.gui.ScaledResolution scaled = new net.minecraft.client.gui.ScaledResolution(mc);
            
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
                tempScale = Math.min(3.0f, tempScale + 0.1f);
            } else {
                // Scroll down - disminuir escala
                tempScale = Math.max(0.5f, tempScale - 0.1f);
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
        return false;
    }
}

