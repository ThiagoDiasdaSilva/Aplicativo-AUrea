package com.example.trabalhofinal

// Definição de 1 UA: animal de 450 kg que consome 2% do peso vivo por dia
object UnidadeAnimal {
    const val PESO_KG = 450f
    const val PERCENTUAL_CONSUMO = 0.02f
    const val DIAS_ANO = 365f

    val CONSUMO_ANUAL_KG: Float = PESO_KG * PERCENTUAL_CONSUMO * DIAS_ANO  // 3285 kg/ano
}

class terreno {

    // t/ha/ano x fração aproveitável x área x 1000 = kg aproveitáveis/ano
    private fun materiaSecaAproveitavel(
        hectare: Float,
        toneladasPorHectare: Float,
        aproveitamento: Float
    ): Float = hectare * toneladasPorHectare * aproveitamento * 1000f

    fun pastagemNativa(hectare: Float): Float =
        materiaSecaAproveitavel(hectare, 4f, 0.10f)

    fun pastagemDesmatada(hectare: Float): Float =
        materiaSecaAproveitavel(hectare, 4f, 0.20f)

    fun pastagemCultivadaExtensiva(hectare: Float): Float =
        materiaSecaAproveitavel(hectare, 8f, 0.40f)

    fun pastagemSemiIntensiva(hectare: Float): Float =
        materiaSecaAproveitavel(hectare, 16f, 0.60f)

    fun pastagemCultivadaIntensiva(hectare: Float): Float =
        materiaSecaAproveitavel(hectare, 48f, 0.70f)

    // converte matéria seca (kg/ano) em Unidades Animais suportadas
    fun capacidadeUA(materiaSecaKgAno: Float): Float =
        materiaSecaKgAno / UnidadeAnimal.CONSUMO_ANUAL_KG
}