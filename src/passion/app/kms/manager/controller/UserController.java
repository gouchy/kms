package passion.app.kms.manager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import passion.app.kms.manager.bean.UserBean;
import passion.app.kms.manager.dao.UserMapper;

/**
 * 用于用户的基本操作
 * @author gouchy
 *
 */
@Controller
public class UserController {
	
	@Autowired
	private UserMapper userMapper;
	
	/**
	 * 注册用户
	 * @param model 数据模型
	 * @param user 注册用户信息
	 * @return 展现页面
	 */
	@RequestMapping(value = "/user/reigster", method= RequestMethod.POST)
	public String registerUser(Model model,
								@RequestBody UserBean user)
	{
		
		// 检查必须的字段是否都已经有了
		
		// 检查提交的用户名是否有重名
		
		// 自动生成token值
		
		// 写入数据库
		
		return "";
	}
	
	/**
	 * 用户登录
	 * @param model 数据模型
	 * @param username 登录用户名
	 * @param password 登录用户密码
	 * @return 展现界面
	 */
	@RequestMapping(value = "/user/login", method = RequestMethod.POST)
	public String loginUser(Model model,
							 @RequestParam("username") String username,
							 @RequestBody String password)
	{
		// 检查必备的参数是否正确
		
		// 查询数据库是否有此用户
		
		// 在Session中增加登录标识
		
		return "";
	}
	
	public String logoutUser(Model model,
							  @RequestParam("username") String username)
	{
		// 检查提交者的身份是否是当前登录者的Session
		
		// 在Session中删除登录标识
		
		return "";
	}
	
}
