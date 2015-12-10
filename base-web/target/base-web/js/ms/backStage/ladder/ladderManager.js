/**
 * 添加阶梯规则信息
 */
function addLadder(){
	$("#ladderPopdiv").attr("class","ladderPopdivshow");
	$('#ladderId').attr("disabled",true);
}

/**
 * 编辑促销信息
 * @param skuId
 */
function editLadder(queryLadderId){
	$("#ladderPopdiv").attr("class","ladderPopdivshow");
	
	var param = "ladderId="+queryLadderId+"&r="+(new Date()).getTime();
	$.ajax({
		  url: "/backstage/ladder/queryLadderByCondition.action",
	      type: "POST",
	      data: param,
	      dataType: "json",
	      async:false,
	      success: function(msg){
	    	  if(msg.success){
	    		  var ladderInfoList = msg.ladderList;
	    		  var len =  ladderInfoList.length;
	    		  if(len>0){
    			  	var ladderInfo = ladderInfoList[0];
					$("#ladderId").val(ladderInfo.id);
					$('#ladderId').attr("disabled",true);
					$("#priceDiscount").val(ladderInfo.priceDiscount);
					$("#numPercent").val(ladderInfo.numPercent);
					$("#type").val(ladderInfo.type);
					if(ladderInfo.yn==true){
						$("input[name='isvaliade'][value=1]").attr("checked",true);
					}else{
						$("input[name='isvaliade'][value=0]").attr("checked",true);
					}
	    		  }
	    	  }else{
	    		  alert("sss"+msg.msg);
	    		  $("#tipInfo").text(msg.msg);
	    	  }
	      },
	      error:function(msg){
	    	  alert(msg.msg);
	      }
		 });
}

/**
 * 将促销信息置为无效
 * @param skuId
 */
function delLadder(ladderId){
	var param = "ladderId="+ladderId+"&r="+(new Date()).getTime();
	$.ajax({
		  url: "/backstage/ladder/delPromotion.action",
	      type: "POST",
	      data: param,
	      dataType: "json",
	      async:false,
	      success: function(msg){
	    	  if(msg.success){
	    		  window.location.href = "/backstage/ladder/queryLadderByPageNum.action?r="+(new Date()).getTime();
	    	  }else{
	    		  alert("sss"+msg.msg);
	    		  $("#tipInfo").text(msg.msg);
	    	  }
	      },
	      error:function(msg){
	    	  alert(msg.msg);
	      }
		 });
}

/**
 * 将促销信息置为无效
 * @param skuId
 */
function makeInvalidLadder(ladderId){
	var param = "ladderId="+ladderId+"&r="+(new Date()).getTime();
	$.ajax({
		  url: "/backstage/ladder/makeInvalidLadder.action",
	      type: "POST",
	      data: param,
	      dataType: "json",
	      async:false,
	      success: function(msg){
	    	  if(msg.success){
	    		  window.location.href = "/backstage/ladder/queryLadderByPageNum.action?r="+(new Date()).getTime();
	    	  }else{
	    		  alert("sss"+msg.msg);
	    		  $("#tipInfo").text(msg.msg);
	    	  }
	      },
	      error:function(msg){
	    	  alert(msg.msg);
	      }
		 });
}

/**
 * 关闭促销信息弹出框
 */
function closeLadderPopDiv(){
	$("#ladderPopdiv").attr("class","ladderPopdivhidden");
}

/**
 * 查询促销信息
 */
function queryLadderInfo(){
	var queryLadderId=$("#queryLadderId").val();
	var queryLadderType=$("#queryLadderType").val();
	
	if (isNullValue(queryLadderId) && isNullValue(queryLadderType)) {
		$("#tipInfo").text("请输入规则编号或者规则类型");
		$("#queryLadderId").focus();
		return;
	}
	
	var param = "ladderId="+queryLadderId+"&type="+queryLadderType+"&r="+(new Date()).getTime();
	$.ajax({
		  url: "/backstage/ladder/queryLadderByCondition.action",
	      type: "POST",
	      data: param,
	      dataType: "json",
	      async:false,
	      success: function(msg){
	    	  if(msg.success){
	    		  var ladderInfoList = msg.ladderList;
	    		  var len =  ladderInfoList.length;
	    		  if(len>0){
	    			  var ladderHtmlStr="";
	    			  for(var index=0;index<len;index++){
	    				  var ladderInfo = ladderInfoList[index];
	    				  var skuStr="<tr><td>"+ladderInfo.id+"</td><td>"+ladderInfo.priceDiscount+"</td><td>"+ladderInfo.numPercent+"</td><td>"+ladderInfo.type+"</td>";
	    					if(ladderInfo.yn==true){
	    						skuStr+="<td>有效</td>";
	    					}else{
	    						skuStr+="<td>无效</td>";
	    					}
	    					skuStr+="<td><input type='button' onclick='makeInvalidLadder("+ladderInfo.id+")' value='置为无效'/><input type='button' onclick='editLadder("+ladderInfo.id+")' value='修改信息'/><input type='button' onclick='delLadder("+ladderInfo.id+")' value='删除'/></td></tr>";
	    					ladderHtmlStr+=skuStr;
	    			  }
	    			  $("#ladderInfoBody").html(ladderHtmlStr);
	    		  }
	    	  }else{
	    		  alert("sss"+msg.msg);
	    		  $("#tipInfo").text(msg.msg);
	    	  }
	    	  
	      },
	      error:function(msg){
	    	  alert(msg.msg);
	      }
		 });
}

