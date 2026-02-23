package br.com.ibk.check.model

// Modelo para cada coleta individual
data class Leitura(
    val horario: String,
    val temperatura: String = "",
    val pressao: String = ""
)

// Modelo da Estufa atualizado
data class Estufa(
    val id: String,
    val nome: String,
    val unidadePressao: String = "bar",
    // Aqui guardamos as coletas do dia (ex: 06:30, 08:30...)
    val leituras: MutableMap<String, Leitura> = mutableMapOf()
)