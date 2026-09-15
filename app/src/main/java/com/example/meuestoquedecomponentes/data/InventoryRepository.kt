package com.example.meuestoquedecomponentes.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class InventoryRepository {
    private val _componentes = MutableStateFlow<List<Componente>>(emptyList())
    val componentes: StateFlow<List<Componente>> = _componentes.asStateFlow()

    fun adicionarComponente(componente: Componente) {
        _componentes.value = _componentes.value + componente
    }

    fun atualizarComponente(componente: Componente) {
        _componentes.value = _componentes.value.map {
            if (it.id == componente.id) componente else it
        }
    }

    fun removerComponente(id: String) {
        _componentes.value = _componentes.value.filter { it.id != id }
    }

    fun buscarComponentes(termo: String): List<Componente> {
        if (termo.isBlank()) return _componentes.value
        val termoLower = termo.lowercase()
        return _componentes.value.filter { componente ->
            componente.nome.lowercase().contains(termoLower) ||
                    componente.categoria.displayName.lowercase().contains(termoLower) ||
                    componente.encapsulamento.lowercase().contains(termoLower) ||
                    componente.localizacao.lowercase().contains(termoLower) ||
                    componente.valorFormatado.lowercase().contains(termoLower)
        }
    }

    fun getComponentesPorCategoria(categoria: Categoria): List<Componente> {
        return _componentes.value.filter { it.categoria == categoria }
    }

    fun getComponentesAbaixoEstoque(): List<Componente> {
        return _componentes.value.filter { it.estaAbaixoMinimo }
    }

    fun getFavoritos(): List<Componente> {
        return _componentes.value.filter { it.ehFavorito }
    }
}