package dev.superroot.core

/**
 * UR-SELFUPDATE: regra pura de decisão da atualização do APK.
 * Fonte: docs/ultraroot/TODOs.md, seção UR-SELFUPDATE (pacote e cert SHA-256 aceitos).
 * Recebe o nome do pacote e os SHA-256 dos certificados já extraídos pelo Android; não lê APK nem instala.
 */
data class UpdateCandidate(
    val packageName: String,
    val versionCode: Long,
    val signerCertSha256: List<String>,
    val apkCopy: String,
)

enum class RejectReason { WRONG_PACKAGE, NO_SIGNER, MULTIPLE_SIGNERS, MALFORMED_CERT_HASH, CERT_MISMATCH, NOT_NEWER }

sealed interface UpdateDecision {
    data object Accept : UpdateDecision
    data class Reject(val reason: RejectReason) : UpdateDecision
}

enum class InstallOutcome { NOT_ATTEMPTED, FAILED, CONFIRMED }

sealed interface UpdateResult {
    data class Success(val versionCode: Long) : UpdateResult
    data class Failure(val reason: String) : UpdateResult
}

data class SelfUpdateState(
    val installedVersionCode: Long,
    val lastAcceptedApkCopy: String?,
    val lastResult: UpdateResult?,
)

/** deletableApkCopy: cópia antiga que só pode ser apagada depois da instalação confirmada. */
data class SelfUpdateTransition(val state: SelfUpdateState, val deletableApkCopy: String?)

object SelfUpdatePolicy {
    const val PACKAGE_NAME = "me.weishu.kernelsu"
    const val CERT_SHA256 = "efb8d49f0bb8022e55e5ec89db26c5131bf4a6c3878496fa583d74fbc8d99ad9"

    private val HEX64 = Regex("^[0-9a-f]{64}$")

    /** Minúsculo, sem dois-pontos e sem espaço; null se não for SHA-256 em hex. */
    fun normalizeCertSha256(raw: String): String? {
        val hex = raw.filterNot { it == ':' || it.isWhitespace() }.lowercase()
        return if (HEX64.matches(hex)) hex else null
    }

    fun decide(candidate: UpdateCandidate, installedVersionCode: Long): UpdateDecision {
        if (candidate.packageName != PACKAGE_NAME) return UpdateDecision.Reject(RejectReason.WRONG_PACKAGE)
        val certs = candidate.signerCertSha256
        if (certs.isEmpty()) return UpdateDecision.Reject(RejectReason.NO_SIGNER)
        if (certs.size > 1) return UpdateDecision.Reject(RejectReason.MULTIPLE_SIGNERS)
        val cert = normalizeCertSha256(certs[0]) ?: return UpdateDecision.Reject(RejectReason.MALFORMED_CERT_HASH)
        if (cert != CERT_SHA256) return UpdateDecision.Reject(RejectReason.CERT_MISMATCH)
        if (candidate.versionCode <= installedVersionCode) return UpdateDecision.Reject(RejectReason.NOT_NEWER)
        return UpdateDecision.Accept
    }

    /** Recusa ou instalação não confirmada: o APK anterior e a cópia aceita ficam, e o resultado é falha. */
    fun complete(
        state: SelfUpdateState,
        candidate: UpdateCandidate,
        decision: UpdateDecision,
        install: InstallOutcome,
    ): SelfUpdateTransition = when {
        decision is UpdateDecision.Reject ->
            SelfUpdateTransition(state.copy(lastResult = UpdateResult.Failure(decision.reason.name)), null)
        install != InstallOutcome.CONFIRMED ->
            SelfUpdateTransition(state.copy(lastResult = UpdateResult.Failure("INSTALL_${install.name}")), null)
        else -> SelfUpdateTransition(
            SelfUpdateState(candidate.versionCode, candidate.apkCopy, UpdateResult.Success(candidate.versionCode)),
            state.lastAcceptedApkCopy,
        )
    }
}
