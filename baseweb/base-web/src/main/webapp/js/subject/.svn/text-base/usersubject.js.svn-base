	
		
		
		function saveOrder() {
			var data = $("#listSel input");
			var result="";
			for(var i=0;i<data.length;i++){
				result += data[i].value + "|";
			}
			$("input[name=list1SortOrder]").val(result);
			
			var data2 = $("#listNoSel input");
			var result2="";
			for(var t=0;t<data2.length;t++){
				result2 += data2[t].value + "|";
			}
			$("input[name=list2SortOrder]").val(result2);
		}
		
		function show(){
			saveOrder();
			alert($("input[name=list1SortOrder]").val()+"\n"+$("input[name=list2SortOrder]").val());
			
			
		}
		
		
		function fun(a)
		{	
			if(a.parent().attr('id')=="listSel")
			{	
				a.find('i.edit-remove').attr('class','edit-add');
				a.appendTo("#listNoSel");		
			}
			else
			{	
				a.find('i.edit-add').attr('class','edit-remove');
				a.appendTo("#listSel");		
			}
			//a.css('background-color', 'gray' );//在ie浏览器中要加这个，否则移动后还是鼠标停留的颜色
		}



		$(document).ready(function(){
			$("#listSel > li").click(function(){
				fun($(this));

			});
			$("#listNoSel > li").click(function(){
				fun($(this));
			});
		});
		
		
		
	function editUserSubject(){
		saveOrder();
    	var paramReply = "userThemeParam.list1SortOrder=";
    	var sel = $("#selThemeList").val();
    	var notSel = $("#selNotThemeList").val();
    	var pin = $("#pinTheme").attr("value");
    	paramReply = paramReply + sel + "&userThemeParam.list2SortOrder=" + notSel + "&userThemeParam.pin=" + pin;
    	
    	
		
		$.ajax({
		  url: "/subject/editUserSubjectRelation.action",
	      type: "POST",
	      data: paramReply,
	      dataType: "json",
	      async:true,
	      success: function(reMsg){
	    	  if(reMsg.success==true){
	    		 alert("保存成功!");
	    	  }else{
	    	  	 var msg = reMsg.msg;
	    	  	 alert(msg);
	    	  }
	    	 
	      },
	      error:function(reMsg){
	      		alert("操作异常！！！");
	      }
		 });
    }