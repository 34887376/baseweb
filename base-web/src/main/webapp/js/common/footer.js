window.onload=myfun;
function myfun()
{
    var u=window.location.pathname;
    var parent = $("#mine").parent().children().attr("class",null);
    if(u.indexOf('/home/')>-1||u.indexOf('/discuss/')>-1){
    	$("#mine").attr("class","footer-cur");
    }else if(u.indexOf('/interest/')>-1){
    	$("#interest").attr("class","footer-cur");
    }else{
    	$("#togther").attr("class","footer-cur");
    }
}
	
function changeItem(obj){
	var parent = $(obj).parent().children().attr("class",null);
	$(obj).attr("class","footer-cur");
	
	var u=window.location.pathname;
	alert(u);
}