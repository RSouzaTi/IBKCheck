package br.com.ibk.check.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import br.com.ibk.check.data.local.LeituraDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LeituraViewModel(private val dao: LeituraDao) : ViewModel() {

    // Transforma os dados brutos do banco em números para o gráfico
    fun obterDadosGrafico(estufaId: String): Flow<List<Float>> {
        return dao.buscarHistoricoUmidade(estufaId).map { lista ->
            lista.mapNotNull { 
                it.valor.replace(",", ".").toFloatOrNull()
            }
        }
    }

    // Factory: O "ajudante" que ensina o Android a criar seu ViewModel com o DAO
    class Factory(private val dao: LeituraDao) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LeituraViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return LeituraViewModel(dao) as T
            }
            throw IllegalArgumentException("ViewModel desconhecido")
        }
    }
}