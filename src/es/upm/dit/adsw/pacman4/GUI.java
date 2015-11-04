package es.upm.dit.adsw.pacman4;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Interfaz grafica. Presenta el estado del juego y captura la interaccion del
 * usuario.
 * 
 * @version 5.5.2014
 */
public class GUI extends View {
	private static final String TAG = "GUI";

	private final Paint paint = new Paint();

	/**
	 * Nombre del juego.
	 */
	public static final String TITULO = "Pacman IV (5.5.2014)";

	/**
	 * Espacio entre la zona de juego y el borde de la ventana.
	 */
	private static final int MARGEN = 10;

	/**
	 * Tamano de una casilla: pixels.
	 */
	private int lado1;

	private Context context;

	private Casilla seleccionada;

	private Bitmap backgroundBmp;
	private Bitmap scaledBackgroundBmp;

	public GUI(Context context, AttributeSet attrs) {
		super(context, attrs);
		Log.d(TAG, "new GUI(Context, AttributeSet)");
		this.context = context;

		setOnTouchListener(new MyTouchListener());
	}

	private void setCasillaSeleccionada(float x1, float y1) {
		Escenario escenario = Escenario.getInstance();
		Terreno terreno = escenario.getTerreno();

		int x = (int) ((x1 - MARGEN) / lado1);
		int y = terreno.getN() - 1 - (int) ((y1 - MARGEN) / lado1);
		try {
			seleccionada = terreno.getCasilla(x, y);
			postInvalidate();
		} catch (Exception e) {
			seleccionada = null;
		}
	}

	/**
	 * Llamada por java para pintarse en la pantalla.
	 *
	 * @param canvas
	 *            sistema grafico 2D para dibujarse.
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		Escenario escenario = Escenario.getInstance();
		Terreno terreno = escenario.getTerreno();

		// para eclipse, preview de layout
		if (terreno == null)
			return;

		int N = terreno.getN();
		int sqw = (getWidth() - 2 * MARGEN) / N;
		int sqh = (getHeight() - 2 * MARGEN) / N;
		lado1 = sqw = sqh = Math.min(sqw, sqh);

		// si una imagen de fondo pero aun no esta escalada a la pantalla
		if (backgroundBmp != null && scaledBackgroundBmp == null) {
			int lado = N * lado1;
			try {
				scaledBackgroundBmp = Bitmap.createScaledBitmap(backgroundBmp,
						lado, lado, false);
			} catch (Exception e) {
				Log.d(TAG, Log.getStackTraceString(e));
				backgroundBmp = null;
				scaledBackgroundBmp = null;
			}
		}

		if (terreno.getEstado() == EstadoJuego.JUGANDO) {
			// si hay una imagen de fondo escalada a la pantalla
			if (scaledBackgroundBmp != null) {
				canvas.drawBitmap(scaledBackgroundBmp, MARGEN, MARGEN, null);

			}

			else {
				paint.setColor(Color.BLACK);
				canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
			}
		}

		else if (terreno.getEstado() == EstadoJuego.GANA_JUGADOR) {
			paint.setColor(Color.GREEN);
			canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
		}

		else if (terreno.getEstado() == EstadoJuego.PIERDE_JUGADOR) {
			paint.setColor(Color.RED);
			canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
		}

		paint.setColor(Color.LTGRAY);
		int nwx = MARGEN;
		int nwy = MARGEN;

		// pinta las celdas con su contenido (movil)
		for (int x = 0; x < N; x++) {
			for (int y = 0; y < N; y++) {
				pintaCasilla(canvas, x, y);
			}
		}

		// pinta el marco
		paint.setColor(Color.WHITE);
		canvas.drawLine(nwx - 1, nwy - 1, nwx - 1, nwy + N * lado1 + 1, paint);
		canvas.drawLine(nwx + N * lado1 + 1, nwy - 1, nwx + N * lado1 + 1, nwy
				+ N * lado1 + 1, paint);
		canvas.drawLine(nwx - 1, nwy - 1, nwx + N * lado1 + 1, nwy - 1, paint);
		canvas.drawLine(nwx - 1, nwy + N * lado1 + 1, nwx + N * lado1 + 1, nwy
				+ N * lado1 + 1, paint);
	}

	/**
	 * Pinta una celda.
	 *
	 * @param canvas
	 *            sistema grafico 2D para dibujarse.
	 * @param x
	 *            columna.
	 * @param y
	 *            fila.
	 */
	private void pintaCasilla(Canvas canvas, int x, int y) {
		Escenario escenario = Escenario.getInstance();
		Terreno terreno = escenario.getTerreno();

		Casilla casilla = terreno.getCasilla(x, y);

		pintaTipo(canvas, casilla);

		// pinta las paredes de la casilla
		paint.setColor(Color.WHITE);
		if (terreno.hayPared(casilla, Direccion.NORTE))
			canvas.drawLine(sw_x(x), sw_y(y + 1), sw_x(x + 1), sw_y(y + 1),
					paint);
		if (terreno.hayPared(casilla, Direccion.SUR))
			canvas.drawLine(sw_x(x), sw_y(y), sw_x(x + 1), sw_y(y), paint);
		if (terreno.hayPared(casilla, Direccion.ESTE))
			canvas.drawLine(sw_x(x + 1), sw_y(y), sw_x(x + 1), sw_y(y + 1),
					paint);
		if (terreno.hayPared(casilla, Direccion.OESTE))
			canvas.drawLine(sw_x(x), sw_y(y), sw_x(x), sw_y(y + 1), paint);

		Movil movil = casilla.getMovil();
		if (movil != null) {
			pintaImagen(canvas, movil.getImagen(), x, y);
		}
	}

