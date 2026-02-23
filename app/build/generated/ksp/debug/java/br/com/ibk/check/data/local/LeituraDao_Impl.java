package br.com.ibk.check.data.local;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class LeituraDao_Impl implements LeituraDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<LeituraEntity> __insertionAdapterOfLeituraEntity;

  private final SharedSQLiteStatement __preparedStmtOfLimparDadosDoTurno;

  public LeituraDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfLeituraEntity = new EntityInsertionAdapter<LeituraEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `leituras_table` (`idChave`,`valor`) VALUES (?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final LeituraEntity entity) {
        statement.bindString(1, entity.getIdChave());
        statement.bindString(2, entity.getValor());
      }
    };
    this.__preparedStmtOfLimparDadosDoTurno = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM leituras_table";
        return _query;
      }
    };
  }

  @Override
  public Object salvarLeitura(final LeituraEntity leitura,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfLeituraEntity.insert(leitura);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object limparDadosDoTurno(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfLimparDadosDoTurno.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfLimparDadosDoTurno.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<LeituraEntity>> buscarTodasLeituras() {
    final String _sql = "SELECT * FROM leituras_table";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"leituras_table"}, new Callable<List<LeituraEntity>>() {
      @Override
      @NonNull
      public List<LeituraEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfIdChave = CursorUtil.getColumnIndexOrThrow(_cursor, "idChave");
          final int _cursorIndexOfValor = CursorUtil.getColumnIndexOrThrow(_cursor, "valor");
          final List<LeituraEntity> _result = new ArrayList<LeituraEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final LeituraEntity _item;
            final String _tmpIdChave;
            _tmpIdChave = _cursor.getString(_cursorIndexOfIdChave);
            final String _tmpValor;
            _tmpValor = _cursor.getString(_cursorIndexOfValor);
            _item = new LeituraEntity(_tmpIdChave,_tmpValor);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
