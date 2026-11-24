package com.example.emicalculatorapp.ui.components.fx

/**
 * Pure calculation logic for 3D tilt angles based on touch position on a card.
 */
object TiltCalculator {
    const val MAX_ANGLE_DEGREES = 8.0f

    /**
     * Calculates 3D tilt angles (rotationX, rotationY) in degrees from pointer offset.
     * @param touchX horizontal offset from card center (-width/2 .. width/2).
     * @param touchY vertical offset from card center (-height/2 .. height/2).
     * @param width card width in pixels.
     * @param height card height in pixels.
     * @param maxAngle maximum tilt angle in degrees (default 8.0).
     * @return Pair(rotationX, rotationY) clamped to [-maxAngle, maxAngle].
     */
    fun calculateTiltAngles(
        touchX: Float,
        touchY: Float,
        width: Float,
        height: Float,
        maxAngle: Float = MAX_ANGLE_DEGREES
    ): Pair<Float, Float> {
        if (width <= 0f || height <= 0f) return Pair(0f, 0f)

        val halfW = width / 2f
        val halfH = height / 2f

        // Touch at top (negative touchY) tilts the card back (positive rotationX).
        // Touch at bottom (positive touchY) tilts the card forward (negative rotationX).
        val normalizedY = (touchY / halfH).coerceIn(-1.0f, 1.0f)
        val rotationX = (-normalizedY * maxAngle).coerceIn(-maxAngle, maxAngle)

        // Touch at right (positive touchX) tilts the card right (positive rotationY).
        // Touch at left (negative touchX) tilts the card left (negative rotationY).
        val normalizedX = (touchX / halfW).coerceIn(-1.0f, 1.0f)
        val rotationY = (normalizedX * maxAngle).coerceIn(-maxAngle, maxAngle)

        return Pair(rotationX, rotationY)
    }
}
