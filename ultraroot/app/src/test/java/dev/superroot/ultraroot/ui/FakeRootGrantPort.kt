package dev.superroot.ultraroot.ui

import dev.superroot.core.KsuRead
import dev.superroot.core.RootGrantPort

class FakeRootGrantPort(
    private val grantResponses: Map<Int, KsuRead<Unit>> = emptyMap(),
    private val revokeResponses: Map<Int, KsuRead<Unit>> = emptyMap(),
) : RootGrantPort {
    val calls = mutableListOf<String>()

    override fun grant(uid: Int): KsuRead<Unit> {
        calls += "grant $uid"
        return grantResponses[uid] ?: KsuRead.Ok(Unit)
    }

    override fun revoke(uid: Int): KsuRead<Unit> {
        calls += "revoke $uid"
        return revokeResponses[uid] ?: KsuRead.Ok(Unit)
    }
}
