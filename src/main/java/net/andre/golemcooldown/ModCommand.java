package net.andre.golemcooldown;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class ModCommand extends CommandBase {
    @Override
    public String getCommandName() {
        return "golemcooldown";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/golemcooldown <test|info|start|reset>";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            mostrarAyuda(sender);
            return;
        }

        String subcomando = args[0].toLowerCase();

        switch (subcomando) {
            case "test":
                // Iniciar cooldown de prueba (30 segundos)
                if (GolemCooldownMod.cooldownManager != null) {
                    GolemCooldownMod.cooldownManager.iniciarCooldown("Golem", 30);
                    sender.addChatMessage(new ChatComponentText(
                        EnumChatFormatting.GREEN + "[GolemCooldown] " + 
                        EnumChatFormatting.YELLOW + "Cooldown de prueba iniciado: Kit Golem 30s"
                    ));
                }
                break;

            case "info":
                mostrarInfo(sender);
                break;

            case "start":
                if (args.length < 3) {
                    sender.addChatMessage(new ChatComponentText(
                        EnumChatFormatting.RED + "Uso: /golemcooldown start <kit> <segundos>"
                    ));
                    return;
                }
                try {
                    String kit = args[1];
                    int segundos = Integer.parseInt(args[2]);
                    if (GolemCooldownMod.cooldownManager != null) {
                        GolemCooldownMod.cooldownManager.iniciarCooldown(kit, segundos);
                        sender.addChatMessage(new ChatComponentText(
                            EnumChatFormatting.GREEN + "[GolemCooldown] " + 
                            EnumChatFormatting.YELLOW + "Cooldown iniciado: Kit " + kit + " " + segundos + "s"
                        ));
                    }
                } catch (NumberFormatException e) {
                    sender.addChatMessage(new ChatComponentText(
                        EnumChatFormatting.RED + "Error: " + args[2] + " no es un número válido"
                    ));
                }
                break;

            case "reset":
                if (GolemCooldownMod.cooldownManager != null) {
                    GolemCooldownMod.cooldownManager.resetear();
                    sender.addChatMessage(new ChatComponentText(
                        EnumChatFormatting.GREEN + "[GolemCooldown] " + 
                        EnumChatFormatting.YELLOW + "Cooldown reseteado"
                    ));
                }
                break;

            default:
                mostrarAyuda(sender);
                break;
        }
    }

    private void mostrarAyuda(ICommandSender sender) {
        sender.addChatMessage(new ChatComponentText(
            EnumChatFormatting.GOLD + "=== Golem Cooldown Counter ==="
        ));
        sender.addChatMessage(new ChatComponentText(
            EnumChatFormatting.YELLOW + "/golemcooldown test" + EnumChatFormatting.WHITE + " - Iniciar cooldown de prueba (30s)"
        ));
        sender.addChatMessage(new ChatComponentText(
            EnumChatFormatting.YELLOW + "/golemcooldown start <kit> <segundos>" + EnumChatFormatting.WHITE + " - Iniciar cooldown manual"
        ));
        sender.addChatMessage(new ChatComponentText(
            EnumChatFormatting.YELLOW + "/golemcooldown info" + EnumChatFormatting.WHITE + " - Ver información del estado"
        ));
        sender.addChatMessage(new ChatComponentText(
            EnumChatFormatting.YELLOW + "/golemcooldown reset" + EnumChatFormatting.WHITE + " - Resetear todos los cooldowns"
        ));
    }

    private void mostrarInfo(ICommandSender sender) {
        if (GolemCooldownMod.cooldownManager == null) {
            sender.addChatMessage(new ChatComponentText(
                EnumChatFormatting.RED + "[GolemCooldown] Manager no inicializado"
            ));
            return;
        }

        String kit = GolemCooldownMod.cooldownManager.getKitActual();
        int cooldown = GolemCooldownMod.cooldownManager.getCooldownActual();

        sender.addChatMessage(new ChatComponentText(
            EnumChatFormatting.GOLD + "=== Estado del Mod ==="
        ));
        sender.addChatMessage(new ChatComponentText(
            EnumChatFormatting.YELLOW + "Kit actual: " + EnumChatFormatting.WHITE + (kit != null && !kit.isEmpty() ? kit : "Ninguno")
        ));
        sender.addChatMessage(new ChatComponentText(
            EnumChatFormatting.YELLOW + "Cooldown: " + EnumChatFormatting.WHITE + (cooldown > 0 ? cooldown + "s" : "0s (inactivo)")
        ));
        
        // Mostrar scoreboard actual
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld != null && mc.theWorld.getScoreboard() != null) {
            sender.addChatMessage(new ChatComponentText(
                EnumChatFormatting.GRAY + "--- Scoreboard detectado ---"
            ));
        } else {
            sender.addChatMessage(new ChatComponentText(
                EnumChatFormatting.RED + "Scoreboard no disponible"
            ));
        }
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0; // Todos pueden usar el comando
    }
}

