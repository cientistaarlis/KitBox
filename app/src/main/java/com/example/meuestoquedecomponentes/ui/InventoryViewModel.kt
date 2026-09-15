package com.example.meuestoquedecomponentes.ui

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.meuestoquedecomponentes.data.Categoria
import com.example.meuestoquedecomponentes.data.Componente
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// Instância do DataStore vinculada ao Context
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "kitbox_datastore")

class InventoryViewModel(application: Application) : AndroidViewModel(application) {
    private val dataStore = application.dataStore
    private val gson = Gson()

    private val KEY_COMPONENTES = stringPreferencesKey("componentes_json")

    private val _componentes = MutableStateFlow<List<Componente>>(emptyList())
    private val _termoBusca = MutableStateFlow("")
    private val _categoriaFiltro = MutableStateFlow<Categoria?>(null)
    private val _apenasFavoritos = MutableStateFlow(false)
    private val _apenasEstoqueBaixo = MutableStateFlow(false)

    init {
        // Carrega os dados do DataStore de forma assíncrona ao iniciar
        viewModelScope.launch {
            dataStore.data.collect { preferences ->
                val json = preferences[KEY_COMPONENTES]
                if (!json.isNullOrEmpty()) {
                    val type = object : TypeToken<List<Componente>>() {}.type
                    _componentes.value = gson.fromJson(json, type) ?: emptyList()
                }
            }
        }
    }

    val componentesFiltrados: StateFlow<List<Componente>> = combine(
        _componentes,
        _termoBusca,
        _categoriaFiltro,
        _apenasFavoritos,
        _apenasEstoqueBaixo
    ) { lista, termo, categoria, apenasFav, apenasBaixo ->
        lista.filter { componente ->
            val atendeBusca = termo.isBlank() ||
                    componente.nome.contains(termo, ignoreCase = true) ||
                    componente.localizacao.contains(termo, ignoreCase = true) ||
                    componente.encapsulamento.contains(termo, ignoreCase = true)

            val atendeCategoria = categoria == null || componente.categoria == categoria
            val atendeFavorito = !apenasFav || componente.ehFavorito
            val atendeEstoque = !apenasBaixo || componente.estaAbaixoMinimo

            atendeBusca && atendeCategoria && atendeFavorito && atendeEstoque
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    fun setTermoBusca(termo: String) { _termoBusca.value = termo }
    fun setCategoriaFiltro(categoria: Categoria?) { _categoriaFiltro.value = categoria }
    fun setApenasFavoritos(apenas: Boolean) { _apenasFavoritos.value = apenas }
    fun setApenasEstoqueBaixo(apenas: Boolean) { _apenasEstoqueBaixo.value = apenas }

    fun adicionarComponente(componente: Componente) {
        val novaLista = _componentes.value + componente
        salvarNoDataStore(novaLista)
    }

    fun atualizarComponente(componente: Componente) {
        val novaLista = _componentes.value.map {
            if (it.id == componente.id) componente else it
        }
        salvarNoDataStore(novaLista)
    }

    fun removerComponente(id: String) {
        val novaLista = _componentes.value.filterNot { it.id == id }
        salvarNoDataStore(novaLista)
    }

    fun toggleFavorito(id: String) {
        val novaLista = _componentes.value.map {
            if (it.id == id) it.copy(ehFavorito = !it.ehFavorito) else it
        }
        salvarNoDataStore(novaLista)
    }

    private fun salvarNoDataStore(novaLista: List<Componente>) {
        _componentes.value = novaLista
        viewModelScope.launch {
            val json = gson.toJson(novaLista)
            dataStore.edit { preferences ->
                preferences[KEY_COMPONENTES] = json
            }
        }
    }
}