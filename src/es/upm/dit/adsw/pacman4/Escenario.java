package es.upm.dit.adsw.pacman4;

import es.upm.dit.adsw.pacman4.R;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

/**
 * Singleton. Mantiene los elementos del juego que son unicos.
 * 
 * @version 5.5.2014
 *
 */
public class Escenario {
	private static final String TAG = "Escenario";

	private static Escenario instance = new Escenario();

	/**
	 * Constructor privado: inaccesible. Nadie mas puede crear objetos de este
	 * tipo.
	 */
	private Escenario() {
		terreno = new Terreno(N);
		terreno.ponSituacionInicial();
	}

	/**
	 * Getter.
	 * @return el objeto de tipo escenario.
	 */
	public static Escenario getInstance() {
		return instance;
	}

	/**
	 * Dimensiones del cuadrado.
	 */
	public static final int N = 10;

	/**
	 * El cuadrado en el que se mueve pacman.
	 */
	private Terreno terreno;

	/**
	 * Interfaz de usuaio. Graphical User Interface.
	 */
	private GUI gui;

	/**
	 * Adaptador para la base de datos.
	 */
	private MyDbAdapter dbAdapter;

	/**
	 * Setter.
	 *
	 * @param gui
	 *            interfaz de usuario.
	 */
	public void setView(GUI gui) {
		this.gui = gui;
		terreno.setView(gui);
	}

	/**
	 * Getter.
	 *
	 * @return terreno.
	 */
	public Terreno getTerreno() {
		return terreno;
	}

	/**
	 * Getter.
	 *
	 * @return interfaz de usuario.
	 */
	public GUI getGui() {
		return gui;
	}

	/**
	 * Setter.
	 *
	 * @param dbAdapter
	 *            adaptador para la base de datos.
	 */
	public void setDbAdapter(MyDbAdapter dbAdapter) {
		this.dbAdapter = dbAdapter;
	}

	/**
	 * Getter.
	 *
	 * @return adaptador para la base de datos.
	 */
	public MyDbAdapter getDbAdapter() {
		return dbAdapter;
	}

	/**
	 * Fuerza que se actualice la pantalla.
	 */
	public void pintar() {
		gui.postInvalidate();
	}

	/**
	 * RESET.
	 */
	public void restart() {
		terreno.paraMoviles();
		terreno.limpiaTerreno();
		terreno.ponSituacionInicial();

		pintar();
	}

	/**
	 * Actualiza los elementos del juego con algunas preferencias.
	 */
	public void cargaPreferencias() {
		Context context = gui.getContext();
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		for (int x = 0; x < terreno.getN(); x++) {
			for (int y = 0; y < terreno.getN(); y++) {
				Casilla casilla = terreno.getCasilla(x, y);
				Movil movil = casilla.getMovil();
				if (movil != null) {
					if (movil.getClass() == Fantasma01.class)
						cargaPreferencias((Fantasma01) movil, preferences);
					if (movil.getClass() == Depredador.class)
						cargaPreferenciasD((Depredador) movil, preferences);
				}
			}
		}
		if (gui != null) {
			String bg = preferences.getString("pref_background", "null");
			if (bg.equals("neb"))
				gui.setMyBackground(R.drawable.neb);
			else if (bg.equals("degradado"))
				gui.setMyBackground(R.drawable.degradado);
			else
				gui.setMyBackground(0);
		}
	}

	private void cargaPreferencias(Fantasma01 movil,
			SharedPreferences preferences) {
		int delta = Integer.parseInt(preferences.getString(
				"pref_velocidad_fantasma", "1000"));
		movil.setDelta(delta);
	}

	private void cargaPreferenciasD(Depredador movil,
			SharedPreferences preferences) {
		int delta = Integer.parseInt(preferences.getString(
				"pref_velocidad_depredador", "1000"));
		movil.setDelta(delta);
	}

	/**
	 * Ponemos un movil en el terreno.
	 *
	 * @param itemId
	 *            Identificacion del movil. Ver menu.
	 * @return TRUE si se pudo anadir.
	 */
	public boolean addMovil(int itemId) {
		Casilla seleccionada = gui.getSeleccionada();
		if (seleccionada == null)
			return false;

		Escenario escenario = Escenario.getInstance();
		Terreno terreno = escenario.getTerreno();

		Movil movil = null;
		switch (itemId) {
		case R.id.fantasma00: {
			movil = new Fantasma01(terreno);
			break;
		}
		case R.id.depredador: {
			movil = new Depredador(terreno);
			break;
		}

		default:
			return false;
		}

		terreno.put(seleccionada.getX(), seleccionada.getY(), movil);
		cargaPreferencias();
		Thread thread = new Thread((Runnable) movil);
		thread.start();
		pintar();
		return true;
	}

	/**
	 * Muestra un mensaje en una pantalla emergente.
	 * A partir de la GUI localiza el contexto y la actividad.
	 * A partir de la actividad localiza la UI thread.
	 * Lanza la tostada en la UI thread.
	 *
	 * @param mensaje
	 *            texto a presentar en la ventana.
	 */
	public void muestra(final String mensaje) {
		final Context context = gui.getContext();
		Activity activity = (Activity) context;
		activity.runOnUiThread(new Runnable() {
			public void run() {
				Toast toast = Toast.makeText(context, mensaje,
						Toast.LENGTH_LONG);
				toast.show();
			}
		});
	}
}
