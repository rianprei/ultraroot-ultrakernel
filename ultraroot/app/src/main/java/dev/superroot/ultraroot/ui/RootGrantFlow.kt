package dev.superroot.ultraroot.ui

import dev.superroot.core.KsuRead
import dev.superroot.core.RootGrantPort

enum class GrantAction { GRANT, REVOKE }

data class GrantTarget(val uid: Int, val packageName: String)

sealed class GrantOutcome {
    data object Applied : GrantOutcome()
    data class Failed(val reason: String) : GrantOutcome()
}

data class AppGrantResult(val target: GrantTarget, val outcome: GrantOutcome)

sealed class GrantState {
    data object Idle : GrantState()
    data class Confirming(val action: GrantAction, val targets: List<GrantTarget>) : GrantState()
    data class Done(val action: GrantAction, val results: List<AppGrantResult>) : GrantState()
}

class RootGrantFlow(private val port: RootGrantPort) {
    var state: GrantState = GrantState.Idle
        private set

    fun request(action: GrantAction, targets: List<GrantTarget>): Boolean {
        if (state != GrantState.Idle || targets.isEmpty()) {
            return false
        }
        if (targets.map { it.uid }.toSet().size != targets.size) {
            return false
        }
        state = GrantState.Confirming(action, targets.toList())
        return true
    }

    fun confirm(): Boolean {
        val pending = state as? GrantState.Confirming ?: return false
        // Fecha a confirmação antes da primeira chamada à porta.
        state = GrantState.Done(pending.action, emptyList())
        val results = mutableListOf<AppGrantResult>()
        try {
            for (target in pending.targets) {
                val response = when (pending.action) {
                    GrantAction.GRANT -> port.grant(target.uid)
                    GrantAction.REVOKE -> port.revoke(target.uid)
                }
                val outcome = when (response) {
                    is KsuRead.Ok -> GrantOutcome.Applied
                    is KsuRead.Unavailable -> GrantOutcome.Failed(response.reason)
                }
                results += AppGrantResult(target, outcome)
            }
        } finally {
            // Preserva os resultados já obtidos, mesmo quando a porta lança exceção.
            state = GrantState.Done(pending.action, results.toList())
        }
        return true
    }

    fun cancel(): Boolean {
        if (state !is GrantState.Confirming) {
            return false
        }
        state = GrantState.Idle
        return true
    }

    fun dismiss(): Boolean {
        if (state !is GrantState.Done) {
            return false
        }
        state = GrantState.Idle
        return true
    }
}
