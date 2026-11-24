package com.example.emicalculatorapp

import com.example.emicalculatorapp.ui.components.fx.CelebrationThresholdTracker
import com.example.emicalculatorapp.ui.components.fx.ParallaxCalculator
import com.example.emicalculatorapp.ui.components.fx.TiltCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AnimationUpgradeTest {

    // ================= 1. TiltCalculator Tests =================

    @Test
    fun testTiltAngleCenterZero() {
        val (rotX, rotY) = TiltCalculator.calculateTiltAngles(
            touchX = 0f,
            touchY = 0f,
            width = 300f,
            height = 200f,
            maxAngle = 8f
        )
        assertEquals(0f, rotX, 0.001f)
        assertEquals(0f, rotY, 0.001f)
    }

    @Test
    fun testTiltAngleSymmetry() {
        val (_, rightRotY) = TiltCalculator.calculateTiltAngles(
            touchX = 100f,
            touchY = 0f,
            width = 300f,
            height = 200f,
            maxAngle = 8f
        )
        val (_, leftRotY) = TiltCalculator.calculateTiltAngles(
            touchX = -100f,
            touchY = 0f,
            width = 300f,
            height = 200f,
            maxAngle = 8f
        )
        assertEquals(rightRotY, -leftRotY, 0.001f)
        assertTrue(rightRotY > 0f)
    }

    @Test
    fun testTiltAngleClamping() {
        // Far out of bounds touch
        val (rotX, rotY) = TiltCalculator.calculateTiltAngles(
            touchX = 10000f,
            touchY = 10000f,
            width = 300f,
            height = 200f,
            maxAngle = 8f
        )
        assertEquals(-8f, rotX, 0.001f)
        assertEquals(8f, rotY, 0.001f)

        val (negX, negY) = TiltCalculator.calculateTiltAngles(
            touchX = -10000f,
            touchY = -10000f,
            width = 300f,
            height = 200f,
            maxAngle = 8f
        )
        assertEquals(8f, negX, 0.001f)
        assertEquals(-8f, negY, 0.001f)
    }

    // ================= 2. ParallaxCalculator Tests =================

    @Test
    fun testParallaxZeroScroll() {
        val offset = ParallaxCalculator.calculateParallaxOffset(
            scrollOffset = 0f,
            speedFactor = 0.25f,
            maxDisplacement = 120f
        )
        assertEquals(0f, offset, 0.001f)
    }

    @Test
    fun testParallaxSpeedFactors() {
        val slowOffset = ParallaxCalculator.calculateParallaxOffset(
            scrollOffset = 100f,
            speedFactor = 0.15f,
            maxDisplacement = 120f
        )
        val fastOffset = ParallaxCalculator.calculateParallaxOffset(
            scrollOffset = 100f,
            speedFactor = 0.30f,
            maxDisplacement = 120f
        )
        assertEquals(-15f, slowOffset, 0.001f)
        assertEquals(-30f, fastOffset, 0.001f)
    }

    @Test
    fun testParallaxClamping() {
        val clampedPositive = ParallaxCalculator.calculateParallaxOffset(
            scrollOffset = 5000f,
            speedFactor = 0.25f,
            maxDisplacement = 120f
        )
        assertEquals(-120f, clampedPositive, 0.001f)

        val clampedNegative = ParallaxCalculator.calculateParallaxOffset(
            scrollOffset = -5000f,
            speedFactor = 0.25f,
            maxDisplacement = 120f
        )
        assertEquals(120f, clampedNegative, 0.001f)
    }

    // ================= 3. CelebrationThresholdTracker Tests =================

    @Test
    fun testCelebrationThresholdRisingEdge() {
        val tracker = CelebrationThresholdTracker(threshold = 60)
        assertFalse("Below threshold should not trigger", tracker.update(30))
        assertFalse("Still below threshold should not trigger", tracker.update(59))
        assertTrue("Reaching 60 on rising edge must trigger", tracker.update(60))
    }

    @Test
    fun testCelebrationThresholdNoRetriggerWhileAbove() {
        val tracker = CelebrationThresholdTracker(threshold = 60)
        assertTrue(tracker.update(60))
        assertFalse("Continuing to drag above threshold must NOT retrigger", tracker.update(61))
        assertFalse("Continuing to drag above threshold must NOT retrigger", tracker.update(80))
        assertFalse("Continuing to drag above threshold must NOT retrigger", tracker.update(72))
    }

    @Test
    fun testCelebrationThresholdResetsWhenBelow() {
        val tracker = CelebrationThresholdTracker(threshold = 60)
        assertTrue(tracker.update(65))
        assertFalse(tracker.update(65))
        assertFalse("Dropping below threshold resets state without triggering", tracker.update(40))
        assertFalse(tracker.update(20))
    }

    @Test
    fun testCelebrationThresholdRefiresAfterReset() {
        val tracker = CelebrationThresholdTracker(threshold = 60)
        assertTrue(tracker.update(62))
        assertFalse(tracker.update(65))

        // Reset by dropping below
        tracker.update(50)

        // Cross above threshold again
        assertTrue("Must fire again when crossing threshold after reset", tracker.update(61))
    }
}
