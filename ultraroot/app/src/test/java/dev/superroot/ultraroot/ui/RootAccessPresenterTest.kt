package dev.superroot.ultraroot.ui

import dev.superroot.core.AllowEntry
import dev.superroot.core.AllowGrant
import dev.superroot.core.KsuBackend
import dev.superroot.core.KsuRead
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RootAccessPresenterTest {
    @Test
    fun unavailableShowsBackendMessage() {
        val ui = presentRootAccess(KsuRead.Unavailable(KsuBackend.UNAVAILABLE))
        assertTrue(ui is RootAccessUi.Unavailable)
        assertEquals(KsuBackend.UNAVAILABLE, (ui as RootAccessUi.Unavailable).message)
        assertEquals("backend indisponível", ui.message)
    }

    @Test
    fun emptyAllowListShowsEmptyState() {
        val ui = presentRootAccess(KsuRead.Ok(emptyList()))
        assertTrue(ui is RootAccessUi.Empty)
    }

    @Test
    fun populatedAllowListShowsUidPackageAndAllowSu() {
        val ui = presentRootAccess(
            KsuRead.Ok(
                listOf(
                    AllowEntry.Confirmed(
                        AllowGrant(
                            uid = 10123,
                            allowSu = true,
                            profileUid = 0,
                            profileGid = 0,
                            context = "u:r:ksu:s0",
                            flags = 0,
                            name = "dev.example.tool",
                        )
                    ),
                    AllowEntry.Confirmed(
                        AllowGrant(
                            uid = 10147,
                            allowSu = false,
                            profileUid = 10147,
                            profileGid = 10147,
                            context = "",
                            flags = 0,
                            name = "dev.example.plain",
                        )
                    ),
                )
            )
        )
        assertTrue(ui is RootAccessUi.Listed)
        val rows = (ui as RootAccessUi.Listed).rows
        assertEquals(2, rows.size)
        assertEquals(10123, rows[0].uid)
        assertEquals("dev.example.tool", rows[0].packageName)
        assertEquals(true, rows[0].allowSu)
        assertEquals(10147, rows[1].uid)
        assertEquals("dev.example.plain", rows[1].packageName)
        assertEquals(false, rows[1].allowSu)
    }

    @Test
    fun failedProfileDoesNotBecomeAllowSuFalse() {
        val ui = presentRootAccess(
            KsuRead.Ok(listOf(AllowEntry.Unavailable(uid = 1000, reason = KsuBackend.PROFILE_UNAVAILABLE)))
        )
        val row = (ui as RootAccessUi.Listed).rows.single()
        assertEquals(1000, row.uid)
        assertNull(row.allowSu)
    }

    @Test
    fun readAllowListWithoutLibIsUnavailable() {
        val read = KsuBackend.readAllowList()
        assertTrue(read is KsuRead.Unavailable)
        val ui = presentRootAccess(read)
        assertTrue(ui is RootAccessUi.Unavailable)
        assertEquals("backend indisponível", (ui as RootAccessUi.Unavailable).message)
    }
}
