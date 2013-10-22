package sk.ayazi.glstnzastupovanie;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.TreeMap;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import biz.source_code.base64Coder.Base64Coder;

public class JedalnyListok {
	public int storeDays=10;
	private TreeMap<Date, String[]> listok;
	private SharedPreferences sp;
	private final String LISTOK="sk.ayazi.glstnzastupovanie.listok";
	Zastup z=new Zastup();
	
	@SuppressWarnings("unchecked")
	public JedalnyListok(SharedPreferences sp){
		try {
			this.sp=sp;
			this.listok=(TreeMap<Date, String[]>) fromString(sp.getString(LISTOK, toString(new TreeMap<Date, String[]>())));
			} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private String toString(Serializable o){
		if(o==null)return null;
		try{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream( baos );
		oos.writeObject( o );
		oos.close();
		return new String( Base64Coder.encode( baos.toByteArray() ) );}catch(Exception e){e.printStackTrace(); return null;}
	}
	private Object fromString(String s) throws IOException,ClassNotFoundException {
		if(s==null)return null;
			byte [] data = Base64Coder.decode( s );
			ObjectInputStream ois = new ObjectInputStream( 
			new ByteArrayInputStream(  data ) );
			Object o  = ois.readObject();
			ois.close();
			return o;
	}
	
	public String[] getObed(Date date) throws ObedNotAvailableException{
		
		if(listok.containsKey(date)){return listok.get(date);}
		else{
			try {
				update();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(listok.containsKey(date)){return listok.get(date);}
		}
		throw new ObedNotAvailableException();
	}
	
	private void update() throws IOException{
		
		TreeMap<Date,String[]> ts= z.getWeekMenu();
		if(ts!=null){listok.putAll(ts);}
		clean();
		save();
	}
	
	@SuppressLint("NewApi")
	private void clean(){
		if(android.os.Build.VERSION.SDK_INT>9){
		Calendar c=Calendar.getInstance();
		c.setTime(new Date());
		c.add(Calendar.DATE, -1*storeDays);
		Date date=c.getTime();
		Date d;
		
		while((d=listok.floorKey(date))!=null){
			listok.remove(d);}
		}//TODO: write version for api 8, no support for floorkey. Api8 will keep all records, taking space and slowing down(its log(n) so not by much)
	}
	
	private void save(){
		sp.edit().putString(LISTOK,toString(listok)).commit();
	}
	
}
