package net.andre.golemcooldown;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = GolemCooldownMod.MODID, name = GolemCooldownMod.NAME, version = GolemCooldownMod.VERSION, acceptedMinecraftVersions = "[1.8.9]")
public class GolemCooldownMod {
    public static final String MODID = "golemcooldown";
    public static final String NAME = "Golem Cooldown Counter";
    public static final String VERSION = "1.0.0";

    @Mod.Instance(MODID)
    public static GolemCooldownMod instance;

    public static CooldownManager cooldownManager;
    public static RenderManager renderManager;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        cooldownManager = new CooldownManager();
        renderManager = new RenderManager();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new ModEventHandler());
        MinecraftForge.EVENT_BUS.register(renderManager);
        ClientCommandHandler.instance.registerCommand(new ModCommand());
    }
}

