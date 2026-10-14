package dev.superroot.core

import java.io.ByteArrayInputStream
import java.util.zip.ZipException
import java.util.zip.ZipInputStream

/**
 * UR-MOD: classifica um pacote de módulo por backend e formato, só por metadados (nomes das entradas e
 * module.prop em memória). Nada é instalado nem extraído para disco.
 * Regras com fonte (kernel/upstream/KernelSU/):
 * - sem module.prop na raiz não é módulo: website/docs/guide/module.md:102; userspace/ksud/src/module.rs:641-644.
 * - metamodule=1|true: userspace/ksud/src/metamodule.rs:19-24; website/docs/guide/metamodule.md:126.
 * - install.sh na raiz é o instalador legado: userspace/ksud/src/installer.sh:331-333.
 * - customize.sh é opcional: website/docs/guide/module.md:271-275.
 * - backend: o formato do KernelSU é quase o mesmo do Magisk (module.md:5), então os metadados não separam
 *   os dois; o único backend com fonte no repo é KSU_COMPATIBLE. Regras de Magisk e APatch (APM, KPM): NÃO
 *   VERIFICADO (docs fora do repo), não entram.
 * Política nossa: entrada com "..", caminho absoluto, barra invertida ou NUL (zip slip) e entrada duplicada
 * tornam o archive malformado.
 */
enum class ModuleBackend { KSU_COMPATIBLE }

enum class ModuleFormat { KSU_MODULE, KSU_METAMODULE, LEGACY_INSTALL_SH }

data class ModulePackageInfo(
    val backend: ModuleBackend,
    val format: ModuleFormat,
    val prop: ModuleProp,
    val hasCustomizeScript: Boolean,
)

sealed interface ClassifyResult {
    data class Ok(val info: ModulePackageInfo) : ClassifyResult
    data class Malformed(val reason: String) : ClassifyResult
}

object ModulePackageClassifier {
    private fun unsafe(name: String): Boolean =
        name.startsWith("/") || name.contains('\\') || name.contains('\u0000') || name.split('/').any { it == ".." }

    fun classify(entries: List<String>, moduleProp: String?): ClassifyResult {
        if (entries.any { unsafe(it) }) return ClassifyResult.Malformed("UNSAFE_ENTRY")
        if (entries.size != entries.toSet().size) return ClassifyResult.Malformed("DUPLICATE_ENTRY")
        if ("module.prop" !in entries || moduleProp == null) return ClassifyResult.Malformed("NO_MODULE_PROP")
        val prop = when (val r = ModulePropParser.parse(moduleProp)) {
            is ModulePropResult.Ok -> r.prop
            is ModulePropResult.Malformed -> return ClassifyResult.Malformed("MODULE_PROP:${r.reason}")
        }
        val format = when {
            prop.metamodule -> ModuleFormat.KSU_METAMODULE
            "install.sh" in entries -> ModuleFormat.LEGACY_INSTALL_SH
            else -> ModuleFormat.KSU_MODULE
        }
        return ClassifyResult.Ok(ModulePackageInfo(ModuleBackend.KSU_COMPATIBLE, format, prop, "customize.sh" in entries))
    }

    /** Lê o diretório do zip em memória; bytes que não são zip resultam em zero entradas (NO_MODULE_PROP). */
    fun classifyZip(bytes: ByteArray): ClassifyResult {
        val names = mutableListOf<String>()
        var prop: String? = null
        try {
            ZipInputStream(ByteArrayInputStream(bytes)).use { z ->
                while (true) {
                    val e = z.nextEntry ?: break
                    names += e.name
                    val data = z.readBytes()
                    if (e.name == "module.prop") prop = String(data, Charsets.UTF_8)
                }
            }
        } catch (_: ZipException) {
            return ClassifyResult.Malformed("ZIP_INVALID")
        } catch (_: java.io.EOFException) {
            return ClassifyResult.Malformed("ZIP_INVALID")
        }
        return classify(names, prop)
    }
}
