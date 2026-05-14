package br.com.ibk.check.data.local // Verifique se o package bate com sua pasta

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LeituraDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun salvarLeitura(leitura: LeituraEntity)

    @Query("SELECT * FROM leituras_table")
    fun buscarTodasLeituras(): Flow<List<LeituraEntity>>

    @Query("DELETE FROM leituras_table")
    suspend fun limparDadosDoTurno()

    /**
     * Busca o histórico de umidade para o gráfico de tendência.
     * Usamos 'dataHoraMillis ASC' para que o gráfico desenhe da esquerda (antigo)
     * para a direita (recente).
     */
    @Query("""
        SELECT * FROM leituras_table 
        WHERE idChave LIKE :estufaId || '%' 
        AND idChave LIKE '%_umid' 
        ORDER BY dataHoraMillis ASC 
        LIMIT 7
    """)
    fun buscarHistoricoUmidade(estufaId: String): Flow<List<LeituraEntity>>
}