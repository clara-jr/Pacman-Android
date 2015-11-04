package es.upm.dit.adsw.pacman4;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    private static final int RESULT_SETTINGS = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	Log.d(TAG, "activity.onCreate()");
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		Escenario escenario = Escenario.getInstance();
		GUI gui = (GUI) findViewById(R.id.gui);
		if (gui == null)
			Log.d(TAG, "view == null");
		escenario.setView(gui);
		escenario.cargaPreferencias();
		MyDbAdapter dbAdapter = new MyDbAdapter(this);
		dbAdapter.open();
		escenario.setDbAdapter(dbAdapter);
    }
    
	@Override
	protected void onStart() {
		super.onStart();
		Log.d(TAG, "activity.onStart()");
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "activity.onResume()");
		Escenario escenario = Escenario.getInstance();
		Terreno terreno = escenario.getTerreno();
		terreno.pausaMoviles(false);
		//escenario.cargaPreferencias();
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.d(TAG, "activity.onPause()");
		Escenario escenario = Escenario.getInstance();
		Terreno terreno = escenario.getTerreno();
		terreno.pausaMoviles(true);
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.d(TAG, "activity.onStop()");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "activity.onDestroy()");
		//tictac.parar();
	}
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
		Escenario escenario = Escenario.getInstance();
		Terreno terreno = escenario.getTerreno();

		int id = item.getItemId();
		
		switch (id) {
		
		case R.id.action_settings: {
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivityForResult(intent, RESULT_SETTINGS);
			return true;
		}

		case R.id.fantasma00: {
			GUI gui = escenario.getGui();
			Casilla seleccionada = gui.getSeleccionada();
			Fantasma01 fantasma = new Fantasma01(terreno);
			if (seleccionada != null) {
				terreno.put(seleccionada.getX(), seleccionada.getY(), fantasma);
				escenario.cargaPreferencias();
				Thread thread = new Thread((Runnable) fantasma);
				thread.start();
			}
			return true;
		}
		
		case R.id.depredador: {
			GUI gui = escenario.getGui();
			Casilla seleccionada = gui.getSeleccionada();
			Depredador fantasma = new Depredador(terreno);
			if (seleccionada != null) {
				terreno.put(seleccionada.getX(), seleccionada.getY(), fantasma);
				escenario.cargaPreferencias();
				Thread thread = new Thread((Runnable) fantasma);
				thread.start();
			}
			return true;
		}
		
		case R.id.boton_trampa: {
			GUI gui = escenario.getGui();
			Casilla seleccionada = gui.getSeleccionada();
			if (seleccionada != null) {
				seleccionada.setTrampa(true);
				escenario.pintar();
			}
			return true;
		}
		
		case R.id.boton_llave: {
			GUI gui = escenario.getGui();
			Casilla seleccionada = gui.getSeleccionada();
			if (seleccionada != null) {
				seleccionada.setLlave(true);
				escenario.pintar();
			}
			return true;
		}

		case R.id.action_db_load: {
			Intent intent = new Intent(this, DbLoadActivity.class);
			startActivity(intent);
			return true;
		}
		
		case R.id.action_db_save: {
			Intent intent = new Intent(this, DbSaveActivity.class);
			startActivity(intent);
			return true;
		}

		case R.id.button_reset: 
			escenario.restart();
			return true;
		}
		return super.onOptionsItemSelected(item);
    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == RESULT_SETTINGS) {
			Log.i(TAG, "Activity: load preferences");
			Escenario escenario = Escenario.getInstance();
			escenario.cargaPreferencias();
		}
	}
}
