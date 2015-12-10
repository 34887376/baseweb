function registry(){
	var userName=$("#userName").val();
	var nickName=$("#nickName").val();
	var password=$("#pwd").val();
	var repeatePassword=$("#repeatePwd").val();
	var userEmail=$("#userEmail").val();
	var userPhone=$("#phone").val();
	
	if (isNullValue(userName)) {
		$("#tipInfo").text("请输入用户帐号");
		$("#userName").focus();
		return;
	}
	
	if (isNullValue(password)) {
		$("#tipInfo").text("请输入密码");
		$("#pwd").focus();
		return;
	}
	
	if (isNullValue(repeatePassword)) {
		$("#tipInfo").text("请输入确认密码");
		$("#repeatePwd").focus();
		return;
	}
	
	if(password!=repeatePassword){
		$("#tipInfo").text("两次输入的密码不一致");
		$("#repeatePwd").focus();
		return;
	}
	
	if (isNullValue(userEmail)) {
		$("#userEmail").text("请输入注册邮箱");
		$("#userEmail").focus();
		return;
	}
	if(!isEmail(userEmail)){
		$("#userEmail").text("请输入正确的邮箱格式");
		$("#userEmail").focus();
		return;
	}
	
	if (isNullValue(userPhone)) {
		$("#tipInfo").text("请输入注册手机号");
		$("#phone").focus();
		return;
	}
	
	if(!isPhone(userPhone)){
		$("#tipInfo").text("请输入正确的手机号码");
		$("#phone").focus();
		return;
	}
	
	var param = "pin="+userName+"&nickName="+nickName+"&pwd="+password+"&repeatPwd="+repeatePassword+"&email="+userEmail+"&phone="+userPhone+"&r="+(new Date()).getTime();
	$.ajax({
		  url: "/login/registry.action",
	      type: "POST",
	      data: param,
	      dataType: "json",
	      async:false,
	      success: function(msg){
	    	  if(msg.success){
	    		  window.location.href = "/index.action?r="+(new Date()).getTime();
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