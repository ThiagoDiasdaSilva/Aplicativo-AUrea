package com.example.trabalhofinal

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class TipoPastagem(val rotulo: String) {
    NATIVA("Pastagem nativa"),
    DESMATADA("Pastagem desmatada"),
    CULTIVADA_EXTENSIVA("Cultivada extensiva"),
    CULTIVADA_SEMI_INTENSIVA("Cultivada semi-intensiva"),
    CULTIVADA_INTENSIVA("Cultivada intensiva")
}

data class Resultado(
    val materiaSecaKgAno: Float,
    val unidadesAnimais: Float
)

data class UiState(
    val hectareTexto: String = "",
    val pastagem: TipoPastagem = TipoPastagem.NATIVA,
    val resultado: Resultado? = null
)

class MainViewModel : ViewModel() {

    private val terreno = terreno()

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    // qualquer mudança limpa o resultado antigo
    fun onHectareChange(texto: String) = _uiState.update {
        it.copy(hectareTexto = texto, resultado = null)
    }

    fun onPastagemChange(pastagem: TipoPastagem) = _uiState.update {
        it.copy(pastagem = pastagem, resultado = null)
    }

    fun calcular() {
        val state = _uiState.value
        val hectare = state.hectareTexto.paraFloat()?.takeIf { it > 0f } ?: return

        val materiaSeca: Float = when (state.pastagem) {
            TipoPastagem.NATIVA -> terreno.pastagemNativa(hectare)
            TipoPastagem.DESMATADA -> terreno.pastagemDesmatada(hectare)
            TipoPastagem.CULTIVADA_EXTENSIVA -> terreno.pastagemCultivadaExtensiva(hectare)
            TipoPastagem.CULTIVADA_SEMI_INTENSIVA -> terreno.pastagemSemiIntensiva(hectare)
            TipoPastagem.CULTIVADA_INTENSIVA -> terreno.pastagemCultivadaIntensiva(hectare)
        }

        _uiState.update {
            it.copy(resultado = Resultado(materiaSeca, terreno.capacidadeUA(materiaSeca)))
        }
    }

    // aceita vírgula, comum em teclado brasileiro
    private fun String.paraFloat(): Float? = replace(',', '.').toFloatOrNull()
}