package passion.app.kms.manager.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
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
	public UserBean readUserByUsername(@Param("username") String username);
	
	/**
	 * 按照指定的用户名和密码取出数据库是否存在这样的用户
	 * @param username 用户名
	 * @param password 密码
	 * @return 用户数据对象
	 */
	@Select("select * from kms_user where username = #{username} and password= #{password}")
	public UserBean readUserByUsernameAndPassword(@Param("username") String username, @Param("password") String password);
	
	/**
	 * 创建用户
	 * @param user 用户信息，实际创建的用户id会在成功后返回
	 * @return 受影响的行数
	 */
	@Insert("insert into kms_user (username, password, usertype, userrole, email, mobile, token) values (#{username}, #{password}, #{usertype}, #{userrole}, #{email}, #{mobile}, #{token})")
	@Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
	public int createUser(UserBean user);
}
