package com.pcloud.sdk

/**
 * A container for the dimensions of an image
 * */
data class Resolution (val width: Int, val height: Int) {

    init {
        require(width >= 0) { "Width must be a positive number." }
        require(height >= 0) { "Height must be a positive number." }
    }

    override fun toString(): String {
        return "Resolution(${width}x${height})"
    }

    companion object {
        val Zero = Resolution(0,0)
    }
}