# Instrucciones para Compilar el Mod

## Requisitos Previos

1. **Java JDK 8** (importante: debe ser JDK 8, no una versión más nueva)
   - Descarga: https://www.oracle.com/java/technologies/javase/javase8-archive-downloads.html
   - O usa OpenJDK 8: https://adoptium.net/temurin/releases/?version=8

2. **Forge 1.8.9** (versión 11.15.1.2318)
   - El build.gradle lo descargará automáticamente

## Pasos para Compilar

### Opción 1: Usando Gradle Wrapper (Recomendado)

1. Abre PowerShell o CMD en la carpeta `GolemCooldownMod`

2. Ejecuta:
   ```bash
   gradlew.bat build
   ```

3. Si es la primera vez, Gradle descargará las dependencias (puede tardar varios minutos)

4. El JAR compilado estará en: `build\libs\GolemCooldownMod-1.0.0.jar`

### Opción 2: Usando Gradle instalado

Si tienes Gradle instalado globalmente:

```bash
gradle build
```

## Instalación del Mod

1. Copia el JAR compilado (`GolemCooldownMod-1.0.0.jar`) a la carpeta `mods` de tu instalación de Minecraft

2. La ruta típica es:
   ```
   C:\Users\TuUsuario\AppData\Roaming\.minecraft\mods\
   ```

3. Asegúrate de tener Forge 1.8.9 instalado en tu cliente

4. Inicia Minecraft con el perfil de Forge

## Solución de Problemas

### Error: "JAVA_HOME is not set"
- Configura la variable de entorno JAVA_HOME apuntando a tu instalación de JDK 8
- Ejemplo: `JAVA_HOME=C:\Program Files\Java\jdk1.8.0_XXX`

### Error: "Could not find or load main class"
- Asegúrate de estar usando JDK 8, no una versión más nueva

### Error al compilar
- Verifica que tienes conexión a internet (Gradle necesita descargar dependencias)
- Asegúrate de estar en la carpeta correcta del proyecto

## Notas

- La primera compilación puede tardar 5-10 minutos mientras descarga dependencias
- Las compilaciones siguientes serán mucho más rápidas
- Si cambias el código, solo necesitas ejecutar `gradlew.bat build` de nuevo

