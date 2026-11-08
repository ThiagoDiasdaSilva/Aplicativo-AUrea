package com.example.trabalhofinal

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.trabalhofinal.entities.Contact
import com.example.trabalhofinal.entities.TrabalhoFinalDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class TipoPastagem(val rotulo: String) {
    NATIVA("Pastagem nativa"),
    DESMATADA("Pastagem desmatada"),
    CULTIVADA_EXTENSIVA("Cultivada extensiva"),
    CULTIVADA_SEMI_INTENSIVA("Cultivada semi-intensiva"),
    CULTIVADA_INTENSIVA("Cultivada intensiva")
}

data class Area(
    val hectareTexto: String = "",
    val pastagem: TipoPastagem = TipoPastagem.NATIVA
)

data class Resultado(
    val totalHectares: Float,
    val materiaSecaKgAno: Float,
    val unidadesAnimais: Float
)

data class UiState(
    val nomeTexto: String = "",
    val areas: List<Area> = listOf(Area()),
    val resultado: Resultado? = null,
    val editandoId: Int? = null
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val terreno = terreno()
    private val dao = TrabalhoFinalDatabase.getInstance(application).contactDao()

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    // sessões salvas no banco
    val contatos: StateFlow<List<Contact>> = dao.getContatosPorNome()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun onNomeChange(texto: String) = _uiState.update { it.copy(nomeTexto = texto) }

    fun onHectareChange(index: Int, texto: String) =
        atualizarArea(index) { it.copy(hectareTexto = texto) }

    fun onPastagemChange(index: Int, pastagem: TipoPastagem) =
        atualizarArea(index) { it.copy(pastagem = pastagem) }

    fun adicionarArea() = _uiState.update {
        it.copy(areas = it.areas + Area(), resultado = null)
    }

    fun removerArea(index: Int) = _uiState.update {
        if (it.areas.size <= 1) it
        else it.copy(areas = it.areas.filterIndexed { i, _ -> i != index }, resultado = null)
    }

    // qualquer mudança limpa o resultado antigo
    private fun atualizarArea(index: Int, transformar: (Area) -> Area) = _uiState.update {
        it.copy(
            areas = it.areas.mapIndexed { i, a -> if (i == index) transformar(a) else a },
            resultado = null
        )
    }

    // áreas com hectare válido (> 0)
    private fun areasValidas(): List<Pair<TipoPastagem, Float>> =
        _uiState.value.areas.mapNotNull { a ->
            a.hectareTexto.paraFloat()?.takeIf { it > 0f }?.let { a.pastagem to it }
        }

    fun calcular() {
        val validas = areasValidas()
        if (validas.isEmpty()) return

        val total = validas.sumOf { it.second.toDouble() }.toFloat()
        val materiaSeca = validas.sumOf { (tipo, ha) ->
            when (tipo) {
                TipoPastagem.NATIVA -> terreno.pastagemNativa(ha)
                TipoPastagem.DESMATADA -> terreno.pastagemDesmatada(ha)
                TipoPastagem.CULTIVADA_EXTENSIVA -> terreno.pastagemCultivadaExtensiva(ha)
                TipoPastagem.CULTIVADA_SEMI_INTENSIVA -> terreno.pastagemSemiIntensiva(ha)
                TipoPastagem.CULTIVADA_INTENSIVA -> terreno.pastagemCultivadaIntensiva(ha)
            }.toDouble()
        }.toFloat()

        _uiState.update {
            it.copy(resultado = Resultado(total, materiaSeca, terreno.capacidadeUA(materiaSeca)))
        }
    }

    fun salvar() {
        val validas = areasValidas()
        if (validas.isEmpty()) return
        val state = _uiState.value
        viewModelScope.launch {
            dao.upsertContact(
                Contact(
                    id = state.editandoId ?: 0,   // id existente = atualiza; 0 = insere novo
                    nome = state.nomeTexto.ifBlank { "Sem nome" },
                    hectare = validas.sumOf { it.second.toDouble() }.toFloat(),
                    pastagem = validas.joinToString(", ") { "${it.first.rotulo} (${it.second} ha)" },
                    areas = validas.joinToString("|") { "${it.first.name}:${it.second}" }
                )
            )
            _uiState.value = UiState()
        }
    }

    fun excluir(contact: Contact) {
        viewModelScope.launch { dao.deleteContact(contact) }
    }

    fun editar(c: Contact) {
        _uiState.value = UiState(
            nomeTexto = c.nome,
            areas = c.areas.decodificarAreas(),
            editandoId = c.id
        )
        calcular()
    }

    fun cancelarEdicao() {
        _uiState.value = UiState()
    }

    private fun String.decodificarAreas(): List<Area> =
        split("|").mapNotNull { parte ->
            val p = parte.split(":")
            if (p.size != 2) return@mapNotNull null
            val tipo = TipoPastagem.entries.firstOrNull { it.name == p[0] }
                ?: return@mapNotNull null
            Area(hectareTexto = p[1], pastagem = tipo)
        }.ifEmpty { listOf(Area()) }

    // aceita vírgula, comum em teclado brasileiro
    private fun String.paraFloat(): Float? = replace(',', '.').toFloatOrNull()
}