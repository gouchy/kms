package passion.app.kms.manager.bean;

import java.util.HashMap;

import passion.app.kms.manager.constant.ErrorCode;

/**
 * REST 接口的返回值
 * @author gouchy
 *
 */
public class ResultBean
{
	
	public ResultBean()
	{
		retcode = ErrorCode.OK;
	}
	
	public ResultBean(int code)
	{
		retcode = code;
	}
	
	public int getRetcode()
	{
		return retcode;
	}

	public void setRetcode(int retcode)
	{
		this.retcode = retcode;
	}

	public String getText()
	{
		return text;
	}

	public void setText(String text)
	{
		this.text = text;
	}

	public HashMap<String, Object> getValue()
	{
		return value;
	}

	public void setValue(HashMap<String, Object> value)
	{
		this.value = value;
	}
	
	public void addEntry(String key, Object object)
	{
		this.value.put(key, object);
	}

	/**
	 * 返回码
	 */
	private int retcode ;
	
	/**
	 * 文本提示
	 */
	private String text;
	
	/**
	 * 返回结果码
	 */
	private HashMap<String, Object> value = new HashMap<String, Object>();
}
