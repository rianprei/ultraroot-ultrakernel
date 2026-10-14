package dev.superroot.core

import java.io.ByteArrayOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

// Pacotes sintéticos montados no teste; nada é instalado nem extraído para disco.
class ModulePackageClassifierTest {
    private val prop = "id=m1\nname=M\nversionCode=1\n"
    private fun zip(vararg files: Pair<String, String>): ByteArray {
        val out = ByteArrayOutputStream()
        ZipOutputStream(out).use { z -> for ((n, c) in files) { z.putNextEntry(ZipEntry(n)); z.write(c.toByteArray()); z.closeEntry() } }
        return out.toByteArray()
    }
    private fun info(r: ClassifyResult) = (r as ClassifyResult.Ok).info
    private fun bad(r: ClassifyResult) = (r as ClassifyResult.Malformed).reason

    @Test fun plainKsuModule() {
        val i = info(ModulePackageClassifier.classify(listOf("module.prop", "service.sh"), prop))
        assertEquals(ModuleBackend.KSU_COMPATIBLE, i.backend); assertEquals(ModuleFormat.KSU_MODULE, i.format)
        assertEquals("m1", i.prop.id); assertEquals(false, i.hasCustomizeScript)
    }

    @Test fun metamodule() =
        assertEquals(ModuleFormat.KSU_METAMODULE, info(ModulePackageClassifier.classify(listOf("module.prop"), prop + "metamodule=1\n")).format)

    @Test fun legacyInstallSh() =
        assertEquals(ModuleFormat.LEGACY_INSTALL_SH, info(ModulePackageClassifier.classify(listOf("module.prop", "install.sh"), prop)).format)

    @Test fun customizeScriptFlag() =
        assertTrue(info(ModulePackageClassifier.classify(listOf("module.prop", "customize.sh"), prop)).hasCustomizeScript)

    @Test fun noModulePropIsRefused() {
        assertEquals("NO_MODULE_PROP", bad(ModulePackageClassifier.classify(listOf("service.sh"), null)))
        assertEquals("NO_MODULE_PROP", bad(ModulePackageClassifier.classify(listOf("sub/module.prop"), prop)))
    }

    @Test fun badModulePropIsRefused() =
        assertEquals("MODULE_PROP:ID_MISSING", bad(ModulePackageClassifier.classify(listOf("module.prop"), "name=x\n")))

    @Test fun zipSlipIsRefused() {
        for (n in listOf("../evil.sh", "system/../../evil", "/abs/evil", "a\\..\\evil", "..")) {
            assertEquals(n, "UNSAFE_ENTRY", bad(ModulePackageClassifier.classify(listOf("module.prop", n), prop)))
        }
    }

    @Test fun duplicateEntryIsRefused() =
        assertEquals("DUPLICATE_ENTRY", bad(ModulePackageClassifier.classify(listOf("module.prop", "a", "a"), prop)))

    @Test fun realZipBytesClassified() {
        val i = info(ModulePackageClassifier.classifyZip(zip("module.prop" to prop, "customize.sh" to "true\n")))
        assertEquals(ModuleFormat.KSU_MODULE, i.format); assertTrue(i.hasCustomizeScript)
    }

    @Test fun realZipWithSlipIsRefused() =
        assertEquals("UNSAFE_ENTRY", bad(ModulePackageClassifier.classifyZip(zip("module.prop" to prop, "../../evil.sh" to "x"))))

    @Test fun garbageBytesAreRefused() {
        assertEquals("NO_MODULE_PROP", bad(ModulePackageClassifier.classifyZip("isto não é zip".toByteArray())))
        val z = zip("module.prop" to prop)
        assertEquals("ZIP_INVALID", bad(ModulePackageClassifier.classifyZip(z.copyOf(z.size / 2))))
    }
}
