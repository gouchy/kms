package passion.app.kms.base;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import passion.app.kms.base.config.ConfigProperties;

public class SystemInitListener  implements ServletContextListener
{

	@Override
	public void contextDestroyed(ServletContextEvent event)
	{
		
	}

	@Override
	public void contextInitialized(ServletContextEvent event)
	{
	    String path = event.getServletContext().getRealPath("/");
	    ConfigProperties.loadConfig(path);
	}

}
