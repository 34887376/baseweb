/**
 * 添加商品信息
 */
function addPromotionSequence(){
	$("#promotionSequencePopdiv").attr("class","promotionSequencePopdivshow");
}

/**
 * 编辑商品信息
 * @param skuId
 */
function editPromotionSequence(querypromotionSequenceId){
	$("#promotionSequencePopdiv").attr("class","promotionSequencePopdivshow");
	
	var param = "promotionSequenceId="+querypromotionSequenceId+"&r="+(new Date()).getTime();
	$.ajax({
		  url: "/backstage/promotionSeq/queryPromotionSequenceByCondition.action",
	      type: "POST",
	      data: param,
	      dataType: "json",
	      async:false,
	      success: function(msg){
	    	  if(msg.success){
	    		  var promotionSequenceList = msg.promotionSequenceBOList;
	    		  var len =  promotionSequenceList.length;
	    		  if(len>0){
    			  	var promotionSequenceInfo = promotionSequenceList[0];
					$("#promotionSequenceId").val(promotionSequenceInfo.promotionSequenceId);
					$("#promotionId").val(promotionSequenceInfo.promotionId);
					$("#previosOrder").val(promotionSequenceInfo.previosOrder);
					$("#nextOrder").val(promotionSequenceInfo.nextOrder);
					$("#startTime").val(promotionSequenceInfo.startTime);
					$("#endTime").val(promotionSequenceInfo.endTime);
					$("#hasLoad").val(promotionSequenceInfo.hasLoad);
					if(promotionSequenceInfo.yn==true){
						$("input[name='isvaliade'][value=1]").attr("checked",true);
					}else{
						$("input[name='isvaliade'][value=0]").attr("checked",true);
					}
	    		  }
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
 * 将商品信息置为无效
 * @param skuId
 */
function makeInvalidePromotionSeq(promotionSequenceId){
	var param = "promotionSequenceId="+promotionSequenceId+"&r="+(new Date()).getTime();
	$.ajax({
		  url: "/backstage/promotionSeq/makeInvalidPromotionSequence.action",
	      type: "POST",
	      data: param,
	      dataType: "json",
	      async:false,
	      success: function(msg){
	    	  if(msg.success){
	    		  window.location.href = "/backstage/promotionSeq/queryPromotionSequenceByPageNum.action?r="+(new Date()).getTime();
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

function delPromotionSequence(promotionSequenceId){
	var param = "promotionSequenceId="+promotionSequenceId+"&r="+(new Date()).getTime();
	$.ajax({
		  url: "/backstage/promotionSeq/physicalDel.action",
	      type: "POST",
	      data: param,
	      dataType: "json",
	      async:false,
	      success: function(msg){
	    	  if(msg.success){
	    		  window.location.href = "/backstage/promotionSeq/queryPromotionSequenceByPageNum.action?r="+(new Date()).getTime();
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
 * 关闭商品信息弹出框
 */
function closePromotionSequencePopDiv(){
	$("#promotionSequencePopdiv").attr("class","promotionSequencePopdivhidden");
}

/**
 * 查询商品信息
 */
function queryPromotionSequenceInfo(){
	var queryPromotionSequenceId=$("#queryPromotionSequenceId").val();
	var queryPromotionId=$("#queryPromotionId").val();
	
	if (isNullValue(queryPromotionSequenceId) && isNullValue(queryPromotionId)) {
		$("#tipInfo").text("请输入促销序列编号或者促销编号");
		$("#queryPromotionSequenceId").focus();
		return;
	}
	
	var param = "promotionSequenceId="+queryPromotionSequenceId+"&promotionId="+queryPromotionId+"&r="+(new Date()).getTime();
	$.ajax({
		  url: "/backstage/promotionSeq/queryPromotionSequenceByCondition.action",
	      type: "POST",
	      data: param,
	      dataType: "json",
	      async:false,
	      success: function(msg){
	    	  if(msg.success){
	    		  var promotionSequenceList = msg.promotionSequenceBOList;
	    		  var len =  promotionSequenceList.length;
	    		  if(len>0){
	    			  var promotionSequenceHtmlStr="";
	    			  for(var index=0;index<len;index++){
	    				  var promotionSequenceInfo = promotionSequenceList[index];
	    				  var skuStr="<tr><td>"+promotionSequenceInfo.id+"</td><td>"+promotionSequenceInfo.promotionId+"</td><td>"+promotionSequenceInfo.previosOrder+"</td><td>"+promotionSequenceInfo.nextOrder+"</td><td>"+skuInfo.startTime+"</td><td>"+skuInfo.endTime+"</td>";
	    					if(promotionSequenceInfo.hasLoad==1){
	    						skuStr+= "<td>已加载</td>";
	    					}else{
	    						skuStr+= "<td>未加载</td>";
	    					}
	    					
	    					if(promotionSequenceInfo.yn==true){
	    						skuStr+="<td>有效</td>";
	    					}else{
	    						skuStr+="<td>无效</td>";
	    					}
	    					
	    					skuStr+="<td><input type='button' onclick='makeInvalidePromotionSeq("+promotionSequenceInfo.id+")' value='置为无效'><input type='button' onclick='editPromotionSequence("+promotionSequenceInfo.id+")' value='修改信息'><input type='button' onclick='delPromotionSequence("+promotionSequenceInfo.id+")' value='删除'/></td></td></tr>";
	    					promotionSequenceHtmlStr+=skuStr;
	    			  }
	    			  $("#promotionSequenceInfoBody").html(promotionSequenceHtmlStr);
	    		  }
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

function updatePromotionSequence(){
	
	var promotionSequenceId=$("#promotionSequenceId").val();
	$('#promotionSequenceId').attr("disabled",true);
	var promotionId=$("#promotionId").val();
	var previosOrder=$("#previosOrder").val();
	var nextOrder=$("#nextOrder").val();
	var startTime=$("#startTime").val();
	var endTime=$("#endTime").val();
	var hasLoad=$("#hasLoad").val();
	var isvaliade=$("input[name='isvaliade']:checked").val();

	if (isNullValue(promotionSequenceId)) {
		$("#tipInfo").text("请输入促销序列编号");
		$("#promotionSequenceId").focus();
		return;
	}
	
	if (isNullValue(promotionId)) {
		$("#tipInfo").text("请输入促销编号");
		$("#promotionId").focus();
		return;
	}
	
	if (isNullValue(previosOrder)) {
		$("#tipInfo").text("请输入上一个促销序列编号");
		$("#previosOrder").focus();
		return;
	}
	
	
	if (isNullValue(nextOrder)) {
		$("#tipInfo").text("请输入下一个促销序列编号");
		$("#nextOrder").focus();
		return;
	}
	
	if (isNullValue(startTime)) {
		$("#tipInfo").text("请输入开始时间");
		$("#startTime").focus();
		return;
	}
//	
//	if(!isPhone(userPhone)){
//		$("#tipInfo").text("请输入正确的手机号码");
//		$("#phone").focus();
//		return;
//	}
	
	if (isNullValue(endTime)) {
		$("#tipInfo").text("请输入出结束时间");
		$("#endTime").focus();
		return;
	}
	
	if (isNullValue(hasLoad)) {
		$("#tipInfo").text("请输入是否加载");
		$("#hasLoad").focus();
		return;
	}
	
	if (isNullValue(isvaliade)) {
		$("#tipInfo").text("请选择是否有效");
		$("#isvaliade").focus();
		return;
	}
	

	
	var param = "promotionSequenceId="+promotionSequenceId+"&promotionId="+promotionId+"&previosOrder="+previosOrder+"&nextOrder="+nextOrder+"&startTime="+startTime+"&endTime="+endTime+"&hasLoad="+hasLoad+"&isvaliade="+isvaliade+"&r="+(new Date()).getTime();
	$.ajax({
		  url: "/backstage/promotionSeq/updatePromotionSequence.action",
	      type: "POST",
	      data: param,
	      dataType: "json",
	      async:false,
	      success: function(msg){
	    	  if(msg.success){
	    		  window.location.href = "/backstage/promotionSeq/queryPromotionSequenceByPageNum.action?r="+(new Date()).getTime();
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
function submitPromotionSequence(){
	
	var promotionSequenceId=$("#promotionSequenceId").val();
	$('#promotionSequenceId').attr("disabled",true);
	var promotionId=$("#promotionId").val();
	var previosOrder=$("#previosOrder").val();
	var nextOrder=$("#nextOrder").val();
	var startTime=$("#startTime").val();
	var endTime=$("#endTime").val();
	var hasLoad=$("#hasLoad").val();
	var isvaliade=$("input[name='isvaliade']:checked").val();

	if (!isNullValue(promotionSequenceId)) {
		updatePromotionSequence();
		return;
	}
	
	if (isNullValue(promotionId)) {
		$("#tipInfo").text("请输入促销编号");
		$("#promotionId").focus();
		return;
	}
	
	if (isNullValue(previosOrder)) {
		$("#tipInfo").text("请输入上一个促销序列编号");
		$("#previosOrder").focus();
		return;
	}
	
	
	if (isNullValue(nextOrder)) {
		$("#tipInfo").text("请输入下一个促销序列编号");
		$("#nextOrder").focus();
		return;
	}
	
	if (isNullValue(startTime)) {
		$("#tipInfo").text("请输入开始时间");
		$("#startTime").focus();
		return;
	}
//	
//	if(!isPhone(userPhone)){
//		$("#tipInfo").text("请输入正确的手机号码");
//		$("#phone").focus();
//		return;
//	}
	
	if (isNullValue(endTime)) {
		$("#tipInfo").text("请输入出结束时间");
		$("#endTime").focus();
		return;
	}
	
	if (isNullValue(hasLoad)) {
		$("#tipInfo").text("请输入是否加载");
		$("#hasLoad").focus();
		return;
	}
	
	if (isNullValue(isvaliade)) {
		$("#tipInfo").text("请选择是否有效");
		$("#isvaliade").focus();
		return;
	}
	

	
	var param = "promotionSequenceId="+promotionSequenceId+"&promotionId="+promotionId+"&previosOrder="+previosOrder+"&nextOrder="+nextOrder+"&startTime="+startTime+"&endTime="+endTime+"&hasLoad="+hasLoad+"&isvaliade="+isvaliade+"&r="+(new Date()).getTime();
	
	$.ajax({
		  url: "/backstage/promotionSeq/addPromotionSequence.action",
	      type: "POST",
	      data: param,
	      dataType: "json",
	      async:false,
	      success: function(msg){
	    	  if(msg.success){
	    		  window.location.href = "/backstage/promotionSeq/queryPromotionSequenceByPageNum.action?r="+(new Date()).getTime();
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
function canclePromotionSequence(){
	$("#promotionSequencePopdiv").attr("class","promotionSequencePopdivhidden");
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