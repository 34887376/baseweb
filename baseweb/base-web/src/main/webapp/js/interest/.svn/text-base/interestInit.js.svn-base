/**
 * 选中某一个主题后的样式变化
 * 
 * @param obj
 */
function submitSubject(obj) {
	var parent = $(obj).parent().children().attr("class", null);
	$(obj).attr("class", "curr");
	var selectedSubjectId = $("#subjectList").children(".curr").attr("id");
	loadInterestSku(selectedSubjectId);
}
/**
 * 根据主题id加载兴趣圈商品列表
 * 
 * @param subjectId
 */
function loadInterestSku(subjectId){
	if (subjectId==null || typeof(subjectId)=="undefined" || subjectId==""){
		var subjectId = $("#subjectList").children(".curr").attr("id");
	}
	var sku=document.getElementsByClassName("love2-icon");
	var skuList=new Array();
	 if(sku!=null && sku.length>0){
		var skuNum = sku.length;
		for(var i=0;i<skuNum;i++){
			skuList[i]=sku[i].getAttribute("value");
		}
	}
	 $.ajax({
		  url: "/interest/obtainInterestBySubject.action", 
		  global: false, 
		  type:"POST", 
		  data: "userSkuVO.subjectId="+subjectId+"&skuId="+skuList +"&r="+ (new Date()).getTime(),
		  dataType: "json", 
		  async:true, 
		  success: function(msg){
			  	$("#interestSkuList").html(""); 
			  	var len = msg.length;
		    	  if(len==0){
		    		  $("#interestSkuList").append("该主题下没有感兴趣商品，请先将商品加入购物车或加关注！！！");
		    		  return;
		    	  }
			  	for(var i=0;i<len;i++){ 
			  		var skuInfo = msg[i]; 
			  		var interestInfo="<div class=\"interest-pro fl\">"+ 
			  						"<div class=\"inp-pic\">"+ 
			  							"<i id="+skuInfo.skuId+" class=\"love-icon\" value="+skuInfo.skuId+" onclick=\"change("+skuInfo.skuId+")\"></i><img src="+skuInfo.imgUrl+">"+
			  						"</div>"+
			  						"<div class=\"inp-name\">" +
			  							"<a href=\"http://item.m.jd.com/product/"+skuInfo.skuId+".html\">"+skuInfo.skuName+"<\/a>"+
			  						"</div>"+
			  							"<div class=\"inp-num\">";
			  		if(skuInfo.price>0){
			  			interestInfo+="<span class=\"inp-pay\"> ¥"+skuInfo.price+" <\/span>";
			  		}			  							
			  			interestInfo+="<span class=\"inp-from\"> 来自："+skuInfo.fromName+"<\/span>"+
			  							"</div>"+
			  						"</div>"+
			  					"</div>";	
			  		$("#interestSkuList").append(interestInfo);
			  	 }
		    	 
	      },
	      error:function(msg){
	    	  alert("网络异常！！！");
	      }
		 });
}
function change(skuId){
	element=document.getElementById(skuId);
	if(element.getAttribute("class")=="love-icon"){
		element.setAttribute("class", "love2-icon");
	}else{
		element.setAttribute("class", "love-icon");
	}		  
}