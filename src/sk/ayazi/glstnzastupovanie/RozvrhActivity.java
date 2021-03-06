package sk.ayazi.glstnzastupovanie;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import biz.source_code.base64Coder.Base64Coder;

public class RozvrhActivity extends ActionBarActivity {

	@SuppressLint("SetJavaScriptEnabled")
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_rozvrh);
		Intent i=getIntent();
		String t=i.getStringExtra(MainActivity.TRIEDA);
		WebView wv=(WebView) findViewById(R.id.webRozvrh);
		WebSettings ws=((WebView) findViewById(R.id.webRozvrh)).getSettings();
		ws.setBuiltInZoomControls(true);
		ws.setUseWideViewPort(true);
		ws.setJavaScriptEnabled(true);
		final String jq=loadJQuery();
		final String js=loadJS();
		JSI jsi=new JSI();
		wv.addJavascriptInterface(jsi, "jsi");
		wv.setWebViewClient(new WebViewClient(){
	        public void onPageFinished(WebView view, String url) {
	            super.onPageFinished(view, url);
	            view.loadUrl("javascript:"+jq);
	            view.loadUrl("javascript:"+js);
	        }
	        public boolean shouldOverrideUrlLoading(WebView view, String url) {
	            view.loadUrl(url);
	            return true;
	        }
	        
	    });
		// Show the Up button in the action bar.
		SharedPreferences sp=getApplicationContext().getSharedPreferences("sk.ayazi.glstnzastupovanie", Context.MODE_PRIVATE);
		HashMap<String,String> classes = null;
		try {
			
			classes=(HashMap<String,String>) fromString(sp.getString(MainActivity.TRIEDY,null));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String triedau=classes.get(t);
		setTitle("Rozvrh "+t);
		((WebView) findViewById(R.id.webRozvrh)).loadUrl(Zastup.getRozvrhURL(triedau));
		//new GetRozvrh().execute(triedau);
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
		getMenuInflater().inflate(R.menu.rozvrh, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId=item.getItemId();
		if (itemId==android.R.id.home) {
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
	else if(itemId == R.id.action_obed){//show obed
			String trieda= getApplicationContext().getSharedPreferences("sk.ayazi.glstnzastupovanie", Context.MODE_PRIVATE).getString("sk.ayazi.glstnzastupovanie.trieda", null);
			//String datum = getApplicationContext().getSharedPreferences("sk.ayazi.glstnzastupovanie", Context.MODE_PRIVATE).getString("sk.ayazi.glstnzastupovanie.datum", null);
			Intent intent=new Intent(this,ObedActivity.class);
			intent.putExtra(MainActivity.TRIEDA, trieda);
			intent.putExtras(getIntent().getExtras());
			startActivity(intent);
			}
		return super.onOptionsItemSelected(item);
	}
	
	//May be useful one day, when I decide to implement Rozvrh in a better way than just load the page...
	@SuppressWarnings("unused")
	private class GetRozvrh extends AsyncTask<String,Void,String>{
		@Override
		protected void onPreExecute (){
			 setProgressBarIndeterminateVisibility(true);
		}
			
		@Override
		protected String doInBackground(String... param) {
			return new Zastup().simpleRozvrhFetch(param[param.length-1]);
		}
		protected void onPostExecute (String res){
			setProgressBarIndeterminateVisibility(false);
			if(res==null){
				new AlertDialog.Builder(RozvrhActivity.this)
				.setTitle("Chyba")
				.setMessage("Chyba")
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				    @Override
				    public void onClick(DialogInterface dialog, int which) {
				    	startActivity(new Intent(RozvrhActivity.this,MainActivity.class));
				    }
				})
				.create()
				.show();
				return;
	    	}else{
	    		//System.out.print(res);
	    		//System.out.println();
	    		((WebView) findViewById(R.id.webRozvrh)).loadData("<html><head><meta http-equiv=\"Content-type\" content=\"text/html;charset=UTF-8\"/></head><body>"+res+"</body></html>", "text/html; charset=utf-8", "UTF-8");
	    	}
	    	
			
		}
		
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
			
			br=new BufferedReader(new InputStreamReader(getAssets().open("rozvrh.js")));
			while((s=br.readLine())!=null){js+=s;}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return js;
	}
	public class JSI{
		public String title;
		@JavascriptInterface
		public void alert(String ti){
			this.title=ti;
			RozvrhActivity.this.runOnUiThread(new Runnable(){

				@Override
				public void run() {
				//	log(title);
					if(title.trim()!=""&&title!=null){
						new AlertDialog.Builder(RozvrhActivity.this)
						.setTitle("Popis")
						.setMessage(title)
						.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					    	@Override
					    	public void onClick(DialogInterface dialog, int which) {
					    	}
						})
						.create()
						.show();
					}
					//wv.loadUrl("http://www.glstn.sk/jedalen/odhlasenieformular.php");
					
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
