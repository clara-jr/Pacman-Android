package es.upm.dit.adsw.pacman4;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


import android.util.Log;

/**
 * El terreno de juego. Funciona como monitor.
 * 
 * @version 6.5.2014
 */
public class Terreno {
	private static final String TAG = "Terreno";

	private Random random = new Random();

	private GUI view;

	private final Casilla[][] casillas;
	private Jugador jugador;

	private EstadoJuego estado;

	/**
	 * Constructor.
	 *
	 * @param n
	 *            numero de casillas en horizontal (X) y vertical (Y).
	 */
	public Terreno(int n) {
		casillas = new Casilla[n][n];

		for (int x = 0; x < n; x++) {
			for (int y = 0; y < n; y++) {
				casillas[x][y] = new Casilla(x, y);
			}
		}
	}

	/**
	 * Getter.
	 *
	 * @return devuelve la vista.
	 */
	public synchronized GUI getView() {
		return view;
	}

	/**
	 * Setter.
	 *
	 * @param view
	 *            vista para presentar el terreno.
	 */
	public synchronized void setView(GUI view) {
		this.view = view;
	}

	/**
	 * Getter.
	 *
	 * @return dimension horizontal o vertical del terreno.
	 */
	public synchronized int getN() {
		return casillas.length;
	}

	/**
	 * Organiza el laberinto.
	 */
	public synchronized void ponParedes() {

		int N = getN();
		Direccion[] direcciones = Direccion.values();
		// pongo todas las paredes
		for (int x=0; x<N; x++) {
			for (int y=0; y<N; y++) {
				Casilla casilla = getCasilla(x,y);
				for (Direccion direccion: direcciones) {
					ponPared(casilla, direccion);
				}
			}
		}
		// quito el mínimo para interconectar todo
		List<Casilla> conectadas = new ArrayList<Casilla>();
		List<Pared> paredes = new ArrayList<Pared>();
		Random random = new Random();
		int x0 = random.nextInt(N);
		int y0 = random.nextInt(N);
		Casilla casilla0 = getCasilla(x0, y0);
		conectadas.add(casilla0);
		for (Direccion direccion : direcciones)
			paredes.add(new Pared(casilla0, direccion));
		while (paredes.size() > 0) {
			int i = random.nextInt(paredes.size());
			Pared pared = paredes.remove(i);
			Casilla origen = pared.getCasilla();
			Direccion direccion = pared.getDireccion();
			Casilla destino =
					getCasilla(origen.getX(),origen.getY(),direccion);
			if (destino != null && !conectadas.contains(destino)) {
				quitaPared(origen, direccion);
				conectadas.add(destino);
				for (Direccion d : direcciones) {
					if (destino.hayPared(d))
						paredes.add(new Pared(destino,d));
				}
			}
		}
		// quito unas cuantas paredes más para abrir rutas alternativas
		for (int q = 0; q < 2 * N; q++) {
			int x = random.nextInt(N);
			int y = random.nextInt(N);
			Casilla casilla = getCasilla(x,y);
			Direccion direccion =
					direcciones[random.nextInt(direcciones.length)];
			quitaPared(casilla, direccion);
		}
	}

	/**
	 * Casilla en una cierta posicion.
	 *
	 * @param x
	 *            posicion horizontal (0..N-1).
	 * @param y
	 *            posicion vertical (0..N-1).
	 * @return casilla en esas coordenadas.
	 * @throws ArrayIndexOutOfBoundsException
	 *             si las coordenadas estan fuera del tablero.
	 */
	public synchronized Casilla getCasilla(int x, int y) {
		return casillas[x][y];
	}

	/**
	 * Casilla anexa en una cierta direccion.
	 *
	 * @param casilla
	 *            casilla origen.
	 * @param direccion
	 *            direccion en la que miramos.
	 * @return casilla anexa en la direccion indicada o null si no existe, por
	 *         ejemplo al borde del terreno.
	 */
	public synchronized Casilla getCasilla(Casilla casilla, Direccion direccion) {
		return getCasilla(casilla.getX(), casilla.getY(), direccion);
	}

