package es.upm.dit.adsw.pacman4;

public class Pared {
	private final Casilla casilla;
	private final Direccion direccion;

	public Pared(Casilla casilla, Direccion direccion) {
		this.casilla = casilla;
		this.direccion = direccion;
	}
	
	public Casilla getCasilla() {
		return casilla;
	}

	public Direccion getDireccion() {
		return direccion;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((casilla == null) ? 0 : casilla.hashCode());
		result = prime * result
				+ ((direccion == null) ? 0 : direccion.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Pared other = (Pared) obj;
		if (casilla == null) {
			if (other.casilla != null)
				return false;
		} else if (!casilla.equals(other.casilla))
			return false;
		if (direccion != other.direccion)
			return false;
		return true;
	}
}
