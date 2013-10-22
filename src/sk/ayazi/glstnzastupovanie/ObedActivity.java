package sk.ayazi.glstnzastupovanie;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
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
import android.widget.TextView;

@SuppressLint("SimpleDateFormat")
public class ObedActivity extends ActionBarActivity {
	private JedalnyListok jl;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		jl=new JedalnyListok(getApplicationContext().getSharedPreferences("sk.ayazi.glstnzastupovanie", Context.MODE_PRIVATE));
		setContentView(R.layout.activity_obed);
		setupActionBar();
		
		setTitle("Obed "+new SimpleDateFormat("d.M.yyyy").format((Date) getIntent().getExtras().get("date")));
		new GetObed().execute((Date) getIntent().getExtras().get("date"));
		
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
		getMenuInflater().inflate(R.menu.obed, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		if (itemId == android.R.id.home) {
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}else if(itemId == R.id.action_zastup){//show zastupovanie again
			String trieda= getApplicationContext().getSharedPreferences("sk.ayazi.glstnzastupovanie", Context.MODE_PRIVATE).getString("sk.ayazi.glstnzastupovanie.trieda", null);
			String datum = getApplicationContext().getSharedPreferences("sk.ayazi.glstnzastupovanie", Context.MODE_PRIVATE).getString("sk.ayazi.glstnzastupovanie.datum", null);
			Intent intent=new Intent(this,Zastupovanie.class);
			intent.putExtra(MainActivity.TRIEDA, trieda);
			intent.putExtra(MainActivity.DATUM, datum);
			startActivity(intent);
			}
		return super.onOptionsItemSelected(item);
	}
	
	private class GetObed extends AsyncTask<Date,Void,String[]>{
		@Override
		protected void onPreExecute (){
			 setProgressBarIndeterminateVisibility(true);
		}
			
		@Override
		protected String[] doInBackground(Date... param) {
			try{
				Date date= new SimpleDateFormat("yyyyMMdd").parse(new SimpleDateFormat("yyyyMMdd").format(param[param.length-1]));
				return jl.getObed(date);
			} catch (ObedNotAvailableException e) {
					// TODO Handle the error somehow
					e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		protected void onPostExecute (String[] res){
			setProgressBarIndeterminateVisibility(false);
			if(res==null){
				new AlertDialog.Builder(ObedActivity.this)
				.setTitle("Chyba")
				.setMessage("Chyba")
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				    @Override
				    public void onClick(DialogInterface dialog, int which) {
				    	startActivity(new Intent(ObedActivity.this,MainActivity.class));
				    }
				})
				.create()
				.show();
				return;
	    	}else{
	    		//fill spinner
	    		((TextView) findViewById(R.id.textObed)).setText(res[1]);
	    		((TextView) findViewById(R.id.textPolievka)).setText(res[0]);
	    	}
	    	
			
		}
		
	}

}
