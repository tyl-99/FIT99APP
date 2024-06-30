package com.example.fit99.model

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import org.pytorch.IValue
import org.pytorch.Module
import org.pytorch.Tensor
import org.pytorch.torchvision.TensorImageUtils
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class MyModel(private val context: Context, private val modelFileName: String) {

    private lateinit var module: Module
    private val inputImageSize = 224
    private val inputChannels = 3

    init {
        loadModel()
    }

    private fun loadModel() {
        try {
            module = Module.load(assetFilePath(context, modelFileName))
        } catch (ex: IOException) {
            Log.e("MyModel", "Error loading model", ex)
        }
    }

    fun runInference(inputData: Tensor): FloatArray {
        val outputTensor = module.forward(IValue.from(inputData)).toTensor()
        return outputTensor.dataAsFloatArray
    }

    fun preprocessImage(imageBitmap: Bitmap): Tensor {
        return TensorImageUtils.bitmapToFloat32Tensor(
            imageBitmap,
            TensorImageUtils.TORCHVISION_NORM_MEAN_RGB,
            TensorImageUtils.TORCHVISION_NORM_STD_RGB
        )
    }

    companion object {
        fun assetFilePath(context: Context, assetName: String): String {
            val file = File(context.filesDir, assetName)
            try {
                context.assets.open(assetName).use { inputStream ->
                    FileOutputStream(file).use { outputStream ->
                        val buffer = ByteArray(4 * 1024)
                        var read: Int
                        while (inputStream.read(buffer).also { read = it } != -1) {
                            outputStream.write(buffer, 0, read)
                        }
                        outputStream.flush()
                    }
                }
            } catch (ex: IOException) {
                Log.e("MyModel", "Error processing asset", ex)
            }
            return file.absolutePath
        }
    }
}
