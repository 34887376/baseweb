/**
 * 添加商品信息
 */
function addSku(){
	$("#popdiv").attr("class","popdivshow");
}

/**
 * 编辑商品信息
 * @param skuId
 */
function editSku(querySkuId){
	$("#popdiv").attr("class","popdivshow");
	
	var param = "skuId="+querySkuId+"&r="+(new Date()).getTime();
	$.ajax({
		  url: "/backstage/sku/querySkuByCondition.action",
	      type: "POST",
	      data: param,
	      dataType: "json",
	      async:false,
	      success: function(msg){
	    	  if(msg.success){
	    		  var skuList = msg.skuListInfoList;
	    		  var len =  skuList.length;
	    		  if(len>0){
    			  	var skuInfo = skuList[0];
					$("#skuId").val(skuInfo.id);
					$("#skuNum").val(skuInfo.num);
					$("#skuName").val(skuInfo.name);
					$("#skuAdverst").val(skuInfo.adverst);
					$("#inprice").val(skuInfo.inprice);
					$("#outprice").val(skuInfo.outprice);
					if(skuInfo.yn==true){
						$("input[name='isvaliade'][value=1]").attr("checked",true);
					}else{
						$("input[name='isvaliade'][value=0]").attr("checked",true);
					}
	    		  }
	    	  }else{
	    		  $("#tipInfo").text(msg.msg);
	    	  }
	    	  alert("sss"+msg);
	      },
	      error:function(msg){
	    	  alert(msg);
	      }
		 });
}

/**
 * 将商品信息置为无效
 * @param skuId
 */
function delSku(skuId){
	var param = "skuId="+skuId+"&r="+(new Date()).getTime();
	$.ajax({
		  url: "/backstage/sku/delSkus.action",
	      type: "POST",
	      data: param,
	      dataType: "json",
	      async:false,
	      success: function(msg){
	    	  if(msg.success){
	    		  window.location.href = "/backstage/sku/querySkus.action?r="+(new Date()).getTime();
	    	  }else{
	    		  $("#tipInfo").text(msg.msg);
	    	  }
	    	  alert("sss"+msg);
	      },
	      error:function(msg){
	    	  alert(msg);
	      }
		 });
	
	
}

/**
 * 关闭商品信息弹出框
 */
function closePopDiv(){
	$("#popdiv").attr("class","popdivhidden");
}

/**
 * 查询商品信息
 */
function querySkuInfo(){
	var querySkuId=$("#querySkuId").val();
	var querySkuName=$("#querySkuName").val();
	
	if (isNullValue(querySkuId) && isNullValue(querySkuName)) {
		$("#tipInfo").text("请输入商品编号或者商品名称");
		$("#querySkuId").focus();
		return;
	}
	
	var param = "skuId="+querySkuId+"&skuName="+querySkuName+"&r="+(new Date()).getTime();
	$.ajax({
		  url: "/backstage/sku/querySkuByCondition.action",
	      type: "POST",
	      data: param,
	      dataType: "json",
	      async:false,
	      success: function(msg){
	    	  if(msg.success){
	    		  var skuList = msg.skuListInfoList;
	    		  var len =  skuList.length;
	    		  if(len>0){
	    			  var skuHtmlStr="";
	    			  for(var index=0;index<len;index++){
	    				  var skuInfo = skuList[index];
	    				  var skuStr="<tr><td>"+skuInfo.id+"</td><td>"+skuInfo.name+"</td><td>"+skuInfo.num+"</td><td>"+skuInfo.adverst+"</td>";
	    					if(skuInfo.imgUrl){
	    						skuStr+= "<td>"+skuInfo.imgUrl+"</td>";
	    					}else{
	    						skuStr+="<td><input type='file' name='"+skuInfo.id+"' id='upfileId'></td>";
	    					}
	    					skuStr+="<td>"+skuInfo.inprice+"</td><td>"+skuInfo.outprice+"</td>";
	    					
	    					if(skuInfo.yn==true){
	    						skuStr+="<td>有效</td>";
	    					}else{
	    						skuStr+="<td>无效</td>";
	    					}
	    					
	    					skuStr+="<td><input type='button' onclick='delSku("+skuInfo.id+")' value='置为无效'><input type='button' onclick='editSku("+skuInfo.id+")' value='修改信息'></td></tr>";
	    					skuHtmlStr+=skuStr;
	    			  }
	    			  $("#skuInfoBody").html(skuHtmlStr);
	    		  }
	    	  }else{
	    		  $("#tipInfo").text(msg.msg);
	    	  }
	    	  alert("sss"+msg);
	      },
	      error:function(msg){
	    	  alert(msg);
	      }
		 });
}

