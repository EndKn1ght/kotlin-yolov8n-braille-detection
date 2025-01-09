package com.tugasakhir.tugasakhirreal.utils

import android.content.Context
import com.tugasakhir.tugasakhirreal.ml.Detector

object DetectorHelper {
    fun createDetector(
        context: Context,
        modelPath: String,
        labelPath: String,
        listener: Detector.DetectorListener
    ): Detector {
        return Detector(
            context = context,
            modelPath = modelPath,
            labelPath = labelPath,
            detectorListener = listener
        )
    }
}
