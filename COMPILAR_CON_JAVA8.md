# Solución: Compilar con Java 8

El error que estás viendo es porque tienes **Java 21** instalado, pero **Gradle 4.9 y ForgeGradle 2.3 requieren Java 8**.

## Solución 1: Instalar Java 8 (Recomendado)

1. **Descarga Java 8 (JDK 8):**
   - Opción 1 (Gratis): https://adoptium.net/temurin/releases/?version=8
   - Opción 2: https://www.oracle.com/java/technologies/javase/javase8-archive-downloads.html

2. **Instala Java 8** (puedes tener múltiples versiones instaladas)

3. **Configura Gradle para usar Java 8:**
   
   Crea o edita el archivo `gradle.properties` en la carpeta del proyecto y agrega:
   ```
   org.gradle.java.home=C:/Program Files/Java/jdk1.8.0_XXX
   ```
   (Reemplaza XXX con tu versión específica de Java 8)

4. **Encuentra la ruta de Java 8:**
   ```powershell
   dir "C:\Program Files\Java\" | Select-Object Name
   ```
   O busca en: `C:\Program Files\Eclipse Adoptium\` si usaste Adoptium

## Solución 2: Usar JAVA_HOME temporalmente

Si tienes Java 8 instalado pero no está configurado:

1. **Encuentra la ruta de Java 8:**
   ```powershell
   dir "C:\Program Files\Java\" | Select-Object Name
   ```

2. **Ejecuta Gradle con Java 8:**
   ```powershell
   $env:JAVA_HOME="C:\Program Files\Java\jdk1.8.0_XXX"
   .\gradlew.bat build
   ```

## Solución 3: Usar un IDE (IntelliJ IDEA o Eclipse)

Los IDEs pueden configurar automáticamente qué versión de Java usar para cada proyecto.

### IntelliJ IDEA:
1. File → Project Structure → Project
2. SDK: Selecciona Java 8
3. Build → Build Project

### Eclipse:
1. Project → Properties → Java Build Path
2. Libraries → JRE System Library → Edit → Java 8

## Verificar qué versiones de Java tienes

```powershell
dir "C:\Program Files\Java\" | Select-Object Name
dir "C:\Program Files\Eclipse Adoptium\" | Select-Object Name
```

## Nota Importante

**Forge 1.8.9 y ForgeGradle 2.3 SOLO funcionan con Java 8**. No hay forma de hacerlos funcionar con Java 21 sin cambiar completamente la herramienta de build.

Si realmente no puedes instalar Java 8, la única alternativa sería:
- Usar una versión más nueva de Minecraft (1.12.2+) que soporte Java más reciente
- O usar el código JavaScript de ChatTriggers que ya funciona