	/**
	 * Pinta el tipo de casilla.
	 *
	 * @param canvas
	 *            sistema grafico 2D para dibujarse.
	 * @param casilla
	 *            casilla a pintar.
	 */
	private void pintaTipo(Canvas canvas, Casilla casilla) {
		int color = Color.TRANSPARENT;
		if (casilla.equals(seleccionada))
			color = Color.DKGRAY;

		if (casilla.isObjetivo())
			color = Color.CYAN;

		if (casilla.isTrampa())
			color = Color.RED;

		if (casilla.isLlave())
			color = Color.GREEN;

		if (color == Color.TRANSPARENT)
			return;

		int x = casilla.getX();
		int y = casilla.getY();
		int nwx = sw_x(x) + 1;
		int nwy = sw_y(y + 1) + 1;
		int dx = this.lado1 - 2;
		int dy = this.lado1 - 2;
		int cx = nwx + dx / 2;
		int cy = nwy + dy / 2;
		paint.setColor(color);
		canvas.drawCircle(cx, cy, dx / 2, paint);
	}

	/**
	 * Pinta la imagen propia del movil.
	 *
	 * @param canvas
	 *            sistema grafico 2D para dibujar.
	 * @param imagen
	 *            imagen a dibujar.
	 * @param x
	 *            columna.
	 * @param y
	 *            fila.
	 */
	private void pintaImagen(Canvas canvas, int id, int x, int y) {
		Resources resources = context.getResources();
		Bitmap imagen = BitmapFactory.decodeResource(resources, id);
		if (imagen == null)
			return;
		int iWidth = imagen.getWidth();
		int iHeight = imagen.getHeight();

		double escalaX = 0.9 * lado1 / iWidth;
		double escalaY = 0.9 * lado1 / iHeight;
		double escala = Math.min(escalaX, escalaY);
		double sWidth = escala * iWidth;
		double sHeight = escala * iHeight;

		double nwX = sw_x(x) + (lado1 - sWidth) / 2;
		double nwY = sw_y(y + 1) + (lado1 - sHeight) / 2;
		double seX = nwX + sWidth;
		double seY = nwY + sHeight;
		Rect rect = new Rect((int) nwX, (int) nwY, (int) seX, (int) seY);
		canvas.drawBitmap(imagen, null, rect, paint);
	}

	/**
	 * Dada una columna, calcula el vertice inferior izquierdo.
	 *
	 * @param columna
	 *            columna.
	 * @return abscisa del vertice inferior izquierdo.
	 */
	private int sw_x(int columna) {
		return MARGEN + columna * lado1;
	}

	/**
	 * Dada una fila, calcula el vertice inferior izquierdo.
	 *
	 * @param fila
	 *            fila.
	 * @return vertice inferior izquierdo.
	 */
	private int sw_y(int fila) {
		Escenario escenario = Escenario.getInstance();
		Terreno terreno = escenario.getTerreno();

		int lado = terreno.getN();
		return MARGEN + (lado - fila) * lado1;
	}

	public Casilla getSeleccionada() {
		Casilla sel = seleccionada;
		seleccionada = null;
		return sel;
	}

	// setter - a partir de un elemento en res/drawable
	public void setMyBackground(int id) {
		try {
			Bitmap tmp = BitmapFactory.decodeResource(getResources(), id);
			setMyBackground(tmp);
		} catch (Exception e) {
			Log.d(TAG, Log.getStackTraceString(e));
			backgroundBmp = null;
		}
	}

	// setter - a partir de un bitmap
	public void setMyBackground(Bitmap background) {
		backgroundBmp = background;
		scaledBackgroundBmp = null;
		invalidate();
	}

	private class MyTouchListener implements OnTouchListener {
		private float x1;
		private float y1;
		private int cx1;
		private int cy1;
		boolean movido = false;

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			Escenario escenario = Escenario.getInstance();
			Terreno terreno = escenario.getTerreno();
			Movil jugador = terreno.getJugador();
			int N = terreno.getN();

			switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN: {
					x1 = event.getX();
					y1 = event.getY();
					cx1 = (int) ((x1 - MARGEN) / lado1);
					cy1 = N - 1 - (int) ((y1 - MARGEN) / lado1);
					return true;
				}
				case MotionEvent.ACTION_MOVE: {
					float x2 = event.getX();
					float y2 = event.getY();
					float dx = x2-x1;
					float dy = y2-y1;

					if (Math.abs(dx) >= Math.abs(dy)) {
						if (!movido && (dx >= 0) && (Math.abs(getWidth()/dx) < 6.0)) {
							terreno.move(jugador, Direccion.ESTE);
							movido = true;
						}
						else if (!movido && (dx < 0) && (Math.abs(getWidth()/dx) < 6.0)) {
							terreno.move(jugador, Direccion.OESTE);
							movido = true;
						}
						//seleccionada = null;
					}

					else if (Math.abs(dx) < Math.abs(dy)) {
						if (!movido && (dy >= 0) && (Math.abs(getHeight()/dy) < 6.0)) {
							terreno.move(jugador, Direccion.SUR);
							movido = true;
						}
						else if (!movido && (dy < 0) && (Math.abs(getHeight()/dy) < 6.0)) {
							terreno.move(jugador, Direccion.NORTE);
							movido = true;
						}
						//seleccionada = null;
					}
					return true;
				}
				case MotionEvent.ACTION_UP: {
					float x2 = event.getX();
					float y2 = event.getY();
					int cx2 = (int) ((x2 - MARGEN) / lado1);
					int cy2 = N - 1 - (int) ((y2 - MARGEN) / lado1);

					if ((cx1 == cx2) && (cy1 == cy2)) setCasillaSeleccionada(x1, y1);
					movido = false;
					return true;
				}
			}
			invalidate();
			return false;
		}
	}

}
