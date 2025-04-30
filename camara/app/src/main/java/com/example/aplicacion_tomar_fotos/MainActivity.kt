package com.example.aplicacion_tomar_fotos

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.example.aplicacion_tomar_fotos.pantallas.PantallaCamara
import com.example.aplicacion_tomar_fotos.ui.theme.Aplicacion_tomar_fotosTheme

class MainActivity : ComponentActivity() {
    private val solicitud_permisos_camara =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {esta_garantizado ->
            if(esta_garantizado){
                //aqui implementamos la previzuñlaisacion
                VistaDelaCamara()
            }
            else{

            }

        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        when(PackageManager.PERMISSION_GRANTED){
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)->{
                //aqui ponemos la vista de la camara
                Log.v("PERMISO GARATIZADO", "Los permisos son correctos")
                VistaDelaCamara()
            }
            else->{
                solicitud_permisos_camara.launch(Manifest.permission.CAMERA)
            }
        }
        enableEdgeToEdge()

    }
    private fun VistaDelaCamara(){
        setContent{
            Aplicacion_tomar_fotosTheme{
                Surface {
                    PantallaCamara()
                }

            }
        }
    }

}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Aplicacion_tomar_fotosTheme {
        Greeting("Android")
    }
}