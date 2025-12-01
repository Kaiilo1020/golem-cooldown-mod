package net.andre.golemcooldown;

import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 * Mod de contador de cooldown para kits en "Destruye el Nexus"
 * 
 * IMPORTANTE: Este mod NO modifica:
 * - Configuraciones de video (zoom, render distance, etc.)
 * - Controles/keybindings
 * - Configuraciones de Feather Client
 * - Configuraciones de otros mods
 * - gameSettings de Minecraft
 * 
 * Solo crea/modifica: config/golemcooldown.cfg
 */
@Mod(modid = GolemCooldownMod.MODID, name = GolemCooldownMod.NAME, version = GolemCooldownMod.VERSION, acceptedMinecraftVersions = "[1.8.9]")
public class GolemCooldownMod {
    public static final String MODID = "golemcooldown";
    public static final String NAME = "Golem Cooldown Counter";
    public static final String VERSION = "1.0.0";

    @Mod.Instance(MODID)
    public static GolemCooldownMod instance;

    // Sistema principal (scoreboard)
    public static ScoreboardCooldownManager scoreboardCooldownManager;
    public static KitRenderManager kitRenderManager;
    
    // Sistema antiguo (mantener para compatibilidad)
    public static CooldownManager cooldownManager;
    public static RenderManager renderManager;
    public static ItemCooldownManager itemCooldownManager;
    public static ItemRenderManager itemRenderManager;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        // Inicializar configuración
        ConfigManager.init(event.getModConfigurationDirectory());
        
        // Sistema principal (scoreboard)
        scoreboardCooldownManager = new ScoreboardCooldownManager();
        kitRenderManager = new KitRenderManager();
        
        // Sistema antiguo (mantener para compatibilidad)
        cooldownManager = new CooldownManager();
        renderManager = new RenderManager();
        itemCooldownManager = new ItemCooldownManager();
        itemRenderManager = new ItemRenderManager();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        // Sistema principal (scoreboard)
        // scoreboardCooldownManager se registra automáticamente con @SubscribeEvent
        MinecraftForge.EVENT_BUS.register(scoreboardCooldownManager);
        MinecraftForge.EVENT_BUS.register(kitRenderManager);
        
        // Sistema antiguo (mantener para compatibilidad)
        MinecraftForge.EVENT_BUS.register(new ModEventHandler());
        MinecraftForge.EVENT_BUS.register(renderManager);
        MinecraftForge.EVENT_BUS.register(new ItemCooldownEventHandler(itemCooldownManager));
        MinecraftForge.EVENT_BUS.register(itemRenderManager);
        
        // Comandos
        ClientCommandHandler.instance.registerCommand(new ModCommand());
        ClientCommandHandler.instance.registerCommand(new CommandCooldown());
    }
}

