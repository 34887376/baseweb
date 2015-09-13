function check(form) {

	var name = form.userName.value;
	var password = form.pwd.value;
	
	if (isNullValue(name)) {
		$("#tipInfo").val("请输入用户帐号");
		form.userName.focus();
		return false;
	}
	if (isNullValue(password)) {
		$("#tipInfo").val("请输入登录密码");
		form.pwd.focus();
		return false;
	}
	return true;
}

function login(){
	var userName=$("#userName").val();
	var password=$("#pwd").val();
	if (isNullValue(userName)) {
		$("#tipInfo").text("请输入用户帐号");
		$("#userName").focus();
		return;
	}
	
	if (isNullValue(password)) {
		$("#tipInfo").text("请输入登录密码");
		$("#pwd").focus();
		return;
	}
	var aimURI=location.search.substring(1);
	var param = "userName="+userName+"&pwd="+password+"&r="+(new Date()).getTime()+"&"+aimURI;
	$.ajax({
		  url: "/login/login.action",
	      global: false,
	      type: "POST",
	      data: param,
	      dataType: "json",
	      async:true,
	      success: function(msg){
	    	  if(msg.success){
	    		  window.location.href = msg.aimUrl;
	    	  }else{
	    		  $("#tipInfo").text(msg.msg);
	    	  }
	    	  alert("sss"+msg);
	      },
	      error:function(msg){
	    	  alert(msg);
	      }
		 });
	
}