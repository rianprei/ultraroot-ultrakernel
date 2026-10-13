package dev.superroot.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BackendStateTest {
    @Test
    fun unavailableReadIsAbsentWithSameReason() {
        val result = classifyBackend(KsuRead.Unavailable(KsuBackend.UNAVAILABLE))
        assertEquals(BackendState.Absent(KsuBackend.UNAVAILABLE), result)
    }

    @Test
    fun zeroVersionIsAbsentEvenWhenUapiDiffers() {
        val result = classifyBackend(
            KsuRead.Ok(
                KsuCapability(
                    kernelVersion = 0,
                    kernelUapi = 0,
                    managerUapi = 5,
                ),
            ),
        )
        assertTrue(result is BackendState.Absent)
        assertTrue((result as BackendState.Absent).reason.isNotEmpty())
    }

    @Test
    fun zeroVersionIsAbsentWhateverTheUapi() {
        for (uapi in listOf(4, 5)) {
            val result = classifyBackend(
                KsuRead.Ok(
                    KsuCapability(
                        kernelVersion = 0,
                        kernelUapi = uapi,
                        managerUapi = 5,
                    ),
                ),
            )
            assertTrue("uapi=$uapi", result is BackendState.Absent)
            assertTrue("uapi=$uapi", (result as BackendState.Absent).reason.isNotEmpty())
        }
    }

    @Test
    fun uapiMismatchIsIncompatibleWithBothNumbers() {
        val result = classifyBackend(
            KsuRead.Ok(
                KsuCapability(
                    kernelVersion = 12000,
                    kernelUapi = 4,
                    managerUapi = 5,
                ),
            ),
        )
        assertTrue(result is BackendState.Incompatible)
        val incompatible = result as BackendState.Incompatible
        assertEquals(4, incompatible.kernelUapi)
        assertEquals(5, incompatible.managerUapi)
        assertTrue(incompatible.reason.isNotEmpty())
    }

    @Test
    fun legacyKernelWithoutUapiIsIncompatible() {
        val result = classifyBackend(
            KsuRead.Ok(
                KsuCapability(
                    kernelVersion = 11000,
                    kernelUapi = 0,
                    managerUapi = 5,
                ),
            ),
        )
        assertTrue(result is BackendState.Incompatible)
        val incompatible = result as BackendState.Incompatible
        assertEquals(0, incompatible.kernelUapi)
        assertEquals(5, incompatible.managerUapi)
        assertTrue(incompatible.reason.isNotEmpty())
    }

    @Test
    fun matchingUapiIsActiveWithVersion() {
        val result = classifyBackend(
            KsuRead.Ok(
                KsuCapability(
                    kernelVersion = 12000,
                    kernelUapi = 5,
                    managerUapi = 5,
                ),
            ),
        )
        assertEquals(BackendState.Active(version = 12000, uapi = 5), result)
    }

    @Test
    fun matchingUapiIsActiveForAnyVersionAndUapi() {
        val result = classifyBackend(
            KsuRead.Ok(
                KsuCapability(
                    kernelVersion = 13000,
                    kernelUapi = 6,
                    managerUapi = 6,
                ),
            ),
        )
        assertEquals(BackendState.Active(version = 13000, uapi = 6), result)
    }
}
