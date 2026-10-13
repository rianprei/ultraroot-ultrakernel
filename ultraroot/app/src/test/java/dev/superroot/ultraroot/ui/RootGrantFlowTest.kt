package dev.superroot.ultraroot.ui

import dev.superroot.core.KsuRead
import dev.superroot.core.RootGrantPort
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class RootGrantFlowTest {
    private val a = GrantTarget(uid = 10123, packageName = "dev.example.a")
    private val b = GrantTarget(uid = 10124, packageName = "dev.example.b")

    @Test
    fun requestDoesNotTouchThePort() {
        val port = FakeRootGrantPort()
        val flow = RootGrantFlow(port)

        assertTrue(flow.request(GrantAction.GRANT, listOf(a)))
        assertEquals(GrantState.Confirming(GrantAction.GRANT, listOf(a)), flow.state)
        assertTrue(port.calls.isEmpty())
    }

    @Test
    fun requestWithoutTargetsIsRefused() {
        val port = FakeRootGrantPort()
        val flow = RootGrantFlow(port)

        assertFalse(flow.request(GrantAction.GRANT, emptyList()))
        assertEquals(GrantState.Idle, flow.state)
        assertTrue(port.calls.isEmpty())
    }

    @Test
    fun requestWhileConfirmingIsRefused() {
        val port = FakeRootGrantPort()
        val flow = RootGrantFlow(port)
        assertTrue(flow.request(GrantAction.GRANT, listOf(a)))

        assertFalse(flow.request(GrantAction.REVOKE, listOf(b)))
        assertEquals(GrantState.Confirming(GrantAction.GRANT, listOf(a)), flow.state)
        assertTrue(port.calls.isEmpty())
    }

    @Test
    fun cancelReturnsToIdleWithoutPortCalls() {
        val port = FakeRootGrantPort()
        val flow = RootGrantFlow(port)
        assertFalse(flow.cancel())
        assertEquals(GrantState.Idle, flow.state)
        assertTrue(flow.request(GrantAction.GRANT, listOf(a)))

        assertTrue(flow.cancel())
        assertEquals(GrantState.Idle, flow.state)
        assertTrue(port.calls.isEmpty())

        assertTrue(flow.request(GrantAction.GRANT, listOf(a)))
        assertTrue(flow.confirm())
        val done = flow.state
        assertFalse(flow.cancel())
        assertEquals(done, flow.state)
        assertEquals(listOf("grant 10123"), port.calls)
    }

    @Test
    fun confirmGrantsEachTargetOnceInOrder() {
        val port = FakeRootGrantPort()
        val flow = RootGrantFlow(port)
        assertTrue(flow.request(GrantAction.GRANT, listOf(a, b)))

        assertTrue(flow.confirm())
        assertEquals(listOf("grant 10123", "grant 10124"), port.calls)
        assertEquals(
            GrantState.Done(
                GrantAction.GRANT,
                listOf(
                    AppGrantResult(a, GrantOutcome.Applied),
                    AppGrantResult(b, GrantOutcome.Applied),
                ),
            ),
            flow.state,
        )
    }

    @Test
    fun confirmRevokeUsesRevokePort() {
        val port = FakeRootGrantPort(revokeResponses = mapOf(a.uid to KsuRead.Ok(Unit)))
        val flow = RootGrantFlow(port)
        assertTrue(flow.request(GrantAction.REVOKE, listOf(a)))

        assertTrue(flow.confirm())
        assertEquals(listOf("revoke 10123"), port.calls)
        assertEquals(
            GrantState.Done(GrantAction.REVOKE, listOf(AppGrantResult(a, GrantOutcome.Applied))),
            flow.state,
        )
    }

    @Test
    fun failureOnOneTargetDoesNotStopTheOthers() {
        val port = FakeRootGrantPort(
            grantResponses = mapOf(a.uid to KsuRead.Unavailable("x"), b.uid to KsuRead.Ok(Unit)),
        )
        val flow = RootGrantFlow(port)
        assertTrue(flow.request(GrantAction.GRANT, listOf(a, b)))

        assertTrue(flow.confirm())
        assertEquals(listOf("grant 10123", "grant 10124"), port.calls)
        assertEquals(
            GrantState.Done(
                GrantAction.GRANT,
                listOf(
                    AppGrantResult(a, GrantOutcome.Failed("x")),
                    AppGrantResult(b, GrantOutcome.Applied),
                ),
            ),
            flow.state,
        )
    }

    @Test
    fun confirmOutsideConfirmingIsRefused() {
        val port = FakeRootGrantPort()
        val flow = RootGrantFlow(port)

        assertFalse(flow.confirm())
        assertEquals(GrantState.Idle, flow.state)
        assertTrue(port.calls.isEmpty())
        assertTrue(flow.request(GrantAction.GRANT, listOf(a)))
        assertTrue(flow.confirm())
        val done = flow.state

        assertFalse(flow.confirm())
        assertEquals(done, flow.state)
        assertEquals(listOf("grant 10123"), port.calls)
        assertFalse(flow.request(GrantAction.REVOKE, listOf(b)))
        assertEquals(done, flow.state)
        assertEquals(listOf("grant 10123"), port.calls)
    }

    @Test
    fun dismissReturnsDoneToIdle() {
        val port = FakeRootGrantPort()
        val flow = RootGrantFlow(port)
        assertTrue(flow.request(GrantAction.GRANT, listOf(a)))
        assertTrue(flow.confirm())

        assertTrue(flow.dismiss())
        assertEquals(GrantState.Idle, flow.state)
        assertEquals(listOf("grant 10123"), port.calls)
        assertTrue(flow.request(GrantAction.REVOKE, listOf(a)))
        assertEquals(GrantState.Confirming(GrantAction.REVOKE, listOf(a)), flow.state)
    }

    @Test
    fun dismissOutsideDoneIsRefused() {
        val port = FakeRootGrantPort()
        val flow = RootGrantFlow(port)

        assertFalse(flow.dismiss())
        assertEquals(GrantState.Idle, flow.state)
        assertTrue(flow.request(GrantAction.GRANT, listOf(a)))
        assertFalse(flow.dismiss())
        assertEquals(GrantState.Confirming(GrantAction.GRANT, listOf(a)), flow.state)
        assertTrue(port.calls.isEmpty())
    }

    @Test
    fun resultCarriesPackageName() {
        val port = FakeRootGrantPort()
        val flow = RootGrantFlow(port)
        assertTrue(flow.request(GrantAction.GRANT, listOf(a, b)))
        assertTrue(flow.confirm())

        val results = (flow.state as GrantState.Done).results
        assertEquals(listOf("dev.example.a", "dev.example.b"), results.map { it.target.packageName })
        assertEquals(listOf(10123, 10124), results.map { it.target.uid })
    }

    @Test
    fun requestWithRepeatedUidIsRefused() {
        val port = FakeRootGrantPort()
        val flow = RootGrantFlow(port)

        assertFalse(flow.request(GrantAction.GRANT, listOf(a, a)))
        assertEquals(GrantState.Idle, flow.state)
        assertTrue(port.calls.isEmpty())
    }

    @Test
    fun requestCopiesTargetsAtConfirmation() {
        val port = FakeRootGrantPort()
        val flow = RootGrantFlow(port)
        val targets = mutableListOf(a)

        assertTrue(flow.request(GrantAction.GRANT, targets))
        targets.add(b)
        assertTrue(flow.confirm())
        assertEquals(listOf("grant 10123"), port.calls)
        assertEquals(
            GrantState.Done(GrantAction.GRANT, listOf(AppGrantResult(a, GrantOutcome.Applied))),
            flow.state,
        )
    }

    @Test
    fun confirmDoesNotRepeatGrantsWhenPortThrows() {
        val calls = mutableListOf<String>()
        val port = object : RootGrantPort {
            override fun grant(uid: Int): KsuRead<Unit> {
                calls += "grant $uid"
                if (uid == 10124) {
                    throw IllegalStateException("boom")
                }
                return KsuRead.Ok(Unit)
            }

            override fun revoke(uid: Int): KsuRead<Unit> = KsuRead.Ok(Unit)
        }
        val flow = RootGrantFlow(port)
        assertTrue(flow.request(GrantAction.GRANT, listOf(a, b)))

        assertThrows(IllegalStateException::class.java) { flow.confirm() }
        assertEquals(
            GrantState.Done(GrantAction.GRANT, listOf(AppGrantResult(a, GrantOutcome.Applied))),
            flow.state,
        )
        assertFalse(flow.confirm())
        assertEquals(listOf("grant 10123", "grant 10124"), calls)
    }

    @Test
    fun requestWithSameUidAndOtherPackageIsRefused() {
        val port = FakeRootGrantPort()
        val flow = RootGrantFlow(port)
        val sameUid = GrantTarget(uid = a.uid, packageName = "dev.example.other")

        assertFalse(flow.request(GrantAction.GRANT, listOf(a, sameUid)))
        assertEquals(GrantState.Idle, flow.state)
        assertTrue(port.calls.isEmpty())
    }
}
