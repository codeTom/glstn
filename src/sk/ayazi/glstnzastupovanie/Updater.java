package sk.ayazi.glstnzastupovanie;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Intent;
import android.net.Uri;

public class Updater {
	private static final Pattern latestP=Pattern.compile("<a href=\"/codeTom/glstn/releases/download/(.*?)/(.*?)\" rel=\"nofollow\" class=\"button primary\">");
	private static final Pattern latestM=Pattern.compile("<div class=\"markdown-body\">\\s*<p>(.*?)</p>");
	private String latest;
	private String message="";
	private URL url;	
	
	public Updater() throws IOException{
		
		String page="";
		URL url=new URL("https://github.com/codeTom/glstn/releases");
	    BufferedReader in = new BufferedReader(
				new InputStreamReader(url.openStream(),Charset.forName("UTF8")));
        String inputLine;
        while((inputLine=in.readLine())!=null){page+=inputLine;}
        Matcher m=latestP.matcher(page);
        if(!m.find()){throw new IOException("Nenajdene");}
        latest=m.group(1).substring(1);
        Matcher m2=latestM.matcher(page);
        if(!m2.find()){System.err.println("nenajdene");throw new IOException("Nenajdene");}
        this.message=m2.group(1);
        //System.err.println(message);
        this.url=new URL("http://github.com/codeTom/glstn/releases/download/"+m.group(1)+"/"+m.group(2));
        }
	
	public String getLatest(){
		return latest;
	}
	
	public boolean isLatest(String current){
		if(current==null||latest==null||latest.equalsIgnoreCase(current)){return true;}
		else{return false;}
	}

	public Intent update() throws URISyntaxException{
		/*Uri uri = null;
		uri=Uri.parse(url.toURI().toString());
		Intent i =new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(uri, "application/vnd.android.package-archive");
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);*/
		Intent i = new Intent(Intent.ACTION_VIEW,Uri.parse(url.toString()));
		return i;
	}

	public String getMessage() {
		return message;
	}

	
	
}