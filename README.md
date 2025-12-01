# Golem Cooldown Counter Mod

Mod de Forge para Minecraft 1.8.9 que muestra el cooldown del kit activo leyendo directamente del scoreboard en el modo de juego "Destruye el Nexus".

## 📋 Características

- ✅ Detecta automáticamente el kit activo desde el scoreboard
- ✅ Muestra el cooldown restante en tiempo real
- ✅ Soporta múltiples kits con diferentes cooldowns
- ✅ Interfaz visual personalizable (posición y tamaño)
- ✅ Mantiene el estado del cooldown al salir y volver a entrar
- ✅ El contador desaparece cuando llega a 0 y reaparece al usar la habilidad

## 🚀 Instalación

### Requisitos Previos

1. **Minecraft 1.8.9** con **Forge 1.8.9** (versión 11.15.1.2318)
2. **Java JDK 8** (importante: debe ser JDK 8, no una versión más nueva)

### Pasos de Instalación

1. **Compilar el mod:**
   ```bash
   cd GolemCooldownMod
   gradlew.bat build
   ```

2. **Encontrar el JAR compilado:**
   - El archivo estará en: `build/libs/GolemCooldownMod-1.0.0.jar`

3. **Instalar el mod:**
   - Copia el JAR a la carpeta `mods` de tu instalación de Minecraft
   - Ruta típica: `C:\Users\TuUsuario\AppData\Roaming\.minecraft\mods\`

4. **Iniciar Minecraft:**
   - Abre Minecraft con el perfil de Forge 1.8.9
   - El mod debería aparecer en la lista de mods cargados

## 📖 Guía de Uso Paso a Paso

### Paso 1: Entrar a una Partida

1. Inicia Minecraft con el mod instalado
2. Únete a un servidor con el modo "Destruye el Nexus"
3. Entra a una partida

### Paso 2: Abrir el Menú de Configuración

1. **Presiona `T` para abrir el chat**
2. **Escribe el comando:** `/cl`
3. **Presiona `Enter`**

**¿Qué verás?**
- Se abrirá un menú con fondo oscuro
- En la parte superior verás el título: **"Golem Cooldown Counter"** en color dorado
- En el centro verás dos botones:
  - **"Delete Cooldown"** (botón rojo) - arriba
  - **"Create Cooldown"** (botón verde) - abajo
- En la parte inferior verás un botón **"Close"**

### Paso 3: Configurar el Contador

1. **Haz clic en el botón "Create Cooldown"** (botón verde)

**¿Qué verás?**
- Se abrirá una nueva pantalla de configuración
- En la parte superior izquierda verás información:
  - **Kit detectado:** El nombre del kit que tienes actualmente (ej: "Golem")
  - **Item ID:** El ID numérico del item del kit (ej: 265)
  - **Posición:** Las coordenadas X e Y actuales del contador
  - **Escala:** El tamaño actual del contador (1.0 = tamaño normal)
- En el centro de la pantalla verás un **contador de prueba** que muestra:
  - El **icono del item** de tu kit (o un **"?"** amarillo si no se detecta el kit)
  - El texto **"30s"** debajo del icono (ejemplo de cooldown)
  - Un **fondo amarillo semi-transparente** alrededor del contador

### Paso 4: Mover el Contador

1. **Haz clic izquierdo** sobre el contador de prueba (el icono del item o el "?")
2. **Mantén presionado** y **arrastra** el contador a la posición que desees
3. **Suelta el clic** cuando esté en la posición deseada

**¿Qué observarás?**
- El contador se moverá siguiendo tu cursor
- La información de "Posición" en la parte superior se actualizará en tiempo real
- El contador tiene un fondo amarillo para que sea fácil de ver mientras lo mueves

### Paso 5: Cambiar el Tamaño del Contador

1. **Mantén el cursor sobre el contador**
2. **Usa la rueda del mouse:**
   - **Hacia arriba** = Aumentar tamaño
   - **Hacia abajo** = Reducir tamaño

**¿Qué observarás?**
- El contador se agrandará o achicará según gires la rueda
- La información de "Escala" se actualizará (ej: 1.0, 1.1, 1.2, etc.)
- El tamaño mínimo es 0.1x y el máximo es 5.0x

### Paso 6: Guardar la Configuración

1. **Haz clic en el botón "Save"** (botón verde en la parte inferior)
2. Verás un mensaje en el chat: **"[GolemCooldown] Configuración guardada"**
3. Se cerrará la pantalla de configuración y volverás al menú principal

**Alternativa:**
- Si no quieres guardar los cambios, haz clic en **"Back"** para volver sin guardar

### Paso 7: Cerrar el Menú

1. **Haz clic en "Close"** o presiona **`ESC`**

## 🎮 Uso Durante la Partida

### Funcionamiento Automático

Una vez configurado, el mod funciona automáticamente:

1. **El mod detecta tu kit** desde el scoreboard (ej: "Kit: Golem (30s)")
2. **Cuando usas una habilidad** (click derecho con el item del kit):
   - El contador aparece automáticamente en la posición que configuraste
   - Muestra el icono del item del kit
   - Muestra el tiempo restante (ej: "30s", "29s", "28s"...)
   - Incluye una barra de progreso que cambia de color:
     - **Verde** cuando queda más del 50% del tiempo
     - **Amarillo** cuando queda entre 25% y 50%
     - **Rojo** cuando queda menos del 25%

3. **Cuando el cooldown llega a 0:**
   - El contador desaparece completamente
   - Solo reaparecerá cuando uses la habilidad nuevamente

### Ejemplo Visual

```
┌─────────┐
│  [Item] │  ← Icono del item del kit
│   30s   │  ← Tiempo restante
│ ████████│  ← Barra de progreso (verde/amarillo/rojo)
└─────────┘
```

## ⚙️ Comandos Adicionales

### `/cl`
- Abre el menú principal de configuración
- **Uso:** `/cl`

### `/golemcooldown`
- Comando de prueba y debugging (sistema antiguo)
- **Subcomandos:**
  - `/golemcooldown test` - Inicia un cooldown de prueba (30s)
  - `/golemcooldown info` - Muestra el estado actual del mod
  - `/golemcooldown start <kit> <segundos>` - Inicia un cooldown manual
  - `/golemcooldown reset` - Resetea todos los cooldowns

## 🔧 Configuración de Kits

El mod mapea automáticamente los kits a sus items. La configuración está en `KitMapper.java`:

```java
// Ejemplo: Kit Golem usa Iron Ingot (ID 265)
KIT_TO_ITEM_ID.put("Golem", Item.getIdFromItem(Items.iron_ingot));
```

Para agregar más kits, edita `KitMapper.java` y agrega el mapeo correspondiente.

## 📝 Notas Importantes

- **El contador solo aparece cuando hay un cooldown activo** (mayor a 0)
- **La posición y escala son globales** - se aplican a todos los kits
- **El contador desaparece cuando:**
  - El cooldown llega a 0
  - Termina la partida
  - Mueres (según configuración del servidor)
- **El mod NO modifica:**
  - Configuraciones de video
  - Controles/keybindings
  - Configuraciones de Feather Client u otros mods
  - Solo crea/modifica: `config/golemcooldown.cfg`

## 🐛 Solución de Problemas

### El contador no aparece
1. Verifica que tengas un kit activo en el scoreboard
2. Verifica que el kit tenga un cooldown configurado
3. Usa `/golemcooldown info` para ver el estado del mod

### El contador aparece en el lugar incorrecto
1. Usa `/cl` → "Create Cooldown"
2. Arrastra el contador a la posición deseada
3. Guarda con "Save"

### El kit no se detecta
1. Verifica que el scoreboard muestre "Kit: [Nombre]"
2. Verifica que el kit esté configurado en `KitMapper.java`
3. El contador mostrará "?" si el kit no está mapeado

## 👥 Créditos

- **Desarrolladores:** Andre, Kaylo
- **Inspiración:** Basado en el mod "Cooldowns" de canelex (2017)
- **Versión:** 1.0.0
- **Minecraft:** 1.8.9
- **Forge:** 11.15.1.2318

## 📄 Licencia

Este mod es de código abierto. Siéntete libre de modificarlo y usarlo según tus necesidades.

---

**¿Necesitas ayuda?** Abre un issue en el repositorio de GitHub.
