package passion.app.kms.manager.dao;

import org.apache.ibatis.annotations.Select;

import passion.app.kms.manager.bean.UserBean;


/**
 * 用户数据操作类
 * @author gouchy
 *
 */
public interface UserMapper {
	
	/**
	 * 根据用户名取用户相关信息
	 * @param username
	 * @return
	 */
	@Select("select * from kms_user where username = #{username}")
	public UserBean readUserByUsername(String username);
	
	/**
	 * 按照指定的用户名和密码取出数据库是否存在这样的用户
	 * @param username 用户名
	 * @param password 密码
	 * @return 用户数据对象
	 */
	@Select("select * from kms_user where username = #{username} and password= #{password}")
	public UserBean readUserByUsernameAndPassword(String username, String password);
}
