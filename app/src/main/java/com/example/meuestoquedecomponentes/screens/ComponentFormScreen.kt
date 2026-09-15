package com.example.meuestoquedecomponentes.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.meuestoquedecomponentes.data.Categoria
import com.example.meuestoquedecomponentes.data.Componente
import com.example.meuestoquedecomponentes.data.UnidadeMedida

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComponentFormScreen(
    componenteExistente: Componente? = null,
    onSalvar: (Componente) -> Unit,
    onCancelar: () -> Unit
) {
    var nome by remember { mutableStateOf(componenteExistente?.nome ?: "") }
    var encapsulamento by remember { mutableStateOf(componenteExistente?.encapsulamento ?: "") }
    var categoria by remember { mutableStateOf(componenteExistente?.categoria) }
    var valorNominalText by remember { mutableStateOf(componenteExistente?.valorNominal?.toString() ?: "") }
    var unidadeMedida by remember { mutableStateOf(componenteExistente?.unidadeMedida ?: UnidadeMedida.UNIDADE) }
    var toleranciaText by remember { mutableStateOf(componenteExistente?.tolerancia?.toString() ?: "") }
    var tensaoText by remember { mutableStateOf(componenteExistente?.tensaoTrabalho?.toString() ?: "") }
    var quantidadeText by remember { mutableStateOf(componenteExistente?.quantidade?.toString() ?: "1") }
    var estoqueMinimo by remember { mutableIntStateOf(componenteExistente?.estoqueMinimo ?: 5) }
    var localizacao by remember { mutableStateOf(componenteExistente?.localizacao ?: "") }
    var ehFavorito by remember { mutableStateOf(componenteExistente?.ehFavorito ?: false) }
    var observacoes by remember { mutableStateOf(componenteExistente?.observacoes ?: "") }

    // Estados de erro
    var erroNome by remember { mutableStateOf<String?>(null) }
    var erroCategoria by remember { mutableStateOf<String?>(null) }
    var erroValor by remember { mutableStateOf<String?>(null) }
    var erroQuantidade by remember { mutableStateOf<String?>(null) }

    val scrollState = rememberScrollState()
    val isEdit = componenteExistente != null

    // Unidades disponíveis por categoria
    fun getUnidadesDisponiveis(cat: Categoria?): List<UnidadeMedida> {
        return when (cat) {
            Categoria.RESISTOR -> listOf(UnidadeMedida.OHM, UnidadeMedida.KILO_OHM, UnidadeMedida.MEGA_OHM)
            Categoria.CAPACITOR -> listOf(UnidadeMedida.PICOFARAD, UnidadeMedida.NANOFARAD, UnidadeMedida.MICROFARAD, UnidadeMedida.MILIFARAD)
            Categoria.SENSOR -> listOf(UnidadeMedida.UNIDADE)
            Categoria.MICROCONTROLADOR -> listOf(UnidadeMedida.UNIDADE)
            Categoria.OUTRO -> UnidadeMedida.entries.toList()
            null -> UnidadeMedida.entries.toList()
        }
    }

    fun validar(): Boolean {
        var valido = true
        erroNome = null
        erroCategoria = null
        erroValor = null
        erroQuantidade = null

        if (nome.isBlank()) {
            erroNome = "Nome é obrigatório"
            valido = false
        }
        if (categoria == null) {
            erroCategoria = "Selecione uma categoria"
            valido = false
        }
        if (valorNominalText.isNotBlank()) {
            valorNominalText.toDoubleOrNull() ?: run {
                erroValor = "Valor inválido"
                valido = false
            }
        }
        quantidadeText.toIntOrNull() ?: run {
            erroQuantidade = "Quantidade inválida"
            valido = false
        }
        return valido
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEdit) "Editar componente" else "Novo componente") },
                navigationIcon = {
                    IconButton(onClick = onCancelar) {
                        Icon(Icons.Default.ArrowBack, "Voltar")
                    }
                },
                actions = {
                    TextButton(onClick = onCancelar) {
                        Text("Cancelar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // === IDENTIFICAÇÃO ===
            Text("Identificação", fontWeight = FontWeight.Bold, fontSize = 18.sp)

            OutlinedTextField(
                value = nome,
                onValueChange = { nome = it; erroNome = null },
                label = { Text("Nome do componente *") },
                isError = erroNome != null,
                supportingText = erroNome?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = encapsulamento,
                onValueChange = { encapsulamento = it },
                label = { Text("Encapsulamento") },
                placeholder = { Text("Ex: 0805, TO-92, Axial") },
                modifier = Modifier.fillMaxWidth()
            )

            // === CATEGORIA ===
            Text("Categoria *", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            erroCategoria?.let { Text(it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp) }

            Categoria.entries.forEach { cat ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = categoria == cat,
                        onClick = {
                            categoria = cat
                            erroCategoria = null
                            // Resetar unidade quando muda categoria
                            val unidades = getUnidadesDisponiveis(cat)
                            if (unidadeMedida !in unidades) {
                                unidadeMedida = unidades.first()
                            }
                        }
                    )
                    Text(cat.displayName, modifier = Modifier.padding(start = 8.dp))
                }
            }

            // === VALOR NOMINAL ===
            Text("Especificações elétricas", fontWeight = FontWeight.Bold, fontSize = 18.sp)

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = valorNominalText,
                    onValueChange = { valorNominalText = it; erroValor = null },
                    label = { Text("Valor Nominal") },
                    placeholder = { Text("Ex: 10, 100, 4.7") },
                    isError = erroValor != null,
                    supportingText = erroValor?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                    modifier = Modifier.weight(1f)
                )

                var expanded by remember { mutableStateOf(false) }
                val unidadesDisponiveis = getUnidadesDisponiveis(categoria)

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = unidadeMedida.simbolo,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Unidade") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        unidadesDisponiveis.forEach { unidade ->
                            DropdownMenuItem(
                                text = { Text(unidade.simbolo) },
                                onClick = {
                                    unidadeMedida = unidade
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = toleranciaText,
                    onValueChange = { toleranciaText = it },
                    label = { Text("Tolerância (%)") },
                    placeholder = { Text("Ex: 5") },
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = tensaoText,
                    onValueChange = { tensaoText = it },
                    label = { Text("Tensão (V)") },
                    placeholder = { Text("Ex: 50") },
                    modifier = Modifier.weight(1f)
                )
            }

            // === QUANTIDADE E CONTROLE ===
            Text("Quantidade e controle", fontWeight = FontWeight.Bold, fontSize = 18.sp)

            OutlinedTextField(
                value = quantidadeText,
                onValueChange = { quantidadeText = it; erroQuantidade = null },
                label = { Text("Quantidade disponível *") },
                isError = erroQuantidade != null,
                supportingText = erroQuantidade?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                modifier = Modifier.fillMaxWidth()
            )

            Text("Estoque mínimo: $estoqueMinimo unidade(s)")

            // Barra de progresso visual
            val qtd = quantidadeText.toIntOrNull() ?: 0
            val progresso = if (estoqueMinimo > 0) (qtd.toFloat() / estoqueMinimo.toFloat()).coerceIn(0f, 1.5f) / 1.5f else 0f
            val corBarra = when {
                qtd < estoqueMinimo -> Color(0xFFE53935)
                qtd < estoqueMinimo * 2 -> Color(0xFFFFA726)
                else -> Color(0xFF43A047)
            }

            LinearProgressIndicator(
                progress = { progresso.coerceIn(0f, 1f) },
                color = corBarra,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
            )

            // Slider para estoque mínimo
            Text("Ajustar estoque mínimo: $estoqueMinimo")
            Slider(
                value = estoqueMinimo.toFloat(),
                onValueChange = { estoqueMinimo = it.toInt() },
                valueRange = 0f..100f,
                steps = 19
            )

            // === LOCALIZAÇÃO ===
            Text("Localização", fontWeight = FontWeight.Bold, fontSize = 18.sp)

            OutlinedTextField(
                value = localizacao,
                onValueChange = { localizacao = it },
                label = { Text("Onde está guardado?") },
                placeholder = { Text("Ex: Gaveta 3, Caixa B") },
                leadingIcon = { Icon(Icons.Default.LocationOn, "Localização") },
                modifier = Modifier.fillMaxWidth()
            )

            // === FAVORITO ===
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Column {
                    Text("Componente favorito", fontWeight = FontWeight.Bold)
                    Text("Aparece nos filtros rápidos", fontSize = 12.sp, color = Color.Gray)
                }
                Switch(
                    checked = ehFavorito,
                    onCheckedChange = { ehFavorito = it }
                )
            }

            // === OBSERVAÇÕES ===
            OutlinedTextField(
                value = observacoes,
                onValueChange = { observacoes = it },
                label = { Text("Observações") },
                placeholder = { Text("Fornecedor, preço, projeto...") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Spacer(modifier = Modifier.height(8.dp))

            // === BOTÃO SALVAR ===
            Button(
                onClick = {
                    if (validar()) {
                        val componente = Componente(
                            id = componenteExistente?.id ?: java.util.UUID.randomUUID().toString(),
                            nome = nome.trim(),
                            categoria = categoria ?: Categoria.OUTRO,
                            valorNominal = valorNominalText.toDoubleOrNull() ?: 0.0,
                            unidadeMedida = unidadeMedida,
                            tolerancia = toleranciaText.toDoubleOrNull(),
                            tensaoTrabalho = tensaoText.toDoubleOrNull(),
                            encapsulamento = encapsulamento.trim(),
                            quantidade = quantidadeText.toIntOrNull() ?: 0,
                            estoqueMinimo = estoqueMinimo,
                            localizacao = localizacao.trim(),
                            ehFavorito = ehFavorito,
                            observacoes = observacoes.trim()
                        )
                        onSalvar(componente)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = nome.isNotBlank() && categoria != null
            ) {
                Text(
                    if (isEdit) "Salvar alterações" else "Cadastrar componente",
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}