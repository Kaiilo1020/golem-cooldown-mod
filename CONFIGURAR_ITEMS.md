# Cómo Configurar Cooldowns por Item ID

## 📋 Resumen

El mod ahora identifica items por su **ID numérico de Minecraft** y detecta cuando haces **click derecho** con ese item para iniciar el cooldown automáticamente.

## 🔢 Lista de IDs de Items

Consulta la lista completa de IDs en: **https://minecraft-ids.grahamedgecombe.com/**

## ⚙️ Cómo Configurar

### Paso 1: Identificar el Item ID

1. Ve a https://minecraft-ids.grahamedgecombe.com/
2. Busca el item que quieres configurar
3. Anota el **número** (ID) del item

**Ejemplo:**
- Iron Sword = **267**
- Diamond Sword = **276**
- Bow = **261**

### Paso 2: Editar el Código

Abre el archivo: `ItemCooldownManager.java`

Busca la sección (líneas 30-45):

```java
static {
    // ============================================
    // CONFIGURACIÓN DE COOLDOWNS POR ITEM ID
    // ============================================
    
    // Agregar tus items aquí:
    COOLDOWNS_POR_ITEM_ID.put(267, 30);  // Iron Sword - 30 segundos
    COOLDOWNS_POR_ITEM_ID.put(276, 25);  // Diamond Sword - 25 segundos
}
```

### Paso 3: Agregar tu Item

Agrega una línea por cada item que quieras configurar:

```java
COOLDOWNS_POR_ITEM_ID.put(ITEM_ID, SEGUNDOS);
```

**Ejemplo completo:**

```java
static {
    // Kit Golem - Item ID 267 (Iron Sword) - 30 segundos
    COOLDOWNS_POR_ITEM_ID.put(267, 30);
    
    // Kit Archer - Item ID 261 (Bow) - 25 segundos
    COOLDOWNS_POR_ITEM_ID.put(261, 25);
    
    // Kit Warrior - Item ID 276 (Diamond Sword) - 20 segundos
    COOLDOWNS_POR_ITEM_ID.put(276, 20);
}
```

## 🎯 Cómo Funciona

1. **Jugador hace click derecho** con un item configurado
2. **Mod detecta el click** y obtiene el ID del item
3. **Mod busca el cooldown** en la lista configurada
4. **Si encuentra cooldown**, inicia el contador automáticamente
5. **Muestra el item** con su tiempo restante en pantalla

## 📝 Ejemplos de IDs Comunes

| Item | ID | Ejemplo de Uso |
|------|-----|----------------|
| Iron Sword | 267 | `COOLDOWNS_POR_ITEM_ID.put(267, 30);` |
| Diamond Sword | 276 | `COOLDOWNS_POR_ITEM_ID.put(276, 35);` |
| Bow | 261 | `COOLDOWNS_POR_ITEM_ID.put(261, 25);` |
| Wooden Sword | 268 | `COOLDOWNS_POR_ITEM_ID.put(268, 20);` |
| Stone Sword | 272 | `COOLDOWNS_POR_ITEM_ID.put(272, 22);` |
| Golden Sword | 283 | `COOLDOWNS_POR_ITEM_ID.put(283, 18);` |

## ⚠️ Notas Importantes

1. **El ID debe ser exacto** - Usa el número de la lista oficial
2. **El cooldown es en segundos** - 30 = 30 segundos
3. **Solo funciona con click derecho** - No detecta click izquierdo
4. **Debes recompilar** el mod después de cambiar la configuración

## 🔄 Después de Configurar

1. Guarda el archivo `ItemCooldownManager.java`
2. Compila el mod: `gradlew build`
3. Copia el nuevo JAR a la carpeta `mods`
4. Prueba haciendo click derecho con el item configurado

## ❓ Preguntas Frecuentes

### ¿Cómo sé qué ID tiene mi item?

1. Usa la lista: https://minecraft-ids.grahamedgecombe.com/
2. O usa el comando `/golemcooldown debug` (si lo implementamos)

### ¿Puedo usar el mismo cooldown para varios items?

Sí, simplemente agrega varias líneas:

```java
COOLDOWNS_POR_ITEM_ID.put(267, 30);  // Item 1
COOLDOWNS_POR_ITEM_ID.put(276, 30);  // Item 2 (mismo cooldown)
```

### ¿Funciona con items personalizados del servidor?

Depende. Si el servidor usa items vanilla con IDs estándar, sí. Si usa items completamente personalizados, puede que necesites otro método.

