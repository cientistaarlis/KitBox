package com.example.meuestoquedecomponentes

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import com.example.meuestoquedecomponentes.screens.CameraScreen
import com.example.meuestoquedecomponentes.ui.MeuEstoqueTheme

class CameraActivity : ComponentActivity() {
    private var cameraPermissionGranted by mutableStateOf(false)

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        cameraPermissionGranted = granted
        if (!granted) {
            Toast.makeText(this, "A permissão da câmera é necessária para escanear componentes.", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cameraPermissionGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (!cameraPermissionGranted) permissionLauncher.launch(Manifest.permission.CAMERA)

        setContent {
            MeuEstoqueTheme {
                CameraScreen(
                    hasPermission = cameraPermissionGranted,
                    onBack = { finish() },
                    onPhotoCaptured = { path ->
                        Toast.makeText(this, "Foto salva em $path", Toast.LENGTH_SHORT).show()
                    },
                    onVideoSaved = { path ->
                        Toast.makeText(this, "Vídeo salvo em $path", Toast.LENGTH_SHORT).show()
                    },
                    onBarcodeDetected = { value ->
                        Toast.makeText(this, "Código detectado: $value", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}
