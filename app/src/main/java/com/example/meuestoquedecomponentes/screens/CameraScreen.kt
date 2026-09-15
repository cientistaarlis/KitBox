package com.example.meuestoquedecomponentes.screens

import android.content.Context
import android.os.Environment
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.Executors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(
    hasPermission: Boolean,
    onBack: () -> Unit,
    onPhotoCaptured: (String) -> Unit,
    onVideoSaved: (String) -> Unit,
    onBarcodeDetected: (String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var isRecording by rememberSaveable { mutableStateOf(false) }
    var detectedCode by rememberSaveable { mutableStateOf<String?>(null) }
    val previewView = remember { PreviewView(context) }
    val imageCapture = remember { mutableStateOf<ImageCapture?>(null) }
    val videoCapture = remember { mutableStateOf<VideoCapture<Recorder>?>(null) }
    val activeRecording = remember { mutableStateOf<Recording?>(null) }

    if (hasPermission) {
        DisposableEffect(previewView) {
            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
            val executor = Executors.newSingleThreadExecutor()
            val scanner = BarcodeScanning.getClient()

            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also { it.surfaceProvider = previewView.surfaceProvider }
                val capture = ImageCapture.Builder().build()
                val recorder = Recorder.Builder()
                    .setQualitySelector(QualitySelector.from(Quality.HD))
                    .build()
                val video = VideoCapture.withOutput(recorder)
                val analysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also { analyzer ->
                        analyzer.setAnalyzer(executor) { imageProxy ->
                            val mediaImage = imageProxy.image
                            if (mediaImage != null && detectedCode == null) {
                                val image = InputImage.fromMediaImage(
                                    mediaImage,
                                    imageProxy.imageInfo.rotationDegrees
                                )
                                scanner.process(image)
                                    .addOnSuccessListener { barcodes ->
                                        barcodes.firstOrNull()?.rawValue?.let { value ->
                                            detectedCode = value
                                            onBarcodeDetected(value)
                                        }
                                    }
                                    .addOnCompleteListener { imageProxy.close() }
                            } else {
                                imageProxy.close()
                            }
                        }
                    }

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        capture,
                        video,
                        analysis
                    )
                    imageCapture.value = capture
                    videoCapture.value = video
                } catch (_: Exception) {
                    // A tela pode sair enquanto a câmera ainda está sendo vinculada.
                }
            }, ContextCompat.getMainExecutor(context))

            onDispose {
                runCatching { cameraProviderFuture.get().unbindAll() }
                scanner.close()
                executor.shutdown()
            }
        }
    }

    Box(Modifier.fillMaxSize().background(Color.Black)) {
        if (hasPermission) {
            AndroidView(
                factory = { previewView },
                modifier = Modifier.fillMaxSize()
            )
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                TopAppBar(
                    title = { Text("Capturar componente", color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", tint = Color.White)
                        }
                    },
                    colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Black.copy(alpha = 0.55f),
                        titleContentColor = Color.White
                    )
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(
                        color = Color.Black.copy(alpha = 0.60f),
                        modifier = Modifier.fillMaxWidth().padding(16.dp)
                    ) {
                        Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Aponte para um código de barras ou QR Code", color = Color.White)
                            detectedCode?.let { code ->
                                Text("Detectado: $code", color = Color(0xFFD3EABB))
                            }
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp, vertical = 22.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                val file = photoFile(context)
                                imageCapture.value?.takePicture(
                                    ImageCapture.OutputFileOptions.Builder(file).build(),
                                    ContextCompat.getMainExecutor(context),
                                    object : ImageCapture.OnImageSavedCallback {
                                        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                                            onPhotoCaptured(file.absolutePath)
                                        }

                                        override fun onError(exception: ImageCaptureException) = Unit
                                    }
                                )
                            },
                            modifier = Modifier.size(64.dp)
                        ) {
                            Icon(Icons.Default.PhotoCamera, contentDescription = "Capturar foto", tint = Color.White, modifier = Modifier.size(34.dp))
                        }
                        IconButton(
                            onClick = {
                                val capture = videoCapture.value ?: return@IconButton
                                if (isRecording) {
                                    activeRecording.value?.stop()
                                    activeRecording.value = null
                                    isRecording = false
                                } else {
                                    val file = videoFile(context)
                                    activeRecording.value = capture.output
                                        .prepareRecording(context, FileOutputOptions.Builder(file).build())
                                        .start(ContextCompat.getMainExecutor(context)) { event ->
                                            if (event is VideoRecordEvent.Finalize && !event.hasError()) {
                                                onVideoSaved(file.absolutePath)
                                            }
                                        }
                                    isRecording = true
                                }
                            },
                            modifier = Modifier.size(76.dp)
                        ) {
                            Icon(
                                if (isRecording) Icons.Default.StopCircle else Icons.Default.FiberManualRecord,
                                contentDescription = if (isRecording) "Parar gravação" else "Gravar vídeo",
                                tint = if (isRecording) Color.Red else Color.White,
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(16.dp))
                Text("Solicitando acesso à câmera", color = Color.White)
                Button(onClick = onBack, modifier = Modifier.padding(top = 20.dp)) { Text("Voltar") }
            }
        }
    }
}

private fun photoFile(context: Context): File = File(
    context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
    "component_${timestamp()}.jpg"
)

private fun videoFile(context: Context): File = File(
    context.getExternalFilesDir(Environment.DIRECTORY_MOVIES),
    "component_${timestamp()}.mp4"
)

private fun timestamp(): String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
