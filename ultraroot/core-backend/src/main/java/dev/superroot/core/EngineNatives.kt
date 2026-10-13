package dev.superroot.core

internal object EngineNatives {
    init {
        System.loadLibrary("ultraroot")
    }

    external fun kernelVersion(): Int
    external fun kernelUapi(): Int
    external fun managerUapi(): Int
    external fun kernelFlags(): Int

    /** Null when the allowlist ioctl fails. Empty when the list exists and has no entries. */
    external fun allowListUids(): IntArray?

    /** Null when GET_APP_PROFILE fails. A stored profile with allowSu=false is a real object. */
    external fun readProfile(uid: Int): EngineProfile?

    /** Writes a profile through SET_APP_PROFILE. Not called from the UI: the UI call waits for the fd origin decision (E2). */
    external fun setAppProfile(uid: Int, packageName: String, allowSu: Boolean): Int
}

data class EngineProfile(
    val allowSu: Boolean,
    val uid: Int,
    val gid: Int,
    val context: String,
    val flags: Long,
    val name: String,
)
