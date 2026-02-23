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
}