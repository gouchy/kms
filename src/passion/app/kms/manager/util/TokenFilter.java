package passion.app.kms.manager.util;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import passion.app.kms.manager.data.UserData;

public class TokenFilter implements Filter
{
	private static Logger log = LoggerFactory.getLogger(TokenFilter.class);
	
	@Override
	public void destroy()
	{

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException
	{
		HttpServletRequest httpRequest = (HttpServletRequest)request;
		Cookie[] cookieList = httpRequest.getCookies();
		
		String username = null;
		String token = null;
		
		// 判断是否需要检测
//		if(httpRequest.getPathInfo().toLowerCase().indexOf("rest") < 0)
//		{
//			chain.doFilter(request, response);
//			return;
//		}
		
		// 跳过user
		if(httpRequest.getPathInfo().toLowerCase().indexOf("/user") >= 0)
		{
			chain.doFilter(request, response);
			return;
		}
		
		for(Cookie cookie : cookieList)
		{
			if(cookie.getName().equals("username"))
			{
				username = cookie.getValue();
				if(token != null)
				{
					break;
				}
			}
			else if(cookie.getName().equals("token"))
			{
				token = cookie.getValue();
				if( username != null)
				{
					break;
				}
			}
		}
		
		// 检查token是否符合
		if(username == null || username.equals(""))
		{	
			// 返回403
			writeResponseNoRight(response, "No Right.");
			return;
		}
		
		username = username.toLowerCase();
		if(!UserData.getToken(username).equals(token))
		{
			// 返回403
			writeResponseNoRight(response, "No Right.");
			return;
		}
		
		// 检查活动时间是否正确,大于30分钟自动失效
		if(UserData.getNoActiveTime(username) > 60 * 30 * 1000)
		{
			UserData.deleteToken(username);
			UserData.deleteActive(username);
			UserData.deleteId(username);
			// 返回403
			writeResponseNoRight(response, "No Right.");
			return;
		}
		
		UserData.activeUser(username);		
		chain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig config) throws ServletException
	{
	}
	
	/**
	 * 返回403
	 * @param response
	 * @param error
	 */
	private void writeResponseNoRight(ServletResponse response, String error)
	{
		HttpServletResponse httpResponse = (HttpServletResponse)response;
		try
		{
			httpResponse.addHeader("Cache-Control", "no-cache");
			httpResponse.setContentType("application/json");
			httpResponse.getWriter().write(error);
			httpResponse.getWriter().flush();
			httpResponse.getWriter().close();
			return;
		} catch (IOException e)
		{
			log.error(e.getMessage());
		}
	}

}
