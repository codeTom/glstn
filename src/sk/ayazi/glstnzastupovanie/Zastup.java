package sk.ayazi.glstnzastupovanie;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Zastup {
	private Pattern trp=Pattern.compile("<tr>(.*?)<\\/tr>");
	private Pattern tdp=Pattern.compile("\\<td.*?>(.*?)\\<\\/td\\>");
	private Pattern oznp=Pattern.compile("<tr class\\=\"Oznam\">(.*?)</tr>");
	private Pattern nav=Pattern.compile("zobrazit\\(\"zast_(.*?)\\.htm");
	private final Pattern men=Pattern.compile("<option value=\"zast_(.*?).htm\">.*?</option>"); 
	private final Pattern tried=Pattern.compile("<option value=\"rozvrh_tr.*?\\.htm\">(.*?)&nbsp;&nbsp;</option>");
	private String fpage;
	private ArrayList<Tr> trs=new ArrayList<Tr>();
	private boolean noZast;
	/**
	 * @param args
	 * @throws Exception 
	 */
	
	public static void main(String[] args) throws Exception {
		
		//zast();
		//System.out.print(loadPage("20130211"));
		Zastup z=new Zastup();
		Iterator i = z.getClasses().iterator();
		while(i.hasNext()){System.out.println(i.next());}
		//z.load("20130211");
		//ArrayList<Hashtable<String,String>> hts=z.getRelevant("II.B");
		//Iterator<Hashtable<String,String>> i=hts.iterator();
		//while(i.hasNext()){
		///	Hashtable<String,String> ht= i.next();
		//	System.out.println(
	//				ht.get("chybajuci")+":"+ht.get("hodina")+":"+ht.get("predmet")+":"+ht.get("ucebna")+":"+ht.get("zastupujuci")+":"+ht.get("poznamka"));
	//	}
	//	System.out.print(z.getOznam());
	}
	
	public void load(String date) throws IOException{
		trs=new ArrayList<Tr>();
		
		this.fpage=loadPage(date);
		
	}
	
	public String getOznam(){
		Matcher m=oznp.matcher(fpage);
		if(m.find()){return m.group(1).substring(17).replaceAll("<BR>", "\n").replaceAll("&nbsp;", "\n").replaceAll("<br>", "").replace("</td>", "");}
		else return "";
	}
	
	
	public ArrayList<String[]> getTable(String trieda){
		ArrayList<String[]> slist=new ArrayList<String[]>();
		if(noZast){return slist;}
		boolean firstReached=false;
			//String fpage=loadPage(date);
			String page;
			if(fpage.contains("<td>&nbsp;oznam<br></td>")){
				page=fpage.substring(0, fpage.indexOf("<td>&nbsp;oznam<br></td>"));}
			else {page=fpage;}
			String t,s;
			Matcher m=trp.matcher(page);			
			Matcher tdm;
			Tr topTr = null;
			while(m.find()){
				Tr tr=new Tr();
				t=m.group(1);
				//System.out.println(t);
				tdm=tdp.matcher(t);
				tdm.find();				
				s=tdm.group(1);
				//System.out.println(s);
				tdm.find();
				tr.hodina=tdm.group(1);
				tdm.find();
				tr.trieda=tdm.group(1);
				tdm.find();
				tr.predmet=tdm.group(1);
				tdm.find();
				tr.ucebna=tdm.group(1);
				tdm.find();
				tr.zastup=tdm.group(1);
				tdm.find();
				tr.typ=tdm.group(1);
				tdm.find();
				tr.poznamka=tdm.group(1);
				if(!firstReached&&!s.contains("nbsp")){firstReached=true;}
				if(!firstReached&&s.contains("nbsp")){tr.chybaj="";tr.isTop=true;}
				if(firstReached&&!s.contains("nbsp")){
					tr.isTop=true;
					tr.chybaj=s;
					topTr=tr;
				}
				if(firstReached&&s.contains("nbsp")){
					tr.isTop=false;
					tr.parent=topTr;
				}
				trs.add(tr);
			}
			Iterator<Tr> i=trs.iterator();
			while(i.hasNext()){
				Tr tr=i.next();
				boolean isRel=false;
				String[] str=new String[6];
				String tried=tr.getData().get("trieda");
				String[] trieds=tried.split(Pattern.quote(","));
				for(int c=0;c<trieds.length;c++){
					if(trieds[c].trim().equalsIgnoreCase(trieda))isRel=true;
				}
				if(isRel){
					str[0]=tr.getChybaj();
					str[1]=tr.hodina.equalsIgnoreCase("&nbsp;")?"-":tr.hodina;
					str[2]=tr.ucebna.equalsIgnoreCase("&nbsp;")?"-":tr.ucebna;
					str[3]=tr.predmet;
					str[4]=tr.zastup.equalsIgnoreCase("-----")?"-":tr.zastup;
					//str[5]=tr.typ.equalsIgnoreCase("&nbsp;")?"-":tr.typ;
					str[5]=tr.poznamka.equalsIgnoreCase("&nbsp;")?"-":tr.poznamka;
					slist.add(str);
					//System.out.println(tr.getData().get("chybajuci"));
					}
				}
			
		return slist;
	}
	/**	Check before use
	 * @deprecated 
	 * */
	public ArrayList<Hashtable<String,String>> getRelevant(String trieda){
		ArrayList<Hashtable<String,String>> hts=new ArrayList<Hashtable<String,String>>();
		boolean firstReached=false;
		
		try {
			//String fpage=loadPage(date);
			String page=fpage.substring(0, fpage.indexOf("<td>&nbsp;oznam<br></td>"));
			String t,s;
			Matcher m=trp.matcher(page);			
			Matcher tdm;
			Tr topTr = null;
			while(m.find()){
				Tr tr=new Tr();
				t=m.group(1);
				//System.out.println(t);
				tdm=tdp.matcher(t);
				tdm.find();				
				s=tdm.group(1);
				//System.out.println(s);
				tdm.find();
				tr.hodina=tdm.group(1);
				tdm.find();
				tr.trieda=tdm.group(1);
				tdm.find();
				tr.predmet=tdm.group(1);
				tdm.find();
				tr.ucebna=tdm.group(1);
				tdm.find();
				tr.zastup=tdm.group(1);
				tdm.find();
				tr.typ=tdm.group(1);
				tdm.find();
				tr.poznamka=tdm.group(1);
				if(!firstReached&&!s.contains("nbsp")){firstReached=true;}
				if(!firstReached&&s.contains("nbsp")){tr.chybaj="";tr.isTop=true;}
				if(firstReached&&!s.contains("nbsp")){
					tr.isTop=true;
					tr.chybaj=s;
					topTr=tr;
				}
				if(firstReached&&s.contains("nbsp")){
					tr.isTop=false;
					tr.parent=topTr;
				}
				trs.add(tr);
			}
			Iterator<Tr> i=trs.iterator();
			while(i.hasNext()){
				Tr tr=i.next();
				boolean isRel=false;
				String tried=tr.getData().get("trieda");
				String[] trieds=tried.split(Pattern.quote(","));
				for(int c=0;c<trieds.length;c++){
					if(trieds[c].trim().equalsIgnoreCase(trieda))isRel=true;
				}
				if(isRel){
					hts.add(tr.getData());
					//System.out.println(tr.getData().get("chybajuci"));
					}
				}
		} catch (Exception e) {
		
			e.printStackTrace();
		}		
		return hts;
	}	
	
	private String loadPage(String date) throws IOException{
		String page="";
		URL url=new URL("http://www.glstn.sk/zastupo/zast_"+date+".htm");
	    BufferedReader in = new BufferedReader(
				new InputStreamReader(url.openStream(),Charset.forName("Cp1250")));
        String inputLine;
       
        boolean b=false;
        while ((inputLine = in.readLine()) != null){
        	if(b)page+=inputLine;
        	if(inputLine.contains("<td class=\"Hlavicka\" width=\"150\"")){noZast=false;b=true;}
        	else if(inputLine.contains(";oznam")&!b){b=true; noZast=true; page+=inputLine;}
        	
        	}
       	return page.length()<5?"":page.substring(5);
       	}
	
	
	/**method to get next non-weekend day,from system time
	 * @return next day(Monday-Friday) in a YYYYMMDD format
	 * */
	    String getNextDay(){
	    	Date d=new Date(System.currentTimeMillis());
			Calendar c=Calendar.getInstance();
			c.setTime(d);
			int toNext;
			switch (c.get(Calendar.DAY_OF_WEEK)){
				case 6: {toNext=3;break;}
				case 7: {toNext=2;break;}
				default: toNext=1;
			}
			String month=String.valueOf(
					(c.get(Calendar.MONTH)+1)<10?("0"+(c.get(Calendar.MONTH)+1)):
				(c.get(Calendar.MONTH)+1));
			String day=String.valueOf(
					(c.get(Calendar.DATE)+toNext)<10?("0"+(c.get(Calendar.DATE)+toNext)):
				(c.get(Calendar.DATE))+toNext);
	    	return ""+c.get(Calendar.YEAR)+month+day;
			  }
	    
	    public String getNextAvailable() throws IOException{
	    	String page="";
	    	URL url=new URL("http://www.glstn.sk/zastupo/zast_menu.htm");
		    BufferedReader in = new BufferedReader(
					new InputStreamReader(url.openStream(),Charset.forName("Cp1250")));
	        String inputLine;
	        while ((inputLine = in.readLine()) != null){
	        	page+=inputLine;}
	    	Date d=new Date();
	    	Date today=new Date(System.currentTimeMillis());
	    	String pdate=null;
	    	String date=null;
	    	Matcher m=men.matcher(page);
	    	if(m==null){return null;}
	    	do{ 
	    		pdate=date;
	    		if(m.find()){date=m.group(1);d=toDate(date);}
	    		else{return pdate;}
	    	}while(d.after(today));
	    	return pdate;
	    }
	    
	    public ArrayList<String> getClasses() throws IOException{
	    	ArrayList<String> classes=new ArrayList<String>();
	    	URL url=new URL("http://www.glstn.sk/rozvrh/rozvrh_tr_menu.htm");
		    BufferedReader in = new BufferedReader(
					new InputStreamReader(url.openStream(),Charset.forName("Cp1250")));
	        String inputLine,page="";
	        while ((inputLine = in.readLine()) != null){
	        	page+=inputLine;}
	        Matcher m=tried.matcher(page);
	        while(m.find()){classes.add(m.group(1));}
	    	return classes;
	    }	    
	    	    
	    /**	Get latest zastupovanie available
	     * */
	    public String getLatest() throws IOException{
	    	String page="";
	    	URL url=new URL("http://www.glstn.sk/zastupo/zast_menu.htm");
		    BufferedReader in = new BufferedReader(
					new InputStreamReader(url.openStream(),Charset.forName("Cp1250")));
	        String inputLine;
	        while ((inputLine = in.readLine()) != null){
	        	page+=inputLine;}
	        Matcher m=nav.matcher(page);
	        if(m.find()) return m.group(1);
	       	return null;
	    }
	    
	    /** @return Date reprezentation of YYYYMMDD string
	     * */
	    private Date toDate(String date){
	    	try {
				return new SimpleDateFormat("yyyyMMdd").parse(date);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;	
	    	
	    }
}
