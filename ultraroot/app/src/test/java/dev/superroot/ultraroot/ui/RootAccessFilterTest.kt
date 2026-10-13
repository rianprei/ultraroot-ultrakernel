package dev.superroot.ultraroot.ui

import dev.superroot.core.AllowEntry
import dev.superroot.core.AllowGrant
import dev.superroot.core.KsuRead
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RootAccessFilterTest {
    private fun granted(uid: Int, name: String, allowSu: Boolean) = AllowEntry.Confirmed(
        AllowGrant(
            uid = uid,
            allowSu = allowSu,
            profileUid = 0,
            profileGid = 0,
            context = "",
            flags = 0,
            name = name,
        )
    )

    private fun unavailable(uid: Int) = AllowEntry.Unavailable(uid = uid, reason = "perfil indisponível")

    private val sample = KsuRead.Ok(
        listOf(
            granted(10123, "dev.example.tool", allowSu = true),
            granted(10147, "dev.example.plain", allowSu = false),
            unavailable(10200),
        )
    )

    @Test
    fun searchMatchesPackageNameIgnoringCase() {
        val ui = presentRootAccess(sample, query = "TOOL")
        assertEquals(listOf(10123), (ui as RootAccessUi.Listed).rows.map { it.uid })
    }

    @Test
    fun searchMatchesUidDigitsForRowWithoutName() {
        val ui = presentRootAccess(sample, query = "10200")
        val rows = (ui as RootAccessUi.Listed).rows
        assertEquals(listOf(10200), rows.map { it.uid })
        assertEquals(UNKNOWN_PACKAGE, rows.single().packageName)
    }

    @Test
    fun blankQueryKeepsEveryRow() {
        val ui = presentRootAccess(sample, query = "   ")
        assertEquals(3, (ui as RootAccessUi.Listed).rows.size)
    }

    @Test
    fun searchWithoutMatchIsNoMatchNotEmpty() {
        val ui = presentRootAccess(sample, query = "zzz")
        assertEquals(RootAccessUi.NoMatch(query = "zzz", filter = RootFilter.ALL), ui)
    }

    @Test
    fun grantedFilterKeepsOnlyAllowSuTrue() {
        val ui = presentRootAccess(sample, filter = RootFilter.GRANTED)
        assertEquals(listOf(10123), (ui as RootAccessUi.Listed).rows.map { it.uid })
    }

    @Test
    fun notGrantedFilterKeepsOnlyAllowSuFalse() {
        val ui = presentRootAccess(sample, filter = RootFilter.NOT_GRANTED)
        assertEquals(listOf(10147), (ui as RootAccessUi.Listed).rows.map { it.uid })
    }

    @Test
    fun grantedFilterWithoutGrantedIsNoMatch() {
        val onlyDenied = KsuRead.Ok(listOf(granted(10147, "dev.example.plain", allowSu = false)))
        val ui = presentRootAccess(onlyDenied, filter = RootFilter.GRANTED)
        assertEquals(RootAccessUi.NoMatch(query = "", filter = RootFilter.GRANTED), ui)
    }

    @Test
    fun allFilterKeepsRowWithUnavailableProfile() {
        val ui = presentRootAccess(sample, filter = RootFilter.ALL)
        val rows = (ui as RootAccessUi.Listed).rows
        assertNull(rows.last().allowSu)
    }

    @Test
    fun emptyAllowlistStaysEmptyWithQuery() {
        val ui = presentRootAccess(KsuRead.Ok(emptyList()), query = "x", filter = RootFilter.GRANTED)
        assertTrue(ui is RootAccessUi.Empty)
    }

    @Test
    fun searchMatchesPartialUid() {
        val ui = presentRootAccess(sample, query = "1020")
        assertEquals(listOf(10200), (ui as RootAccessUi.Listed).rows.map { it.uid })
    }

    @Test
    fun searchIgnoresCaseInStoredPackageName() {
        val mixedCase = KsuRead.Ok(listOf(granted(10300, "Dev.Example.Tool", allowSu = true)))
        val ui = presentRootAccess(mixedCase, query = "tool")
        assertEquals(listOf(10300), (ui as RootAccessUi.Listed).rows.map { it.uid })
    }

    @Test
    fun emptyPackageNameFallsBackToUnknownPackage() {
        val noName = KsuRead.Ok(listOf(granted(10300, "", allowSu = true)))
        val ui = presentRootAccess(noName)
        assertEquals(UNKNOWN_PACKAGE, (ui as RootAccessUi.Listed).rows.single().packageName)
    }

    @Test
    fun noMatchKeepsTrimmedQuery() {
        val ui = presentRootAccess(sample, query = "  zzz  ")
        assertEquals(RootAccessUi.NoMatch(query = "zzz", filter = RootFilter.ALL), ui)
    }

    @Test
    fun grantedTotalCountsWholeAllowlistNotFilteredRows() {
        val allowlist = KsuRead.Ok(
            listOf(
                granted(10123, "dev.example.tool", allowSu = true),
                granted(10124, "dev.example.other", allowSu = true),
                granted(10147, "dev.example.plain", allowSu = false),
            )
        )
        val ui = presentRootAccess(allowlist, filter = RootFilter.NOT_GRANTED) as RootAccessUi.Listed
        assertEquals(listOf(10147), ui.rows.map { it.uid })
        assertEquals(2, ui.grantedTotal)
    }

    @Test
    fun searchMatchesUidFragmentNotOnlyPrefix() {
        val ui = presentRootAccess(sample, query = "200")
        assertEquals(listOf(10200), (ui as RootAccessUi.Listed).rows.map { it.uid })
    }

    @Test
    fun grantedTotalIsZeroWhenNoneGranted() {
        val noneGranted = KsuRead.Ok(
            listOf(
                granted(10147, "dev.example.plain", allowSu = false),
                unavailable(10200),
            )
        )
        val ui = presentRootAccess(noneGranted) as RootAccessUi.Listed
        assertEquals(0, ui.grantedTotal)
    }

    @Test
    fun grantedTotalCountsThreeGrantedBehindAFilter() {
        val threeGranted = KsuRead.Ok(
            listOf(
                granted(10001, "dev.example.one", allowSu = true),
                granted(10002, "dev.example.two", allowSu = true),
                granted(10003, "dev.example.three", allowSu = true),
                granted(10004, "dev.example.four", allowSu = false),
            )
        )
        val ui = presentRootAccess(threeGranted, filter = RootFilter.NOT_GRANTED) as RootAccessUi.Listed
        assertEquals(listOf(10004), ui.rows.map { it.uid })
        assertEquals(3, ui.grantedTotal)
    }
}
