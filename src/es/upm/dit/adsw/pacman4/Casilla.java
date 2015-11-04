package es.upm.dit.adsw.pacman4;

import java.util.HashSet;
import java.util.Set;


/**
 * Recopila la informacion pertinente a una casilla del terreno. Es basicamente
 * un almacen de informacion, sin comportamiento.
 * 
 * @version 2.2.2014
 */
public class Casilla {
	private static final String TAG = "Casilla";

	private final int x;
	private final int y;

	private Movil movil = null;

	/**
	 * Indica si la casilla es objetivo.
	 */
	private boolean objetivo;

	/**
	 * Direcciones en las que la celda est� separada por una pared.
	 */
	private Set<Direccion> paredes = new HashSet<Direccion>();


	private boolean trampa = false;
	private boolean llave = false;

	/**
	 * Constructor.
	 *
	 * @param x
	 *            abscisa.
	 * @param y
	 *            ordenada.
	 */
	public Casilla(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Getter.
	 *
	 * @return posicion en el eje X.
	 */
	public int getX() {
		return x;
	}

	/**
	 * Getter.
	 *
	 * @return posicion en el eje Y.
	 */
	public int getY() {
		return y;
	}

	/**
	 * Getter.
	 *
	 * @return movil en la casilla; null si est� vacia.
	 */
	public Movil getMovil() {
		return movil;
	}

	/**
	 * Pon un movil en la casilla. Si ponemos null, se interpreta que la casilla
	 * queda vacia.
	 *
	 * @param movil
	 *            movil que queremos colocar en esta casilla.
	 */
	public void setMovil(Movil movil) {
		this.movil = movil;
	}

	/**
	 * Coloca una pared entre dos casillas.
	 *
	 * @param direccion
	 *            en la que se encuentra la otra casilla a separar por la pared.
	 */
	public void ponPared(Direccion direccion) {
		paredes.add(direccion);
	}

	/**
	 * Elimina la pared entre dos casillas. Si no hubiera pared, no pasa nada.
	 *
	 * @param direccion
	 *            en la que se encuentra la otra casilla separada por la pared.
	 */
	public void quitaPared(Direccion direccion) {
		paredes.remove(direccion);
	}

	/**
	 * Pregunta si hay pared.
	 *
	 * @param direccion
	 *            en la que miramos desde la casilla a ver si hay una pared.
	 * @return cierto si la hay; falso si no.
	 */
	public boolean hayPared(Direccion direccion) {
		return paredes.contains(direccion);
	}

	/**
	 * Quita todas las paredes de la casilla.
	 */
	public void quitaParedes() {
		paredes.clear();
	}

	/**
	 * Getter.
	 *
	 * @return true si la casilla es objetivo.
	 */
	public boolean isObjetivo() {
		return objetivo;
	}

	/**
	 * Setter.
	 *
	 * @param objetivo
	 *            valor a cargar.
	 */
	public void setObjetivo(boolean objetivo) {
		this.objetivo = objetivo;
	}

	public boolean isLlave() {
		return llave;
	}

	public void setLlave(boolean llave) {
		this.llave = llave;
	}

	public boolean isTrampa() {
		return trampa;
	}

	public void setTrampa(boolean trampa) {
		this.trampa = trampa;
	}

	public Direccion getDireccion(Casilla destino) {
		if (destino == null) return null;

		int equis = getX();
		int ye = getY();
		int equisDestino = destino.getX();
		int yeDestino = destino.getY();

		if ((equis != equisDestino) && (ye == yeDestino)) {
			if (equis < equisDestino) return Direccion.ESTE;
			else if (equis > equisDestino) return Direccion.OESTE;
		}

		else if ((ye != yeDestino) && (equis == equisDestino)) {
			if (ye < yeDestino) return Direccion.NORTE;
			else if (ye > yeDestino) return Direccion.SUR;
		}

		return null;
	}

}