function updateLadder(){
	var ladderId=$("#ladderId").val();
	$('#ladderId').attr("disabled",true);
	var priceDiscount=$("#priceDiscount").val();
	var numPercent=$("#numPercent").val();
	var type=$("#type").val();
	var isvaliade=$("input[name='isvaliade']:checked").val();

	if (isNullValue(ladderId)) {
		$("#tipInfo").text("请输入阶梯规则编号");
		$("#ladderId").focus();
		return;
	}
	
	if (isNullValue(priceDiscount)) {
		$("#tipInfo").text("请输入价格折扣率");
		$("#priceDiscount").focus();
		return;
	}
	
	if (isNullValue(numPercent)) {
		$("#tipInfo").text("请输入数量折扣率");
		$("#numPercent").focus();
		return;
	}
	if (isNullValue(type)) {
		$("#tipInfo").text("请输入阶梯规则类型");
		$("#type").focus();
		return;
	}
	if (isNullValue(isvaliade)) {
		$("#tipInfo").text("请选择是否有效");
		$("#isvaliade").focus();
		return;
	}
	

	
	var param = "ladderId="+ladderId+"&priceDiscount="+priceDiscount+"&numPercent="+numPercent+"&type="+type+"&isvaliade="+isvaliade+"&r="+(new Date()).getTime();
	$.ajax({
		  url: "/backstage/ladder/updateLadder.action",
	      type: "POST",
	      data: param,
	      dataType: "json",
	      async:false,
	      success: function(msg){
	    	  if(msg.success){
	    		  window.location.href = "/backstage/ladder/queryLadderByPageNum.action?r="+(new Date()).getTime();
	    	  }else{
	    		  alert(msg.msg);
	    		  $("#tipInfo").text(msg.msg);
	    	  }
	      },
	      error:function(msg){
	    	  alert(msg.msg);
	      }
		 });
}

/**
 * 添加商品信息
 */
function submitLadder(){
	var ladderId=$("#ladderId").val();
	$('#ladderId').attr("disabled",true);
	var priceDiscount=$("#priceDiscount").val();
	var numPercent=$("#numPercent").val();
	var type=$("#type").val();
	var isvaliade=$("input[name='isvaliade']:checked").val();

	if (!isNullValue(ladderId)) {
		updateLadder();
		return;
	}
	
	if (isNullValue(priceDiscount)) {
		$("#tipInfo").text("请输入价格折扣率");
		$("#priceDiscount").focus();
		return;
	}
	
	if (isNullValue(numPercent)) {
		$("#tipInfo").text("请输入数量折扣率");
		$("#numPercent").focus();
		return;
	}
	if (isNullValue(type)) {
		$("#tipInfo").text("请输入阶梯规则类型");
		$("#type").focus();
		return;
	}
	if (isNullValue(isvaliade)) {
		$("#tipInfo").text("请选择是否有效");
		$("#isvaliade").focus();
		return;
	}
	

	
	var param = "ladderId="+ladderId+"&priceDiscount="+priceDiscount+"&numPercent="+numPercent+"&type="+type+"&isvaliade="+isvaliade+"&r="+(new Date()).getTime();
	$.ajax({
		  url: "/backstage/ladder/addLadder.action",
	      type: "POST",
	      data: param,
	      dataType: "json",
	      async:false,
	      success: function(msg){
	    	  if(msg.success){
	    		  window.location.href = "/backstage/ladder/queryLadderByPageNum.action?r="+(new Date()).getTime();
	    	  }else{
	    		  alert(msg.msg);
	    		  $("#tipInfo").text(msg.msg);
	    	  }
	      },
	      error:function(msg){
	    	  alert(msg.msg);
	      }
		 });
	
}

/**
 * 取消添加商品信息
 */
function cancleLadder(){
	$("#ladderPopdiv").attr("class","ladderPopdivhidden");
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