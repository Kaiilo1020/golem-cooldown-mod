package net.andre.golemcooldown;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

/**
 * Comando /cl con subcomandos: sin argumentos abre menú, con argumentos ejecuta acciones
 */
public class CommandCooldown extends CommandBase {
    @Override
    public String getCommandName() {
        return "cl";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/cl [info|reset|help]";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        // Sin argumentos: abrir menú principal
        if (args.length == 0) {
            net.minecraft.client.Minecraft.getMinecraft().displayGuiScreen(new GuiMainMenu());
            return;
        }

        String subcomando = args[0].toLowerCase();

        switch (subcomando) {
            case "info":
                mostrarInfo(sender);
                break;

            case "reset":
                if (CooldownAnni.scoreboardCooldownManager != null) {
                    CooldownAnni.scoreboardCooldownManager.resetear();
                    sender.addChatMessage(new ChatComponentText(
                        EnumChatFormatting.GREEN + "[CooldownAnni] " + 
                        EnumChatFormatting.YELLOW + "Cooldown reseteado"
                    ));
                } else {
                    sender.addChatMessage(new ChatComponentText(
                        EnumChatFormatting.RED + "[CooldownAnni] Manager no inicializado"
                    ));
                }
                break;

            case "help":
                mostrarAyuda(sender);
                break;

            default:
                // Si no es un subcomando reconocido, abrir menú (comportamiento por defecto)
                net.minecraft.client.Minecraft.getMinecraft().displayGuiScreen(new GuiMainMenu());
                break;
        }
    }

    private void mostrarAyuda(ICommandSender sender) {
        sender.addChatMessage(new ChatComponentText(
            EnumChatFormatting.GOLD + "=== CooldownAnni ==="
        ));
        sender.addChatMessage(new ChatComponentText(
            EnumChatFormatting.YELLOW + "/cl" + EnumChatFormatting.WHITE + " - Abrir menú de configuración"
        ));
        sender.addChatMessage(new ChatComponentText(
            EnumChatFormatting.YELLOW + "/cl info" + EnumChatFormatting.WHITE + " - Ver información del estado actual"
        ));
        sender.addChatMessage(new ChatComponentText(
            EnumChatFormatting.YELLOW + "/cl reset" + EnumChatFormatting.WHITE + " - Resetear el cooldown actual"
        ));
        sender.addChatMessage(new ChatComponentText(
            EnumChatFormatting.GRAY + "El cooldown se detecta automáticamente desde el scoreboard."
        ));
    }

    private void mostrarInfo(ICommandSender sender) {
        if (CooldownAnni.scoreboardCooldownManager == null) {
            sender.addChatMessage(new ChatComponentText(
                EnumChatFormatting.RED + "[CooldownAnni] Manager no inicializado"
            ));
            return;
        }

        String kit = CooldownAnni.scoreboardCooldownManager.getKitActual();
        int cooldown = CooldownAnni.scoreboardCooldownManager.getCooldownActual();
        int cooldownMax = CooldownAnni.scoreboardCooldownManager.getCooldownMaximo();
        boolean tieneCooldownActivo = CooldownAnni.scoreboardCooldownManager.tieneCooldownActivo();

        sender.addChatMessage(new ChatComponentText(
            EnumChatFormatting.GOLD + "=== Estado del Mod ==="
        ));
        sender.addChatMessage(new ChatComponentText(
            EnumChatFormatting.YELLOW + "Kit actual: " + EnumChatFormatting.WHITE + (kit != null && !kit.isEmpty() ? kit : "Ninguno detectado")
        ));
        
        if (tieneCooldownActivo) {
            sender.addChatMessage(new ChatComponentText(
                EnumChatFormatting.YELLOW + "Cooldown: " + EnumChatFormatting.WHITE + cooldown + "s" + 
                (cooldownMax > 0 ? " / " + cooldownMax + "s" : "")
            ));
        } else {
            sender.addChatMessage(new ChatComponentText(
                EnumChatFormatting.GRAY + "Cooldown: " + EnumChatFormatting.WHITE + "0s (inactivo)"
            ));
        }
        
        // Mostrar scoreboard actual
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld != null && mc.theWorld.getScoreboard() != null) {
            sender.addChatMessage(new ChatComponentText(
                EnumChatFormatting.GREEN + "✓ Scoreboard disponible"
            ));
        } else {
            sender.addChatMessage(new ChatComponentText(
                EnumChatFormatting.RED + "✗ Scoreboard no disponible"
            ));
        }
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0; // Todos pueden usar el comando
    }
}

