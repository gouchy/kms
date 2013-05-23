package passion.app.kms.wechat.controller;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import passion.app.kms.base.BaseConfig;

/**
 * 用于微信的接入验证
 * @author gouchy
 *
 */
@Controller
public class VerifyController {
	
	private static Logger log = LoggerFactory.getLogger(VerifyController.class);
	
	/**
	 * 校验来自微信服务器的签名
	 * @param model 数据模型
	 * @param account 区分不同的微信用户
	 * @param signature 签名值
	 * @param timestamp 时间戳
	 * @param nonce 挑战值
	 * @param echostr 回显值
	 * @return 跳转的页面
	 * @throws NoSuchAlgorithmException 异常
	 */
	@RequestMapping(value= "/wechat/{account}", method = RequestMethod.GET)
	public String verify(Model model,
						  @PathVariable("account") String account,
						  @RequestParam("signature") String signature,
						  @RequestParam("timestamp") String timestamp,
						  @RequestParam("nonce") String nonce,
						  @RequestParam("echostr") String echostr) throws NoSuchAlgorithmException {
		
			log.info("Verify Request: signature:{}, timestamp:{}, nonce:{}, echostr:{}", new Object[]{signature, timestamp, nonce, echostr});
			
			String token = BaseConfig.WECHAT_ACCOUNT.get(account);
			if( token == null)
			{
				model.addAttribute("echostr", "error-account");
			}			
			String[] tmpSortStr = new String[]{token, timestamp, nonce};
			Arrays.sort(tmpSortStr);
			String tmpDestStr = tmpSortStr[0] + tmpSortStr[1] + tmpSortStr[2];
			MessageDigest md = MessageDigest.getInstance("SHA1");
			byte[] result = md.digest(tmpDestStr.getBytes());
			StringBuffer sb = new StringBuffer();
	        for (int i = 0; i < result.length; i++) {
	            sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
	        }
	        if (sb.toString().equals(signature))
	        {       			
	        	model.addAttribute("echostr", echostr);	
	        }
	        else
	        {
	        	model.addAttribute("echostr", "error-check");
	        }
	        
		return "wechat/verify";
	}
}
