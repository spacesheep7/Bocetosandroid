package com.example.aplicacion_tomar_fotos.pantallas

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.internal.OutputStorage
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView

import androidx.core.content.ContextCompat
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Composable
    fun PantallaCamara(){
        val lente_a_usar= CameraSelector.LENS_FACING_BACK
        val ciclo_de_vida_dueño = LocalLifecycleOwner.current

        val contexto = LocalContext.current

        val prevista =Preview.Builder().build()
        val vista_prevista = remember {
        PreviewView(contexto)
        }

        val  camarax_selector = CameraSelector.Builder().requireLensFacing(lente_a_usar).build()
        val capturador_de_imagen = remember { ImageCapture.Builder().build() }

        LaunchedEffect(lente_a_usar) {
            val proveedor_local_camara = contexto.obtenerProveedorDeCamara()
            proveedor_local_camara.unbindAll()

            proveedor_local_camara.bindToLifecycle(ciclo_de_vida_dueño, camarax_selector, prevista, capturador_de_imagen )

            prevista.setSurfaceProvider ( vista_prevista.surfaceProvider )

        }
        Box(contentAlignment = Alignment.BottomCenter){
            AndroidView(factory = {vista_prevista}, modifier = Modifier.fillMaxSize())
            Button(onClick = {tomar_foto(capturador_de_imagen, contexto)}) {
                Text("hola")
            }
        }

    }
private suspend fun Context.obtenerProveedorDeCamara(): ProcessCameraProvider =
    suspendCoroutine { continuacion ->
        ProcessCameraProvider.getInstance(this).also {proveedor_camara ->
            proveedor_camara.addListener({
                continuacion.resume(proveedor_camara.get())
            },ContextCompat.getMainExecutor(this))
        }
    }
private fun tomar_foto(capturador_imagen: ImageCapture, contexto: Context){
    val nombre_del_archivo = "CapturarFoto.jpg"

    val valores_del_contenido= ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, nombre_del_archivo)
        put(MediaStore.MediaColumns.MIME_TYPE,"image/jpg")
        if(Build.VERSION.SDK_INT>Build.VERSION_CODES.P){
            put(MediaStore.Images.Media.RELATIVE_PATH,"Pictures/Nuestra_app")
        }
    }
    val salida_foto= ImageCapture.OutputFileOptions.Builder(
        contexto.contentResolver,
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        valores_del_contenido
    ).build()

    capturador_imagen.takePicture(
        salida_foto,
        ContextCompat.getMainExecutor(contexto),
        object : ImageCapture.OnImageSavedCallback{
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                Log.v("FUNCIONAAA" , "si jalo papito ")
            }

            override fun onError(exception: ImageCaptureException) {

                Log.v("error capturar" , "se identifico el siguiente error:${exception.message}")
            }
        }

    )
}