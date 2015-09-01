/**
 * 根据主题id加载话题
 * @param subjectId
 */
function loadTopic(subjectId){
	if(subjectId==null){
		var selectedSubjectId = $("#subjectList").children(".curr").attr("id");
		subjectId = selectedSubjectId.split("_")[1];
	}
	$.ajax({
		  url: "/topic/queryTopicBySubject.action",
	      global: false,
	      type: "POST",
	      data: "topicAbstractInfoVO.belongSubjectId="+subjectId+"&r="+ (new Date()).getTime(),
	      dataType: "json",
	      async:true,
	      success: function(msg){
	    	  
	    	  $("#topicList").html("");
	    	  var len = msg.length;
	    	  if(len==0){
	    		  $("#topicList").append("该话题下还没有任何人来过，留下你的脚印，争当第一个哥伦布吧！！！");
	    		  return;
	    	  }
	    	  var topicInfoStr="";
	    	  
	    	  for(var i=0;i<len;i++){
	    		  var topic = msg[i];
	    		  var topicInfo="<div class=\"item bdb-1px\">"+
					                "<div class=\"fl\">"+
					                    "<img src=\""+topic.headImgUrl+"\">"+
					                "</div>"+
					                "<div class=\"fr\" id=\"relationCategoryShow\">"+topic.relationCategoryShow+"</div>"+
					                "<div class=\"des\">"+
					                    "<h3 id=\"niceName\">"+topic.niceName+"</h3>"+
					                    "<p class=\"time\" id=\"timeDetailShow\">"+topic.timeDetailShow+"</p>"+
					                    "<p class=\"commet\" onclick=\"goToContent("+topic.topicId+")\">"+topic.contentAbstract+"</p><ul class=\"gallery\">";
	    		  var imgListInfo = topic.imgUrlList;
	    		  var hasImg = !(imgListInfo==null || typeof(imgListInfo)=="undefined" || imgListInfo=="undefined" || imgListInfo==undefined);
    			  if (hasImg){
    	    		  var imgNum = imgListInfo.length;
    	    		  if(imgNum>0){
    	    			  //topicInfo+="<div class=\"gallery\">";
    		    		  for(var mgnum=0;mgnum<imgNum;mgnum++){
    		    			  var mgItem = imgListInfo[mgnum];
    		    			  topicInfo+="<div class=\"pics\"><a href="+mgItem+"><img src="+mgItem+"></img></a></div>";
    		    		  }
    	    			  //topicInfo+="</div>";
    	    		  }
    			  }
	    		  var imgMarkInfo = topic.markInfoList;
	    		  var markNum = imgMarkInfo.length;
	    		  topicInfo+="</ul><div class=\"tags\">";
	    		  if(markNum>0){
		    		  for(var mknum=0;mknum<markNum;mknum++){
		    			  var mkItem = imgMarkInfo[mknum];
		    			  topicInfo+="<a href=\"javascript:void(0)\" onclick=\"goToTopicList("+mkItem.markId+")\"><img src=\""+mkItem.markImgUrl+"\"></a>";
		    		  }
	    		  }
	    		  topicInfo+= "<div class=\"fr\">"+
					                            "<div class=\"reply-count\">"+
					                                "<i class=\"reply-icon\"></i>"+topic.commentTimes+
					                            "</div>"+
					                            "<div class=\"love\">"+
					                                "<i class=\"love-icon\"></i>"+topic.priaseTimes+
					                            "</div>"+
					                        "</div>"+
					                    "</div>"+
					                "</div>"+
					            "</div>";
	    		  $("#topicList").append(topicInfo);
	    	  }
	    	  $.fn.enlargeShowImage();
	      },
	      error:function(msg){
	    	  alert("网络异常！！！");
	      }
		 });
	
}

/**
 * 进入话题编辑页面
 */
function goToEdit(){
	var selectedSubjectId = $("#subjectList").children(".curr").attr("id");
	var subjectId = selectedSubjectId.split("_")[1];
	window.location.href = "/topic/publishTopicStepFirst.action?topicAbstractInfoVO.belongSubjectId="+ subjectId+"&r=" + (new Date()).getTime();
}

/**
 * 展示更多主题
 */
function showMoreSubject(){
	
	$.ajax({
		  url: "/subject/obtainMoreSubject.action",
	      global: false,
	      type: "POST",
	      data: null,
	      dataType: "json",
	      async:true,
	      success: function(msg){
	    	  var len = msg.length;
	    	  var subjectParentEle=$("#subjectList");
	    	  for(var i=0;i<len;i++){
	    		  var id=msg[i].id;
	    		  var name=msg[i].name;
	    		  var subjectAppend = "<li id=\"subjectId_"+id+"\" onclick=\"selectSubject(this);\"><label>"+name+"</label></li>";
	    		  alert(subjectAppend);
	    		  subjectParentEle.append(subjectAppend);
	    	  }
	      },
	      error:function(msg){
	    	  alert(msg);
	      }
		 });
}

/**
 * 选中某一个主题后的样式变化
 * @param obj
 */
function selectSubject(obj){
	var parent = $(obj).parent().children().attr("class",null);
	$(obj).attr("class","curr");
	var selectedSubjectId = $("#subjectList").children(".curr").attr("id");
	var subjectId = selectedSubjectId.split("_")[1];
	loadTopic(subjectId);
}

function goToContent(topicId){
	window.location.href = "/topic/queryTopicById.action?topicAbstractInfoVO.topicId="+ topicId+"&r=" + (new Date()).getTime();
}

function showSku(skuId){
	window.location.href = "http://item.jd.com/"+ skuId+".html";
}

function shareEQcode(){
	window.location.href = "/share/shareEQ.action?r=" + (new Date()).getTime();
}
function goToTopicList(markId){
	window.location.href = "/topic/queryTopicBySkuId.action?markId="+ markId+"&r=" + (new Date()).getTime();
}