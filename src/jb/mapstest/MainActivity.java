package jb.mapstest;

import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.json.JSONStringer;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Interpolator;
import android.graphics.Point;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends FragmentActivity implements
		OnMapClickListener, OnMapLongClickListener, OnMarkerClickListener {
	GoogleMap mapa;
	Context context;
	Marker myPoint;

	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_main);
		context = this;
		mapa = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.mapExample)).getMap();

		// mapa.setMyLocationEnabled(true);
		mapa.setMapType(GoogleMap.MAP_TYPE_HYBRID);
		// mapa.setMyLocationEnabled(true);

		UiSettings settings = mapa.getUiSettings();
		settings.setCompassEnabled(false);
		settings.setMyLocationButtonEnabled(false);
		settings.setZoomControlsEnabled(false);

		Criteria criteria = new Criteria();
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		String provider = locationManager.getBestProvider(criteria, false);
		Location location = locationManager.getLastKnownLocation(provider);
		double lat, lng;

		lat = -12.045029;
		lng = -77.062252;
		LatLng coordinate = new LatLng(lat, lng);

		// CameraUpdate center = CameraUpdateFactory.newLatLng(coordinate);
		CameraPosition cameraPosition = new CameraPosition.Builder().tilt(60)
				.zoom(17).target(coordinate).build();

		CameraUpdate zoom = CameraUpdateFactory
				.newCameraPosition(cameraPosition);

		mapa.animateCamera(zoom, 1500, null);
		mapa.setOnMapClickListener(this);
		mapa.setOnMarkerClickListener(this);
		// mapa.moveCamera(center);
		// MuestraGeoPunto("sd");

		Button btnSolicitar = (Button) findViewById(R.id.btnSolicitar);
		btnSolicitar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String URLService = "http://192.168.1.48:8888";
				HttpClient client = new DefaultHttpClient();
				HttpGet consultarUsuarioGet = new HttpGet(URLService
						+ "/users/fakelogin/cat");
				consultarUsuarioGet.setHeader("Accept", "application/json");
				consultarUsuarioGet.setHeader("content-type",
						"application/json");
				try {
					HttpResponse resp = client.execute(consultarUsuarioGet);
					String respStr = EntityUtils.toString(resp.getEntity());
					JSONObject respJson = new JSONObject(respStr);
					if (respJson.getString("user_id") != null) {
						EditText txtDireccion = (EditText) findViewById(R.id.txtDireccion);
						EditText txtReferencia = (EditText) findViewById(R.id.txtReferencia);

						HttpPost httpPost = new HttpPost(URLService
								+ "/users/start_ride?");

						ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
						postParameters.add(new BasicNameValuePair("u", respJson
								.getString("user_id")));
						postParameters.add(new BasicNameValuePair("la", ""
								+ myPoint.getPosition().latitude));
						postParameters.add(new BasicNameValuePair("ln", ""
								+ myPoint.getPosition().longitude));
						postParameters.add(new BasicNameValuePair("a",
								txtDireccion.getText().toString()));
						postParameters.add(new BasicNameValuePair("r",
								txtReferencia.getText().toString()));

						httpPost.setEntity(new UrlEncodedFormEntity(
								postParameters));

						@SuppressWarnings("unchecked")
						HttpResponse responsePOST = client.execute(httpPost);

					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	private void MostrarMarcador(String Titulo, String Mensaje, double lat,
			double lng, int Marcador) {
		LatLng lugar = new LatLng(lat, lng);
		mapa.addMarker(new MarkerOptions().position(lugar).title(Titulo)
				.snippet(Mensaje)
				.icon(BitmapDescriptorFactory.fromResource(Marcador)));
	}

	public void MuestraGeoPunto(String CatlogPara) {
		double XLatitud;
		double XLongitud;

		double p1;
		double p2;

		String Nombre;
		String Extra;
		String Marcador = "drawable/ic_launcher";
		int imageResource = getResources().getIdentifier(Marcador, null,
				getPackageName());
		MostrarMarcador("hostal", "asd", -12.04612, -77.062225, imageResource);

		LatLng starPoint = new LatLng(-12.04612, -77.062225);

		mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(starPoint, 15));
	}

	@Override
	public void onMapClick(LatLng point) {
		// TODO Auto-generated method stub
		mapa.clear();
		MarkerOptions markerOption = new MarkerOptions().position(point)
				.title("Aqui estoy").snippet("Estoy Aqui");
		myPoint = mapa.addMarker(markerOption);
	}

	@Override
	public void onMapLongClick(LatLng point) {
		// TODO Auto-generated method stub
		// mapa.clear();
		// mapa.addMarker(new MarkerOptions().position(point).title("Yo"));
	}

	@Override
	public boolean onMarkerClick(final Marker marker) {
		// TODO Auto-generated method stub
		marker.hideInfoWindow();

		AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		dialog.setTitle(marker.getTitle());
		dialog.setMessage(marker.getSnippet());

		dialog.setNegativeButton("cerrar",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.cancel();
						LatLng newPos = new LatLng(
								marker.getPosition().latitude + 2, marker
										.getPosition().longitude);
						animateMarker(marker, newPos, false);
					}
				});

		dialog.show();
		return true;
	}

	public void animateMarker(final Marker marker, final LatLng toPosition,
			final boolean hideMarker) {
		final Handler handler = new Handler();
		final long start = SystemClock.uptimeMillis();
		Projection proj = mapa.getProjection();
		Point startPoint = proj.toScreenLocation(marker.getPosition());
		final LatLng startLatLng = proj.fromScreenLocation(startPoint);
		final long duration = 500;

		final LinearInterpolator interpolator = new LinearInterpolator();

		handler.post(new Runnable() {
			@Override
			public void run() {
				long elapsed = SystemClock.uptimeMillis() - start;
				float t = interpolator.getInterpolation((float) elapsed
						/ duration);
				double lng = t * toPosition.longitude + (1 - t)
						* startLatLng.longitude;
				double lat = t * toPosition.latitude + (1 - t)
						* startLatLng.latitude;
				marker.setPosition(new LatLng(lat, lng));

				if (t < 1.0) {
					// Post again 16ms later.
					handler.postDelayed(this, 16);
				} else {
					if (hideMarker) {
						marker.setVisible(false);
					} else {
						marker.setVisible(true);
					}
				}
			}
		});
	}
}
