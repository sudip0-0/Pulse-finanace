package com.pulsefinance.presentation.receipt

import android.graphics.Bitmap
import com.pulsefinance.domain.repository.ReceiptImageInput

internal fun Bitmap.toReceiptImageInput(): ReceiptImageInput {
    val pixels = IntArray(width * height)
    getPixels(pixels, 0, width, 0, 0, width, height)
    return ReceiptImageInput(
        width = width,
        height = height,
        pixels = pixels,
    )
}
