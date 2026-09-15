package com.example.meuestoquedecomponentes.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.meuestoquedecomponentes.data.Categoria
import com.example.meuestoquedecomponentes.data.Componente
import com.example.meuestoquedecomponentes.ui.InventoryViewModel
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormScreen(
    viewModel: InventoryViewModel,
    componenteId: String?,
    onBack: () -> Unit
) {
    val componentes by viewModel.componentesFiltrados.collectAsState()
    val componenteEdicao = remember(componenteId, componentes) {
        componentes.find { it.id == componenteId }
    }

    var nome by remember(componenteEdicao) { mutableStateOf(componenteEdicao?.nome ?: "") }
    var quantidade by remember(componenteEdicao) { mutableStateOf(componenteEdicao?.quantidade?.toString() ?: "1") }
    var estoqueMinimo by remember(componenteEdicao) { mutableStateOf(componenteEdicao?.estoqueMinimo?.toFloat() ?: 5f) }
    var valorNominal by remember(componenteEdicao) { mutableStateOf(componenteEdicao?.valorNominal?.toString() ?: "0.0") }
    var encapsulamento by remember(componenteEdicao) { mutableStateOf(componenteEdicao?.encapsulamento ?: "") }
    var localizacao by remember(componenteEdicao) { mutableStateOf(componenteEdicao?.localizacao ?: "") }
    var observacoes by remember(componenteEdicao) { mutableStateOf(componenteEdicao?.observacoes ?: "") }
    var ehFavorito by remember(componenteEdicao) { mutableStateOf(componenteEdicao?.ehFavorito ?: false) }
    var categoriaSelecionada by remember(componenteEdicao) { mutableStateOf(componenteEdicao?.categoria ?: Categoria.RESISTOR) }

    var expandedMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (componenteEdicao != null) "Editar Componente" else "Novo Componente") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Campos Iniciais
            OutlinedTextField(
                value = nome,
                onValueChange = { nome = it },
                label = { Text("Nome do Componente *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            ExposedDropdownMenuBox(
                expanded = expandedMenu,
                onExpandedChange = { expandedMenu = !expandedMenu }
            ) {
                OutlinedTextField(
                    value = categoriaSelecionada.displayName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoria") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedMenu) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedMenu,
                    onDismissRequest = { expandedMenu = false }
                ) {
                    Categoria.entries.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat.displayName) },
                            onClick = {
                                categoriaSelecionada = cat
                                expandedMenu = false
                            }
                        )
                    }
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = valorNominal,
                    onValueChange = { valorNominal = it },
                    label = { Text("Valor Nominal") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = encapsulamento,
                    onValueChange = { encapsulamento = it },
                    label = { Text("Encapsulamento") },
                    modifier = Modifier.weight(1f)
                )
            }

            OutlinedTextField(
                value = localizacao,
                onValueChange = { localizacao = it },
                label = { Text("Localização") },
                modifier = Modifier.fillMaxWidth()
            )

            // --- SEÇÃO IDÊNTICA À IMAGEM ---
            Text(
                text = "Quantidade e controle",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            OutlinedTextField(
                value = quantidade,
                onValueChange = { quantidade = it },
                label = { Text("Quantidade disponível *") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Column {
                Text(
                    text = "Estoque mínimo: ${estoqueMinimo.toInt()} unidade(s)",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Slider(
                    value = estoqueMinimo,
                    onValueChange = { estoqueMinimo = it },
                    valueRange = 0f..50f,
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFF4A6741),
                        activeTrackColor = Color(0xFF4A6741)
                    )
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Componente favorito", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("Aparece nos filtros rápidos", fontSize = 14.sp, color = Color.Gray)
                }
                Switch(
                    checked = ehFavorito,
                    onCheckedChange = { ehFavorito = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFF4A6741)
                    )
                )
            }

            OutlinedTextField(
                value = observacoes,
                onValueChange = { observacoes = it },
                placeholder = { Text("Observações") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp),
                maxLines = 4
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Botão igual à imagem
            Button(
                onClick = {
                    if (nome.isNotBlank()) {
                        val novoComponente = Componente(
                            id = componenteEdicao?.id ?: UUID.randomUUID().toString(),
                            nome = nome,
                            categoria = categoriaSelecionada,
                            quantidade = quantidade.toIntOrNull() ?: 0,
                            estoqueMinimo = estoqueMinimo.toInt(),
                            valorNominal = valorNominal.toDoubleOrNull() ?: 0.0,
                            encapsulamento = encapsulamento,
                            localizacao = localizacao,
                            observacoes = observacoes,
                            ehFavorito = ehFavorito
                        )

                        if (componenteEdicao != null) {
                            viewModel.atualizarComponente(novoComponente)
                        } else {
                            viewModel.adicionarComponente(novoComponente)
                        }
                        onBack()
                    }
                },
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A6741)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (componenteEdicao != null) "Salvar alterações" else "Cadastrar componente",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}