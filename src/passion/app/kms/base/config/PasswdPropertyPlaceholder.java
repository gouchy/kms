package passion.app.kms.base.config;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PasswdPropertyPlaceholder
{
  public static final String PASSWORD_SUBFIX = ".password";
  public static final String ENCRYPASSWORD_SUBFIX = ".encryptpassword";
  private static Logger log = LoggerFactory.getLogger(PasswdPropertyPlaceholder.class);

  private static StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();

  public static void loadProperties(String file, Properties propsOut)
  {
    FileInputStream fin = null;

    List<String> replacedLines = new LinkedList<String>();
    try
    {
      fin = new FileInputStream(file);
      @SuppressWarnings("resource")
	BufferedReader br = new BufferedReader(new InputStreamReader(fin));
      String line;
      while ((line = br.readLine()) != null)
      {
        line = line.trim();
        if (line.startsWith("#"))
        {
          replacedLines.add(line);
        }
        else
        {
          int spIndex = line.indexOf("=");
          if (spIndex < 0)
          {
            replacedLines.add(line);
          }
          else
          {
            String key = line.substring(0, spIndex).trim();
            String value = line.substring(spIndex + 1).trim();

            if (key.endsWith(".password"))
            {
              line = key.replace(".password", ".encryptpassword");
              line = line + "=";
              line = line + encryptor.encrypt(value);

              propsOut.remove(key);
              propsOut.setProperty(key.substring(0, key.length() - ".password".length()), value);
            }
            else if (key.endsWith(".encryptpassword"))
            {
              propsOut.remove(key);
              propsOut.setProperty(key.substring(0, key.length() - ".encryptpassword".length()), encryptor.decrypt(value));
            }

            replacedLines.add(line);
          }
        }
      }
    } catch (IOException e) {
      log.error("read properties file failed", e);
      return;
    }
    finally
    {
      try {
        if (fin != null)
        {
          fin.close();
        }
      }
      catch (Exception e)
      {
        log.error("close properties file failed", e);
      }
    }

    if (!replacedLines.isEmpty())
    {
      replace(file, replacedLines);
    }
  }

  public static void replace(String file, List<String> replacedLines)
  {
    FileOutputStream fout = null;
    try
    {
      fout = new FileOutputStream(file, false);
      @SuppressWarnings("resource")
	PrintWriter pw = new PrintWriter(fout);
      for (String line : replacedLines)
      {
        pw.println(line);
      }
      pw.flush();
    }
    catch (IOException e)
    {
      log.error("write properties file failed", e);
    }
    finally
    {
      try
      {
        if (fout != null)
        {
          fout.close();
        }
      }
      catch (Exception e)
      {
        log.error("close stream failed!", e);
      }
    }
  }

  static
  {
    encryptor.setPassword("elipswebserver");
  }
}