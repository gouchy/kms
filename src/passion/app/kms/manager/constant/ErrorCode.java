package passion.app.kms.manager.constant;

public class ErrorCode
{
	/*
	 * 成功
	 */
	public final static int OK = 0;
	
	/*
	 * 用户已存在
	 */
	public final static int USER_EXIST = 10001;
	
	/**
	 * 参数非法
	 */
	public final static int PARA_IS_NULL = 10002;
	
	/**
	 * 数据库操作失败
	 */
	public final static int DB_FAIL = 10003;
	
	/**
	 * 用户不存在
	 */
	public final static int USER_NOT_EXIST = 10004;
	
	/**
	 * 用户未登录
	 */
	public final static int USER_NOT_ONLINE = 10005;
	
	/**
	 * 没有权限
	 */
	public final static int NO_RIGHT = 10006;
}
