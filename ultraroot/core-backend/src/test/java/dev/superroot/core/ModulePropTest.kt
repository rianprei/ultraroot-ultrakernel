package dev.superroot.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

// Spec: kernel/upstream/KernelSU/website/docs/guide/module.md:100-121.
class ModulePropTest {
    private val full = """
        id=a_module
        name=Um Módulo
        version=v1.0
        versionCode=10
        author=fulano
        description=faz algo = com igual
        updateJson=https://exemplo.invalid/update.json
    """.trimIndent() + "\n"

    private fun ok(t: String) = (ModulePropParser.parse(t) as ModulePropResult.Ok).prop
    private fun bad(t: String) = (ModulePropParser.parse(t) as ModulePropResult.Malformed).reason

    @Test fun parsesAllDocumentedFields() {
        val p = ok(full)
        assertEquals("a_module", p.id); assertEquals("Um Módulo", p.name); assertEquals("v1.0", p.version)
        assertEquals(10L, p.versionCode); assertEquals("fulano", p.author); assertEquals("faz algo = com igual", p.description)
        assertEquals("https://exemplo.invalid/update.json", p.updateJson); assertFalse(p.metamodule)
    }

    @Test fun updateJsonIsOptionalAndUnknownKeysKept() {
        val p = ok("id=m1\nactionIcon=icon/a.png\n")
        assertEquals(null, p.updateJson); assertEquals(null, p.versionCode); assertEquals(mapOf("actionIcon" to "icon/a.png"), p.extra)
    }

    @Test fun idRegexFromSpec() {
        for (good in listOf("a_module", "a.module", "module-101")) ok("id=$good\n")
        for (badId in listOf("a module", "1_module", "-a-module", "a")) assertEquals(badId, "BAD_ID", bad("id=$badId\n"))
    }

    @Test fun missingIdIsRefused() = assertEquals("ID_MISSING", bad("name=x\n"))

    @Test fun nonIntegerVersionCodeIsRefused() = assertEquals("BAD_VERSION_CODE", bad("id=m1\nversionCode=1.0\n"))

    @Test fun crlfIsRefused() = assertEquals("CR_LINE_BREAK", bad("id=m1\r\nname=x\r\n"))

    @Test fun lineWithoutEqualsIsRefused() = assertEquals("NO_SEPARATOR", bad("id=m1\nlixo\n"))

    @Test fun duplicateKeyIsRefused() = assertEquals("DUPLICATE_KEY", bad("id=m1\nid=m2\n"))

    @Test fun commentsAndBlankLinesIgnored() = assertEquals("m1", ok("# comentário\n\nid=m1\n").id)

    @Test fun metamoduleFlag() {
        assertTrue(ok("id=m1\nmetamodule=1\n").metamodule)
        assertTrue(ok("id=m1\nmetamodule= TRUE \n").metamodule)
        assertFalse(ok("id=m1\nmetamodule=0\n").metamodule)
    }
}
