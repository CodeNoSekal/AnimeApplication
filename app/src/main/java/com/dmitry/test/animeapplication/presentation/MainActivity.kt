package com.dmitry.test.animeapplication.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dmitry.test.animeapplication.presentation.screens.CatalogPage
import com.dmitry.test.animeapplication.presentation.screens.HomePage
import com.dmitry.test.animeapplication.presentation.ui.theme.AnimeApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AnimeApplicationTheme {
                AnimeApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimeApp(){
    val navController = rememberNavController()

    Scaffold(
        topBar = { TopBar(navController) },
        bottomBar = { BottomBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "homePage",
            modifier = Modifier.padding(innerPadding)
        ){
            composable("homePage") { HomePage() }
            composable("catalogPage") { CatalogPage() }
        }
    }

}


@Composable
fun TopBar(
    navController: NavController
) {

}


@Composable
fun BottomBar(
    navController: NavController
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Button(
            onClick = {navController.navigate("homePage")}
        ) {
            Text("Home")
        }

        Button(
            onClick = {navController.navigate("catalogPage")}
        ) {
            Text("Catalog")
        }
    }
}