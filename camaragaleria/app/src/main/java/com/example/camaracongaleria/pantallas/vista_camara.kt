package com.example.camaracongaleria.pantallas

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.internal.OutputStorage
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.materialIcon
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

import com.example.camaracongaleria.R

import androidx.core.content.ContextCompat

import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import androidx.compose.runtime.*



@Composable
fun PantallaCamara() {
    val contexto = LocalContext.current
    val cicloDeVidaDueño = LocalLifecycleOwner.current

    // Estado mutable para alternar entre cámaras
    var lenteAUsar by remember { mutableStateOf(CameraSelector.LENS_FACING_BACK) }


    val vistaPrevista = remember { PreviewView(contexto) }

    // El capturador se mantiene fijo entre Precomposiciones
    val capturadorDeImagen = remember { ImageCapture.Builder().build() }

    // Reacciona cada vez que se cambia de cámara
    LaunchedEffect(lenteAUsar) {
        val proveedorLocalCamara = contexto.obtenerProveedorDeCamara()
        proveedorLocalCamara.unbindAll()

        val prevista = Preview.Builder().build().apply {
            setSurfaceProvider(vistaPrevista.surfaceProvider)
        }

        val camaraxSelector = CameraSelector.Builder()
            .requireLensFacing(lenteAUsar)
            .build()

        proveedorLocalCamara.bindToLifecycle(
            cicloDeVidaDueño,
            camaraxSelector,
            prevista,
            capturadorDeImagen
        )
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        // Vista previa de cámara
        AndroidView(factory = { vistaPrevista }, modifier = Modifier.fillMaxSize())

        // Imagen  (marco)
        Image(
            painter = painterResource(id = R.drawable.piko),
            contentDescription = "Fondo de la cámara",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxWidth()
                .padding(16.dp),
        )

        // Botones en fila
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {

            Button(
                onClick = {

                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "VOLVER")
            }

            Button(
                onClick = {
                    tomar_foto(capturadorDeImagen, contexto)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Icon(imageVector = Icons.Default.AddCircle, contentDescription = "tomarfoto")
            }

            Button(
                onClick = {
                    lenteAUsar = if (lenteAUsar == CameraSelector.LENS_FACING_BACK)
                        CameraSelector.LENS_FACING_FRONT
                    else
                        CameraSelector.LENS_FACING_BACK
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
            ) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = "tomarfoto")
            }


        }
    }}



private suspend fun Context.obtenerProveedorDeCamara(): ProcessCameraProvider =
    suspendCoroutine { continuacion ->
        ProcessCameraProvider.getInstance(this).also { proveedorCamara ->
            proveedorCamara.addListener({
                continuacion.resume(proveedorCamara.get())
            }, ContextCompat.getMainExecutor(this))
        }
    }



fun imageProxyToBitmap(image: ImageProxy): Bitmap {
    val buffer = image.planes[0].buffer
    val bytes = ByteArray(buffer.remaining())
    buffer.get(bytes)

    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

    val rotationDegrees = image.imageInfo.rotationDegrees
    return rotateBitmap(bitmap, rotationDegrees)
}


//acomodar pokemon
fun rotateBitmap(bitmap: Bitmap, rotationDegrees: Int): Bitmap {
    if (rotationDegrees == 0) return bitmap

    val matrix = Matrix()
    matrix.postRotate(rotationDegrees.toFloat())

    return Bitmap.createBitmap(
        bitmap,
        0, 0,
        bitmap.width,
        bitmap.height,
        matrix,
        true
    )
}

//combinar foto y pokemon
fun combinarFotoConMarco(contexto: Context, fotoOriginal: Bitmap, idMarco: Int): Bitmap {
    val marco = BitmapFactory.decodeResource(contexto.resources, idMarco)

    val marcoRedimensionado = Bitmap.createScaledBitmap(
        marco,
        fotoOriginal.width,
        fotoOriginal.height,
        true
    )

    val bitmapFinal = Bitmap.createBitmap(fotoOriginal.width, fotoOriginal.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmapFinal)

    canvas.drawBitmap(fotoOriginal, 0f, 0f, null)
    canvas.drawBitmap(marcoRedimensionado, 10f, 0f, null)

    return bitmapFinal
}
//guarfdar foto con el pokemon
fun guardarBitmap(bitmap: Bitmap, contexto: Context) {
    val nombreArchivo = "FotoConMarco_${System.currentTimeMillis()}.jpg"
    val valores = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, nombreArchivo)
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Nuestra_app")
        }
    }

    val uri = contexto.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, valores)

    uri?.let {
        contexto.contentResolver.openOutputStream(it)?.use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        }
    }
}


fun tomar_foto(capturadorImagen: ImageCapture, contexto: Context) {
    capturadorImagen.takePicture(
        ContextCompat.getMainExecutor(contexto),
        object : ImageCapture.OnImageCapturedCallback() {

            override fun onCaptureSuccess(image: ImageProxy) {
                val bitmapOriginal = imageProxyToBitmap(image)
                image.close()

                // Combinar con el marco
                val bitmapConMarco =
                    combinarFotoConMarco(contexto, bitmapOriginal, R.drawable.piko)

                // Guardar la imagen fusionada
                guardarBitmap(bitmapConMarco, contexto)

                Log.v("FUNCIONAAA", "ayta tu perro pokemon")
            }

            override fun onError(exception: ImageCaptureException) {
                Log.e("ErrorCaptura", "No jalo :(: ${exception.message}")
            }
        }
    )
}