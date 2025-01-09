package com.tugasakhir.tugasakhirreal

object Constants {
    val models = listOf(
/*        ModelInfo("model1_16.tflite", "labels1.txt"),
        ModelInfo("model1_32.tflite", "labels1.txt"),
        ModelInfo("model640_rgb_16.tflite", "labels2.txt"),
        ModelInfo("model640_rgb_32.tflite", "labels2.txt"),
        ModelInfo("model512_rgb_16.tflite", "labels2.txt"),
        ModelInfo("model512_rgb_32.tflite", "labels2.txt"),
        ModelInfo("model256_rgb_16.tflite", "labels2.txt"),
        ModelInfo("model256_rgb_32.tflite", "labels2.txt"),
        ModelInfo("model640_grayscale_32.tflite", "labels2.txt"),*/
        ModelInfo("model640_grayscale_16.tflite", "labels2.txt"),
       /* ModelInfo("model512_grayscale_32.tflite", "labels2.txt"),
        ModelInfo("model512_grayscale_16.tflite", "labels2.txt"),
        ModelInfo("model256_grayscale_32.tflite", "labels2.txt"),
        ModelInfo("model256_grayscale_16.tflite", "labels2.txt"),
        ModelInfo("model640_32_base.tflite", "labels3.txt"),
        ModelInfo("best_float16_50ep.tflite", "labels4.txt"),
        ModelInfo("best16.tflite", "labels4.txt"),*/
    )
}

data class ModelInfo(
    val modelPath: String,
    val labelPath: String
)