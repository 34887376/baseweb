function delComment(commentID,topicId){
	var parm ="delCommentID="+commentID;
	$.ajax({
		  url: "/topic/delComment.action",
	      type: "POST",
	      data: parm,
	      dataType: "json",
	      async:true,
	      success: function(reMsg){
	    	  if(reMsg.success==true){
	    		  alert("删除评论成功！");
	    		  obtainTopicDetail(topicId);
	    	  }else{
	    		  alert("操作越权！！！");
	    	  }
	    	 
	      },
	      error:function(msg){
	    	  alert("评论操作异常！！！");
	      }
	});
}
function addComment(topicId){

	var parm ="replyParam.belongTitle=";
	var comment = $("#conComment").val();
	parm = parm + topicId + "&replyParam.commentContent=" + comment;
	
	$.ajax({
		  url: "/topic/addComment.action",
	      type: "POST",
	      data: parm,
	      dataType: "json",
	      async:true,
	      success: function(reMsg){
	    	  if(reMsg.success==true){
	    		  alert("评论保存成功！！！");
	    		  obtainTopicDetail(topicId);
	    	  }else{
	    		  alert("评论保存异常！！！");
	    	  }
	    	 
	      },
	      error:function(msg){
	    	  alert("评论操作异常！！！");
	      }
	});
}
/*这里的replyToPin表示我要回复的人的pin,commentParent表示我回复的这个人他之前发表的那条评论id,belongTitle表示这些评论属于的主题，commentType表示我的这条回复类型是2(回复的是人)  */
function replyMess(replyToName, replyToPin, commentParent, belongTitle,
		commentType) {
	$("#replyCont").attr("placeholder", "回复" + replyToName + "：");
	$("#replyToPin").attr("value", replyToPin);
	$("#replyParent").attr("value", commentParent);
	$("#replyTitle").attr("value", belongTitle);
	$("#replyType").attr("value", commentType);
	$("#reply-box1").hide()
	$("#reply-box").show();
	$("#reply-box input").focus();

}
function obtainTopicDetail(topicId) {
	param = "topicAbstractInfoVO.topicId="+ topicId ;
	$.ajax({
		url : "/topic/queryTopicByIdAjax.action",
		type : "POST",
		data : param,
		dataType : "json",
		async : true,
		success : function(msg) {
		  	$("#show_reply").html(""); 
			$("#replyCount").html(""); 
		  	var len = msg.length;
	    	  if(len==0){
	    		  return;
	    	  }
	      var contentCountInfo="<div class=\"cos-b-txt\">"+
	      "<img src=\"../../../css/jd/skin/i/reply-icon.png\" id=\"topicComment\" onclick=\"showReply()\" width=\"14\" heigth=\"12\">"+len+"</div>";
	      $("#replyCount").append(contentCountInfo);
		  	for(var i=0;i<len;i++){ 
		  		var contentItem = msg[i]; 
		  		var contentInfo="<div class=\"item bdb-1px\">"+ 
		  						"<div class=\"fl\">"+ 
		  							"<img src="+contentItem.imgPathAuthor+">"+
		  						"</div>"+
		  						"<div class=\"des\">" +
		  							"<h3>"+contentItem.nameAuthor+"</h3>"+
		  							"<div class=\"fr\" onclick=\"replyMess('"+contentItem.nameAuthor+"','"+contentItem.commentAuthor+"','"+contentItem.commentID+"','"+contentItem.belongTitle+"',2)\">回复"+
		  							"</div>"+
		  							"<div class=\"fr\"> &nbsp;&nbsp;"+
		  							"</div>"+
		  							"<div class=\"fr\" onclick=\"delComment('"+contentItem.commentID+"','"+contentItem.belongTitle+"')\">删除"+
		  							"</div>"+
		  							"<p class=\"time\">"+contentItem.commentDate+"<\/p>";
		  		if((contentItem.replyToPin)&&(contentItem.commentType==2)){
		  			contentInfo+="<p class=\"commet\" style=\"color:green;\">回复"+contentItem.replyToName+"："+contentItem.commentContent+"<\/p>";
		  		}else{
		  			contentInfo+="<p class=\"commet\" style=\"color:green;\">"+contentItem.commentContent+"<\/p>";
		  		} 							
		  							"</div>"+
		  						"</div>"+
		  					"</div>";	
		  		$("#show_reply").append(contentInfo);
		  	 }
	    	 
      },
		error : function(msg) {
			alert("网络异常！！！");
		}
	});
	$("#reply-box").hide()
	$("#reply-box1").hide();
/*	window.location.href = "/topic/queryTopicById.action?topicAbstractInfoVO.topicId="
			+ topicId;*/
}
function submitReply() {
	var replyParam = "replyParam.commentAuthor=";
	var commentAuthor = $("#commentAuthor").val();
	var replyToPin = $("#replyToPin").val();
	var commentParent = $("#replyParent").val();
	var belongTitle = $("#replyTitle").val();
	var commentType = $("#replyType").val();
	var commentContent = $("#replyCont").val();
	replyParam = replyParam + commentAuthor + "&replyParam.replyToPin="
			+ replyToPin + "&replyParam.commentParent=" + commentParent
			+ "&replyParam.belongTitle=" + belongTitle
			+ "&replyParam.commentType=" + commentType
			+ "&replyParam.commentContent=" + commentContent;
	$.ajax({
		url : "/discuss/addReplyComment.action",
		type : "POST",
		data : replyParam,
		dataType : "json",
		async : true,
		success : function(reMsg) {
			if (reMsg.success == true) {
				alert("保存成功！！！");
				obtainTopicDetail(belongTitle);
			} else {
				alert("保存失败！！！");
			}

		},
		error : function(reMsg) {
			alert("保存异常！！！");
		}
	});
	$("#reply-box").hide()
	$("#reply-box1").show();
	$("#reply-box1 input").focus();
}
function showReply(){
    if($("#show_reply").is(":hidden")){
    	$("#show_reply").show();
    	$("#reply-box1").show();
        $("#reply-box1 input").focus();
    }else{
    	$("#reply-box1").show();
/*    	$("#show_reply").hide();*/
/*    	$("#reply-box1").hide();*/
    }
    if($("#reply-box").is(":visible")){
    	$("#reply-box").hide();
    }
}