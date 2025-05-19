package com.example.camarapoke

// Archivo: PokeApiModels.kt

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.view.ViewGroup
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.material3.*
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.util.concurrent.Executor
import kotlin.coroutines.resume

// 1. MODELOS PARA POKEAPI

data class PokemonResponse(
    @SerializedName("sprites") val sprites: Sprites
)

data class Sprites(
    @SerializedName("front_default") val frontDefault: String?
)

// 2. INTERFAZ RETROFIT
interface PokeApiService {
    @GET("pokemon/{id}")
    suspend fun getPokemon(@Path("id") id: Int): PokemonResponse
}

// 3. CLIENTE RETROFIT
object ApiClient {
    val pokeApi: PokeApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://pokeapi.co/api/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PokeApiService::class.java)
    }
}

// 4. FUNCION PARA OBTENER URL DEL POKEMON ESTATICO
@Composable
fun PokemonOverlay(): String? {
    var imagenUrl by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val idAleatorio = (1..151).random()
        try {
            val poke = ApiClient.pokeApi.getPokemon(idAleatorio)
            imagenUrl = poke.sprites.frontDefault
        } catch (e: Exception) {
            Log.e("Pokemon", "Error al obtener Pokémon", e)
        }
    }

    return imagenUrl
}

// 5. EXTENSION PARA OBTENER CAMARA
suspend fun Context.obtenerProveedorDeCamara(): ProcessCameraProvider =
    suspendCancellableCoroutine { continuacion ->
        val proveedor = ProcessCameraProvider.getInstance(this)
        proveedor.addListener({
            continuacion.resume(proveedor.get())
        }, ContextCompat.getMainExecutor(this))
    }

// 6. FUNCION PARA TOMAR FOTO
fun tomar_foto(capturador_imagen: ImageCapture, contexto: Context) {
    val nombre_del_archivo = "CapturaPokemon.jpg"
    val valores_del_contenido = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, nombre_del_archivo)
        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/PokeCamera")
        }
    }
    val salida_foto = ImageCapture.OutputFileOptions.Builder(
        contexto.contentResolver,
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        valores_del_contenido
    ).build()

    capturador_imagen.takePicture(
        salida_foto,
        ContextCompat.getMainExecutor(contexto),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                Log.v("FOTO", "Foto guardada correctamente")
            }

            override fun onError(exception: ImageCaptureException) {
                Log.e("ERROR", "Error al capturar: ${exception.message}")
            }
        }
    )
}

// 7. COMPOSABLE PRINCIPAL DE CAMARA
@Composable
fun PantallaCamara() {
    var lenteAUsar by remember { mutableStateOf(CameraSelector.LENS_FACING_BACK) }
    val cicloVida = LocalLifecycleOwner.current
    val contexto = LocalContext.current
    val prevista = Preview.Builder().build()
    val vistaPrevista = remember { PreviewView(contexto).apply {
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }}

    val selectorCamara = remember(lenteAUsar) {
        CameraSelector.Builder().requireLensFacing(lenteAUsar).build()
    }
    val capturadorImagen = remember { ImageCapture.Builder().build() }
    val imagenPokemon = PokemonOverlay()

    LaunchedEffect(lenteAUsar) {
        val proveedorCamara = contexto.obtenerProveedorDeCamara()
        proveedorCamara.unbindAll()
        proveedorCamara.bindToLifecycle(
            cicloVida, selectorCamara, prevista, capturadorImagen
        )
        prevista.setSurfaceProvider(vistaPrevista.surfaceProvider)
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        AndroidView(factory = { vistaPrevista }, modifier = Modifier.fillMaxSize())

        imagenPokemon?.let {
            AsyncImage(
                model = ImageRequest.Builder(contexto)
                    .data(it)
                    .crossfade(true)
                    .build(),
                contentDescription = "Pokémon",
                modifier = Modifier.size(150.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = {
                lenteAUsar = if (lenteAUsar == CameraSelector.LENS_FACING_BACK)
                    CameraSelector.LENS_FACING_FRONT else CameraSelector.LENS_FACING_BACK
            }) {
                Text("Cambiar Cámara")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = { tomar_foto(capturadorImagen, contexto) }) {
                Text("Tomar Foto")
            }
        }
    }
}
