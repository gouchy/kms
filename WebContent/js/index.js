 // 页面完成加载初始化
 $(document).ready(function () {
	// 布局设置
    $('body').layout({applyDefaultStyles: false,
    	resizable: false,
    	resizerClass: "resizer"});
 });
 
 /**
  * 登录操作
  */
 $(function() {
	$("#signin").button().click(function(event) {
		var username = $("#username").val();
		var password = $("#password").val();
		
		// 检查有效性
		if(username.length == 0)
		{
			jAlert("请输入用户名", "提醒");
			return;
		}
		if(password.length == 0)
		{
			jAlert("请输入密码", "提醒");
			return;
		}
		
		// 密码加密
		password = hex_md5(password);
		

		var result = UserController.loginUser({
			"username": username,
			"password": password
		});
		
		switch(result.retcode)
		{
		case RET_OK:
			window.location.href = "manager/frame.html";
			//jAlert("登录成功。", "提醒");
			break;
		case RET_PARA_IS_ERROR:
			$("#password").val("");
			jAlert("登录参数错误，请重试。", "提醒");
			break;
		case RET_USER_NOT_EXIST:
			$("#password").val("");
			jAlert("用户名或者密码错误，请重新尝试。", "提醒");
			break;
		default:
			$("#password").val("");
			jAlert("服务器发生故障，请稍后再试。", "错误");
			break;
		}

	});
 });
 
 $(function() {
	$("#register").button().click(function(event) {
			
	});
 });

 