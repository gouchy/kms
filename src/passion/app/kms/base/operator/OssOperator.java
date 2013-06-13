package passion.app.kms.base.operator;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.UUID;

import passion.app.kms.base.config.ConfigList;
import passion.app.kms.base.config.ConfigProperties;

import com.aliyun.openservices.oss.OSSClient;
import com.aliyun.openservices.oss.model.ObjectMetadata;

public class OssOperator
{
	
	private static OSSClient client = new OSSClient(ConfigProperties.getKey(ConfigList.BASIC, "OSS_ADDRESS"),
			ConfigProperties.getKey(ConfigList.BASIC, "OSS_ACCESS_KEY"),
			ConfigProperties.getKey(ConfigList.BASIC, "OSS_ACCESS_SECRET"));
	
	public static boolean putMessage(String account, String xmlStr, boolean haveAnswer)
	{
//		if(!client.doesBucketExist(account))
//		{
//			client.createBucket(account);
//		}
		
		InputStream in = new ByteArrayInputStream(xmlStr.getBytes());
		ObjectMetadata meta = new ObjectMetadata();
//		if(haveAnswer)
//		{
//			meta.addUserMetadata("haveAnswer", "yes");
//		}
//		else
//		{
//			meta.addUserMetadata("haveAnswer", "no");
//		}
		meta.setContentLength(xmlStr.getBytes().length);
		meta.setContentType("text/xml");
		
		if(haveAnswer)
		{
			meta.addUserMetadata("x-oss-meta-haveanswer", "yes");
			client.putObject(ConfigProperties.getKey(ConfigList.BASIC, "OSS_BUCKET_NAME"), account + "/answer/" +UUID.randomUUID().toString(), in, meta);
		}
		else
		{
			meta.addUserMetadata("x-oss-meta-haveanswer", "no");
			client.putObject(ConfigProperties.getKey(ConfigList.BASIC, "OSS_BUCKET_NAME"), account + "/noanswer/" +UUID.randomUUID().toString(), in, meta);
		}
		
		return true;
	}
	
	public static void main(String[] args)
	{
		//putMessage("51talking", "<xml><a>abc</a></xml>");

	}
}
