package passion.app.kms.manager.data;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用户信息
 * @author gouchy
 *
 */
public class UserData
{
	private static ConcurrentHashMap<String, String> loginTokenMap = new ConcurrentHashMap<String, String>();
	
	private static ConcurrentHashMap<String, Date> lastActiveMap = new ConcurrentHashMap<String, Date>();
	
	private static ConcurrentHashMap<String, Long> loginIdMap = new ConcurrentHashMap<String, Long>();
	
	/**
	 * 获取用户鉴权token
	 * @param user 用户名
	 * @return token 
	 */
	public static String getToken(String user)
	{
		if(loginTokenMap.containsKey(user))
		{
			return loginTokenMap.get(user);
		}
		else
		{
			String token = UUID.randomUUID().toString();
			loginTokenMap.put(user, token);
			return token;
		}
	}
	
	/**
	 * 去掉用户的token记录
	 * @param user 用户名
	 */
	public static void deleteToken(String user)
	{
		loginTokenMap.remove(user);
	}
	
	/**
	 * 更新用户的活动时间
	 * @param user 用户名
	 */
	public static void activeUser(String user)
	{
		lastActiveMap.put(user, new Date());
	}
	
	/**
	 * 检查用户的活动时间间隔
	 * @param user 用户名
	 * @return
	 */
	public static long getNoActiveTime(String user)
	{
		if(lastActiveMap.containsKey(user))
		{
			Date oldDate = lastActiveMap.get(user);
			return new Date().getTime() - oldDate.getTime();
		}
		else
		{
			return Long.MAX_VALUE;
		}
	}
	
	/**
	 * 删除活动时间记录
	 * @param user 用户名
	 */
	public static void deleteActive(String user)
	{
		lastActiveMap.remove(user);
	}
	
	/**
	 * 增加绑定的用户ID
	 * @param username 用户名
	 * @param id ID
	 */
	public static void addBindId(String username, long id)
	{
		loginIdMap.put(username, new Long(id));
	}
	
	/**
	 * 获得绑定的用户ID
	 * @param username 用户名
	 * @return ID
	 */
	public static long getBindId(String username)
	{
		if(loginIdMap.containsKey(username))
		{
			return (long)loginIdMap.get(username);
		}
		else
		{
			return 0;
		}
	}
	
	/**
	 * 删除id
	 * @param username 用户名
	 */
	public static void deleteId(String username)
	{
		loginIdMap.remove(username);
	}
}
