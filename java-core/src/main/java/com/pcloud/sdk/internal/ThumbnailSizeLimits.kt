package com.pcloud.sdk.internal

import com.pcloud.sdk.Resolution
import kotlin.math.abs
import kotlin.math.ceil


private val _thumbSizeRange = 16..2048
object ThumbnailSizeLimits : ClosedRange<Int> by _thumbSizeRange, Iterable<Int> by _thumbSizeRange {

    val AllowedMultiples = listOf(4, 5)

    override fun toString(): String {
        return _thumbSizeRange.toString()
    }
}

internal fun ThumbnailSizeLimits.normalize(resolution: Resolution): Resolution {
    var targetWidth = resolution.width.coerceIn(this)
    var targetHeight = resolution.height.coerceIn(this)
    val minorSize = targetWidth.coerceAtMost(targetHeight)
    val targetDimension = if (minorSize == targetWidth) targetWidth else targetHeight
    val nearestDimension = this.nearestMultiple(
        targetDimension,
        AllowedMultiples
    )
    val divisor = findDivisor(nearestDimension, AllowedMultiples)
    targetWidth = this.nearestMultiple(targetWidth, divisor)
    targetHeight = this.nearestMultiple(targetHeight, divisor)
    return if (targetWidth != resolution.width || targetHeight != resolution.height) {
        Resolution(targetWidth, targetHeight)
    } else {
        resolution
    }
}

internal fun findDivisor(value: Int, multiples: List<Int>): Int =
    multiples.first { value % it == 0 }

internal fun nearestMultiple(dividend: Int, divisor: Int): Int {
    return dividend + distanceToNearestMultiple(dividend, divisor)
}

internal fun ClosedRange<Int>.nearestMultiple(dividend: Int, divisor: Int): Int {
    var nearest = com.pcloud.sdk.internal.nearestMultiple(dividend, divisor)
    if (nearest > endInclusive) {
        val delta = (nearest - endInclusive)
        nearest -= ceil(delta / divisor.toFloat()).toInt() * divisor
    } else if (nearest < start) {
        val delta = (start - nearest)
        nearest += ceil(delta / divisor.toFloat()).toInt() * divisor
    }
    return nearest
}

internal fun ClosedRange<Int>.nearestMultiple(dividend: Int, divisors: Iterable<Int>): Int {
    var nearestDistance = Int.MAX_VALUE
    for (divisor in divisors) {
        val multiple = this.nearestMultiple(dividend, divisor)
        val distance = multiple - dividend
        if (distance == 0) {
            // Short path, dividend is a direct multiple of this divisor and the nearest possible value.
            return dividend
        } else if (abs(distance) < abs(nearestDistance)) {
            // The distance to a multiple is the current nearest distance to one of the divisors
            nearestDistance = distance
        }
    }
    require(nearestDistance < Int.MAX_VALUE) { "No divisors provided." }
    return dividend + nearestDistance
}

internal fun distanceToNearestMultiple(dividend: Int, divisor: Int): Int {
    val remainder = dividend % divisor
    if (remainder == 0) return 0

    val distanceToNext = divisor - remainder
    return if (distanceToNext >= remainder) {
        -remainder
    } else {
        distanceToNext
    }
}
