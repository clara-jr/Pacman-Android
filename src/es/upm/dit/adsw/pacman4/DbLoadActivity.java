package es.upm.dit.adsw.pacman4;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class DbLoadActivity extends ListActivity {
	private static final String TAG = "DbLoadActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_db);

		Escenario escenario = Escenario.getInstance();
		MyDbAdapter dbAdapter= escenario.getDbAdapter();
		Cursor cursor = dbAdapter.selectAll();

		startManagingCursor(cursor);
		String[] columns = { MyDbAdapter.COL_NAME, MyDbAdapter.COL_DATE };
		int[] to = new int[] { android.R.id.text1, android.R.id.text2 };
		SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(this,
				android.R.layout.simple_list_item_2, cursor, columns, to);
		setListAdapter(cursorAdapter);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onListItemClick(ListView listView, View view, int position, long id) {
		super.onListItemClick(listView, view, position, id);
		ListAdapter listAdapter = listView.getAdapter();
		CursorAdapter cursorAdapter = (CursorAdapter) listAdapter;
		Cursor cursor = cursorAdapter.getCursor();
		cursor.moveToPosition(position);

		Escenario escenario = Escenario.getInstance();
		Terreno terreno = escenario.getTerreno();
		DbUtils utilidadesBD = new DbUtils();
		
		terreno.limpiaTerreno();
		
		utilidadesBD.setStringCasillas(terreno, (cursor.getString(cursor.getColumnIndex(MyDbAdapter.COL_CASILLAS))));
		utilidadesBD.setStringMoviles(terreno, (cursor.getString(cursor.getColumnIndex(MyDbAdapter.COL_MOVILES))));
		
		escenario.addMovil(cursor.getInt(cursor.getColumnIndex(MyDbAdapter.COL_MOVILES)));
		escenario.getGui().invalidate();
		terreno.setView(escenario.getGui());

		cursor.close();
		finish();
	}
}
