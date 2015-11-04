package es.upm.dit.adsw.pacman4;

import java.util.Random;

import es.upm.dit.adsw.pacman4.R;

/**
 * Uno de los moviles sobre el terreno. Un fantasma basico, con movimiento
 * erratico, aunque tiende a mantenerse en una cierta direccion hasta que choca
 * con una pared y cambia de direccion.
 * 
 * @version 24.4.2014
 */
public class Fantasma01 extends Movil implements Runnable {
	private static final String TAG = "Fantasma01";

	private final Terreno terreno;

	private Casilla casilla;

	private volatile long delta = 500;
	private boolean vivo;
	private boolean devorado;

	/**
	 * Constructor. Usa super.loadImage() para inicializar la imagen.
	 *
	 * @param terreno
	 *            el fantasma apunta el Terreno en el que va a estar
	 *            funcionando.
	 * @see Movil#loadImage(String)
	 */
	public Fantasma01(Terreno terreno) {
		this.terreno = terreno;
		setImage(R.drawable.fantasma_rojo);
		vivo = true;
	}

	/**
	 * Setter.
	 *
	 * @param delta
	 *            Intervalo entre movimientos. Cuando mas alto es el valor, mas
	 *            despacio de mueve.
	 */
	public void setDelta(int delta) {
		this.delta = delta;
	}

	/**
	 * Getter.
	 *
	 * @return la casilla en la que esta en cada momento.
	 */
	public Casilla getCasilla() {
		return casilla;
	}

	/**
	 * Setter.
	 *
	 * @param casilla
	 *            en que casilla me colocan.
	 */
	public void setCasilla(Casilla casilla) {
		this.casilla = casilla;
	}

	/**
	 * El fantasma deja de estar vivo, bien porque es devorado por otro movil o
	 * porque se acaba el juego.
	 *
	 * @param devorado
	 *            true si muere devorado por otro movil.
	 */
	public void muere(boolean devorado) {
		vivo = false;
	}

	/**
	 * Alguien pregunta si mpuedo moverme ahora o en el futuro en una cierta
	 * direccion.
	 *
	 * @param direccion
	 *            direccion en la que intento moverme.
	 * @return 0 = puedo moverme en esa direccion. Porque esta vacia, o porque
	 *         esta el jugador y me lo como.<br/>
	 *         1 = ahora mismo no puedo, pero en el futuro puede que si.
	 *         Sugerencia: tropiezo con otro fantasma.<br/>
	 *         2 = no puedo moverme, ni ahora ni nunca.
	 */
	public int puedoMoverme(Direccion direccion) {
		if (!vivo)
			return 2;
		if (terreno.hayPared(casilla, direccion))
			return 2;
		Casilla destino = terreno.getCasilla(casilla, direccion);
		if (destino == null)
			return 2;
		Movil movil = destino.getMovil();
		if (movil != null && movil.getClass() != Jugador.class)
			return 1;
		return 0;
	}

	/**
	 * El fantasma se activa. Sugerencia: mientras esta vivo, elige una
	 * direccion al azar, e intenta moverse en esa direccion.
	 * <p>
	 * </p>
	 * Luego duerme DELAY secs y vuelve a moverse.
	 *
	 * @see Terreno#move(Movil, Direccion);
	 */
	public void run() {
		Random random = new Random();
		Direccion[] direcciones = Direccion.values();

		Direccion direccion = Direccion.NORTE;

		while (vivo) {
			if (casilla != null && !isPause()) {
				if (terreno.hayPared(casilla, direccion))
					direccion = direcciones[random.nextInt(direcciones.length)];

				// no es imprescindible, pero da viveza al fantasma
				if (terreno.hayPared(casilla, direccion))
					continue;
				terreno.move(this, direccion);
			}

			try {
				Thread.sleep(delta);
			} catch (InterruptedException ignored) {
			}
		}
	}

}
