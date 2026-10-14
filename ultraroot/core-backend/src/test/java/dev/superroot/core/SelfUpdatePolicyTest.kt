package dev.superroot.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

// Certificados sintéticos: só o hash esperado vem de docs/ultraroot/TODOs.md (UR-SELFUPDATE). Nenhuma chave ou APK do dono.
class SelfUpdatePolicyTest {
    private val good = SelfUpdatePolicy.CERT_SHA256
    private val other = "0".repeat(64)
    private fun cand(pkg: String = SelfUpdatePolicy.PACKAGE_NAME, vc: Long = 11, certs: List<String> = listOf(good)) =
        UpdateCandidate(pkg, vc, certs, "copia-v$vc.apk")
    private val state = SelfUpdateState(10, "copia-v10.apk", null)

    @Test fun expectedHashMatchesTodosLine482() =
        assertEquals("efb8d49f0bb8022e55e5ec89db26c5131bf4a6c3878496fa583d74fbc8d99ad9", good)

    @Test fun normalizeAcceptsColonsAndUppercase() {
        val colon = good.uppercase().chunked(2).joinToString(":")
        assertEquals(good, SelfUpdatePolicy.normalizeCertSha256(colon))
        assertEquals(good, SelfUpdatePolicy.normalizeCertSha256("  $good\n"))
    }

    @Test fun normalizeRejectsWrongLengthOrNonHex() {
        assertNull(SelfUpdatePolicy.normalizeCertSha256(good.dropLast(2)))
        assertNull(SelfUpdatePolicy.normalizeCertSha256(good.dropLast(1) + "g"))
        assertNull(SelfUpdatePolicy.normalizeCertSha256(""))
    }

    @Test fun acceptsRightPackageAndCert() =
        assertEquals(UpdateDecision.Accept, SelfUpdatePolicy.decide(cand(certs = listOf(good.uppercase().chunked(2).joinToString(":"))), 10))

    @Test fun rejectsWrongPackage() =
        assertEquals(UpdateDecision.Reject(RejectReason.WRONG_PACKAGE), SelfUpdatePolicy.decide(cand(pkg = "me.weishu.kernelsu.fake"), 10))

    @Test fun rejectsDivergentCert() =
        assertEquals(UpdateDecision.Reject(RejectReason.CERT_MISMATCH), SelfUpdatePolicy.decide(cand(certs = listOf(other)), 10))

    @Test fun rejectsMalformedCertHash() =
        assertEquals(UpdateDecision.Reject(RejectReason.MALFORMED_CERT_HASH), SelfUpdatePolicy.decide(cand(certs = listOf("xyz")), 10))

    @Test fun rejectsNoSignerAndMultipleSigners() {
        assertEquals(UpdateDecision.Reject(RejectReason.NO_SIGNER), SelfUpdatePolicy.decide(cand(certs = emptyList()), 10))
        assertEquals(UpdateDecision.Reject(RejectReason.MULTIPLE_SIGNERS), SelfUpdatePolicy.decide(cand(certs = listOf(good, other)), 10))
    }

    @Test fun rejectsNotNewer() =
        assertEquals(UpdateDecision.Reject(RejectReason.NOT_NEWER), SelfUpdatePolicy.decide(cand(vc = 10), 10))

    @Test fun rejectedUpdateKeepsPreviousApkAndIsNotSuccess() {
        val c = cand(certs = listOf(other))
        val t = SelfUpdatePolicy.complete(state, c, SelfUpdatePolicy.decide(c, 10), InstallOutcome.NOT_ATTEMPTED)
        assertEquals(10L, t.state.installedVersionCode)
        assertEquals("copia-v10.apk", t.state.lastAcceptedApkCopy)
        assertEquals(UpdateResult.Failure("CERT_MISMATCH"), t.state.lastResult)
        assertNull(t.deletableApkCopy)
    }

    @Test fun failedInstallKeepsPreviousApkAndCopy() {
        val c = cand()
        val t = SelfUpdatePolicy.complete(state, c, SelfUpdatePolicy.decide(c, 10), InstallOutcome.FAILED)
        assertEquals(10L, t.state.installedVersionCode)
        assertEquals("copia-v10.apk", t.state.lastAcceptedApkCopy)
        assertEquals(UpdateResult.Failure("INSTALL_FAILED"), t.state.lastResult)
        assertNull(t.deletableApkCopy)
    }

    @Test fun acceptedButNotConfirmedIsNotSuccess() {
        val c = cand()
        val t = SelfUpdatePolicy.complete(state, c, UpdateDecision.Accept, InstallOutcome.NOT_ATTEMPTED)
        assertEquals(UpdateResult.Failure("INSTALL_NOT_ATTEMPTED"), t.state.lastResult)
        assertNull(t.deletableApkCopy)
    }

    @Test fun confirmedInstallReplacesAndOnlyThenFreesOldCopy() {
        val c = cand()
        val t = SelfUpdatePolicy.complete(state, c, UpdateDecision.Accept, InstallOutcome.CONFIRMED)
        assertEquals(SelfUpdateState(11, "copia-v11.apk", UpdateResult.Success(11)), t.state)
        assertEquals("copia-v10.apk", t.deletableApkCopy)
    }

    @Test fun confirmedInstallOfRejectedCandidateStillFails() {
        val c = cand(certs = listOf(other))
        val t = SelfUpdatePolicy.complete(state, c, UpdateDecision.Reject(RejectReason.CERT_MISMATCH), InstallOutcome.CONFIRMED)
        assertEquals(10L, t.state.installedVersionCode)
        assertEquals(UpdateResult.Failure("CERT_MISMATCH"), t.state.lastResult)
    }
}
