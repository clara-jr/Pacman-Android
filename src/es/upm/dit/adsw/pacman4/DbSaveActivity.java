package es.upm.dit.adsw.pacman4;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

public class DbSaveActivity extends Activity {
	private static final String TAG = "DbSaveActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.db_save_activity);
	}

	public void button(View view) {
		switch (view.getId()) {
		case R.id.button_save: {
			TextView textView = (TextView) findViewById(R.id.save_name);
			String name = textView.getText().toString();

			Escenario escenario= Escenario.getInstance();
			MyDbAdapter dbAdapter = escenario.getDbAdapter();
			dbAdapter.insertFoto(name);
			finish();
		}

		case R.id.button_cancel:
			finish();
		}
	}
}