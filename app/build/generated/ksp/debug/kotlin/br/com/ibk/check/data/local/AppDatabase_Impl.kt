package br.com.ibk.check.`data`.local

import androidx.room.InvalidationTracker
import androidx.room.RoomOpenDelegate
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.room.util.TableInfo
import androidx.room.util.TableInfo.Companion.read
import androidx.room.util.dropFtsSyncTriggers
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import javax.`annotation`.processing.Generated
import kotlin.Lazy
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.MutableSet
import kotlin.collections.Set
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.mutableSetOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class AppDatabase_Impl : AppDatabase() {
  private val _leituraDao: Lazy<LeituraDao> = lazy {
    LeituraDao_Impl(this)
  }

  protected override fun createOpenDelegate(): RoomOpenDelegate {
    val _openDelegate: RoomOpenDelegate = object : RoomOpenDelegate(1, "fe1514336de698676cbbb5350c6c314e", "81b5169195d3bdb882e59b1a1413f330") {
      public override fun createAllTables(connection: SQLiteConnection) {
        connection.execSQL("CREATE TABLE IF NOT EXISTS `leituras_table` (`idChave` TEXT NOT NULL, `valor` TEXT NOT NULL, `dataHoraMillis` INTEGER NOT NULL, PRIMARY KEY(`idChave`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        connection.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'fe1514336de698676cbbb5350c6c314e')")
      }

      public override fun dropAllTables(connection: SQLiteConnection) {
        connection.execSQL("DROP TABLE IF EXISTS `leituras_table`")
      }

      public override fun onCreate(connection: SQLiteConnection) {
      }

      public override fun onOpen(connection: SQLiteConnection) {
        internalInitInvalidationTracker(connection)
      }

      public override fun onPreMigrate(connection: SQLiteConnection) {
        dropFtsSyncTriggers(connection)
      }

      public override fun onPostMigrate(connection: SQLiteConnection) {
      }

      public override fun onValidateSchema(connection: SQLiteConnection): RoomOpenDelegate.ValidationResult {
        val _columnsLeiturasTable: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsLeiturasTable.put("idChave", TableInfo.Column("idChave", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsLeiturasTable.put("valor", TableInfo.Column("valor", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsLeiturasTable.put("dataHoraMillis", TableInfo.Column("dataHoraMillis", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysLeiturasTable: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesLeiturasTable: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoLeiturasTable: TableInfo = TableInfo("leituras_table", _columnsLeiturasTable, _foreignKeysLeiturasTable, _indicesLeiturasTable)
        val _existingLeiturasTable: TableInfo = read(connection, "leituras_table")
        if (!_infoLeiturasTable.equals(_existingLeiturasTable)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |leituras_table(br.com.ibk.check.data.local.LeituraEntity).
              | Expected:
              |""".trimMargin() + _infoLeiturasTable + """
              |
              | Found:
              |""".trimMargin() + _existingLeiturasTable)
        }
        return RoomOpenDelegate.ValidationResult(true, null)
      }
    }
    return _openDelegate
  }

  protected override fun createInvalidationTracker(): InvalidationTracker {
    val _shadowTablesMap: MutableMap<String, String> = mutableMapOf()
    val _viewTables: MutableMap<String, Set<String>> = mutableMapOf()
    return InvalidationTracker(this, _shadowTablesMap, _viewTables, "leituras_table")
  }

  public override fun clearAllTables() {
    super.performClear(false, "leituras_table")
  }

  protected override fun getRequiredTypeConverterClasses(): Map<KClass<*>, List<KClass<*>>> {
    val _typeConvertersMap: MutableMap<KClass<*>, List<KClass<*>>> = mutableMapOf()
    _typeConvertersMap.put(LeituraDao::class, LeituraDao_Impl.getRequiredConverters())
    return _typeConvertersMap
  }

  public override fun getRequiredAutoMigrationSpecClasses(): Set<KClass<out AutoMigrationSpec>> {
    val _autoMigrationSpecsSet: MutableSet<KClass<out AutoMigrationSpec>> = mutableSetOf()
    return _autoMigrationSpecsSet
  }

  public override fun createAutoMigrations(autoMigrationSpecs: Map<KClass<out AutoMigrationSpec>, AutoMigrationSpec>): List<Migration> {
    val _autoMigrations: MutableList<Migration> = mutableListOf()
    return _autoMigrations
  }

  public override fun leituraDao(): LeituraDao = _leituraDao.value
}
