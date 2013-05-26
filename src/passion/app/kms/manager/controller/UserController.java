package passion.app.kms.manager.controller;

import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import passion.app.kms.manager.bean.ResultBean;
import passion.app.kms.manager.bean.UserBean;
import passion.app.kms.manager.constant.ErrorCode;
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
	 * @return 返回的结果 PARA_IS_NULL, USER_EXIST, DB_FAIL, OK
	 */
	@RequestMapping(value = "/user", method= RequestMethod.PUT, consumes = {"application/json"}, produces = {"application/json"})
	public @ResponseBody ResultBean registerUser(@RequestBody UserBean user)
	{
		ResultBean result = new ResultBean(ErrorCode.OK);
		// 检查必须的字段是否都已经有了
		if(user.getUsername() == null || user.getUsername().equals(""))
		{
			result.setRetcode(ErrorCode.PARA_IS_ERROR);
			return result;
		}
		
		if(user.getPassword() == null || user.getPassword().equals(""))
		{
			result.setRetcode(ErrorCode.PARA_IS_ERROR);
			return result;
		}
				
		// 检查提交的用户名是否有重名
		UserBean checkUser = userMapper.readUserByUsername(user.getUsername());
		if(checkUser != null)
		{
			result.setRetcode(ErrorCode.USER_EXIST);
			return result;
		}
		
		// 自动生成token及部分值
		user.setUserrole(0);
		user.setUsertype(0);
		user.setToken(UUID.randomUUID().toString());
		
		// 写入数据库
		if(userMapper.createUser(user) == 0)
		{
			result.setRetcode(ErrorCode.DB_FAIL);
		}
		
		return result;
	}
	
	/**
	 * 用户登录
	 * @param model 数据模型
	 * @param session Session对象
	 * @param username 登录用户名
	 * @param password 登录用户密码
	 * @return 返回结果 PARA_IS_NULL, USER_NOT_EXIST, OK
	 */
	@RequestMapping(value = "/user", method = RequestMethod.POST, produces = {"application/json"})
	public @ResponseBody ResultBean loginUser(Model model,
							 HttpSession session,
							 @RequestParam("username") String username,
							 @RequestParam("password") String password)
	{
		ResultBean result = new ResultBean(ErrorCode.OK);
		// 检查必备的参数是否正确
		if(username == null || username.equals(""))
		{
			result.setRetcode(ErrorCode.PARA_IS_ERROR);
			return result;
		}
		
		if(password == null || password.equals(""))
		{
			result.setRetcode(ErrorCode.PARA_IS_ERROR);
			return result;
		}
		
		// 查询数据库是否有此用户
		UserBean checkUser = userMapper.readUserByUsernameAndPassword(username, password);
		if(checkUser == null)
		{
			result.setRetcode(ErrorCode.USER_NOT_EXIST);
			return result;
		}
		
		// 在Session中增加登录标识
		session.setAttribute("userId", checkUser.getId());
		session.setAttribute("username", checkUser.getUsername());
		
		return result;
	}
	
	/**
	 * 用户登出
	 * @param model 数据模型
	 * @param session session对象
	 * @param username 用户名
	 * @return 返回结果 USER_NOT_ONLINE, OK
	 */
	@RequestMapping(value = "/user", method = RequestMethod.DELETE, produces = {"application/json"})
	public @ResponseBody ResultBean logoutUser(Model model,
							  HttpSession session,
							  @RequestParam("username") String username)
	{
		ResultBean result = new ResultBean(ErrorCode.OK);
		// 检查提交者的身份是否是当前登录者的Session
		if(!session.getAttribute("username").equals(username))
		{
			result.setRetcode(ErrorCode.USER_NOT_ONLINE);
			return result;
		}
		
		// 在Session中删除登录标识
		session.removeAttribute("userId");
		session.removeAttribute("username");
		
		return result;
	}
	
}
