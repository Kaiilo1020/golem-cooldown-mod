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
 * Solo crea/modifica: config/cooldownanni.cfg
 */
@Mod(modid = CooldownAnni.MODID, name = CooldownAnni.NAME, version = CooldownAnni.VERSION, acceptedMinecraftVersions = "[1.8.9]", clientSideOnly = true)
public class CooldownAnni {
    public static final String MODID = "cooldownanni";
    public static final String NAME = "CooldownAnni";
    public static final String VERSION = "2.0";


    // Sistema principal (scoreboard)
    public static ScoreboardCooldownManager scoreboardCooldownManager;
    public static KitRenderManager kitRenderManager;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        // Inicializar configuración
        ConfigManager.init(event.getModConfigurationDirectory());
        
        // Sistema principal (scoreboard)
        scoreboardCooldownManager = new ScoreboardCooldownManager();
        kitRenderManager = new KitRenderManager();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        // Sistema principal (scoreboard)
        // scoreboardCooldownManager se registra automáticamente con @SubscribeEvent
        MinecraftForge.EVENT_BUS.register(scoreboardCooldownManager);
        MinecraftForge.EVENT_BUS.register(kitRenderManager);
        
        // Comandos
        ClientCommandHandler.instance.registerCommand(new CommandCooldown());
    }
}

