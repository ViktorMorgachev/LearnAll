package com.sedi.viktor.learnAll.ui.recognizers

import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer
import com.sedi.viktor.learnAll.ui.scan_words.CameraActivity


class TextRecognizer(
    private val detector: FirebaseVisionTextRecognizer,
    private val lifecycleOwner: LifecycleOwner,
    private val textRecognizedCallback: CameraActivity.TextRecognizedCallback,
    private var availableNetwork: Boolean
) :
    ImageAnalysis.Analyzer {

    private var lastTimeStampAnalized = System.currentTimeMillis()


    override fun analyze(imageProxy: ImageProxy?, rotationDegrees: Int) {

        if (!availableNetwork) {
            analizeDevice(imageProxy, rotationDegrees)
        } else {
            analizeCloud(imageProxy, rotationDegrees)
        }

    }

    private fun analizeCloud(imageProxy: ImageProxy?, rotationDegrees: Int) {


        // TODO Тут будем анализировать через облако

        Log.d("LearnAll", "Web analise")

    }


    fun analizeDevice(imageProxy: ImageProxy?, rotationDegrees: Int) {


        Log.d("LearnAll", "Device analise")

        if (imageProxy == null || imageProxy.image == null || (System.currentTimeMillis() - lastTimeStampAnalized < 2000)) return

        val image = imageProxy.image

        var rotation = degreesToFireBaseRotarion(rotationDegrees)

        var firebaseImage = FirebaseVisionImage.fromMediaImage(image!!, rotation)


        lastTimeStampAnalized = System.currentTimeMillis()

        detector.processImage(firebaseImage)
            .addOnSuccessListener {

                for (block in it.textBlocks) {
                    for (line in block.lines) {
                        val lineFrame = line.boundingBox
                        val text = line.text

                        when (lifecycleOwner.lifecycle.currentState) {
                            Lifecycle.State.RESUMED -> textRecognizedCallback.onImageRecognized(
                                text,
                                lineFrame
                            )
                        }
                    }
                }

            }
            .addOnFailureListener {
                when (lifecycleOwner.lifecycle.currentState) {
                    Lifecycle.State.RESUMED -> textRecognizedCallback.onImageUnRecognized()
                }
            }
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

    fun isAvailableNetwork(availableNetwork: Boolean) {
        this.availableNetwork = availableNetwork
    }
}