package net.andre.golemcooldown;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

/**
 * Menú principal del mod (/cl)
 * Muestra botones: "Delete Cooldown" y "Create Cooldown"
 */
public class GuiMainMenu extends GuiScreen {
    private GuiButton btnDeleteCooldown;
    private GuiButton btnCreateCooldown;
    private GuiButton btnClose;
    
    @Override
    public void initGui() {
        super.initGui();
        
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        int buttonWidth = 200;
        int buttonHeight = 20;
        int spacing = 10;
        
        // Botón "Delete Cooldown"
        this.buttonList.add(this.btnDeleteCooldown = new GuiButton(
            1,
            centerX - buttonWidth / 2,
            centerY - buttonHeight - spacing / 2,
            buttonWidth,
            buttonHeight,
            EnumChatFormatting.RED + "Delete Cooldown"
        ));
        
        // Botón "Create Cooldown"
        this.buttonList.add(this.btnCreateCooldown = new GuiButton(
            2,
            centerX - buttonWidth / 2,
            centerY + spacing / 2,
            buttonWidth,
            buttonHeight,
            EnumChatFormatting.GREEN + "Create Cooldown"
        ));
        
        // Botón "Close"
        this.buttonList.add(this.btnClose = new GuiButton(
            3,
            centerX - buttonWidth / 2,
            this.height - 30,
            buttonWidth,
            buttonHeight,
            "Close"
        ));
    }
    
    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 1) {
            // Delete Cooldown - Resetear configuración
            ConfigManager.reset();
            this.mc.thePlayer.addChatMessage(
                new net.minecraft.util.ChatComponentText(
                    EnumChatFormatting.GREEN + "[GolemCooldown] " + 
                    EnumChatFormatting.YELLOW + "Configuración reseteada"
                )
            );
            this.mc.displayGuiScreen(null);
        } else if (button.id == 2) {
            // Create Cooldown - Abrir interfaz de configuración
            this.mc.displayGuiScreen(new GuiCreateCooldown(this));
        } else if (button.id == 3) {
            // Close
            this.mc.displayGuiScreen(null);
        }
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        
        // Título
        String title = EnumChatFormatting.GOLD + EnumChatFormatting.BOLD.toString() + 
                      "Golem Cooldown Counter";
        int titleWidth = this.fontRendererObj.getStringWidth(title);
        this.fontRendererObj.drawStringWithShadow(
            title,
            (this.width - titleWidth) / 2,
            30,
            0xFFFFFF
        );
        
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
    
    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            this.mc.displayGuiScreen(null);
        } else {
            super.keyTyped(typedChar, keyCode);
        }
    }
    
    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}

