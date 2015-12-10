/**
 * 添加阶梯规则信息
 */
function addLadderPromotion(){
	$("#ladderPromotionPopdiv").attr("class","ladderPromotionPopdivshow");
	$('#ladderId').attr("disabled",true);
}

/**
 * 编辑促销信息
 * @param skuId
 */
function editLadderPromotion(queryladderPromotionId){
	$("#ladderPromotionPopdiv").attr("class","ladderPromotionPopdivshow");
	
	var param = "ladderPromotionId="+queryladderPromotionId+"&r="+(new Date()).getTime();
	$.ajax({
		  url: "/backstage/ladderPromotion/queryLadderPromotionByCondition.action",
	      type: "POST",
	      data: param,
	      dataType: "json",
	      async:false,
	      success: function(msg){
	    	  if(msg.success){
	    		  var ladderPromotionInfoList = msg.ladderPromotionBOList;
	    		  var len =  ladderPromotionInfoList.length;
	    		  if(len>0){
    			  	var ladderPromotionInfo = ladderPromotionInfoList[0];
					$("#ladderPromotionId").val(ladderPromotionInfo.id);
					$('#ladderPromotionId').attr("disabled",true);
					$("#promotionId").val(ladderPromotionInfo.promotionId);
					$("#ladderId").val(ladderPromotionInfo.ladderId);
					if(ladderPromotionInfo.yn==true){
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
function delLadderPromotion(ladderPromotionId){
	var param = "ladderPromotionId="+ladderPromotionId+"&r="+(new Date()).getTime();
	$.ajax({
		  url: "/backstage/ladderPromotion/physicalDel.action",
	      type: "POST",
	      data: param,
	      dataType: "json",
	      async:false,
	      success: function(msg){
	    	  if(msg.success){
	    		  window.location.href = "/backstage/ladderPromotion/queryLadderPromotionByPageNum.action?r="+(new Date()).getTime();
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
function makeInvalidLadderPromotion(ladderPromotionId){
	var param = "ladderPromotionId="+ladderPromotionId+"&r="+(new Date()).getTime();
	$.ajax({
		  url: "/backstage/ladderPromotion/makeInvalidLadderPromotion.action",
	      type: "POST",
	      data: param,
	      dataType: "json",
	      async:false,
	      success: function(msg){
	    	  if(msg.success){
	    		  window.location.href = "/backstage/ladderPromotion/queryLadderPromotionByPageNum.action?r="+(new Date()).getTime();
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
function closeLadderPromotionPopDiv(){
	$("#ladderPromotionPopdiv").attr("class","ladderPromotionPopdivhidden");
}

/**
 * 查询促销信息
 */
function queryLadderPromotionInfo(){
	var queryLadderPromotionId=$("#queryLadderPromotionId").val();
	var queryLadderId=$("#queryLadderId").val();
	var queryPromotionId=$("#queryPromotionId").val();
	
	if (isNullValue(queryLadderPromotionId) && isNullValue(queryLadderId) && isNullValue(queryPromotionId)) {
		$("#tipInfo").text("请输入规则编号或者促销编号");
		$("#queryLadderPromotionId").focus();
		return;
	}
	
	var param = "ladderId="+queryLadderId+"&ladderPromotionId="+queryLadderPromotionId+"&promotionId="+queryPromotionId+"&r="+(new Date()).getTime();
	$.ajax({
		  url: "/backstage/ladderPromotion/queryLadderPromotionByCondition.action",
	      type: "POST",
	      data: param,
	      dataType: "json",
	      async:false,
	      success: function(msg){
	    	  if(msg.success){
	    		  var ladderPromotionInfoList = msg.ladderPromotionBOList;
	    		  var len =  ladderPromotionInfoList.length;
	    		  if(len>0){
	    			  var ladderPromotionHtmlStr="";
	    			  for(var index=0;index<len;index++){
	    				  var ladderPromotionInfo = ladderPromotionInfoList[index];
	    				  var skuStr="<tr><td>"+ladderPromotionInfo.id+"</td><td>"+ladderPromotionInfo.ladderId+"</td><td>"+ladderPromotionInfo.promotionId+"</td>";
	    					if(ladderPromotionInfo.yn==true){
	    						skuStr+="<td>有效</td>";
	    					}else{
	    						skuStr+="<td>无效</td>";
	    					}
	    					skuStr+="<td><input type='button' onclick='makeInvalidLadderPromotion("+ladderPromotionInfo.id+")' value='置为无效'/><input type='button' onclick='editLadderPromotion("+ladderPromotionInfo.id+")' value='修改信息'/><input type='button' onclick='delLadderPromotion("+ladderPromotionInfo.id+")' value='删除'/></td></tr>";
	    					ladderPromotionHtmlStr+=skuStr;
	    			  }
	    			  $("#ladderPromotionInfoBody").html(ladderPromotionHtmlStr);
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

function updateLadderPromotion(){

	var ladderPromotionId=$("#ladderPromotionId").val();
	$('#ladderPromotionId').attr("disabled",true);
	var ladderId=$("#ladderId").val();
	var promotionId=$("#promotionId").val();
	var isvaliade=$("input[name='isvaliade']:checked").val();

	if (isNullValue(ladderPromotionId)) {
		$("#tipInfo").text("请输入阶梯促销编号");
		$("#ladderPromotionId").focus();
		return;
	}
	
	if (isNullValue(ladderId)) {
		$("#tipInfo").text("请输入阶梯规则编号");
		$("#ladderId").focus();
		return;
	}
	
	if (isNullValue(promotionId)) {
		$("#tipInfo").text("请输入促销编号");
		$("#promotionId").focus();
		return;
	}
	if (isNullValue(isvaliade)) {
		$("#tipInfo").text("请选择是否有效");
		$("#isvaliade").focus();
		return;
	}
	

	
	var param = "ladderId="+ladderId+"&ladderPromotionId="+ladderPromotionId+"&promotionId="+promotionId+"&isvaliade="+isvaliade+"&r="+(new Date()).getTime();
	$.ajax({
		  url: "/backstage/ladderPromotion/updateLadderPromotion.action",
	      type: "POST",
	      data: param,
	      dataType: "json",
	      async:false,
	      success: function(msg){
	    	  if(msg.success){
	    		  window.location.href = "/backstage/ladderPromotion/queryLadderPromotionByPageNum?r="+(new Date()).getTime();
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
function submitLadderPromotion(){
	var ladderPromotionId=$("#ladderPromotionId").val();
	$('#ladderPromotionId').attr("disabled",true);
	var ladderId=$("#ladderId").val();
	var promotionId=$("#promotionId").val();
	var isvaliade=$("input[name='isvaliade']:checked").val();

	if (!isNullValue(ladderPromotionId)) {
		updateLadderPromotion();
		return;
	}
	
	if (isNullValue(ladderId)) {
		$("#tipInfo").text("请输入阶梯规则编号");
		$("#ladderId").focus();
		return;
	}
	
	if (isNullValue(promotionId)) {
		$("#tipInfo").text("请输入促销编号");
		$("#promotionId").focus();
		return;
	}
	if (isNullValue(isvaliade)) {
		$("#tipInfo").text("请选择是否有效");
		$("#isvaliade").focus();
		return;
	}
	
	var param = "ladderId="+ladderId+"&ladderPromotionId="+ladderPromotionId+"&promotionId="+promotionId+"&isvaliade="+isvaliade+"&r="+(new Date()).getTime();
	$.ajax({
		  url: "/backstage/ladderPromotion/addLadderPromotion.action",
	      type: "POST",
	      data: param,
	      dataType: "json",
	      async:false,
	      success: function(msg){
	    	  if(msg.success){
	    		  window.location.href = "/backstage/ladderPromotion/queryLadderPromotionByPageNum.action?r="+(new Date()).getTime();
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
function cancleLadderPromotion(){
	$("#ladderPromotionPopdiv").attr("class","ladderPromotionPopdivhidden");
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