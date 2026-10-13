package dev.superroot.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class KsuBackendHostTest {
    @Test
    fun readCapabilityWithoutLibIsUnavailable() {
        val result = KsuBackend.readCapability()
        assertTrue(result is KsuRead.Unavailable)
        assertEquals(KsuBackend.UNAVAILABLE, (result as KsuRead.Unavailable).reason)
    }

    @Test
    fun readAllowListWithoutLibIsUnavailable() {
        val result = KsuBackend.readAllowList()
        assertTrue(result is KsuRead.Unavailable)
        assertEquals(KsuBackend.UNAVAILABLE, (result as KsuRead.Unavailable).reason)
    }

    @Test
    fun failedProfileIsUnavailableNotNoRoot() {
        val failed = KsuBackend.interpretProfile(uid = 10123, allowSu = null)
        assertTrue(failed is AllowEntry.Unavailable)
        val unavailable = failed as AllowEntry.Unavailable
        assertEquals(10123, unavailable.uid)
        assertEquals(KsuBackend.PROFILE_UNAVAILABLE, unavailable.reason)

        val noRoot = KsuBackend.interpretProfile(uid = 10123, allowSu = false, name = "pkg")
        assertTrue(noRoot is AllowEntry.Confirmed)
        assertEquals(false, (noRoot as AllowEntry.Confirmed).grant.allowSu)
    }
}
