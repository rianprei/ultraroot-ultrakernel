package dev.superroot.core

sealed class BackendState {
    data class Active(val version: Int, val uapi: Int) : BackendState()
    data class Absent(val reason: String) : BackendState()
    data class Incompatible(val kernelUapi: Int, val managerUapi: Int, val reason: String) : BackendState()
}

fun classifyBackend(read: KsuRead<KsuCapability>): BackendState = when (read) {
    is KsuRead.Unavailable -> BackendState.Absent(read.reason)
    is KsuRead.Ok -> {
        // Version 0 goes first: a missing KSU reads as version 0, which must not be reported as a UAPI mismatch.
        val cap = read.value
        when {
            cap.kernelVersion == 0 -> BackendState.Absent("sem resposta do kernel: driver ausente ou ioctl falhou")
            cap.kernelUapi == 0 -> BackendState.Incompatible(0, cap.managerUapi, "kernel não informou UAPI")
            cap.kernelUapi != cap.managerUapi -> BackendState.Incompatible(
                cap.kernelUapi, cap.managerUapi, "UAPI ${cap.kernelUapi} do kernel, ${cap.managerUapi} do manager",
            )
            else -> BackendState.Active(cap.kernelVersion, cap.kernelUapi)
        }
    }
}
