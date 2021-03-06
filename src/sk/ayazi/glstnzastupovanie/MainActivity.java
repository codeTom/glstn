package sk.ayazi.glstnzastupovanie;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

import com.inscription.ChangeLogDialog;
import com.inscription.WhatsNewDialog;

import android.annotation.SuppressLint;
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
	 public final static String DATE="sk.ayazi.glstnzastupovanie.DATA";
	 public final static String LASTUPDATE = "sk.ayazi.glstnzastupovanie.LASTUPDATE";
	 public final static String NOUPDATE="sk.ayazi.glstnzastupovanie.NOUPDATE";
	 public final static String LASTUPDATEPROMPT="sk.ayazi.glstnzastupovanie.LASTUDPROMPT";
	 public final Zastup z=new Zastup();
	 private static final int MENU_CONTACT = 1;
	 private static final int MENU_UPDATE = 2;
	 private static final int MENU_INFO = 3;
	 private static int failed=0;
	 static HashMap<String,String> classes;
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
	
	public void showOdhlasenie(View view){
		Intent intent=new Intent(this,Odhlasenie.class);
		Spinner spinner = (Spinner) findViewById(R.id.spinner_trieda);
		String message = String.valueOf(spinner.getSelectedItem());
		intent.putExtra(TRIEDA, message);
		startActivity(intent);
	}
	
	public void showListok(View view){
		Intent intent=new Intent(this,ObedKomplet.class);
		startActivity(intent);
	}
	
	@SuppressLint("SimpleDateFormat")
	public void getRozvrh(View view){
		Intent intent=new Intent(this,RozvrhActivity.class);
		Spinner spinner = (Spinner) findViewById(R.id.spinner_trieda);
		String message = String.valueOf(spinner.getSelectedItem());
		try {
			intent.putExtra(MainActivity.DATE, new SimpleDateFormat("yyyyMMdd").parse(new SimpleDateFormat("yyyyMMdd").format(new Date())));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		intent.putExtra(TRIEDA, message);
		startActivity(intent);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		final WhatsNewDialog whatsNewDialog = new WhatsNewDialog(this);
		whatsNewDialog.show();
		//new UpdateTask().execute(false); //update (playstore)
		try {
			version=getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
			((TextView) findViewById(R.id.textView4)).setText("Verzia "+version);
		} catch (NameNotFoundException e1) {
			e1.printStackTrace();
		}		
		SharedPreferences sp=getApplicationContext().getSharedPreferences("sk.ayazi.glstnzastupovanie", Context.MODE_PRIVATE);
		String trieda=sp.getString("sk.ayazi.glstnzastupovanie.trieda", "III.B");
		try {
			classes=(HashMap<String,String>) fromString(sp.getString(TRIEDY,null));
		} catch (IOException e) {
			
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			
			e.printStackTrace();
		}
		Long lastUpdateTime=Long.valueOf(sp.getString(LASTUPDATE, "0"));
		if((System.currentTimeMillis()-lastUpdateTime)>200000000l&& failed!=1 || classes==null){//update every few days
			new GetClasses().execute();
		}
		if(classes==null){classes=new HashMap<String,String>();classes.put("III.B","");}
		Spinner spinner=(Spinner) findViewById(R.id.spinner_trieda);
		ArrayList<String> so=new ArrayList<String>(classes.keySet());
		Collections.sort(so);
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, so);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(dataAdapter);
		spinner.setSelection(so.indexOf(trieda));
		getApplicationContext().getSharedPreferences("sk.ayazi.glstnzastupovanie", Context.MODE_PRIVATE).getString("sk.ayazi.glstnzastupovanie.trieda", "III.B");
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		menu.add(0, MENU_CONTACT, 0, "Autor");
		menu.add(0,MENU_UPDATE,1,"Aktualizovať");
		menu.add(0,MENU_INFO,2,"Info");
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
    		case MENU_CONTACT:{
    			AlertDialog ad = new AlertDialog.Builder(MainActivity.this)
				.setTitle("Autor")
				.setMessage("Filip Ayazi \n IV.B \n filipayazi@gmail.com \n skype:filip.ayazi")
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
    		case MENU_UPDATE:{clearCache();new GetClasses().execute();}
    		case MENU_INFO:{ChangeLogDialog cd = new ChangeLogDialog(this);
    		cd.show();  }
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
	
	private class GetClasses extends AsyncTask<Void,Void,HashMap<String,String>>{
		@Override
		protected void onPreExecute (){
			 setProgressBarIndeterminateVisibility(true);
		}
			
		@Override
		protected HashMap<String,String> doInBackground(Void... param) {
				try {
				if(!isNetworkAvailable()){
					return null;
				}
				return z.getClasses();
				} catch (IOException e) {
					return null;
			}
			
		}
		
		protected void onPostExecute (HashMap<String,String> res){
			if(res==null){
				failed=1;
				setProgressBarIndeterminateVisibility(false);
				new AlertDialog.Builder(MainActivity.this)
				.setTitle("Chyba")
				.setMessage("Pripojenie nie je dostupné")
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
	 public void clearCache() {
		 try {
	         File dir = getCacheDir();
	         if (dir != null && dir.isDirectory()) {
	            deleteDir(dir);
	         }
	      } catch (Exception e) {
	         // TODO: handle exception
	      }
	    }

	    public static boolean deleteDir(File dir) {
	        if (dir != null && dir.isDirectory()) {
	            String[] children = dir.list();
	            for (int i = 0; i < children.length; i++) {
	                boolean success = deleteDir(new File(dir, children[i]));
	                if (!success) {
	                    return false;
	                }
	            }
	        }

	        return dir.delete();
	    }
}
