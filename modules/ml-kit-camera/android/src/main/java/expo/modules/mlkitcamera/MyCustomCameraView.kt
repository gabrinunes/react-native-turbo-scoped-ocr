package expo.modules.mlkitcamera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import expo.modules.kotlin.AppContext
import expo.modules.kotlin.viewevent.EventDispatcher
import expo.modules.kotlin.views.ComposableScope
import expo.modules.kotlin.views.ExpoComposeView
import java.lang.Runnable
import java.util.concurrent.Executors


class MyCustomCameraView(context: Context, appContext: AppContext) :
    ExpoComposeView<MyCustomCameraProps>(context, appContext) {
    private val cameraExecutor = Executors.newSingleThreadExecutor()
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    private val onTextRecognized by EventDispatcher()
    private var lastText: String? = null
    private var lastSentAt = 0L

    @Composable
    override fun ComposableScope.Content() {
        val currentContext = LocalContext.current
        val lifecycleOwner = LocalLifecycleOwner.current

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            AndroidView(
                factory = { ctx ->
                    PreviewView(ctx).apply {
                        startCamera(this, lifecycleOnwer = lifecycleOwner, currentContext)
                    }
                }, modifier = Modifier.fillMaxSize()
            )
            ScannerOverlay()
        }
    }


    private fun startCamera(
        view: PreviewView, lifecycleOnwer: LifecycleOwner, currentContext: Context
    ) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(currentContext)
        val cameraRunnable = Runnable {
            try {
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(view.surfaceProvider)
                }
                val imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build()

                imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                    processImageProxy(imageProxy)
                }

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOnwer, cameraSelector, preview, imageAnalysis
                )

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        cameraProviderFuture.addListener(
            cameraRunnable, ContextCompat.getMainExecutor(currentContext)
        )
    }

    @OptIn(ExperimentalGetImage::class)
    private fun processImageProxy(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage == null) {
            imageProxy.close()
            return
        }
        try {
            val rawBitmap = imageProxy.toBitmap()
            val rotationDegrees = imageProxy.imageInfo.rotationDegrees

            val bitmap = if (rotationDegrees != 0) {
                val matrix = Matrix().apply { postRotate(rotationDegrees.toFloat()) }
                Bitmap.createBitmap(
                    rawBitmap, 0, 0, rawBitmap.width, rawBitmap.height, matrix, true
                )
            } else {
                rawBitmap
            }


            val targetWidthPercent = 0.8f
            val targetHeightPercent = 0.4f

            val cropWidth = (bitmap.width * targetWidthPercent).toInt()
            val cropHeight = (bitmap.height * targetHeightPercent).toInt()

            val cropX = (bitmap.width - cropWidth) / 2
            val cropY = (bitmap.height - cropHeight) / 2

            val croppedBitmap = Bitmap.createBitmap(bitmap, cropX, cropY, cropWidth, cropHeight)


            val image = InputImage.fromBitmap(croppedBitmap, 0)
            recognizer.process(image).addOnSuccessListener { visonText ->
                if (visonText.text.isNotBlank()) {
                    sendTextToJs(visonText.text)
                }
            }.addOnCompleteListener {
                imageProxy.close()
            }

        } catch (e: Exception) {
            e.printStackTrace()
            imageProxy.close()
        }
    }

    private fun sendTextToJs(text: String) {
        val clean = text.trim()
        val now = System.currentTimeMillis()

        if (clean.length < 4) return
        if (clean == lastText) return
        if (now - lastSentAt < 800) return   // ajuste conforme a necessidade

        lastText = clean
        lastSentAt = now
        onTextRecognized(mapOf("text" to text))

    }
}
