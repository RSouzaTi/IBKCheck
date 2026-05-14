package br.com.ibk.check.data.local


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "leituras_table")
data class LeituraEntity(
    @PrimaryKey val idChave: String, // Ex: "1_08:30_umid"
    val valor: String,
    val dataHoraMillis: Long = System.currentTimeMillis() // Novo campo!
)