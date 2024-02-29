package com.example.fit99.model

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.FileInputStream
import java.io.IOException
import java.nio.channels.FileChannel

class MyModel(private val context: Context, private val modelFileName: String) {

    private lateinit var interpreter: Interpreter
    private val inputImageSize = 224
    private val inputChannels = 3

    init {
        loadModel()
    }

    private fun loadModel() {
        try {
            val tfliteOptions = Interpreter.Options()
            val modelFileDescriptor = context.assets.openFd(modelFileName)
            val modelFileStream = FileInputStream(modelFileDescriptor.fileDescriptor)
            val modelFileLength = modelFileDescriptor.length
            val modelByteBuffer = modelFileStream.channel.map(
                FileChannel.MapMode.READ_ONLY,
                modelFileDescriptor.startOffset,
                modelFileLength
            )
            modelFileStream.close()

            interpreter = Interpreter(modelByteBuffer, tfliteOptions)
        } catch (ex: IOException) {
        }
    }

    fun runInference(inputData: Array<Array<Array<FloatArray>>>): FloatArray {
        val outputArray = Array(1) { FloatArray(OUTPUT_CLASSES_COUNT) }
        Log.d("MyModel", "Input data dimensions: 1 x ${inputData[0].size} x ${inputData[0][0].size} x ${inputData[0][0][0].size}")
        interpreter.run(inputData, outputArray)
        return outputArray[0]
    }

    fun preprocessImage(imageBitmap: Bitmap): Array<Array<Array<FloatArray>>> {
        val inputTensorImage = TensorImage.fromBitmap(imageBitmap)

        val imageProcessor = ImageProcessor.Builder()
            .add(ResizeOp(inputImageSize, inputImageSize, ResizeOp.ResizeMethod.BILINEAR))
            .add(NormalizeOp(IMAGE_MEAN, IMAGE_STD))
            .build()


        val processedImageBuffer: TensorBuffer = imageProcessor.process(inputTensorImage).tensorBuffer


        val floatArray: FloatArray = processedImageBuffer.floatArray


        val reshapedArray = Array(1) { Array(inputImageSize) { Array(inputImageSize) { FloatArray(inputChannels) } } }
        var index = 0
        for (i in 0 until inputImageSize) {
            for (j in 0 until inputImageSize) {
                for (k in 0 until inputChannels) {
                    reshapedArray[0][i][j][k] = floatArray[index++]
                }
            }
        }
        return reshapedArray
    }

    companion object {
        private const val OUTPUT_CLASSES_COUNT = 24
        private const val IMAGE_MEAN = 127.5f
        private const val IMAGE_STD = 127.5f
    }
}
