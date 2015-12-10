/**
 * 添加促销信息
 */
function addPromotion(){
	$("#promotionPopdiv").attr("class","promotionPopdivshow");
	$('#promotionId').attr("disabled",true);
}

/**
 * 编辑促销信息
 * @param skuId
 */
function editPromotion(queryPromotionId){
	$("#promotionPopdiv").attr("class","promotionPopdivshow");
	
	var param = "promotionId="+queryPromotionId+"&r="+(new Date()).getTime();
	$.ajax({
		  url: "/backstage/promotion/queryPromotionByCondition.action",
	      type: "POST",
	      data: param,
	      dataType: "json",
	      async:false,
	      success: function(msg){
	    	  if(msg.success){
	    		  var promotionList = msg.promotionListInfoList;
	    		  var len =  promotionList.length;
	    		  if(len>0){
    			  	var promotionInfo = promotionList[0];
					$("#promotionId").val(promotionInfo.id);
					$('#promotionId').attr("disabled",true);
					$("#skuId").val(promotionInfo.skuId);
					$("#skuNum").val(promotionInfo.skuNum);
					if(promotionInfo.yn==true){
						$("input[name='isvaliade'][value=1]").attr("checked",true);
					}else{
						$("input[name='isvaliade'][value=0]").attr("checked",true);
					}
	    		  }
	    	  }else{
	    		  alert("sss"+msg);
	    		  $("#tipInfo").text(msg.msg);
	    	  }
	      },
	      error:function(msg){
	    	  alert(msg);
	      }
		 });
}

/**
 * 将促销信息置为无效
 * @param skuId
 */
function delPromotion(promotionId){
	var param = "promotionId="+promotionId+"&r="+(new Date()).getTime();
	$.ajax({
		  url: "/backstage/promotion/delPromotion.action",
	      type: "POST",
	      data: param,
	      dataType: "json",
	      async:false,
	      success: function(msg){
	    	  if(msg.success){
	    		  window.location.href = "/backstage/promotion/queryPromotions.action?r="+(new Date()).getTime();
	    	  }else{
	    		  alert("sss"+msg);
	    		  $("#tipInfo").text(msg.msg);
	    	  }
	      },
	      error:function(msg){
	    	  alert(msg);
	      }
		 });
	
	
}

/**
 * 关闭促销信息弹出框
 */
function closePromotionPopDiv(){
	$("#promotionPopdiv").attr("class","promotionPopdivhidden");
}

/**
 * 查询促销信息
 */
function queryPromotionInfo(){
	var querySkuId=$("#querySkuId").val();
	var queryPromotionId=$("#queryPromotionId").val();
	
	if (isNullValue(querySkuId) && isNullValue(queryPromotionId)) {
		$("#tipInfo").text("请输入商品编号或者促销编号");
		$("#querySkuId").focus();
		return;
	}
	
	var param = "skuId="+querySkuId+"&promotionId="+queryPromotionId+"&r="+(new Date()).getTime();
	$.ajax({
		  url: "/backstage/promotion/queryPromotionByCondition.action",
	      type: "POST",
	      data: param,
	      dataType: "json",
	      async:false,
	      success: function(msg){
	    	  if(msg.success){
	    		  var promotionList = msg.promotionListInfoList;
	    		  var len =  promotionList.length;
	    		  if(len>0){
	    			  var promotionHtmlStr="";
	    			  for(var index=0;index<len;index++){
	    				  var promotionInfo = promotionList[index];
	    				  var skuStr="<tr><td>"+promotionInfo.id+"</td><td>"+promotionInfo.skuId+"</td><td>"+promotionInfo.skuNum+"</td>";
	    					if(promotionInfo.yn==true){
	    						skuStr+="<td>有效</td>";
	    					}else{
	    						skuStr+="<td>无效</td>";
	    					}
	    					skuStr+="<td><input type='button' onclick='delPromotion("+promotionInfo.id+")' value='置为无效'><input type='button' onclick='editPromotion("+promotionInfo.id+")' value='修改信息'></td></tr>";
	    					promotionHtmlStr+=skuStr;
	    			  }
	    			  $("#promotionInfoBody").html(promotionHtmlStr);
	    		  }
	    	  }else{
	    		  alert("sss"+msg);
	    		  $("#tipInfo").text(msg.msg);
	    	  }
	    	  
	      },
	      error:function(msg){
	    	  alert(msg);
	      }
		 });
}

