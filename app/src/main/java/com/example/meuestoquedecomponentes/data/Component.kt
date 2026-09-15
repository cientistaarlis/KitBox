package com.example.meuestoquedecomponentes.data

enum class Categoria(val displayName: String, val cor: Long) {
    RESISTOR("Resistor", 0xFF4CAF50),
    CAPACITOR("Capacitor", 0xFF2196F3),
    SENSOR("Sensor", 0xFFFF9800),
    MICROCONTROLADOR("Microcontrolador", 0xFF9C27B0),
    OUTRO("Outro", 0xFF607D8B)
}

enum class UnidadeMedida(val simbolo: String) {
    OHM("Ω"),
    KILO_OHM("kΩ"),
    MEGA_OHM("MΩ"),
    PICOFARAD("pF"),
    NANOFARAD("nF"),
    MICROFARAD("µF"),
    MILIFARAD("mF"),
    MICROHENRY("µH"),
    MILIHENRY("mH"),
    VOLT("V"),
    UNIDADE("un")
}

data class Componente(
    val id: String = java.util.UUID.randomUUID().toString(),
    val nome: String,
    val categoria: Categoria,
    val valorNominal: Double = 0.0,
    val unidadeMedida: UnidadeMedida = UnidadeMedida.UNIDADE,
    val tolerancia: Double? = null,
    val tensaoTrabalho: Double? = null,
    val encapsulamento: String = "",
    val quantidade: Int = 0,
    val estoqueMinimo: Int = 5,
    val localizacao: String = "",
    val ehFavorito: Boolean = false,
    val observacoes: String = "",
    val caminhoFoto: String? = null
) {
    val valorFormatado: String
        get() = if (valorNominal > 0) {
            "${valorNominal} ${unidadeMedida.simbolo}"
        } else {
            "—"
        }

    val estaAbaixoMinimo: Boolean
        get() = quantidade < estoqueMinimo

    val estaProximoMinimo: Boolean
        get() = quantidade < estoqueMinimo * 2 && !estaAbaixoMinimo
}