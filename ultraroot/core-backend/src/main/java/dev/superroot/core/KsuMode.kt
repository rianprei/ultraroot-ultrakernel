package dev.superroot.core

/**
 * GET_INFO mode bits; flags alone do not establish KernelSU availability.
 * lkm is false when KernelSU is built into the kernel instead of loaded as a module (dispatch.c sets the bit only under #ifdef MODULE); a false value does not mean KernelSU is absent.
 * Source: KernelSU/kernel/supercall/dispatch.c:44-45 (#ifdef MODULE).
 * manager describes the calling process, not a global manager state.
 * Sources: KernelSU/kernel/supercall/dispatch.c:51-52;
 * KernelSU/kernel/manager/manager_identity.h:47-49 (current_uid()).
 * lateLoad comes from ksu_late_loaded: KernelSU/kernel/supercall/dispatch.c:54-55.
 */
data class KsuMode(
    val lkm: Boolean,
    val manager: Boolean,
    val lateLoad: Boolean,
)

fun decodeKsuMode(flags: Int): KsuMode = KsuMode(
    lkm = flags and 1 != 0,
    manager = flags and 2 != 0,
    lateLoad = flags and 4 != 0,
)

fun decodeKernelMode(version: Int, flags: Int): KsuRead<KsuMode> =
    if (version == 0) {
        KsuRead.Unavailable(KsuBackend.UNAVAILABLE)
    } else {
        KsuRead.Ok(decodeKsuMode(flags))
    }