function updatePromotion(){
	var promotionId=$("#promotionId").val();
	var skuId=$("#skuId").val();
	var skuNum=$("#skuNum").val();
	var isvaliade=$("input[name='isvaliade']:checked").val();

	if (isNullValue(skuId)) {
		$("#tipInfo").text("请输入商品编号");
		$("#skuId").focus();
		return;
	}
	
	if (isNullValue(skuNum)) {
		$("#tipInfo").text("请输入商品数量");
		$("#skuNum").focus();
		return;
	}
	
	if (isNullValue(promotionId)) {
		$("#tipInfo").text("促销编号不可为空");
		$("#promotionId").focus();
		return;
	}
	
	
	if (isNullValue(isvaliade)) {
		$("#tipInfo").text("请选择是否有效");
		$("#isvaliade").focus();
		return;
	}
	

	
	var param = "skuId="+skuId+"&skuNum="+skuNum+"&promotionId="+promotionId+"&yn="+isvaliade+"&r="+(new Date()).getTime();
	$.ajax({
		  url: "/backstage/promotion/updatePromotion.action",
	      type: "POST",
	      data: param,
	      dataType: "json",
	      async:false,
	      success: function(msg){
	    	  if(msg.success){
	    		  window.location.href = "/backstage/promotion/queryPromotions.action?r="+(new Date()).getTime();
	    	  }else{
	    		  alert("sss"+msg);
	    		  $("#tipInfo").text(msg.msg);
	    	  }
	      },
	      error:function(msg){
	    	  alert(msg);
	      }
		 });
}

/**
 * 添加商品信息
 */
function submitPromotion(){
	var promotionId=$("#promotionId").val();
	var skuId=$("#skuId").val();
	var skuNum=$("#skuNum").val();
	var isvaliade=$("input[name='isvaliade']:checked").val();

	$('#promotionId').attr("disabled",true); 
	
	if (!isNullValue(promotionId)) {
		updatePromotion();
		return;
	}
	
	if (isNullValue(skuNum)) {
		$("#tipInfo").text("请输入商品数量");
		$("#skuNum").focus();
		return;
	}
	
	if (isNullValue(skuId)) {
		$("#tipInfo").text("请输入商品编号");
		$("#skuName").focus();
		return;
	}
	
	
	if (isNullValue(isvaliade)) {
		$("#tipInfo").text("请选择是否有效");
		$("#isvaliade").focus();
		return;
	}
	

	
	var param = "skuId="+skuId+"&skuNum="+skuNum+"&promotionId="+promotionId+"&yn="+isvaliade+"&r="+(new Date()).getTime();
	$.ajax({
		  url: "/backstage/promotion/addPromotion.action",
	      type: "POST",
	      data: param,
	      dataType: "json",
	      async:false,
	      success: function(msg){
	    	  if(msg.success){
	    		  window.location.href = "/backstage/promotion/queryPromotions.action?r="+(new Date()).getTime();
	    	  }else{
	    		  alert("sss"+msg);
	    		  $("#tipInfo").text(msg.msg);
	    	  }
	      },
	      error:function(msg){
	    	  alert(msg);
	      }
		 });
	
}

/**
 * 取消添加商品信息
 */
function canclePromotion(){
	$("#promotionPopdiv").attr("class","promotionPopdivhidden");
}


function checkImgName(imgName){
	//正则表达式，校验图片格式
	var syn=new RegExp("\.(jpg|jpeg|ico|bmp|png)$","i");
	//var syn=/\.(jpg|jpeg|ico|bmp|png)$/i;  注意这里不是字符串
	var r = syn.test(imgName);
	if(imgName == "") return false;
	if (!syn.test(imgName)){
		alert("请上传jpg/png/ico/bmp格式的图片！");
		return false;
	}
	return true;
}
var preivew = function(file){
    try{ 
    	var imgNames = file.value;
    	var uid = "#" + file.id;

    	//校验图片格式
    	if(checkImgName(imgNames)==false){
    		$(uid).remove();
    	}
    	return;
    }catch(e){ 
        alert(e); 
    } 
}