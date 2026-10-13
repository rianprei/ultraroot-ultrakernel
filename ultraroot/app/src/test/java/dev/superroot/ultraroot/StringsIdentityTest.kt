package dev.superroot.ultraroot

import org.junit.Assert.assertFalse
import org.junit.Test
import java.io.File

// Guarda de identidade: texto visível ao usuário não pode citar backends (identidade própria do app).
class StringsIdentityTest {

    private val forbidden = listOf("KSU", "KernelSU", "weishu", "Magisk", "APatch")

    private fun assertNoBackendName(path: String) {
        val text = File(path).readText()
        forbidden.forEach { word ->
            assertFalse("$path contém '$word' em texto visível", text.contains(word, ignoreCase = true))
        }
    }

    @Test
    fun stringResourcesCarryNoBackendName() {
        assertNoBackendName("src/main/res/values/strings.xml")
    }

    @Test
    fun sectionTextCarriesNoBackendName() {
        assertNoBackendName("src/main/java/dev/superroot/ultraroot/Section.kt")
    }
}
