package sk.ayazi.glstnzastupovanie;

import java.util.Hashtable;

public class Tr {
	Tr parent;
	boolean isTop;
	
	String chybaj;
	String hodina;
	String zastup;
	String trieda;
	String predmet;
	String typ;
	String poznamka;
	String ucebna;
	
	
	/**@param String chybaj,
			String hodina,
			String zastup,
			String trieda,
			String predmet,
			String typ,
			String poznamka,
			String ucebna
		
	 * */
	public Tr(String chybaj,
			String hodina,
			String zastup,
			String trieda,
			String predmet,
			String typ,
			String poznamka,
			String ucebna){
		this.poznamka=poznamka;
		this.chybaj=chybaj;
		this.zastup=zastup;
		this.trieda=trieda;
		this.typ=typ;
		this.predmet=predmet;
		this.hodina=hodina;
		this.ucebna=ucebna;
		this.isTop=true;
	}
	
	public Tr(){}
	
	public Tr(Tr parent,
			String hodina,
			String zastup,
			String trieda,
			String predmet,
			String typ,
			String poznamka,
			String ucebna){
		this.parent=parent;
		this.poznamka=poznamka;
		this.zastup=zastup;
		this.trieda=trieda;
		this.typ=typ;
		this.predmet=predmet;
		this.hodina=hodina;
		this.ucebna=ucebna;
		this.isTop=false;
	}
	
	public Hashtable<String,String> getData(){
		Hashtable<String,String> ht=new Hashtable<String,String>();
		ht.put("zastupujuci", zastup);
		ht.put("chybajuci", isTop?chybaj:parent.chybaj);
		ht.put("ucebna", ucebna);
		ht.put("trieda", trieda);
		ht.put("typ", typ);
		ht.put("poznamka", poznamka);
		ht.put("hodina", hodina);
		ht.put("predmet",predmet);
		return ht;
	}
	public String getChybaj(){
		return isTop?chybaj:parent.chybaj;
	}
}
