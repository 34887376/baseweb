var imgMaxSize=2* 1024*1024;
function nextStep(){
	
}

function trim(str)
{
	return str.replace(/(^\s*)|(\s*$)/g, "");
} 

function abortPublish(){
	var subjectId = $("#subjectId").attr("value");
	 window.location.href = "/index.action?topicAbstractInfoVO.belongSubjectId="+subjectId+"&r=" + (new Date()).getTime();
}

//改变选中的主题
function   changeSelectedSubject(subjectId,subjectName){
	//alert("subjectId："+subjectId+",  subjectName:"+subjectName);
	//选择当前主题，无效果
	var oldSubjectId = $("#subjectId").val();
	if(subjectId==oldSubjectId){
//		alert("cannot change!");
		return false;
	}
	if(confirm("更换主题后，话题内容将被清空，确定更换么？")){
		var subjectId = $("#subjectId").val(subjectId);
		var topicContent = $("#topicContent").val("");
		$("#publishSubjectName").html(subjectName);
	}
}

function publishTopic(){
	var subjectId = $("#subjectId").attr("value");
	var topicContent = $("#topicContent").val();
	topicContent = trim(topicContent);
	if (topicContent==null || typeof(topicContent)=="undefined" || topicContent=="" ||topicContent.length==0){
		alert("评论内容不能为空！！！");
		return;
	}
	$("#publishButton").attr("onclick","javascript:volid(0);");
	
	$.ajax({
		  url: "/topic/publishTopic.action",
	      type: "POST",
	      data: "topicAbstractInfoVO.belongSubjectId="+subjectId+"&topicDetailInfoVO.content="+topicContent+"&r="+ (new Date()).getTime(),
	      dataType: "json",
	      async:true,
	      success: function(reMsg){
	    	  if(reMsg.success==true){
	    		  window.location.href = "/index.action?topicAbstractInfoVO.belongSubjectId="+subjectId+"&r=" + (new Date()).getTime();
	    	  }else{
	    		 $("#publishButton").attr("onclick","publishTopic();");
	    		 var msg = reMsg.msg;
	    	  	 alert(msg);
	    	  }
	    	 
	      },
	      error:function(reMsg){
	    	  alert("链接服务器异常！！！");
	      }
		 });
	
}

function showMoreSubject(){
	
}

function saveContentDraft(){
	
	var param="";
	var subjectId = $("#subjectId").attr("value");
	subjectId = $("#subjectId").text();
	var topicContent = $("#topicContent").val();
	topicContent = trim(topicContent);
	if (topicContent==null || typeof(topicContent)=="undefined" || topicContent=="" ||topicContent.length==0){
		alert("评论内容不能为空！！！");
		return;
	}
	param+="topicAbstractInfoVO.belongSubjectId="+subjectId+"&topicDetailInfoVO.content="+topicContent;
	
	var ch = $("#upImgFileList").children();
	var imgLen = ch.length;
	for(var imgInd=0; imgInd<imgLen;imgInd++){
		var sequen = imgInd+1;
		var imgParam = "&img"+sequen+"="+ch[imgInd].value;
		param+=imgParam;
	}
	$("#upImgFileList").each(function(index){
		alert(index + "" + $(this).name);
	});
		
	return;
/*	$.ajax({
		  url: "/topic/saveContentDraft.action",
	      global: false,
	      type: "POST",
	      data: "topicAbstractInfoVO.belongSubjectId="+subjectId+"&r="+ (new Date()).getTime(),
	      dataType: "json",
	      async:true,
	      success: function(msg){
	    	  
	    	  $("#topicList").html("");
	    	  var len = msg.length;
	    	  var topicInfoStr="";

	    	 
	      },
	      error:function(msg){
	    	  alert(msg);
	      }
		 });*/
}

function addMark(){
//	var topicContent = $("#topicContent").val();
//	topicContent = trim(topicContent);
//	if (topicContent==null || typeof(topicContent)=="undefined" || topicContent=="" ||topicContent.length==0){
//		alert("评论内容不能为空！！！");
//		return;
//	}
	$("#uploadTopicSubmit").click();
	//window.location.href = "/page/topic/createTopic.html?r=" + (new Date()).getTime();
/*	$("#uploadTopicInfo").submit(function(){
		alert("Submitted");
		return false;

		window.location.href = "/WEB-INF/page/topic/createTopic.html?r=" + (new Date()).getTime();
	});*/
}

function showPhoto(){
	
	$("#touchPhotoTool").attr("class","");
	$("#touchPhotoTool").attr("style","display:none");
	$("#imgUploadTool").attr("style","");
	$("#imgUploadTool").attr("class","pop-wrap");
}

function delPhoto(){
	$("#imgUploadTool").attr("class","");
	$("#imgUploadTool").attr("style","display:none");
	$("#touchPhotoTool").attr("style","");
	$("#touchPhotoTool").attr("class","add-pic");
	
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

function getImgUrl(uploadImgId){
	var strSrc = $("#showImg").val();
	var f = document.getElementById('upfile1').files[0];
	var imgItem = $(uploadImgId)[0].files[0];
	//校验图片大小
    if(imgItem.size>0){ 
    if(imgItem.size>imgMaxSize){
        alert("图片不大于2M。"); 
        return null; 
     } 
   } 
	var imgurl = window.URL.createObjectURL(imgItem);
	return imgurl;
}

function addImg(){
	var sumTd = $("#upImgFileList").find("input").length;
	var fileId = sumTd+1;
	var upfileObjId="#upfile"+sumTd;
	$(upfileObjId).click();
	$(upfileObjId).change(function (){
		var imgurl=getImgUrl(upfileObjId);
		if(imgurl==null || imgurl=="null"){
			return;
		}
		console.error(imgurl);
		$("#upImgFileList").append("<input id=upfile"+fileId+" name=\"img"+fileId+"\" type=\"file\" onChange=\"preivew(this);\">");
		var imgItemStr = "<a href=\"#\" id=\"imgshowTd"+sumTd+"\"><img src=\""+imgurl+"\"></a>";
		$("#imgList").append(imgItemStr);
	});
}

/**
 * 上传图片
 */
function uploadPhoto(){
	addImg();
}


























