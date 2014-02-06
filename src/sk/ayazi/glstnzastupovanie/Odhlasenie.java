package sk.ayazi.glstnzastupovanie;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class Odhlasenie extends Activity {
	protected static String js=""; 
	protected static String jquery="";
	public static String meno="sk.ayazi.glstnzastupovanie.MENOPRIEZVISKO";
	public static String cislo="sk.ayazi.glstnzastupovanie.CISLO";
	@SuppressLint({ "SetJavaScriptEnabled", "SimpleDateFormat", "JavascriptInterface" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		jquery=loadJQuery();
		js=loadJS();
		Intent i=getIntent();
		String trieda=i.getStringExtra(MainActivity.TRIEDA);
		Date datum=(Date) i.getSerializableExtra(MainActivity.DATE);	
		if(datum==null){datum=new Date();}
		SharedPreferences sp=getApplicationContext().getSharedPreferences("sk.ayazi.glstnzastupovanie", Context.MODE_PRIVATE);
		String meno=sp.getString(Odhlasenie.meno, "");
		String cislo=sp.getString(Odhlasenie.cislo, "");
		js=prepareJS(js, trieda, meno, cislo, new SimpleDateFormat("yyyy/MM/dd").format(datum));
		setContentView(R.layout.activity_odhlasenie);
		JSiface jsi=new JSiface();
		jsi.sp=sp; 
		WebView wv=(WebView) findViewById(R.id.webOdhlasenie);
		wv.getSettings().setJavaScriptEnabled(true);
		wv.getSettings().setBuiltInZoomControls(true);
		wv.setWebChromeClient(new WebChromeClient());
		wv.addJavascriptInterface(jsi, "jsi");
		wv.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		wv.setWebViewClient(new WebViewClient(){
		        public void onPageFinished(WebView view, String url) {
		            super.onPageFinished(view, url);
		            view.loadUrl("javascript:"+jquery); 
		            view.loadUrl("javascript:"+js);
		        }
		        public boolean shouldOverrideUrlLoading(WebView view, String url) {
		            view.loadUrl(url);
		            return true;
		        }
		        
		    });
		wv.loadUrl("http://www.glstn.sk/jedalen/odhlasenieformular.php");
		// Show the Up button in the action bar.
		setupActionBar();
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
		getMenuInflater().inflate(R.menu.odhlasenie, menu);
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
	
	private String loadJQuery(){
		String js="",s="";
		try { 
			BufferedReader br=new BufferedReader(new InputStreamReader(getAssets().open("jquery.js")));
			while((s=br.readLine())!=null){js+=s;} 
			//br=new BufferedReader(new InputStreamReader(getAssets().open("odhlasenie.js")));
			//while((s=br.readLine())!=null){js+=s;}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return js;
	}
	
	private String loadJS(){
		String js="",s="";
		try { 
			BufferedReader br;//=new BufferedReader(new InputStreamReader(getAssets().open("jquery.js")));
			
			br=new BufferedReader(new InputStreamReader(getAssets().open("odhlasenie.js")));
			while((s=br.readLine())!=null){js+=s;}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return js;
	}
	private String prepareJS(String js,String trieda,String meno, String cislo,String datum){
		js=js.replace("_INPUT_DATUM_", datum);
		js=js.replace("_TRIEDA_", trieda);
		js=js.replace("_INPUT_MENO_", meno);
		js=js.replace("_INPUT_CISLO_", cislo);
		return js;
	}

	public class JSiface{
		SharedPreferences sp;
		boolean failedBefore=false; 
		public void setSP(SharedPreferences sp){
			this.sp=sp;
		}
		
		@JavascriptInterface
		public void saveData(String cislo, String meno){
			sp.edit().putString(Odhlasenie.cislo, cislo).commit();
			sp.edit().putString(Odhlasenie.meno, meno).commit();
		}
		
		@JavascriptInterface
		public boolean hasFailedBefore(){
			return this.failedBefore;
		}
		
		@JavascriptInterface
		public void setFailedBefore(boolean val){
			this.failedBefore=val;
		}
		
		@JavascriptInterface
		public void reload(){
			Odhlasenie.this.runOnUiThread(new Runnable(){

				@Override
				public void run() {
					WebView wv=(WebView) findViewById(R.id.webOdhlasenie);
					//wv.loadUrl("http://www.glstn.sk/jedalen/odhlasenieformular.php");
					wv.reload();
				}
			});
			//failedBefore=false;
			/*Intent i=getIntent();
			Intent i2=new Intent(Odhlasenie.this,Odhlasenie.class);
			i2.putExtras(i.getExtras());
			startActivity(i2);*/
		}
		@JavascriptInterface 
		public void log(String string){
			System.err.print(string);
		}
	
	}

}

