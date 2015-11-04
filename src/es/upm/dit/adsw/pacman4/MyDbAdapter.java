package es.upm.dit.adsw.pacman4;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;

public class MyDbAdapter {
	private static final String TAG = "MyDbAdapter";

	private static final int DATABASE_VERSION = 3;
	private static final String DATABASE_NAME = "Escenarios";
	private static final String TABLE_FOTOS = "fotos";

	public static final String COL_ID = "_id";
	public static final String COL_NAME = "name";
	public static final String COL_MOVILES = "moviles";
	public static final String COL_CASILLAS = "casillas";
	public static final String COL_DATE = "date";

	private DatabaseHelper dbHelper;
	private SQLiteDatabase db;

	private final Context context;

	private static class DatabaseHelper extends SQLiteOpenHelper {
		private static final String DATABASE_CREATE;

		static {
			StringBuilder builder = new StringBuilder();
			builder.append("CREATE TABLE ").append(TABLE_FOTOS).append(" (");
			builder.append(COL_ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
			builder.append(COL_NAME).append(" TEXT, ");
			builder.append(COL_MOVILES).append(" TEXT, ");
			builder.append(COL_CASILLAS).append(" TEXT, ");
			builder.append(COL_DATE).append(" TEXT");
			builder.append(");");
			DATABASE_CREATE = builder.toString();
		}

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DATABASE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_FOTOS);
			onCreate(db);
		}
	}

	public MyDbAdapter(Context context) {
		this.context = context;
	}

	public MyDbAdapter open() throws SQLException {
		dbHelper = new DatabaseHelper(context);
		db = dbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		dbHelper.close();
	}

	public long insertFoto(String name) {
		Escenario escenario = Escenario.getInstance();
		Terreno terreno = escenario.getTerreno();
		ContentValues values = new ContentValues();
		
		values.put(COL_NAME, name);
		values.put(COL_DATE, prepareDate());
		values.put(COL_CASILLAS, DbUtils.getStringCasillas(terreno));
		values.put(COL_MOVILES, DbUtils.getStringMoviles(terreno));
		return db.insert(TABLE_FOTOS, null, values);
	}

	public Cursor selectAll() {
		String table = TABLE_FOTOS;
		String[] columns = { COL_ID, COL_NAME, COL_MOVILES, COL_CASILLAS, COL_DATE };
		String selection = null;
		String[] selectionArgs = null;
		String groupBy = null;
		String having = null;
		String orderBy = null;
		Cursor cursor = db.query(table, columns, selection, selectionArgs,
				groupBy, having, orderBy);
		return cursor;
	}

	public Cursor selectByName() {
		String table = TABLE_FOTOS;
		String[] columns = { COL_ID, COL_NAME, COL_MOVILES, COL_CASILLAS, COL_DATE };
		String selection = null;
		String[] selectionArgs = null;
		String groupBy = null;
		String having = null;
		String orderBy = COL_NAME + " ASC";
		Cursor cursor = db.query(table, columns, selection, selectionArgs,
				groupBy, having, orderBy);
		return cursor;
	}

	public Bundle selectFoto(long id) throws SQLException {
		try {
			String table = TABLE_FOTOS;
			String[] columns = { COL_ID, COL_NAME, COL_MOVILES, COL_CASILLAS, COL_DATE };
			String selection = COL_ID + "= ?";
			String[] selectionArgs = { String.valueOf(id) };
			String groupBy = null;
			String having = null;
			String orderBy = null;
			Cursor cursor = db.query(table, columns, selection, selectionArgs,
					groupBy, having, orderBy);

			cursor.moveToFirst();
			Bundle bundle = new Bundle();
			bundle.putLong(COL_ID,
					cursor.getLong(cursor.getColumnIndex(COL_ID)));
			bundle.putString(COL_NAME,
					cursor.getString(cursor.getColumnIndex(COL_NAME)));
			bundle.putInt(COL_MOVILES,
					cursor.getInt(cursor.getColumnIndex(COL_MOVILES)));
			bundle.putInt(COL_CASILLAS,
					cursor.getInt(cursor.getColumnIndex(COL_CASILLAS)));
			bundle.putString(COL_DATE,
					cursor.getString(cursor.getColumnIndex(COL_DATE)));
			return bundle;
		} catch (Exception e) {
		     Log.e(TAG, Log.getStackTraceString(e)); 
		     return null;
		}
	}
	
	public int updateFoto(long id) {
		Escenario escenario = Escenario.getInstance();
		Terreno terreno = escenario.getTerreno();
		
		ContentValues values = new ContentValues();
		values.put(COL_DATE, prepareDate());
		values.put(COL_CASILLAS, DbUtils.getStringCasillas(terreno));
		values.put(COL_MOVILES, DbUtils.getStringMoviles(terreno));

		String table = TABLE_FOTOS;
		String selection = COL_ID + "= ?";
		String[] selectionArgs = { String.valueOf(id) };
		return db.update(table, values, selection, selectionArgs);
	}

	public int deleteFoto(long id) {
		String table = TABLE_FOTOS;
		String whereClause = COL_ID + "= ?";
		String[] whereArgs = { String.valueOf(id) };
		int n = db.delete(table, whereClause, whereArgs);
		return n;
	}

	public void deleteAll() {
		String sql = String.format("DELETE FROM %s;", TABLE_FOTOS);
		db.execSQL(sql);
	}
	
	private String prepareDate() {
		String pattern = "H:mm d.M.y";
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		String date = sdf.format(new Date());
		return date;
	}
}