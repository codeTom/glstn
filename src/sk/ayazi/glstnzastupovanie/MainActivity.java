package sk.ayazi.glstnzastupovanie;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import biz.source_code.base64Coder.Base64Coder;


public class MainActivity extends Activity {
	 public final static String TRIEDA = "sk.ayazi.glstnzastupovanie.TRIEDA";
	 public final static String DATUM = "sk.ayazi.glstnzastupovanie.DATUM";
	 public final static String TRIEDY = "sk.ayazi.glstnzastupovanie.TRIEDY";
	 public final static String LASTUPDATE = "sk.ayazi.glstnzastupovanie.LASTUPDATE";
	 public final static String NOUPDATE="sk.ayazi.glstnzastupovanie.NOUPDATE";
	 public final static String LASTUPDATEPROMPT="sk.ayazi.glstnzastupovanie.LASTUDPROMPT";
	 public final Zastup z=new Zastup();
	 private static final int MENU_CONTACT = 1;
	 private static final int MENU_UPDATE = 2;
	 private static int failed=0;
	 static ArrayList<String> classes;
	 String version="";
	 
	 public void showDnes(View view){
		Intent intent=new Intent(this,Zastupovanie.class);
		Spinner spinner = (Spinner) findViewById(R.id.spinner_trieda);
		//EditText editText = (EditText) findViewById(R.id.edit_trieda);
		String message = String.valueOf(spinner.getSelectedItem());
		intent.putExtra(TRIEDA, message);
		Date d=new Date(System.currentTimeMillis());
		Calendar c=Calendar.getInstance();
		c.setTime(d);
		int toNext=0;
		String month=String.valueOf(
				(c.get(Calendar.MONTH)+1)<10?("0"+(c.get(Calendar.MONTH)+1)):
			(c.get(Calendar.MONTH)+1));
		String day=String.valueOf(
				(c.get(Calendar.DATE)+toNext)<10?("0"+(c.get(Calendar.DATE)+toNext)):
			(c.get(Calendar.DATE))+toNext);
    	String da= ""+c.get(Calendar.YEAR)+month+day;
    	intent.putExtra(DATUM, da);
    	startActivity(intent);
	}
	public void showZajtra(View view){
		Intent intent=new Intent(this,Zastupovanie.class);
		Spinner spinner = (Spinner) findViewById(R.id.spinner_trieda);
		//EditText editText = (EditText) findViewById(R.id.edit_trieda);
		String message = String.valueOf(spinner.getSelectedItem());
		intent.putExtra(TRIEDA, message);
		intent.putExtra(DATUM, "zajtra");
		startActivity(intent);
	}
	
	public void showLatest(View view){
		Intent intent=new Intent(this,Zastupovanie.class);
		Spinner spinner = (Spinner) findViewById(R.id.spinner_trieda);
		String message = String.valueOf(spinner.getSelectedItem());
		intent.putExtra(TRIEDA, message);
		intent.putExtra(DATUM, "latest");
		startActivity(intent);		
	}
	
