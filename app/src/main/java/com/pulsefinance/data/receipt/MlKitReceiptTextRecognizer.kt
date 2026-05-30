package com.pulsefinance.data.receipt

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.pulsefinance.domain.model.DomainError
import com.pulsefinance.domain.model.DomainResult
import com.pulsefinance.domain.repository.ReceiptImageInput
import com.pulsefinance.domain.repository.ReceiptTextRecognizer
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MlKitReceiptTextRecognizer : ReceiptTextRecognizer {

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    override suspend fun recognizeText(image: ReceiptImageInput): DomainResult<String> {
        return withContext(Dispatchers.Default) {
            val bitmap = image.toBitmap()
            val inputImage = InputImage.fromBitmap(bitmap, 0)
            suspendCoroutine { continuation ->
                recognizer.process(inputImage)
                    .addOnSuccessListener { visionText ->
                        val text = visionText.textBlocks.joinToString("\n") { block ->
                            block.lines.joinToString("\n") { it.text }
                        }.trim()
                        if (text.isBlank()) {
                            continuation.resume(
                                DomainResult.Failure(DomainError.Validation("No text found on the receipt.")),
                            )
                        } else {
                            continuation.resume(DomainResult.Success(text))
                        }
                    }
                    .addOnFailureListener { error ->
                        continuation.resume(
                            DomainResult.Failure(
                                DomainError.Repository("Could not read receipt text.", error),
                            ),
                        )
                    }
            }
        }
    }

    suspend fun recognizeBitmap(bitmap: Bitmap): DomainResult<String> {
        if (bitmap.width <= 0 || bitmap.height <= 0) {
            return DomainResult.Failure(DomainError.Validation("Invalid receipt image."))
        }
        val pixels = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        return recognizeText(
            ReceiptImageInput(
                width = bitmap.width,
                height = bitmap.height,
                pixels = pixels,
            ),
        )
    }

    private fun ReceiptImageInput.toBitmap(): Bitmap {
        return Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888)
    }
}
