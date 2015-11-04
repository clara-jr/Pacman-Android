package es.upm.dit.adsw.pacman4;

/**
 * Modela jugadores y fantasmas.
 * 
 * @version 17.4.2014
 */
public abstract class Movil {
	private int imagen;
	private boolean pause;

	/**
	 * Proporciona como quiere ser presentado graficamente.
	 *
	 * @return una imagen adecuada.
	 */
	public final int getImagen() {
		return imagen;
	};

	/**
	 * Carga la imagen con la que quiere ser presentado graficamente.
	 *
	 * @param resource
	 *            en el directorio res.
	 */
	public final void setImage(int resource) {
		imagen = resource;
	}

	/**
	 * Getter.
	 *
	 * @return en que casilla me encuentro.
	 */
	public abstract Casilla getCasilla();

	/**
	 * Setter.
	 *
	 * @param casilla
	 *            en que casilla me colocan.
	 */
	public abstract void setCasilla(Casilla casilla);

	/**
	 * El movil deja de estar vivo, bien porque es devorado por otro movil o
	 * porque se acaba el juego.
	 *
	 * @param devorado
	 *            true si muere devorado por otro movil.
	 */
	public abstract void muere(boolean devorado);

	/**
	 * Alguien pregunta si mpuedo moverme ahora o en el futuro en una cierta
	 * direccion.
	 *
	 * @param direccion
	 *            direccion en la que intento moverme.
	 * @return 0 - si puedo moverme en esa direccion.<br/>
	 *         1 - si ahora mismo no puedo, pero en el futuro puede que si.<br/>
	 *         2 - si no puedo moverme, ni ahora ni nunca mas.
	 */
	public abstract int puedoMoverme(Direccion direccion);

	/**
	 * Congela al movil, sin terminar la thread.
	 * @param pause
	 */
	public void pause(boolean pause) {
		this.pause= pause;
	}

	/**
	 * Getter.
	 * @return true si el movil no debe moverse.
	 */
	public boolean isPause() {
		return pause;
	}

}
