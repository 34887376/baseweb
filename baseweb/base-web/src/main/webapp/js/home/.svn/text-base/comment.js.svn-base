/*这里的replyToPin表示我要回复的人的pin,commentParent表示我回复的这个人他之前发表的那条评论id,belongTitle表示这些评论属于的主题，commentType表示我的这条回复类型是2(回复的是人)  */
function replyMess(replyToName, replyToPin, commentParent, belongTitle,
		commentType) {
	$("#replyCont").attr("placeholder", "回复" + replyToName + "：");
	$("#replyToPin").attr("value", replyToPin);
	$("#replyParent").attr("value", commentParent);
	$("#replyTitle").attr("value", belongTitle);
	$("#replyType").attr("value", commentType);
	$("#reply-box").show();
	$("#reply-box input").focus();
}
function obtainTopicDetail(topicId) {
	window.location.href = "/topic/queryTopicById.action?topicAbstractInfoVO.topicId="
			+ topicId;
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
				alert("保存成功!");
				window.location.href = "/discuss/showReplyComment.action";
			} else {
				alert("保存失败！！！");
			}

		},
		error : function(reMsg) {
			alert("保存异常！！！");
		}
	});
}