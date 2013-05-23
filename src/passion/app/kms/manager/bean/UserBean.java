package passion.app.kms.manager.bean;

/**
 * 用户信息
 * @author gouchy
 *
 */
public class UserBean {
	
	/**
	 * 数据库中的编号
	 */
	private long id;
	
	/**
	 * 用户名
	 */
	private String username;
	
	/**
	 * 密码
	 */
	private String password;
	
	/**
	 * 用户类型
	 */
	private int usertype;
	
	/** 
	 * 用户权限
	 */
	private int userrole;
	
	/**
	 * 邮件地址
	 */
	private String email;
	
	/**
	 * 手机号码
	 */
	private String mobile;
	
	/**
	 * 微信token
	 */
	private String token;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getUsertype() {
		return usertype;
	}

	public void setUsertype(int usertype) {
		this.usertype = usertype;
	}

	public int getUserrole() {
		return userrole;
	}

	public void setUserrole(int userrole) {
		this.userrole = userrole;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}
