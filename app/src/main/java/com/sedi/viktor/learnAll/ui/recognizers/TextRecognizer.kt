package com.sedi.viktor.learnAll.ui.recognizers

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata


class TextRecognizer : ImageAnalysis.Analyzer {
    override fun analyze(imageProxy: ImageProxy?, rotationDegrees: Int) {

        if (imageProxy == null || imageProxy.image == null) return

        val image = imageProxy.image

        var rotation = degreesToFireBaseRotarion(rotationDegrees)

        var firebaseImage = FirebaseVisionImage.fromMediaImage(image!!, rotation)

    }

    fun degreesToFireBaseRotarion(degrees: Int): Int {
        return when (degrees) {
            0 -> FirebaseVisionImageMetadata.ROTATION_0
            90 -> FirebaseVisionImageMetadata.ROTATION_90
            180 -> FirebaseVisionImageMetadata.ROTATION_180
            270 -> FirebaseVisionImageMetadata.ROTATION_270
            else -> throw IllegalArgumentException(
                "Rotation must be 0, 90, 180, or 270."
            )
        }
    }
}