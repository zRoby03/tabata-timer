package it.tabatastudio

import it.tabatastudio.domain.AudioFocusLease
import org.junit.Assert.*
import org.junit.Test

class AudioFocusLeaseTest {
    @Test fun deniedFocusIsNotHeldAndCanBeRetriedAtNextCue() {
        var granted=false;var requests=0;var abandoned=0
        val lease=AudioFocusLease({0},{requests++;granted},{abandoned++})
        assertFalse(lease.hold(120));assertFalse(lease.held);assertEquals(0L,lease.until)
        granted=true;assertTrue(lease.hold(120));assertEquals(2,requests)
        lease.release();lease.release();assertEquals(1,abandoned)
    }
    @Test fun countdownAndTransitionShareOneLeaseUntilLastCueEnds() {
        var now=0L;var requests=0;var abandoned=0
        val lease=AudioFocusLease({now},{requests++;true},{abandoned++})
        lease.hold(4485);now=1000;lease.hold(3485);now=3000;lease.hold(1485)
        assertEquals(1,requests);assertEquals(4485L,lease.until)
        now=4484;lease.expire();assertTrue(lease.held)
        now=4485;lease.expire();assertFalse(lease.held);assertEquals(1,abandoned)
    }
    @Test fun oldReleaseCannotExpireExtendedOrNewSessionLease() {
        var now=0L;var abandoned=0
        val lease=AudioFocusLease({now},{true},{abandoned++})
        lease.hold(500);now=400;lease.hold(1920);now=500;lease.expire();assertTrue(lease.held)
        lease.release();assertEquals(1,abandoned)
        lease.hold(1050);now=1000;lease.expire();assertTrue(lease.held)
        now=1550;lease.expire();assertFalse(lease.held);assertEquals(2,abandoned)
    }
    @Test fun pauseMuteLossAndServiceCleanupReleaseImmediately() {
        var requests=0;var abandoned=0
        val lease=AudioFocusLease({0},{requests++;true},{abandoned++})
        repeat(4){lease.hold(4920);lease.release();assertFalse(lease.held);assertEquals(0L,lease.until)}
        assertEquals(4,requests);assertEquals(4,abandoned)
    }
}
