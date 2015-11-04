package es.upm.dit.adsw.pacman4;

import android.util.Log;
import es.upm.dit.adsw.pacman4.R;

/**
 * Uno de los moviles sobre el terreno. El mas importante, por cierto.
 * 
 * @version 1.4.2014
 */
public class Jugador extends Movil {
	private static final String TAG = "Jugador";

	/**
	 * El terreno en el que se mueve.
	 */
	private final Terreno terreno;

	/**
	 * Casilla en la que esta.
	 */
	private Casilla casilla;

	/**
	 * TRUE si no ha muerto.
	 */
	private boolean vivo;

	/**
	 * Constructor.
	 *
	 * @param terreno
	 *            para que el jugador pueda actuar en funci�n de su entorno.
	 */
	public Jugador(Terreno terreno) {
		this.terreno = terreno;
		setImage(R.drawable.user);
		vivo = true;
	}

	/**
	 * Getter.
	 *
	 * @return en que casilla me encuentro.
	 */
	public Casilla getCasilla() {
		return casilla;
	}

	/**
	 * Setter. Ademas, si la casilla es objetivo y el jugador esta vivo, el
	 * juego termina, ganando el jugador.
	 *
	 * @param casilla
	 *            en que casilla me colocan.
	 */
	public void setCasilla(Casilla casilla) {
		this.casilla = casilla;
		if (casilla.isObjetivo()) {
			terreno.setEstado(EstadoJuego.GANA_JUGADOR);
			vivo = false;
		}
	}

	/**
	 * El jugador deja de estar vivo, bien porque es devorado por un fantasma o
	 * porque se acaba el juego. Si estaba vivo y muere devorado, el juego
	 * termina perdiendo el jugador.
	 *
	 * @param devorado
	 *            true si muere devorado por un fantasma.
	 */
	public void muere(boolean devorado) {
		if (devorado)
			terreno.setEstado(EstadoJuego.PIERDE_JUGADOR);
		vivo = false;
	}

	/**
	 * Alguien pregunta si mpuedo moverme ahora o en el futuro en una cierta
	 * direccion.
	 *
	 * @param direccion
	 *            direccion en la que intento moverme.
	 * @return 0 - si puedo moverme en esa direccion. 1 - si ahora mismo no
	 *         puedo, pero en el futuro puede que si. 2 - si no puedo moverme,
	 *         ni ahora ni nunca mas.
	 */
	public int puedoMoverme(Direccion direccion) {
		if (!vivo)
			return 2;
		if (terreno.hayPared(casilla, direccion))
			return 2;
		return 0;
	}
}