	/**
	 * Casilla anexa en una cierta direccion.
	 *
	 * @param x
	 *            posicion horizontal (0..N-1).
	 * @param y
	 *            posicion vertical (0..N-1).
	 * @param direccion
	 *            direccion en la que miramos.
	 * @return casilla anexa en la direccion indicada o null si no existe, por
	 *         ejemplo al borde del terreno.
	 */
	public synchronized Casilla getCasilla(int x, int y, Direccion direccion) {
		try {
			switch (direccion) {
			case NORTE:
				return getCasilla(x, y + 1);
			case SUR:
				return getCasilla(x, y - 1);
			case ESTE:
				return getCasilla(x + 1, y);
			case OESTE:
				return getCasilla(x - 1, y);
			}
		} catch (Exception ignored) {
		}
		return null;
	}

	/**
	 * Coloca un movil sobre el terreno. Indirectamente, hay una casilla
	 * afectada.
	 *
	 * @param x
	 *            posicion horizontal (0..N-1).
	 * @param y
	 *            posicion vertical (0..N-1).
	 * @param movil
	 *            lo que queremos colocar.
	 * @return true si se puede y queda colocado; false si no es posible.
	 */
	public synchronized boolean put(int x, int y, Movil movil) {
		try {
			Casilla casilla = getCasilla(x, y);
			if (casilla.getMovil() != null)
				return false;
			casilla.setMovil(movil);
			movil.setCasilla(casilla);
			if (movil instanceof Jugador)
				jugador = (Jugador)movil;
			Escenario.getInstance().pintar();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Intenta trasladar el movil en la direccion indicada, devolviendo la
	 * casilla en la que acaba el movil. Si no puede moverse, devuelve la misma
	 * casilla origen. Si en el destino hay un fantasma, se lo 'come' = muere().
	 *
	 * @param movil
	 *            el que queremos mover.
	 * @param direccion
	 *            direccion en la que nos queremos desplazar.
	 * @return la casilla a donde se mueve el movil, si es posible moverse.
	 * @see Movil#puedoMoverme(Direccion)
	 */
	public synchronized Casilla move(Movil movil, Direccion direccion) {
		Casilla origen = movil.getCasilla();
		try {
			while (movil.puedoMoverme(direccion) == 1)
				wait();
			if (movil.puedoMoverme(direccion) == 2)
				return origen;

			Casilla destino = getCasilla(origen, direccion);
			if (destino == null)
				return origen;

			if ((movil.puedoMoverme(direccion) == 0) && (movil instanceof Jugador) && (destino.isLlave()))
				quitaTrampasLlaves();

			while ((movil.puedoMoverme(direccion) == 0) && !(movil instanceof Jugador) && (origen.isTrampa()))
				wait();


			Movil m2 = destino.getMovil();
			if (m2 != null) {
				if (m2.getClass() == Jugador.class)
					m2.muere(true);
				else
					movil.muere(true);
			}
			origen.setMovil(null);
			destino.setMovil(movil);
			movil.setCasilla(destino);

			Escenario escenario = Escenario.getInstance();
			escenario.pintar();
			if (estado == EstadoJuego.GANA_JUGADOR) {
				paraMoviles();
				escenario.muestra("El jugador gana");
			}
			if (estado == EstadoJuego.PIERDE_JUGADOR) {
				paraMoviles();
				escenario.muestra("El jugador pierde");
			}
			return destino;
		} catch (Exception e) {
			// e.printStackTrace();
			return origen;
		} finally {
			notifyAll();
		}
	}

	/**
	 * Plantamos una pared en el terreno. Las paredes separan casillas, de forma
	 * que hay 2 casillas afectadas.
	 *
	 * @param casilla
	 *            una de las casillas en la que colocar la pared.
	 * @param direccion
	 *            en que lado de la casilla se coloca.
	 */
	public synchronized void ponPared(Casilla casilla, Direccion direccion) {
		casilla.ponPared(direccion);
		Casilla anexa = getCasilla(casilla, direccion);
		if (anexa != null)
			anexa.ponPared(direccion.opuesta());
	}

	/**
	 * Quitamos una pared del terreno. Las paredes separan casillas, de forma
	 * que hay 2 casillas afectadas.
	 *
	 * @param casilla
	 *            una de las casillas de la que retirar la pared.
	 * @param direccion
	 *            de que lado de la casilla se retira.
	 */
	public synchronized void quitaPared(Casilla casilla, Direccion direccion) {
		casilla.quitaPared(direccion);
		Casilla anexa = getCasilla(casilla, direccion);
		if (anexa != null)
			anexa.quitaPared(direccion.opuesta());
	}

	/**
	 * Pregunta si mirando desde una casilla en una cierta direccion nos topamos
	 * con una pared. Si estamos al borde del terreno, tambiÈn devuelve TRUE.
	 *
	 * @param casilla
	 *            casilla desde la que miramos.
	 * @param direccion
	 *            en que direccion miramos.
	 * @return cierto si hay pared.
	 */
	public synchronized boolean hayPared(Casilla casilla, Direccion direccion) {
		if (getCasilla(casilla, direccion) == null)
			return true;
		return casilla.hayPared(direccion);
	}

	/**
	 * Marca una posicion como objetivo para que el jugador gane.
	 *
	 * @param x
	 *            posicion horizontal (0..N-1).
	 * @param y
	 *            posicion vertical (0..N-1).
	 * @return true si es posible marcar la casilla como objetivo; false si no
	 *         es posible.
	 */
	public synchronized boolean setObjetivo(int x, int y) {
		try {
			Casilla casilla = getCasilla(x, y);
			casilla.setObjetivo(true);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Congela los moviles. Mueren todos.
	 */
	public synchronized void pausaMoviles(boolean pause) {
		for (int x = 0; x < getN(); x++) {
			for (int y = 0; y < getN(); y++) {
				Casilla casilla = getCasilla(x, y);
				Movil movil = casilla.getMovil();
				if (movil != null)
					movil.pause(pause);
			}
		}
	}

	/**
	 * Para los moviles. Mueren todos.
	 */
	public synchronized void paraMoviles() {
		for (int x = 0; x < getN(); x++) {
			for (int y = 0; y < getN(); y++) {
				Casilla casilla = getCasilla(x, y);
				Movil movil = casilla.getMovil();
				if (movil != null)
					movil.muere(false);
			}
		}
	}

	/**
	 * Elimina paredes, moviles, trampas y llaves.
	 */
	public synchronized void limpiaTerreno() {
		int n = getN();

		// 1. quitamos las paredes
		for (int x = 0; x < n; x++) {
			for (int y = 0; y < n; y++) {
				Casilla casilla = getCasilla(x, y);
				casilla.quitaParedes();
			}
		}

		// 2. quitamos los moviles
		for (int x = 0; x < n; x++) {
			for (int y = 0; y < n; y++) {
				Casilla casilla = getCasilla(x, y);
				casilla.setMovil(null);
			}
		}

		// 3. quitamos trampas y llaves
		for (int i=0; i<getN(); i++) {
			for (int j=0; j<getN(); j++) {
				if (casillas[i][j].isLlave()) {
					casillas[i][j].setLlave(false);
				}
				if (casillas[i][j].isTrampa()) {
					casillas[i][j].setTrampa(false);
				}
			}
		}
	}


	public synchronized void quitaTrampasLlaves() {
		for (int i=0; i<getN(); i++) {
			for (int j=0; j<getN(); j++) {
				if (casillas[i][j].isLlave()) {
					casillas[i][j].setLlave(false);
				}
				if (casillas[i][j].isTrampa()) {
					casillas[i][j].setTrampa(false);
				}
			}
		}
	}


	/**
	 * Posicion de salida.
	 */
	public synchronized void ponSituacionInicial() {
		estado = EstadoJuego.JUGANDO;
		ponParedes();
		setObjetivo(getN() - 1, getN() - 1);
		jugador = new Jugador(this);
		put(0, 0, jugador);
	}

	/**
	 * Setter.
	 *
	 * @param estado
	 *            marca el estado del juego.
	 */
	public synchronized void setEstado(EstadoJuego estado) {
		this.estado = estado;
	}

	public synchronized EstadoJuego getEstado() {
		return estado;
	}

	/**
	 * Getter.
	 *
	 * @return movil que hace de jugador.
	 */
	public synchronized Movil getJugador() {
		return jugador;
	}

}
