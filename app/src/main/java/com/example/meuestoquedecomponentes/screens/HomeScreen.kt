package com.example.meuestoquedecomponentes.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.meuestoquedecomponentes.data.Categoria
import com.example.meuestoquedecomponentes.data.Componente
import com.example.meuestoquedecomponentes.ui.InventoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: InventoryViewModel,
    onAdicionar: () -> Unit,
    onEditar: (Componente) -> Unit,
    onDetalhes: (Componente) -> Unit
) {
    var termoBusca by remember { mutableStateOf("") }
    var categoriaFiltro by remember { mutableStateOf<Categoria?>(null) }
    var apenasFavoritos by remember { mutableStateOf(false) }
    var mostrarFiltros by remember { mutableStateOf(false) }

    val componentes by viewModel.componentesFiltrados.collectAsState()

    LaunchedEffect(termoBusca) {
        viewModel.setTermoBusca(termoBusca)
    }

    LaunchedEffect(categoriaFiltro) {
        viewModel.setCategoriaFiltro(categoriaFiltro)
    }

    LaunchedEffect(apenasFavoritos) {
        viewModel.setApenasFavoritos(apenasFavoritos)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Kit Box", fontWeight = FontWeight.Bold)
                        Text(
                            "Componentes eletrônicos",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { mostrarFiltros = !mostrarFiltros }) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filtros",
                            tint = if (mostrarFiltros || categoriaFiltro != null || apenasFavoritos) Color(0xFF4A6741) else Color.Gray
                        )
                    }
                    IconButton(onClick = { /* CameraScreen */ }) {
                        Icon(Icons.Default.PhotoCamera, "Câmera")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAdicionar,
                containerColor = Color(0xFF4A6741)
            ) {
                Icon(Icons.Default.Add, "Adicionar componente", tint = Color.White)
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Barra de busca
            OutlinedTextField(
                value = termoBusca,
                onValueChange = { termoBusca = it },
                placeholder = { Text("Buscar por nome, localização...") },
                leadingIcon = { Icon(Icons.Default.Search, "Buscar") },
                trailingIcon = {
                    if (termoBusca.isNotEmpty()) {
                        IconButton(onClick = { termoBusca = "" }) {
                            Icon(Icons.Default.Clear, "Limpar")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                singleLine = true
            )

            // Chips de filtro com rolagem horizontal
            if (mostrarFiltros) {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = apenasFavoritos,
                            onClick = { apenasFavoritos = !apenasFavoritos },
                            label = { Text("Favoritos") },
                            leadingIcon = {
                                Icon(
                                    imageVector = if (apenasFavoritos) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = null,
                                    tint = if (apenasFavoritos) Color(0xFFE53935) else Color.Gray,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        )
                    }
                    item {
                        FilterChip(
                            selected = categoriaFiltro == null && !apenasFavoritos,
                            onClick = {
                                categoriaFiltro = null
                                apenasFavoritos = false
                            },
                            label = { Text("Todos") }
                        )
                    }
                    items(Categoria.entries.toTypedArray()) { cat ->
                        FilterChip(
                            selected = categoriaFiltro == cat,
                            onClick = { categoriaFiltro = if (categoriaFiltro == cat) null else cat },
                            label = { Text(cat.displayName) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Header da lista
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Componentes", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text(
                    "${componentes.size} registro(s)",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            // Lista de componentes
            if (componentes.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Inventory2,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            if (termoBusca.isNotEmpty() || categoriaFiltro != null || apenasFavoritos)
                                "Nenhum componente encontrado"
                            else
                                "Nenhum componente cadastrado",
                            color = Color.Gray
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(componentes, key = { it.id }) { componente ->
                        ComponenteCard(
                            componente = componente,
                            onEditar = { onEditar(componente) },
                            onDetalhes = { onDetalhes(componente) },
                            onExcluir = { viewModel.removerComponente(componente.id) },
                            onToggleFavorito = { viewModel.toggleFavorito(componente.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ComponenteCard(
    componente: Componente,
    onEditar: () -> Unit,
    onDetalhes: () -> Unit,
    onExcluir: () -> Unit,
    onToggleFavorito: () -> Unit
) {
    var mostrarConfirmacao by remember { mutableStateOf(false) }

    Card(
        onClick = onDetalhes,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Nome + Favorito
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = componente.nome,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onToggleFavorito) {
                    Icon(
                        imageVector = if (componente.ehFavorito) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorito",
                        tint = if (componente.ehFavorito) Color(0xFFE53935) else Color.Gray
                    )
                }
            }

            // Categoria como chip colorido
            Row(
                modifier = Modifier.padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Color(componente.categoria.cor),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = componente.categoria.displayName,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }

                if (componente.valorNominal > 0) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = componente.valorFormatado,
                        fontSize = 14.sp,
                        color = Color(0xFF4A6741),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Quantidade + Encapsulamento + Localização
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "${componente.quantidade} unidade(s)",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Column(horizontalAlignment = Alignment.End) {
                    if (componente.encapsulamento.isNotBlank()) {
                        Text(
                            componente.encapsulamento,
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                    if (componente.localizacao.isNotBlank()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = Color.Gray
                            )
                            Text(
                                componente.localizacao,
                                fontSize = 12.sp,
                                color = Color.Gray,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            // Barra de estoque com indicador
            val progresso = if (componente.estoqueMinimo > 0) {
                (componente.quantidade.toFloat() / (componente.estoqueMinimo * 2).toFloat()).coerceIn(0f, 1f)
            } else 1f

            val corBarra = when {
                componente.estaAbaixoMinimo -> Color(0xFFE53935)
                componente.estaProximoMinimo -> Color(0xFFFFA726)
                else -> Color(0xFF43A047)
            }

            Box(modifier = Modifier.fillMaxWidth()) {
                LinearProgressIndicator(
                    progress = { 1f },
                    color = Color(0xFFE0E0E0),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                )
                LinearProgressIndicator(
                    progress = { progresso },
                    color = corBarra,
                    modifier = Modifier
                        .fillMaxWidth(progresso)
                        .height(8.dp)
                )
            }

            // Badge de alerta de reposição
            if (componente.estaAbaixoMinimo) {
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    color = Color(0xFFFFEBEE),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        "Repor em breve",
                        color = Color(0xFFE53935),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            // Botões de ação
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = onEditar,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(Icons.Default.Edit, "Editar")
                }
                IconButton(
                    onClick = { mostrarConfirmacao = true },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(Icons.Default.Delete, "Excluir", tint = Color(0xFFE53935))
                }
            }
        }
    }

    // Diálogo de confirmação de exclusão
    if (mostrarConfirmacao) {
        AlertDialog(
            onDismissRequest = { mostrarConfirmacao = false },
            title = { Text("Excluir componente") },
            text = { Text("Deseja realmente excluir \"${componente.nome}\"?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onExcluir()
                        mostrarConfirmacao = false
                    }
                ) {
                    Text("Excluir", color = Color(0xFFE53935))
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarConfirmacao = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}