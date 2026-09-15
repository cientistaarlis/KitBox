package com.example.meuestoquedecomponentes.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.meuestoquedecomponentes.screens.ComponentDetailScreen
import com.example.meuestoquedecomponentes.screens.FormScreen
import com.example.meuestoquedecomponentes.screens.HomeScreen
import com.example.meuestoquedecomponentes.ui.InventoryViewModel

@Composable
fun AppNavigation(viewModel: InventoryViewModel) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        // TELA 1: Lista e Filtros
        composable("home") {
            HomeScreen(
                viewModel = viewModel,
                onAdicionar = { navController.navigate("form") },
                onEditar = { componente -> navController.navigate("form?id=${componente.id}") },
                onDetalhes = { componente -> navController.navigate("detalhes/${componente.id}") }
            )
        }

        // TELA 2: Detalhes do Componente
        composable(
            route = "detalhes/{id}",
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")
            val componentes by viewModel.componentesFiltrados.collectAsState()
            val componente = componentes.find { it.id == id }

            if (componente != null) {
                ComponentDetailScreen(
                    componente = componente,
                    onBack = { navController.popBackStack() },
                    onEdit = { navController.navigate("form?id=${componente.id}") },
                    onDelete = {
                        viewModel.removerComponente(componente.id)
                        navController.popBackStack()
                    }
                )
            }
        }

        // TELA 3: Cadastro / Edição (Formulário)
        composable(
            route = "form?id={id}",
            arguments = listOf(navArgument("id") {
                type = NavType.StringType
                nullable = true
            })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")
            FormScreen(
                viewModel = viewModel,
                componenteId = id,
                onBack = { navController.popBackStack() }
            )
        }
    }
}