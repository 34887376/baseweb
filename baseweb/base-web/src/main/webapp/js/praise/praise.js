
function praise(topicId,targetPin,obj){
	var praiseId=$(obj).attr("praiseId");
	//alert(" attr praiseId:"+praiseId);
	//return false;
	if(praiseId>0){
		//取消赞
		delPraise(targetPin,topicId,praiseId,obj);
	}
	else{
		//点赞
		addPraise(targetPin,topicId,obj);
	}
	
}
//点赞
function  addPraise(targetPin,topicId,obj){
	var param ="targetPin="+targetPin+"&belongTopicId="+topicId;
//alert("add param:"+param);
	$.ajax({
		  url: "/praise/addPraise.action",
	      type: "POST",
	      data: param,
	      dataType: "json",
	      async:true,
	      success: function(msg){
	    	  if(msg.isSuccess==true){
	    		  changeClass(obj);
	    		  //alert("add praiseId:"+msg.praiseId);
	    		  $(obj).attr("praiseId",msg.praiseId);
	    		 var  praiseId= $(obj).attr("praiseId");
	    		  //alert(" attr set after praiseId:"+praiseId);
	    	  }else{
	    		  if(msg.isValidateSuccess==false){
	    			  
	    			  alert("重复点赞");
	    		  }else{
	    			  alert("点赞异常");
	    		  }
	    	  }
	      },
	      error:function(msg){
	    	 //do noting
	      }
		 });
}
//切换点赞和取消赞样式
 function changeClass(obj){
		var typevalue = $(obj).attr("class");
		if(typevalue=="love-icon_cancel"){
			//取消赞
			$(obj).attr("class","love-icon_add");
			var priaseTimes=$(obj).parent().children("#priaseTimes").html();
			//alert("del priaseTimes:"+priaseTimes);
			priaseTimes--;
			if(priaseTimes<0)
				priaseTimes=0;
			$(obj).next("#priaseTimes").html(priaseTimes);
		}else{
			$(obj).attr("class","love-icon_cancel");
			var priaseTimes=$(obj).parent().children("#priaseTimes").html();
			//alert("add  priaseTimes:"+priaseTimes);
			priaseTimes++;
			$(obj).next("#priaseTimes").html(priaseTimes);
		}
 }
   //取消赞
	function  delPraise(targetPin,topicId,praiseId,obj){
		var param ="targetPin="+targetPin+"&belongTopicId="+topicId+"&praiseId="+praiseId;
		//alert(" del param:"+param);
		$.ajax({
			url: "/praise/delPraise.action",
			type: "POST",
			data: param,
			dataType: "json",
			async:true,
			success: function(msg){
				if(msg.isSuccess==true){
					  $(obj).attr("praiseId",0);
					  changeClass(obj);
				}else{
					if(msg.isValidateSuccess==false){
						alert("重复点赞");
					}else{
						alert("点赞异常");
					}
				}
			},
			error:function(msg){
				//do noting
			}
		});
	
}
