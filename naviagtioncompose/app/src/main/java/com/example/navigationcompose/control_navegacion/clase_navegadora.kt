package com.example.navigationcompose.control_navegacion

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.navigationcompose.Pantallas.PantallaDosVista
import com.example.navigationcompose.Pantallas.PantallaTresVista
import com.example.navigationcompose.Pantallas.PantallaUnoVista
@Preview
@Composable
fun pantallaNavegadora(){
    val control_de_navegacion= rememberNavController()
    NavHost(navController=control_de_navegacion,startDestination=Pantalla1){
        composable<Pantalla1>{
                    PantallaUnoVista()
        }
        composable<Pantalla2> {
                    PantallaDosVista()
        }
        composable<Pantalla3> {
                    PantallaTresVista()
        }
    }
}