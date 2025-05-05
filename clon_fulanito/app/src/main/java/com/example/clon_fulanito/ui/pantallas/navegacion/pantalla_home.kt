package com.example.clon_fulanito.ui.pantallas.navegacion

import android.text.style.BackgroundColorSpan
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.content.MediaType.Companion.Text
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.clon_fulanito.R
import com.example.clon_fulanito.ui.pantallas.PantallaNavegadora
import com.example.clon_fulanito.ui.pantallas.navegacion.controladores.BotonesInferioresNavegacion
import com.example.clon_fulanito.ui.pantallas.navegacion.controladores.PantallaMenuPrincipal
import com.example.clon_fulanito.ui.pantallas.principales.star_wars.PantallaNavesEspaciales
import com.example.clon_fulanito.ui.theme.PurpleGrey40
import com.example.clon_fulanito.ui.theme.PurpleGrey80
import com.example.clon_fulanito.vista_modelos.FulanitoViewModel
import com.example.clon_fulanito.vista_modelos.SWAPIModelo


@Composable
fun MenuPrincipal(modifier: Modifier){
    var pantalla_actual by remember {
        mutableStateOf(0)
    }


    val control_navegacion = rememberNavController()

    val vm_fulanito = FulanitoViewModel()
    val vm_swapi = SWAPIModelo()
    val Pink80 = Color(0xFFEFB8C8)
    Scaffold(modifier = modifier, bottomBar = {
        NavigationBar {
            BotonesInferioresNavegacion().botones_de_navegacion().forEachIndexed { indice, boton_de_navegacion ->
                NavigationBarItem(

                    selected = indice == pantalla_actual,
                    label = {
                        Text("${boton_de_navegacion.etiqueta}")
                    },
                    icon = {
                        Icon(boton_de_navegacion.icono, contentDescription = "${boton_de_navegacion.etiqueta}")
                    },
                    onClick = {
                        pantalla_actual = indice

                        control_navegacion.navigate(boton_de_navegacion.ruta){
                            popUpTo(control_navegacion.graph.startDestinationId){
                                saveState = true
                            }

                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }

    }) { innerPadding ->
        NavHost(navController = control_navegacion,
            startDestination = PantallaMenuPrincipal.Home.ruta,
            modifier = Modifier.padding(innerPadding)){

            composable(PantallaMenuPrincipal.Home.ruta) {
                PantallaNavegadora(modifier = Modifier.fillMaxSize(), vm_fulanito = vm_fulanito)
            }

            composable(PantallaMenuPrincipal.StarWars.ruta) {
                PantallaNavesEspaciales(modifier, vm_swapi)
            }

            composable(PantallaMenuPrincipal.Perfil.ruta) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(), // ocupa toda la pantalla
                    contentAlignment = Alignment.Center // centra el contenido del Box
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Perfil",
                            fontWeight = FontWeight.Bold,
                            color = Color.DarkGray,
                            fontSize = 25.sp
                        )
                        Image(
                            painter = painterResource(id = R.drawable.perfil),
                            contentDescription = "Icono de perfil",
                            modifier = Modifier.size(150.dp)
                        )
                        Text("Usuario",

                            color = Color.DarkGray,
                            fontSize = 20.sp
                        )
                        Button(
                            onClick = { /* acción */ },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Red,
                                contentColor = Color.White
                            )
                        ) {
                            Text("Iniciar sesion")
                        }
                        Button(
                            onClick = {  },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Gray,
                                contentColor = Color.White
                            )
                        ) {
                            Text("Registrarse")
                        }

                    }
                }
            }


        }
    }
}

@Preview(showBackground = true)
@Composable
fun prevista(){
    MenuPrincipal(Modifier.fillMaxSize())
}



/*
NavigationBar {

            Text("Hola mundo", modifier = Modifier.clickable {
                control_navegacion.navigate(PantallaMenuPrincipal.Home.ruta){
                    popUpTo(control_navegacion.graph.startDestinationId){
                        saveState = true
                    }

                    launchSingleTop = true
                    restoreState = true
                }
            })

            Text("Swapi", modifier = Modifier.clickable {
                control_navegacion.navigate(PantallaMenuPrincipal.StarWars.ruta){
                    popUpTo(control_navegacion.graph.startDestinationId){
                        saveState = true
                    }

                    launchSingleTop = true
                    restoreState = true
                }
            })
            Text("Perfil", modifier = Modifier.clickable {
                control_navegacion.navigate(PantallaMenuPrincipal.Perfil.ruta){
                    popUpTo(control_navegacion.graph.startDestinationId){
                        saveState = true
                    }

                    launchSingleTop = true
                    restoreState = true
                }
            })
        }

*/
