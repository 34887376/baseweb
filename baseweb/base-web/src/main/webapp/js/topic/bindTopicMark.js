/**
 * 选中某一个主题后的样式变化
 * @param obj
 */
function selectSku(obj){
	if($(obj).hasClass("binded")){
		$(obj).attr("class",null);
	}else{
		$(obj).attr("class","binded");
	}
}

function bindMarkSubmit(){
	var subjectId=$("#subjectId").val();
	var selectedSubjectId = $("#skuList").children(".binded");
	var len = selectedSubjectId.length;
	if(len==0 || len>5){
		alert("请勾选至少一件商品，至多五件商品");
		return;
	}
	encodeURIComponent();
	var test="topicAbstractInfoVO.belongSubjectId="+subjectId;
	for (var index=0;index<len;index++){
		var tempParm="&topicAbstractInfoVO.markIdList="+selectedSubjectId[index].id;
		test+=tempParm;
	}
	test+="&r="+ (new Date()).getTime();
	
	$.ajax({
		  url: "/topic/addMarkDraft.action",
	      type: "POST",
	      data: test,
	      dataType: "json",
	      async:true,
	      success: function(msg){
	    	  if(msg=="saveMarkSuccess"){
	    		  window.location.href = "/topic/publishTopicStepFirst.action?topicAbstractInfoVO.belongSubjectId="+ subjectId+"&r=" + (new Date()).getTime();
	    	  }else{
	    	  	 alert(msg);
	    	  }
	    	 
	      },
	      error:function(msg){
	    	  alert("勾选异常！！！");
	      }
		 });

}

function dropBindMark(){
	var subjectId=$("#subjectId").val();
	window.location.href = "/topic/publishTopicStepFirst.action?topicAbstractInfoVO.belongSubjectId="+ subjectId+"&r=" + (new Date()).getTime();
}






























