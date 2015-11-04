package es.upm.dit.adsw.pacman4;

import android.util.Log;

/**
 * Metodos auxiliares para pasar serializar y restaurar informacion. Se requiere
 * esta funcionalidad para poder usar columnas TEXT en la base de datos.
 * 
 * @version 6.5.2014
 */
public class DbUtils {
	private static final String TAG = "DbUtils";

	/**
	 * Una string que representa todos los moviles en el terreno. Es util para
	 * guardar el estado en la base de datos.
	 *
	 * @param terreno
	 *            Terreno que queremos serializar.
	 * @return codificacion de los moviles en el terreno.
	 */
	public static String getStringMoviles(Terreno terreno) {
		synchronized (terreno) {
			int N = terreno.getN();
			StringBuilder builder = new StringBuilder();
			for (int x = 0; x < N; x++) {
				for (int y = 0; y < N; y++) {
					Casilla casilla = terreno.getCasilla(x, y);
					Movil movil = casilla.getMovil();
					builder.append(getCode(movil));
				}
			}
			return builder.toString();
		}
	}

	private static char getCode(Movil movil) {
		if (movil == null)
			return '.';
		Class<? extends Movil> clazz = movil.getClass();
		if (clazz == Jugador.class)
			return 'J';
		if (clazz == Fantasma01.class)
			return 'F';
		if (clazz == Depredador.class)
			return 'D';
		return '.';
	}

	/**
	 * Invierte getStringMoviles().
	 *
	 * @param terreno
	 *            Terreno que queremos cargar.
	 * @param s
	 *            string generada por getStringMoviles().
	 */
	public static void setStringMoviles(Terreno terreno, String s) {
		Log.d(TAG, "setStringMoviles: " + s);
		synchronized (terreno) {
			int N = terreno.getN();
			for (int x = 0; x < N; x++) {
				for (int y = 0; y < N; y++) {
					int idx = x * N + y;
					char codigo = s.charAt(idx);
					if (codigo == 'J') {
						Jugador jugador = new Jugador(terreno);
						terreno.put(x, y, jugador);
					} else if (codigo == 'F') {
						Fantasma01 fantasma00 = new Fantasma01(terreno);
						terreno.put(x, y, fantasma00);
						Thread thread = new Thread(fantasma00);
						thread.start();
					} else if (codigo == 'D') {
						Depredador depredador00 = new Depredador(terreno);
						terreno.put(x, y, depredador00);
						Thread thread = new Thread(depredador00);
						thread.start();
					}
				}
			}
		}
	}

	/**
	 * Una string que representa todas las paredes entre casillas en el terreno.
	 * Es util para guardar el estado en la base de datos.
	 *
	 * @param terreno
	 *            Terreno que queremos serializar.
	 *
	 * @return codificacion de las paredes en el terreno.
	 */
	public static String getStringCasillas(Terreno terreno) {
		synchronized (terreno) {
			int N = terreno.getN();
			StringBuilder builder = new StringBuilder();
			for (int x = 0; x < N; x++) {
				for (int y = 0; y < N; y++) {
					Casilla casilla = terreno.getCasilla(x, y);
					int codigo = 0;
					if (casilla.hayPared(Direccion.NORTE))
						codigo += 1;
					if (casilla.hayPared(Direccion.SUR))
						codigo += 2;
					if (casilla.hayPared(Direccion.ESTE))
						codigo += 4;
					if (casilla.hayPared(Direccion.OESTE))
						codigo += 8;
					if (casilla.isTrampa())
						codigo += 16;
					if (casilla.isLlave())
						codigo += 32;
					builder.append((char) ('A' + codigo));
				}
			}
			return builder.toString();
		}
	}

	/**
	 * Invierte getStringCasillas().
	 *
	 * @param terreno
	 *            Terreno que queremos cargar.
	 * @param s
	 *            string generada por getStringCasillas().
	 */
	public static void setStringCasillas(Terreno terreno, String s) {
		Log.d(TAG, "setStringCasillas: " + s);
		synchronized (terreno) {
			int N = terreno.getN();
			for (int x = 0; x < N; x++) {
				for (int y = 0; y < N; y++) {
					int idx = x * N + y;
					Casilla casilla = terreno.getCasilla(x, y);
					int codigo = s.charAt(idx) - 'A';
					if ((codigo & 1) != 0)
						casilla.ponPared(Direccion.NORTE);
					if ((codigo & 2) != 0)
						casilla.ponPared(Direccion.SUR);
					if ((codigo & 4) != 0)
						casilla.ponPared(Direccion.ESTE);
					if ((codigo & 8) != 0)
						casilla.ponPared(Direccion.OESTE);
					if ((codigo & 16) != 0)
						casilla.setTrampa(true);
					if ((codigo & 32) != 0)
						casilla.setLlave(true);
				}
			}
		}
	}
}