	public void showListok(View view){
		Intent intent=new Intent(this,ObedKomplet.class);
		startActivity(intent);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		new UpdateTask().execute(false); //update
		try {
			version=getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
			((TextView) findViewById(R.id.textView4)).setText("Verzia "+version);
		} catch (NameNotFoundException e1) {
			e1.printStackTrace();
		}		
		SharedPreferences sp=getApplicationContext().getSharedPreferences("sk.ayazi.glstnzastupovanie", Context.MODE_PRIVATE);
		String trieda=sp.getString("sk.ayazi.glstnzastupovanie.trieda", "III.B");
		try {
			classes=(ArrayList<String>) fromString(sp.getString(TRIEDY,null));
		} catch (IOException e) {
			
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Long lastUpdateTime=Long.valueOf(sp.getString(LASTUPDATE, "0"));
		if((System.currentTimeMillis()-lastUpdateTime)>1000000000l&& failed!=1 || classes==null){
			new GetClasses().execute();
		}
		if(classes==null){classes=new ArrayList<String>();classes.add("III.B");}
		Spinner spinner=(Spinner) findViewById(R.id.spinner_trieda);
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, classes);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(dataAdapter);
		spinner.setSelection(classes.indexOf(trieda));
		getApplicationContext().getSharedPreferences("sk.ayazi.glstnzastupovanie", Context.MODE_PRIVATE).getString("sk.ayazi.glstnzastupovanie.trieda", "III.B");
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		menu.add(0, MENU_CONTACT, 0, "Autor");
		menu.add(0,MENU_UPDATE,1,"Aktualizovať");
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
    		case MENU_CONTACT:{
    			AlertDialog ad = new AlertDialog.Builder(MainActivity.this)
				.setTitle("Autor")
				.setMessage("Filip Ayazi \n III.B \n filipayazi@gmail.com \n skype:filip.ayazi")
				.setPositiveButton("Close", new DialogInterface.OnClickListener() {

				    @Override
				    public void onClick(DialogInterface dialog, int which) {
				    	  //	startActivity(new Intent(MainActivity.this,MainActivity.class));
				    }
				})
				.create()
				;
    			ad.show();
    			((TextView) ad.findViewById(android.R.id.message)).setGravity(Gravity.CENTER);
    		}
    		case MENU_UPDATE:{new UpdateTask().execute(true);}
            return true;
        }
        return false;
    }
	
	boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null;
	}
	
	private class GetClasses extends AsyncTask<Void,Void,ArrayList<String>>{
		@Override
		protected void onPreExecute (){
			 setProgressBarIndeterminateVisibility(true);
		}
			
		@Override
		protected ArrayList<String> doInBackground(Void... param) {
			
			try {
				if(!isNetworkAvailable()){
					return null;
				}
				return z.getClasses();
				} catch (IOException e) {
					return null;
			}
			
		}
		protected void onPostExecute (ArrayList<String> res){
			if(res==null){
				failed=1;
				setProgressBarIndeterminateVisibility(false);
				new AlertDialog.Builder(MainActivity.this)
				.setTitle("Chyba")
				.setMessage("Pripojenie nie je dostupná")
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				    @Override
				    public void onClick(DialogInterface dialog, int which) {
				    	startActivity(new Intent(MainActivity.this,MainActivity.class));
				    }
				})
				.create()
				.show();
				return;
	    	}else{
	    		SharedPreferences sp=getApplicationContext().getSharedPreferences("sk.ayazi.glstnzastupovanie", Context.MODE_PRIVATE);
	    		sp.edit().putString(LASTUPDATE,String.valueOf(System.currentTimeMillis())).commit();
	    		sp.edit().putString(TRIEDY,MainActivity.toString(res)).commit();
				failed=0;
				startActivity(new Intent(MainActivity.this,MainActivity.class));
				//fill spinner
				/*Spinner spinner=(Spinner) findViewById(R.id.spinner_trieda);
				ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(MainActivity.this,
						android.R.layout.simple_spinner_item, classes);
				dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				spinner.setAdapter(dataAdapter);
				String trieda=sp.getString("sk.ayazi.glstnzastupovanie.trieda", "III.B");
				spinner.setSelection(classes.indexOf(trieda));*/
	    	}
	    	
			
		}
		
	}
	
	private class UpdateTask extends AsyncTask<Boolean,Void,Boolean>{
		@Override
		protected void onPreExecute (){
			 }
		Updater u;
		boolean force=false;
		SharedPreferences sp;
		@Override
		protected Boolean doInBackground(Boolean... param) {
			sp=getApplicationContext().getSharedPreferences("sk.ayazi.glstnzastupovanie", Context.MODE_PRIVATE);
				if(param[param.length-1]){
						//TODO zobrazit dialog, verzia najnovsia ak vynutene z menu
						sp.edit().putBoolean(NOUPDATE, false).commit();
						sp.edit().putLong(LASTUPDATEPROMPT, 0).commit();
						force=true;
				}
				sp=getApplicationContext().getSharedPreferences("sk.ayazi.glstnzastupovanie", Context.MODE_PRIVATE);
				try {
					if(!isNetworkAvailable()||sp.getBoolean(NOUPDATE,false)||(System.currentTimeMillis()-sp.getLong(LASTUPDATEPROMPT,0))<3600000){return false;}
					
					u=new Updater();
				    if(u!=null&&!u.isLatest(version)){
						return true;
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
			
			return false;		
		}
		@Override
		protected void onPostExecute(Boolean param){
			if(param){
				new AlertDialog.Builder(MainActivity.this)
				.setTitle("Update")
				.setMessage("Je dostupná novšia verzia")
				.setPositiveButton("Stiahnuť", new DialogInterface.OnClickListener() {
				    @Override
				    public void onClick(DialogInterface dialog, int which){
				    	new PerformUpdate().execute(u);
						sp.edit().putBoolean(NOUPDATE, false).commit();
				    }
				})
				.setNegativeButton("Nepripomínať", new DialogInterface.OnClickListener() {
				    @Override
				    public void onClick(DialogInterface dialog, int which) {
				   		sp.edit().putBoolean(NOUPDATE, true).commit();
						}
				})
				.setNeutralButton("Neskôr", new DialogInterface.OnClickListener() {
				    @Override
				    public void onClick(DialogInterface dialog, int which) {
				    	sp.edit().putBoolean(NOUPDATE, false).commit();
				    	sp.edit().putLong(LASTUPDATEPROMPT, System.currentTimeMillis()).commit();
				    }
				})
				.create()
				.show();				
			}else if(force){
				new AlertDialog.Builder(MainActivity.this)
				.setTitle("Update")
				.setMessage("Používate najnovšiu verziu")
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				    @Override
				    public void onClick(DialogInterface dialog, int which){
				    	sp.edit().putBoolean(NOUPDATE, false).commit();
				    }
				}).create()
				.show();
				}
		}
	}
	
	private class PerformUpdate extends AsyncTask<Updater,Void,Void>{
		@Override
		protected void onPreExecute (){
			 }
		
		
		@Override
		protected Void doInBackground(Updater... param) {
				try{
					startActivity(param[param.length-1].update());				
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
		}
		@Override
		protected void onPostExecute(Void param){
		}
	}
	
	//save class list classes private static 
	public static String toString( Serializable o ){
		if(o==null)return null;
		try{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream( baos );
		oos.writeObject( o );
		oos.close();
		return new String( Base64Coder.encode( baos.toByteArray() ) );}catch(Exception e){e.printStackTrace(); return null;}
	}
	private static Object fromString( String s ) throws IOException, ClassNotFoundException {
		if(s==null)return null;
		byte [] data = Base64Coder.decode( s );
		ObjectInputStream ois = new ObjectInputStream( 
	new ByteArrayInputStream(  data ) );
	Object o  = ois.readObject();
	ois.close();
	return o;
}
}
