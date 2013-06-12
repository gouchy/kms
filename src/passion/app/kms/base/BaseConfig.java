package passion.app.kms.base;

import java.util.concurrent.ConcurrentHashMap;



/**
 * 基本配置数据类
 * @author gouchy
 *
 */
public class BaseConfig {
	
	/**
	 * 微信token集合，Key值为分配路径标识，value为token
	 */
	public static ConcurrentHashMap<String, String> WECHAT_ACCOUNT = new ConcurrentHashMap<String, String>();
	
	static
	{
		/**
		 * 测试初始化数据
		 */
		WECHAT_ACCOUNT.put("ccisv", "huawei");

	}
}
