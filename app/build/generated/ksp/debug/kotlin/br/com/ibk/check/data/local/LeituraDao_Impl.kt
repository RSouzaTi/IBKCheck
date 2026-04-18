package br.com.ibk.check.`data`.local

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import javax.`annotation`.processing.Generated
import kotlin.Int
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class LeituraDao_Impl(
  __db: RoomDatabase,
) : LeituraDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfLeituraEntity: EntityInsertAdapter<LeituraEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfLeituraEntity = object : EntityInsertAdapter<LeituraEntity>() {
      protected override fun createQuery(): String = "INSERT OR REPLACE INTO `leituras_table` (`idChave`,`valor`) VALUES (?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: LeituraEntity) {
        statement.bindText(1, entity.idChave)
        statement.bindText(2, entity.valor)
      }
    }
  }

  public override suspend fun salvarLeitura(leitura: LeituraEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfLeituraEntity.insert(_connection, leitura)
  }

  public override fun buscarTodasLeituras(): Flow<List<LeituraEntity>> {
    val _sql: String = "SELECT * FROM leituras_table"
    return createFlow(__db, false, arrayOf("leituras_table")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfIdChave: Int = getColumnIndexOrThrow(_stmt, "idChave")
        val _columnIndexOfValor: Int = getColumnIndexOrThrow(_stmt, "valor")
        val _result: MutableList<LeituraEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: LeituraEntity
          val _tmpIdChave: String
          _tmpIdChave = _stmt.getText(_columnIndexOfIdChave)
          val _tmpValor: String
          _tmpValor = _stmt.getText(_columnIndexOfValor)
          _item = LeituraEntity(_tmpIdChave,_tmpValor)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun limparDadosDoTurno() {
    val _sql: String = "DELETE FROM leituras_table"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
