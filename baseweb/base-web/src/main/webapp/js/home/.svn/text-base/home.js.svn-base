var imgMaxSize=2* 1024*1024;
function upHeadImg(){
	var obj = document.getElementById("upHeadfile");
	if(obj==null){
		var upFileStr="<form id=\"userInfoForm\" action=\"/home/updateUserInfo.action\" method=\"post\" enctype=\"multipart/form-data\">" +
				"<input id=\"upHeadfile\" name=\"img1\" type=\"file\" style=\"display:none\" onChange=\"preivew(this);\">"+
				" <button id=\"uploadTopicSubmit\" type=\"submit\" style=\"display:none\">Submit</button>";
		$("#headInfo").append(upFileStr);
	}
	$("#upHeadfile").click();
	$("#upHeadfile").change(function (){
		var imgurl=getImgUrl("#upHeadfile");
		if(imgurl==null || imgurl=="null"){
			return;
		}
		console.error(imgurl);
		//var imgItemStr = "<a href=\"#\" id=\"imgshowTd\"><img src=\""+imgurl+"\"></a>";
		$("#head-portrait").attr("src",imgurl);
	});

}

//function uploadHeadFile(){
//	var imgItem = $("#upHeadfile")[0].files[0];
//	//校验图片大小
//    if(imgItem.size>0){
//    if(imgItem.size>2*1024){
//        alert("图片不大于2M。"); 
//        return; 
//     } 
//   } 
//	 document.getElementById('userInfoForm').submit();
//}


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

    	if(checkImgName(imgNames)==false){
    		$(uid).remove();
    		return;
    	}
    	
		var imgurl=getImgUrl("#upHeadfile");
		if(imgurl==null || imgurl=="null"){
	    	alert("图片不大于2M。"); 
			return;
		}
		$("#uploadTopicSubmit").click();
		return;
    }catch(e){ 
        alert(e); 
    } 
}

function getImgUrl(uploadImgId){
	var imgItem = $(uploadImgId)[0].files[0];
	//校验图片大小
    if(imgItem.size>0){ 
    if(imgItem.size>imgMaxSize){
        return null; 
     } 
   } 
	var imgurl = window.URL.createObjectURL(imgItem);
	return imgurl;
}
function goToContent(topicId){
	window.location.href = "/topic/queryTopicById.action?topicAbstractInfoVO.topicId="+ topicId+"&r=" + (new Date()).getTime();
}
function myComment(){
	var parent = $("#myComment").parent().children().attr("class",null);
	$("#myComment").attr("class","curr");
	$("#myTopic_bg").attr("class",null);
	$("#comment_count").hide();
	$.ajax({
		  url: "/discuss/showReplyComment.action", 	
		  global: false, 
		  type:"POST", 
		  dataType: "json", 
		  async:true, 
		  success: function(msg){
			  	$("#my_home").html(""); 
			  	var len = msg.length; 
			  	for(var i=0;i<len;i++){ 
			  		var contentItem = msg[i]; 
			  		var contentInfo="<div class=\"item bdb-1px\">"+ 
			  						"<div class=\"fl\">"+ 
			  						"<img src="+contentItem.imgPathAuthor+">"+
			  						"</div>"+
			  						"<div class=\"fr\">与我感兴趣同款手机<\/div>"+
			  						"<div class=\"des\">"+
			  						"<h3>"+contentItem.nameAuthor+"</h3>"+
			  						"<div class=\"fr\" onclick=\"replyMess('"+contentItem.nameAuthor+"','"+contentItem.commentAuthor+"','"+contentItem.commentID+"','"+contentItem.belongTitle+"',2)\">回复"+
			  						"</div>"+
			  						"<p class=\"time\">"+contentItem.commentDate+"<\/p>";
			  		if(contentItem.commentType==2){
			  			contentInfo+="<p class=\"commet\" style=\"color:green;\">回复我："+contentItem.commentContent+"<\/p>";
			  		}else{
			  			contentInfo+="<p class=\"commet\" style=\"color:green;\">"+contentItem.commentContent+"<\/p>";
			  		}
			  		contentInfo+="<div class=\"reply\" onclick=\"obtainTopicDetail("+contentItem.belongTitle+")\">"+ 
			  						"<span class=\"red\"> 所属话题的摘要为：<\/span>"+contentItem.topicAbstractInfo+
			  						"</div>"+
			  					"</div>"+
			  					"</div>";	
			  		$("#my_home").append(contentInfo);
			  	 } 
	      },
	      error:function(msg){
	    	  alert("网络异常！！！");
	      }
		 });
}

function myTopic(){
	window.location.href = "/home/home.action";
	$("#comment_count").hide();
}
/*这里的replyToPin表示我要回复的人的pin,commentParent表示我回复的这个人他之前发表的那条评论id,belongTitle表示这些评论属于的主题，commentType表示我的这条回复类型是2(回复的是人)  */
function replyMess(replyToName, replyToPin, commentParent, belongTitle,commentType) {
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
				myComment();
				$("#reply-box").hide();
			} else {
				alert("保存失败！！！");
			}

		},
		error : function(reMsg) {
			alert("保存异常！！！");
		}
	});
}