$("td").click(
	function(event){
		a=this.title;		
		if(a!=null&&a!=''){
			jsi.alert(a);
			jsi.log("log:"+a);		
		}else{jsi.log("log:empty");}
	});
