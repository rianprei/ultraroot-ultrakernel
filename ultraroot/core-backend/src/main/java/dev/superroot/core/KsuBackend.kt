package dev.superroot.core

data class KsuCapability(
    val kernelVersion: Int,
    val kernelUapi: Int,
    val managerUapi: Int,
)

data class AllowGrant(
    val uid: Int,
    val allowSu: Boolean,
    val profileUid: Int,
    val profileGid: Int,
    val context: String,
    val flags: Long,
    val name: String,
)

sealed class KsuRead<out T> {
    data class Ok<T>(val value: T) : KsuRead<T>()
    data class Unavailable(val reason: String) : KsuRead<Nothing>()
}

sealed class AllowEntry {
    data class Confirmed(val grant: AllowGrant) : AllowEntry()
    data class Unavailable(val uid: Int, val reason: String) : AllowEntry()
}

object KsuBackend {
    const val UNAVAILABLE = "backend indisponível"
    const val PROFILE_UNAVAILABLE = "perfil indisponível"

    /**
     * null allowSu means the profile ioctl failed. false is a stored profile
     * that really has no root.
     */
    fun interpretProfile(
        uid: Int,
        allowSu: Boolean?,
        profileUid: Int = 0,
        profileGid: Int = 0,
        context: String = "",
        flags: Long = 0,
        name: String = "",
    ): AllowEntry {
        if (allowSu == null) {
            return AllowEntry.Unavailable(uid, PROFILE_UNAVAILABLE)
        }
        return AllowEntry.Confirmed(
            AllowGrant(
                uid = uid,
                allowSu = allowSu,
                profileUid = profileUid,
                profileGid = profileGid,
                context = context,
                flags = flags,
                name = name,
            )
        )
    }

    fun readCapability(): KsuRead<KsuCapability> = readNative {
        KsuCapability(
            kernelVersion = EngineNatives.kernelVersion(),
            kernelUapi = EngineNatives.kernelUapi(),
            managerUapi = EngineNatives.managerUapi(),
        )
    }

    fun readMode(): KsuRead<KsuMode> {
        val read = readNative { decodeKernelMode(EngineNatives.kernelVersion(), EngineNatives.kernelFlags()) }
        return when (read) {
            is KsuRead.Unavailable -> read
            is KsuRead.Ok -> read.value
        }
    }

    fun readAllowList(): KsuRead<List<AllowEntry>> {
        val listed = readNative { EngineNatives.allowListUids() }
        val uids = when (listed) {
            is KsuRead.Unavailable -> return listed
            is KsuRead.Ok -> listed.value ?: return KsuRead.Unavailable(UNAVAILABLE)
        }
        return readNative {
            uids.map { uid ->
                val profile = EngineNatives.readProfile(uid)
                if (profile == null) {
                    interpretProfile(uid, allowSu = null)
                } else {
                    interpretProfile(
                        uid = uid,
                        allowSu = profile.allowSu,
                        profileUid = profile.uid,
                        profileGid = profile.gid,
                        context = profile.context,
                        flags = profile.flags,
                        name = profile.name,
                    )
                }
            }
        }
    }

    private inline fun <T> readNative(block: () -> T): KsuRead<T> {
        return try {
            KsuRead.Ok(block())
        } catch (e: UnsatisfiedLinkError) {
            KsuRead.Unavailable(UNAVAILABLE)
        } catch (e: ExceptionInInitializerError) {
            if (e.cause is UnsatisfiedLinkError) {
                KsuRead.Unavailable(UNAVAILABLE)
            } else {
                throw e
            }
        } catch (e: NoClassDefFoundError) {
            val msg = e.message.orEmpty()
            if (msg.contains("EngineNatives") || msg.contains("ultraroot")) {
                KsuRead.Unavailable(UNAVAILABLE)
            } else {
                throw e
            }
        }
    }
}
