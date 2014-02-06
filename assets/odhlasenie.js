$(document).ready(function(event){
	window.setTimeout(function(){
		jsi.setFailedBefore(false);
		if(!$("#recaptcha_table").length){reload();}
	},1500);
});
$("input[name='meno']").val("_INPUT_MENO_");
$("input[name='cislo']").val("_INPUT_CISLO_");
$("input[name='datum']").val("_INPUT_DATUM_");
$("option[value='_TRIEDA_']").prop("selected","selected");
$("form[action='odhlas.php']").submit(
	function(event){
		var meno=$("input[name='meno']").val();
		var cislo=$("input[name='cislo']").val();
		jsi.saveData(cislo,meno);
	}
);

function reload(){
	jsi.reload();
}

if(!jsi.hasFailedBefore()){
	if(document.URL.indexOf("akcia=captcha")!=-1){
		alert("Zla captcha");
		jsi.setFailedBefore(true);
		reload();
	}else if(document.URL.indexOf("akcia=karta")!=-1){
		alert("Neplatny znak v karta");
		jsi.setFailedBefore(true);
		reload();
	}else if(document.URL.indexOf("akcia=datum")!=-1){
		alert("Zly datum");
		jsi.setFailedBefore(true);
		reload();
	}else if(document.URL.indexOf("akcia=cas")!=-1){
		alert("Neskoro");
		jsi.setFailedBefore(true);	
		reload();
	}
}

