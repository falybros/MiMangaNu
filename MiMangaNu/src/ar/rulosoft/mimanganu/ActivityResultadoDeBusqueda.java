package ar.rulosoft.mimanganu;

import java.util.ArrayList;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import ar.rulosoft.mimanganu.componentes.Manga;
import ar.rulosoft.mimanganu.servers.ServerBase;
import ar.rulosoft.mimanganu.R;

public class ActivityResultadoDeBusqueda extends ActionBarActivity {
	public static final String TERMINO = "termino_busqueda";
	String termino = "";
	int serverId;
	ProgressBar cargando;
	ListView lista;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_activity_resultado_de_busqueda);
		serverId = getIntent().getExtras().getInt(ActivityMisMangas.SERVER_ID);
		termino = getIntent().getExtras().getString(TERMINO);
		lista = (ListView) findViewById(R.id.resultados);
		cargando = (ProgressBar) findViewById(R.id.cargando);
		lista.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Manga m = (Manga) lista.getAdapter().getItem(position);
				Intent intent = new Intent(getApplication(), ActivityDetalles.class);
				intent.putExtra(ActivityMisMangas.SERVER_ID, serverId);
				intent.putExtra(ActivityDetalles.TITULO, m.getTitulo());
				intent.putExtra(ActivityDetalles.PATH, m.getPath());
				startActivity(intent);
				
			}
		});
		new RealizarBusqueda().execute();
	}

	public class RealizarBusqueda extends AsyncTask<Void, Void, ArrayList<Manga>> {
		public String error = "";

		@Override
		protected void onPreExecute() {
			cargando.setVisibility(ProgressBar.VISIBLE);
		}

		@Override
		protected ArrayList<Manga> doInBackground(Void... params) {
			ArrayList<Manga> mangas = new ArrayList<Manga>();
			ServerBase s = ServerBase.getServer(serverId);
			try {
				mangas = s.getBusqueda(termino);
			} catch (Exception e) {
				error = e.getMessage();
			}
			return mangas;
		}

		@Override
		protected void onPostExecute(ArrayList<Manga> result) {
			cargando.setVisibility(ProgressBar.INVISIBLE);
			if (error.length() < 2) {
				if (result != null && !result.isEmpty() && lista != null) {
					lista.setAdapter(new ArrayAdapter<Manga>(ActivityResultadoDeBusqueda.this, android.R.layout.simple_list_item_1, result));
				} else if (result == null || result.isEmpty()) {
					Toast.makeText(ActivityResultadoDeBusqueda.this, getResources().getString(R.string.busquedanores), Toast.LENGTH_LONG).show();
				}
			} else {
				Toast.makeText(ActivityResultadoDeBusqueda.this, error, Toast.LENGTH_LONG).show();
			}
			super.onPostExecute(result);
		}
	}
}
