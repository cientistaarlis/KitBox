package com.example.meuestoquedecomponentes.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.meuestoquedecomponentes.data.Componente

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComponentDetailScreen(
    componente: Componente,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(componente.nome) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Voltar")
                    }
                },
                actions = {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, "Editar")
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, "Excluir", tint = Color(0xFFE53935))
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Categoria: ${componente.categoria.displayName}", fontSize = 16.sp)
            Text("Quantidade em Estoque: ${componente.quantidade}", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            if (componente.valorNominal > 0) {
                Text("Valor Nominal: ${componente.valorFormatado}", fontSize = 16.sp)
            }
            if (componente.encapsulamento.isNotBlank()) {
                Text("Encapsulamento: ${componente.encapsulamento}", fontSize = 16.sp)
            }
            if (componente.localizacao.isNotBlank()) {
                Text("Localização: ${componente.localizacao}", fontSize = 16.sp)
            }
        }
    }
}