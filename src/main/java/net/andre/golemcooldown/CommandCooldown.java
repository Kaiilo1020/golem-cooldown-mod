package net.andre.golemcooldown;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

/**
 * Comando /cl para abrir el menú principal
 */
public class CommandCooldown extends CommandBase {
    @Override
    public String getCommandName() {
        return "cl";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/cl";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        net.minecraft.client.Minecraft.getMinecraft().displayGuiScreen(new GuiMainMenu());
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0; // Todos pueden usar el comando
    }
}

