package es.upm.dit.adsw.pacman4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Depredador extends Movil implements Runnable {
    private final Terreno terreno;
    private Casilla casilla;
    private boolean vivo = true;
    
	private static final String TAG = "Fantasma01";

	private volatile long delta = 500;
    
    public Depredador(Terreno terreno) {
        this.terreno = terreno;
        setImage(R.drawable.anibal);
    }
    
    public Casilla getCasilla() {
    	return casilla;
    }
    
    public void setCasilla(Casilla casilla) {
    	this.casilla = casilla;
    }
    
    public void muere(boolean devorado) {
    	vivo = false;
    }
    
	public void setDelta(int delta) {
		this.delta = delta;
	}
    
    public int puedoMoverme(Direccion direccion) {
    	if ((getCasilla() != null) && (direccion != null)) {
    		try {
    			int equis = getCasilla().getX();
    			int ye = getCasilla().getY();

    			if (terreno.getCasilla(equis, ye, direccion) != null 
    				&& !getCasilla().hayPared(direccion)
    				&& vivo
    				&& (terreno.getCasilla(equis, ye, direccion).getMovil() instanceof Jugador
    					|| terreno.getCasilla(equis, ye, direccion).getMovil() == null))
    				return 0;

    			else if (terreno.getCasilla(equis, ye, direccion) != null 
    					&& !getCasilla().hayPared(direccion)
    					&& vivo
    					&& (terreno.getCasilla(equis, ye, direccion).getMovil() instanceof Fantasma01
    						|| terreno.getCasilla(equis, ye, direccion).getMovil() instanceof Depredador))
    				return 1;

    			else return 2;
    		} catch (Exception e) {e.printStackTrace();}
    	}
		return 2;
    }
    
    public void run() {

    	while(vivo) {
    		Movil jugador = terreno.getJugador();
    		Casilla origen = getCasilla();
    		Casilla destino = jugador.getCasilla();
    		Casilla otraCasilla = bfs(origen, destino);
    		Direccion direccion = origen.getDireccion(otraCasilla);

    		if (otraCasilla != null) {
    			try {
    				terreno.move(this, direccion);
    				Thread.sleep(delta);
    			} catch (InterruptedException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
    		}
    	}
    }

    public Casilla bfs(Casilla origen, Casilla destino) {
    	try {
    		List<Casilla> pendientes = new ArrayList<Casilla>();
    		Map<Casilla, Casilla> visitadas = new HashMap<Casilla, Casilla>();

    		pendientes.add(origen);

    		boolean encontrado = bfs(destino, pendientes, visitadas);

    		if (encontrado) {
    			Casilla auxiliar = destino;
    			while ((visitadas.get(auxiliar) != null) && !(visitadas.get(auxiliar).equals(origen))) {
    				if (auxiliar == null) return null;
    				auxiliar = visitadas.get(auxiliar);
    			}
    			return auxiliar;
    		}
    		return null;
    	} catch (Exception e) {e.printStackTrace();}
    	return null;
    }


    private boolean bfs(Casilla destino, List<Casilla> pendientes, Map<Casilla, Casilla> visitadas) {
    	try {
    		if (pendientes.isEmpty()) return false;

    		else {
    			while (!pendientes.isEmpty()) {
    				Casilla c1 = pendientes.remove(0);
    				if (c1 != null && destino!= null && c1.equals(destino)) {
    					return true;
    				}

    				else {
    					for (Direccion direccion : Direccion.values()) {
    						Casilla casilla = terreno.getCasilla(c1, direccion);
    						if (c1.hayPared(direccion)) continue;
    						if (casilla != null && !visitadas.containsKey(casilla)) {
    							visitadas.put(casilla, c1);
    							pendientes.add(casilla);
    						}
    					}
    				}
    			}
    		}
    		return false;
    	} catch (Exception e) {e.printStackTrace();}
    	return false;
    }

}