function updateSku(){
	var skuId=$("#skuId").val();
	var skuNum=$("#skuNum").val();
	var skuName=$("#skuName").val();
	var skuAdverst=$("#skuAdverst").val();
	var inprice=$("#inprice").val();
	var outprice=$("#outprice").val();
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
	
	if (isNullValue(skuName)) {
		$("#tipInfo").text("请输入商品名称");
		$("#skuName").focus();
		return;
	}
	
	
	if (isNullValue(skuAdverst)) {
		$("#userEmail").text("请输入广告词");
		$("#skuAdverst").focus();
		return;
	}
	
	if (isNullValue(inprice)) {
		$("#tipInfo").text("请输入进货价");
		$("#inprice").focus();
		return;
	}
//	
//	if(!isPhone(userPhone)){
//		$("#tipInfo").text("请输入正确的手机号码");
//		$("#phone").focus();
//		return;
//	}
	
	if (isNullValue(outprice)) {
		$("#tipInfo").text("请输入出货价");
		$("#outprice").focus();
		return;
	}
	
	if (isNullValue(isvaliade)) {
		$("#tipInfo").text("请选择是否有效");
		$("#isvaliade").focus();
		return;
	}
	

	
	var param = "skuId="+skuId+"&skuNum="+skuNum+"&skuName="+skuName+"&skuAdverst="+skuAdverst+"&inprice="+inprice+"&outprice="+outprice+"&isvaliade="+isvaliade+"&r="+(new Date()).getTime();
	$.ajax({
		  url: "/backstage/sku/updateSkus.action",
	      type: "POST",
	      data: param,
	      dataType: "json",
	      async:false,
	      success: function(msg){
	    	  if(msg.success){
	    		  window.location.href = "/backstage/sku/querySkus.action?r="+(new Date()).getTime();
	    	  }else{
	    		  $("#tipInfo").text(msg.msg);
	    	  }
	    	  alert("sss"+msg);
	      },
	      error:function(msg){
	    	  alert(msg);
	      }
		 });
}

/**
 * 添加商品信息
 */
function submitSku(){
	var skuId=$("#skuId").val();
	var skuNum=$("#skuNum").val();
	var skuName=$("#skuName").val();
	var skuAdverst=$("#skuAdverst").val();
	var inprice=$("#inprice").val();
	var outprice=$("#outprice").val();
	var upfileId=$("#upfileId").val();
	var isvaliade=$("input[name='isvaliade']:checked").val();

	if (!isNullValue(skuId)) {
		updateSku();
		return;
	}
	
	if (isNullValue(skuNum)) {
		$("#tipInfo").text("请输入商品数量");
		$("#skuNum").focus();
		return;
	}
	
	if (isNullValue(skuName)) {
		$("#tipInfo").text("请输入商品名称");
		$("#skuName").focus();
		return;
	}
	
	
	if (isNullValue(skuAdverst)) {
		$("#userEmail").text("请输入广告词");
		$("#skuAdverst").focus();
		return;
	}
	
	if (isNullValue(inprice)) {
		$("#tipInfo").text("请输入进货价");
		$("#inprice").focus();
		return;
	}
//	
//	if(!isPhone(userPhone)){
//		$("#tipInfo").text("请输入正确的手机号码");
//		$("#phone").focus();
//		return;
//	}
	
	if (isNullValue(outprice)) {
		$("#tipInfo").text("请输入出货价");
		$("#outprice").focus();
		return;
	}
	
	if (isNullValue(isvaliade)) {
		$("#tipInfo").text("请选择是否有效");
		$("#isvaliade").focus();
		return;
	}
	

	
	var param = "skuId="+skuId+"&skuNum="+skuNum+"&skuName="+skuName+"&skuAdverst="+skuAdverst+"&inprice="+inprice+"&outprice="+outprice+"&isvaliade="+isvaliade+"&r="+(new Date()).getTime();
	$.ajax({
		  url: "/backstage/sku/addSkus.action",
	      type: "POST",
	      data: param,
	      dataType: "json",
	      async:false,
	      success: function(msg){
	    	  if(msg.success){
	    		  window.location.href = "/backstage/sku/querySkus.action?r="+(new Date()).getTime();
	    	  }else{
	    		  $("#tipInfo").text(msg.msg);
	    	  }
	    	  alert("sss"+msg);
	      },
	      error:function(msg){
	    	  alert(msg);
	      }
		 });
	
}

/**
 * 取消添加商品信息
 */
function cancleSku(){
	$("#popdiv").attr("class","popdivhidden");
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