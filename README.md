# Golem Cooldown Counter Mod

Mod de Forge para Minecraft 1.8.9 que muestra el cooldown del kit leyendo directamente del scoreboard.

## Características

- Lee el cooldown del scoreboard (formato: "Kit: Golem (28s)")
- Soporta múltiples kits con cooldowns diferentes
- Mantiene el estado del cooldown al salir y volver a entrar
- Contador visual personalizable (posición y tamaño)
- Detección automática desde el chat cuando se usa una habilidad
- Comandos para testing y control manual

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

### Comandos

- `/golemcooldown` - Muestra la ayuda
- `/golemcooldown test` - Inicia un cooldown de prueba (30s)
- `/golemcooldown start <kit> <segundos>` - Inicia un cooldown manual
- `/golemcooldown info` - Muestra el estado actual del mod
- `/golemcooldown reset` - Resetea todos los cooldowns

## Cómo funciona

El mod detecta el kit y cooldown de 3 formas:

1. **Desde el scoreboard**: Lee automáticamente cuando aparece "Kit: Golem (30s)"
2. **Desde el chat**: Detecta mensajes como "Pisotón" con tiempos de cooldown
3. **Manual**: Usa `/golemcooldown start` para iniciar un cooldown

El contador solo se muestra cuando hay un cooldown activo (mayor a 0) y un kit detectado.

## Créditos

- **Desarrolladores:** Andre, Kaylo
- **Inspiración:** Basado en el mod "Cooldowns" de canelex (2017)
