package passion.app.kms.wechat.controller;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Date;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import passion.app.kms.base.operator.OssOperator;
import passion.app.kms.base.operator.SolrOperator;
import passion.app.kms.manager.bean.KnowledgeBean;
import passion.app.kms.manager.bean.UserBean;
import passion.app.kms.manager.dao.KnowledgeMapper;
import passion.app.kms.manager.dao.UserMapper;
import passion.app.kms.wechat.bean.Message;

/**
 * 用于接收来自微信的消息
 * @author gouchy
 *
 */
@Controller
@Path("/weixin")
public class MessageController {
	private static Logger log = LoggerFactory.getLogger(MessageController.class);
	
	@Autowired
	private KnowledgeMapper knowledgeMapper;
	
	@Autowired
	private UserMapper userMapper;
	
	/**
	 * 接收来自微信服务器的消息
	 * @param model 返回的数据模型
	 * @param account 发送的测试账号
	 * @param xmlText 发送的微信内容
	 * @return 跳转的jsp页面（消息类型所替代的xml）
	 * @throws UnsupportedEncodingException 
	 * @throws NoSuchAlgorithmException 
	 */
	@POST
	@Path("/{account}")
	@Produces("text/xml; charset=utf-8")
	public String message(@PathParam("account") String account,
						   @QueryParam("signature") String signature,
						   @QueryParam("timestamp") String timestamp,
						   @QueryParam("nonce") String nonce,
						   String xmlText) throws UnsupportedEncodingException, NoSuchAlgorithmException
	{
		Message msg = null;
		Message responseMsg = null;
		
		// 检查账号
		UserBean user = userMapper.readUserByUsername(account);		
		String token = user.getToken();		
		if( token == null)
		{
			return "error check token";
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
        if (!sb.toString().equals(signature))
        {       			
        	return "error signature";
        }
		
		try {
			msg = parseXml(xmlText);
		} catch (SAXException | IOException | ParserConfigurationException e) {
			log.error("receive a error xml content message.");
			return "receive a error xml content message.";
		}
		switch(msg.getMsgType())
		{
		case TEXT:
			responseMsg = reponseText(user, msg);
			break;
		case EVENT:
			break;
		case IMAGE:
			break;
		case LINK:
			break;
		case LOCATION:
			break;
		case MUSIC:
			break;
		case NEWS:
			break;
		default:
			break;
		}
		if (responseMsg == null)
		{
			return "It doesn't have a response.";
		}
		String response ="It doesn't support this message type";
		if(responseMsg.getMsgType() == Message.WeChatMessageType.TEXT)
		{
			response = "<xml>\r\n";
			response += "<ToUserName><![CDATA[" + responseMsg.getToUserName() + "]]></ToUserName>\r\n";
			response += "<FromUserName><![CDATA[" + responseMsg.getFromUserName() + "]]></FromUserName>\r\n";
			response += "<CreateTime>" + Long.toString(responseMsg.getCreateTime().getTime()) + "</CreateTime>\r\n";
			response += "<MsgType><![CDATA[text]]></MsgType>\r\n";
			response += "<Content><![CDATA[" + responseMsg.getContent() + "]]></Content>\r\n";
			response += "<FuncFlag>0</FuncFlag>\r\n";
			response += "</xml>\r\n";
		}
		else if(responseMsg.getMsgType() == Message.WeChatMessageType.MUSIC)
		{
			response = "<xml>\r\n";
			response += "<ToUserName><![CDATA[" + responseMsg.getToUserName() + "]]></ToUserName>\r\n";
			response += "<FromUserName><![CDATA[" + responseMsg.getFromUserName() + "]]></FromUserName>\r\n";
			response += "<CreateTime>" + Long.toString(responseMsg.getCreateTime().getTime()) + "</CreateTime>\r\n";
			response += "<MsgType><![CDATA[music]]></MsgType>\r\n";
			response += "<Music>\r\n";
			response += "<Title><![CDATA[" + responseMsg.getTitle() + "]]></Title>\r\n";
			response += "<Description><![CDATA[" + responseMsg.getDescription() + "]]></Description>\r\n";
			response += "<MusicUrl><![CDATA[" + responseMsg.getMusicUrl() + "]]></MusicUrl>\r\n";
			response += "<HQMusicUrl><![CDATA[" + responseMsg.getHqMusicUrl() + "]]></HQMusicUrl>\r\n";
			response += "</Music>\r\n";
			response += "<FuncFlag>0</FuncFlag>\r\n";
			response += "</xml>\r\n";
		}
		
		// 存放信息到OSS
		OssOperator.putMessage(account, xmlText, responseMsg.isHaveAnswer());
		
		return response;
	}
	
	/**
	 * 文本消息的回复处理
	 * @param msg
	 * @return
	 */
	private Message reponseText(UserBean user, Message msg)
	{
		log.info(msg.getContent());
		Message responseMsg = new Message();
		responseMsg.setFromUserName(msg.getToUserName());
		responseMsg.setToUserName(msg.getFromUserName());
		responseMsg.setCreateTime(new Date());
		responseMsg.setMsgType(Message.WeChatMessageType.TEXT);
		responseMsg.setContent("test");
		
		long knowledgeId = SolrOperator.queryKnowledge(user.getId(), msg.getContent());
		if(knowledgeId == 0)
		{
			responseMsg.setContent("Sorry, we can't find you wanted.");
			responseMsg.setHaveAnswer(false);
		}
		else
		{
			KnowledgeBean knowledge = knowledgeMapper.readKnowledgeById(knowledgeId);
			responseMsg.setContent(knowledge.getContent());
			responseMsg.setHaveAnswer(true);
		}
		
		return responseMsg;
	}
	
	/**
	 * 解析来自微信服务器的消息
	 * @param xmlText 需要解析的内容
	 * @return 接续后的消息对象
	 * @throws SAXException 异常
	 * @throws IOException 异常
	 * @throws ParserConfigurationException 异常
	 */
	private Message parseXml(String xmlText) throws SAXException, IOException, ParserConfigurationException
	{
		Message msg = new Message();
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(new InputSource(new StringReader(xmlText)));
		Element root = doc.getDocumentElement();
		NodeList nodeList = root.getChildNodes();
		if ( nodeList != null)
		{
			int nodeLength = nodeList.getLength();
			for(int i = 0; i < nodeLength; i++)
			{
				Node node = nodeList.item(i);
				if (node.getNodeName().equals("ToUserName"))
				{
					msg.setToUserName(node.getTextContent());
				}
				else if (node.getNodeName().equals("FromUserName"))
				{
					msg.setFromUserName(node.getTextContent());
				}
				else if (node.getNodeName().equals("CreateTime"))
				{
					Date createDate = new Date();
					try
					{
						long tmpSecond = Long.parseLong(node.getTextContent());
						createDate.setTime(tmpSecond * 1000L);
					}
					catch (Exception e)
					{
						//
					}
					
					msg.setCreateTime(createDate);
				}
				else if (node.getNodeName().equals("MsgType"))
				{
					String tmpTypeStr = node.getTextContent();
					if(tmpTypeStr.equals("text"))
					{
						msg.setMsgType(Message.WeChatMessageType.TEXT);
					}
					else if(tmpTypeStr.equals("event"))
					{
						msg.setMsgType(Message.WeChatMessageType.EVENT);
					}
					else if(tmpTypeStr.equals("link"))
					{
						msg.setMsgType(Message.WeChatMessageType.LINK);
					}
					else if(tmpTypeStr.equals("location"))
					{
						msg.setMsgType(Message.WeChatMessageType.LOCATION);
					}
					else if(tmpTypeStr.equals("music"))
					{
						msg.setMsgType(Message.WeChatMessageType.MUSIC);
					}
					else if(tmpTypeStr.equals("news"))
					{
						msg.setMsgType(Message.WeChatMessageType.NEWS);
					}
				}
				else if (node.getNodeName().equals("Content"))
				{
					msg.setContent(node.getTextContent());
				}
				else if (node.getNodeName().equals("PicUrl"))
				{
					msg.setPicUrl(node.getTextContent());
				}
				else if (node.getNodeName().equals("Location_X"))
				{
					msg.setLocation_X(node.getTextContent());
				}
				else if (node.getNodeName().equals("Location_Y"))
				{
					msg.setLocation_Y(node.getTextContent());
				}
				else if (node.getNodeName().equals("Label"))
				{
					msg.setLabel(node.getTextContent());
				}
				else if (node.getNodeName().equals("Title"))
				{
					msg.setTitle(node.getTextContent());
				}
				else if (node.getNodeName().equals("Description"))
				{
					msg.setDescription(node.getTextContent());
				}
				else if (node.getNodeName().equals("Url"))
				{
					msg.setUrl(node.getTextContent());
				}
				else if (node.getNodeName().equals("Event"))
				{
					msg.setEvent(node.getTextContent());
				}
				else if (node.getNodeName().equals("EventKey"))
				{
					msg.setEventKey(node.getTextContent());
				}
				else if (node.getNodeName().equals("MusicUrl"))
				{
					msg.setMusicUrl(node.getTextContent());
				}
				else if (node.getNodeName().equals("HQMusicUrl"))
				{
					msg.setHqMusicUrl(node.getTextContent());
				}
				else if (node.getNodeName().equals("MsgId"))
				{
					msg.setMsgID(node.getTextContent());
				}
				//TODO: 图文消息需要添加
			}
		}
		return msg;
	}
}
