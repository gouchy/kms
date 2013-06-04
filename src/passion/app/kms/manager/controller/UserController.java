package passion.app.kms.manager.controller;

import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.annotations.Form;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import passion.app.kms.manager.bean.ResultBean;
import passion.app.kms.manager.bean.UserBean;
import passion.app.kms.manager.constant.ErrorCode;
import passion.app.kms.manager.dao.UserMapper;
import passion.app.kms.manager.data.UserData;

/**
 * 用于用户的基本操作
 * @author gouchy
 *
 */
@Controller
@Path("/user")
public class UserController {
	
	@Autowired
	private UserMapper userMapper;
	
	/**
	 * 注册用户
	 * @param model 数据模型
	 * @param user 注册用户信息
	 * @return 返回的结果 PARA_IS_NULL, USER_EXIST, DB_FAIL, OK
	 */
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public ResultBean registerUser(@Form UserBean user)
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
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public ResultBean loginUser(@QueryParam("username") String username,
							     @QueryParam("password") String password,
							     @Context HttpServletResponse response)
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
		
		// 用户名做小写转换
		username = username.toLowerCase();
		
		// 查询数据库是否有此用户
		UserBean checkUser = userMapper.readUserByUsernameAndPassword(username, password);
		if(checkUser == null)
		{
			result.setRetcode(ErrorCode.USER_NOT_EXIST);
			return result;
		}
		
		// 在Session中增加登录标识
		Cookie tokenCookie = new Cookie("token", UserData.getToken(username));
		tokenCookie.setPath("/");
		tokenCookie.setMaxAge(60 * 60 * 10);
		response.addCookie(tokenCookie);
		Cookie userCookie = new Cookie("username", username);
		userCookie.setPath("/");
		userCookie.setMaxAge(60 * 60 * 10);
		response.addCookie(userCookie);		
//		Cookie useridCookie = new Cookie("userid", Long.toString(checkUser.getId()));
//		useridCookie.setPath("/");
//		useridCookie.setMaxAge(60 * 60 * 10);
//		response.addCookie(useridCookie);
		
		// 在内存中增加记录
		UserData.activeUser(username);
		UserData.addBindId(username, checkUser.getId());
		
		return result;
	}
	
	/**
	 * 用户登出
	 * @param model 数据模型
	 * @param session session对象
	 * @param username 用户名
	 * @return 返回结果 USER_NOT_ONLINE, OK
	 */
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	public ResultBean logoutUser(Model model,
							  HttpSession session,
							  @QueryParam("username") String username)
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
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ResultBean getInfo()
	{
		ResultBean result = new ResultBean(ErrorCode.OK);
		return result;
	}
	
}
