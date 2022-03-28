package kr.hs.dgsw.camera

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Size
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.common.model.LocalModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.ObjectDetector
import com.google.mlkit.vision.objects.custom.CustomObjectDetectorOptions
import kr.hs.dgsw.camera.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var objectDetector: ObjectDetector
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    lateinit var binding: ActivityMainBinding

    private var imageAnalysis: ImageAnalysis? = null

    private var isStarted: Boolean = false

    override fun onResume() {
        super.onResume()
        isStarted = false
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            bindPreview(cameraProvider)
        }, ContextCompat.getMainExecutor(this))

        val localModel = LocalModel.Builder()
            .setAssetFilePath("mobilenet_v1_1.0_224_quantized_1_metadata_1.tflite").build()
        val customObjectDetectorOptions = CustomObjectDetectorOptions.Builder(localModel)
            .setDetectorMode(CustomObjectDetectorOptions.STREAM_MODE)
            .enableClassification()
            .setClassificationConfidenceThreshold(0.5f)
            .setMaxPerObjectLabelCount(3)
            .build()

        objectDetector = ObjectDetection.getClient(customObjectDetectorOptions)

        binding.button.setOnClickListener {
            if (imageAnalysis != null) {
                getAnalysis(imageAnalysis)
            }
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    @RequiresApi(Build.VERSION_CODES.R)
    private fun bindPreview(cameraProvider: ProcessCameraProvider) {
        val preview: Preview = Preview.Builder().build()
        preview.setSurfaceProvider(binding.previewView.surfaceProvider)

        val cameraSelector: CameraSelector =
            CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()

        val point = Point()
        val size = display?.getRealSize(point)
        imageAnalysis = ImageAnalysis.Builder().setTargetResolution(Size(point.x, point.y))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build()

        getAnalysis(imageAnalysis)

        cameraProvider.bindToLifecycle(this as LifecycleOwner,
            cameraSelector,
            imageAnalysis,
            preview)
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun getAnalysis(imageAnalysis: ImageAnalysis?) {
        imageAnalysis?.setAnalyzer(ContextCompat.getMainExecutor(this)) {
            val rotationDegrees = it.imageInfo.rotationDegrees
            val image = it.image
            if (image != null) {
                val inputImage = InputImage.fromMediaImage(image, rotationDegrees)
                objectDetector.process(inputImage)
                    .addOnFailureListener { e ->
                        it.close()
                    }.addOnSuccessListener { objects ->
                        for (it in objects) {
                            val element =
                                it.labels.firstOrNull()?.text ?: "Undefined"
                            if(element == "cup" && !isStarted){
                                isStarted = true

                                val intent = Intent(this, PassActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                Log.d("object", element)
                            }
                        }
                        it.close()
                    }
            }
        }
    }
}