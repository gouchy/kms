package passion.app.kms.base.config;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class ConfigProperties
{
  private static Logger log = LoggerFactory.getLogger(ConfigProperties.class);

  private static Map<String, Properties> propsMap = new ConcurrentHashMap<String, Properties>();

  public static boolean loadConfig(String rootPath)
  {
    Field[] configFileFieldList = ConfigList.class.getFields();

    for (int i = 0; i < configFileFieldList.length; i++)
    {
      String filename;
      try
      {
        filename = (String)configFileFieldList[i].get(new String[0]);
      }
      catch (Exception e)
      {
        log.error("Load config file failed.\r\n{}", e.getMessage());
        return false;
      }
      String filepath = rootPath + "/WEB-INF/config/" + filename;
      File file = new File(filepath);
      if (!file.exists())
      {
        propsMap.put(filename, new Properties());
      }
      else
      {
        InputStream inputFile = null;
        try
        {
          inputFile = new BufferedInputStream(new java.io.FileInputStream(file.getAbsolutePath()));

          Properties props = new Properties();
          props.load(inputFile);
          propsMap.put(filename, props);
        }
        catch (Exception e)
        {
          log.error("Load {} failed.\r\n{}", filename, e.getMessage());
        }
        finally
        {
          try
          {
            if (inputFile != null)
            {
              inputFile.close();
            }
          }
          catch (IOException e)
          {
            log.error(e.getMessage());
          }
        }

        PasswdPropertyPlaceholder.loadProperties(filepath, (Properties)propsMap.get(filename));
      }
    }

    return true;
  }

  public static String getKey(String configFile, String key)
  {
    if (propsMap.containsKey(configFile))
    {
      String result = ((Properties)propsMap.get(configFile)).getProperty(key);
      if (result != null)
      {
        return result.trim();
      }
    }
    return "";
  }

  public static void setKey(String configFile, String key, String value)
  {
    if (propsMap.containsKey(configFile))
    {
      if (null != key)
      {
        ((Properties)propsMap.get(configFile)).setProperty(key, value == null ? "" : value);
      }
    }
  }

  public static boolean saveConfig(String rootPath, String fileName, Properties snippetProps)
  {
    boolean returnValue = false;

    String filePath = rootPath + "/WEB-INF/config/" + fileName;
    File file = new File(filePath);
    if (!file.exists())
    {
      try
      {
        returnValue = file.createNewFile();
      }
      catch (IOException e)
      {
        log.error("create file {} failed.\r\n{}", fileName, e.getMessage());
      }

    }

    PropertiesConfiguration configuration = null;
    Properties props = (Properties)propsMap.get(fileName);
    try
    {
      configuration = new PropertiesConfiguration(file);

      for (@SuppressWarnings("rawtypes")
	Enumeration e = snippetProps.propertyNames(); e.hasMoreElements(); )
      {
        String key = (String)e.nextElement();
        String value = snippetProps.getProperty(key);

        props.setProperty(key, value);

        configuration.setProperty(key, value);
        configuration.save();
      }
      returnValue = true;
    }
    catch (ConfigurationException e1)
    {
      log.error("load PropertiesConfiguration failed !");
    }

    propsMap.put(fileName, props);
    PasswdPropertyPlaceholder.loadProperties(filePath, props);

    return returnValue;
  }

  public static Properties getProperties(String fileName)
  {
    return (Properties)propsMap.get(fileName);
  }
}