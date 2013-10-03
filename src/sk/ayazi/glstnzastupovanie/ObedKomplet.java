package sk.ayazi.glstnzastupovanie;

import java.io.IOException;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.webkit.WebView;

public class ObedKomplet extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_obed_koplet);
		// Show the Up button in the action bar.
		setupActionBar();
		new GetLatestListok().execute();
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.obed_koplet, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	private class GetLatestListok extends AsyncTask<Void,Void,String[]>{
		@Override
		protected void onPreExecute (){
			 setProgressBarIndeterminateVisibility(true);
		}
			
		@Override
		protected String[] doInBackground(Void... param) {
			try{
			return new Zastup().getLatestMenuHTML();
			} catch (IOException e) {
					// TODO Handle the error somehow
					e.printStackTrace();
			}
			return null;
		}
		protected void onPostExecute (String[] res){
			setProgressBarIndeterminateVisibility(false);
			if(res==null){
				new AlertDialog.Builder(ObedKomplet.this)
				.setTitle("Chyba")
				.setMessage("Chyba")
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				    @Override
				    public void onClick(DialogInterface dialog, int which) {
				    	startActivity(new Intent(ObedKomplet.this,MainActivity.class));
				    }
				})
				.create()
				.show();
				return;
	    	}else{
	    		//fill spinner
	    		setTitle(res[1]+" do "+res[2]);
	    		((WebView) findViewById(R.id.webTable)).loadData("<html><head><meta http-equiv=\"Content-type\" content=\"text/html;charset=UTF-8\"/></head><body>"+res[0]+"</body></html>", "text/html; charset=utf-8", "UTF-8");
	    	}
	    	
			
		}
		
	}

}
