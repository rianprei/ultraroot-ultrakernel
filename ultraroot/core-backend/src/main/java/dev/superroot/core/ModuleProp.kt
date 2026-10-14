package dev.superroot.core

/**
 * KSU-MOD: leitura de module.prop.
 * Spec: kernel/upstream/KernelSU/website/docs/guide/module.md:100-121 (campos, regex do id, versionCode
 * inteiro, linha única, só LF). id obrigatório: kernel/upstream/KernelSU/userspace/ksud/src/module.rs:654-655.
 * Regex do id: module.md:116 e module.rs:52. metamodule=1|true: userspace/ksud/src/metamodule.rs:19-24.
 * Política nossa (mais estrita que o ksud): linha sem "=" e chave duplicada são recusadas.
 */
data class ModuleProp(
    val id: String,
    val name: String?,
    val version: String?,
    val versionCode: Long?,
    val author: String?,
    val description: String?,
    val updateJson: String?,
    val metamodule: Boolean,
    val extra: Map<String, String>,
)

sealed interface ModulePropResult {
    data class Ok(val prop: ModuleProp) : ModulePropResult
    data class Malformed(val reason: String) : ModulePropResult
}

object ModulePropParser {
    private val ID = Regex("^[a-zA-Z][a-zA-Z0-9._-]+$")
    private val KNOWN = setOf("id", "name", "version", "versionCode", "author", "description", "updateJson", "metamodule")

    fun parse(text: String): ModulePropResult {
        if (text.contains('\r')) return ModulePropResult.Malformed("CR_LINE_BREAK")
        val map = LinkedHashMap<String, String>()
        for (line in text.split('\n')) {
            if (line.isBlank() || line.trimStart().startsWith("#")) continue
            val eq = line.indexOf('=')
            if (eq < 0) return ModulePropResult.Malformed("NO_SEPARATOR")
            val key = line.substring(0, eq).trim()
            if (key.isEmpty()) return ModulePropResult.Malformed("EMPTY_KEY")
            if (map.put(key, line.substring(eq + 1)) != null) return ModulePropResult.Malformed("DUPLICATE_KEY")
        }
        val id = map["id"]?.trim() ?: return ModulePropResult.Malformed("ID_MISSING")
        if (!ID.matches(id)) return ModulePropResult.Malformed("BAD_ID")
        val versionCode = map["versionCode"]?.let { it.trim().toLongOrNull() ?: return ModulePropResult.Malformed("BAD_VERSION_CODE") }
        val meta = map["metamodule"]?.trim()?.let { it == "1" || it.equals("true", ignoreCase = true) } ?: false
        return ModulePropResult.Ok(
            ModuleProp(
                id = id,
                name = map["name"],
                version = map["version"],
                versionCode = versionCode,
                author = map["author"],
                description = map["description"],
                updateJson = map["updateJson"],
                metamodule = meta,
                extra = map.filterKeys { it !in KNOWN },
            ),
        )
    }
}
