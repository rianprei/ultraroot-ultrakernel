package dev.superroot.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class KsuModeTest {
    @Test
    fun decodeZeroFlagsHasNoBits() {
        assertEquals(KsuMode(lkm = false, manager = false, lateLoad = false), decodeKsuMode(0))
    }

    @Test
    fun decodeLkmBit() {
        assertEquals(KsuMode(lkm = true, manager = false, lateLoad = false), decodeKsuMode(1))
    }

    @Test
    fun decodeManagerBit() {
        assertEquals(KsuMode(lkm = false, manager = true, lateLoad = false), decodeKsuMode(2))
    }

    @Test
    fun decodeLateLoadBit() {
        assertEquals(KsuMode(lkm = false, manager = false, lateLoad = true), decodeKsuMode(4))
    }

    @Test
    fun decodeAllKnownBits() {
        assertEquals(KsuMode(lkm = true, manager = true, lateLoad = true), decodeKsuMode(7))
    }

    @Test
    fun decodeIgnoresPrBuildAndBundledBits() {
        assertEquals(KsuMode(lkm = false, manager = false, lateLoad = false), decodeKsuMode(8))
        assertEquals(KsuMode(lkm = false, manager = false, lateLoad = false), decodeKsuMode(16))
    }

    @Test
    fun readModeWithoutLibIsUnavailable() {
        val result = KsuBackend.readMode()
        assertTrue(result is KsuRead.Unavailable)
        assertEquals(KsuBackend.UNAVAILABLE, (result as KsuRead.Unavailable).reason)
    }

    @Test
    fun decodeKernelModeWithoutAnswerIsUnavailable() {
        val result = decodeKernelMode(version = 0, flags = 7)
        assertTrue(result is KsuRead.Unavailable)
        assertEquals(KsuBackend.UNAVAILABLE, (result as KsuRead.Unavailable).reason)
    }

    @Test
    fun decodeKernelModeWithAnswerAndNoFlagsIsOkWithoutBits() {
        val result = decodeKernelMode(version = 12000, flags = 0)
        assertTrue(result is KsuRead.Ok<*>)
        assertEquals(
            KsuMode(lkm = false, manager = false, lateLoad = false),
            (result as KsuRead.Ok<KsuMode>).value,
        )
    }

    @Test
    fun decodeKernelModeWithAnswerDecodesFlags() {
        val result = decodeKernelMode(version = 12000, flags = 5)
        assertTrue(result is KsuRead.Ok<*>)
        assertEquals(
            KsuMode(lkm = true, manager = false, lateLoad = true),
            (result as KsuRead.Ok<KsuMode>).value,
        )
    }
}
