# Golem Cooldown Counter Mod

Mod de Forge para Minecraft 1.8.9 que muestra el cooldown del kit leyendo directamente del scoreboard.

## Características

- Lee el cooldown del scoreboard (formato: "Kit: Golem (28s)")
- Soporta múltiples kits con cooldowns diferentes
- Mantiene el estado del cooldown al salir y volver a entrar
- Contador visual personalizable (posición y tamaño)
- Basado en el mod Cooldowns de canelex

## Instalación

1. Instala Forge 1.8.9 (versión 11.15.1.2318)
2. Compila el mod (ver instrucciones abajo)
3. Coloca el JAR en la carpeta `mods` de tu instalación de Minecraft

## Compilación

### Requisitos
- Java JDK 8
- Gradle (se descarga automáticamente)

### Pasos

1. Abre una terminal en la carpeta del proyecto
2. Ejecuta: `gradlew build`
3. El JAR compilado estará en `build/libs/GolemCooldownMod-1.0.0.jar`

## Uso

El mod funciona automáticamente. Lee el scoreboard y muestra el cooldown del kit actual.

## Comandos

- `/golemhud` - Activa/desactiva el modo edición para mover el contador
- `/golemstatus` - Muestra el estado actual del cooldown

## Créditos

- **Desarrolladores:** Andre, Kaylo
- **Inspiración:** Basado en el mod "Cooldowns" de canelex (2017)

