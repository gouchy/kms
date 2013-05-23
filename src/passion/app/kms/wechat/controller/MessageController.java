package passion.app.kms.wechat.controller;

import java.io.IOException;
import java.io.StringReader;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import passion.app.kms.wechat.bean.Message;

/**
 * 用于接收来自微信的消息
 * @author gouchy
 *
 */
@Controller
public class MessageController {
	private static Logger log = LoggerFactory.getLogger(MessageController.class);
	
	/**
	 * 接收来自微信服务器的消息
	 * @param model 返回的数据模型
	 * @param account 发送的测试账号
	 * @param xmlText 发送的微信内容
	 * @return 跳转的jsp页面（消息类型所替代的xml）
	 */
	@RequestMapping(value = "/wechat/{account}", method = RequestMethod.POST)
	public String message(Model model,
									  @PathVariable("account") String account,
									  @RequestBody String xmlText)
	{
		Message msg = null;
		Message responseMsg = null;
		try {
			msg = parseXml(xmlText);
		} catch (SAXException | IOException | ParserConfigurationException e) {
			log.error("receive a error xml content message.");
			model.addAttribute("error_str", "receive a error xml content message.");
			return "wechat/error";
		}
		switch(msg.getMsgType())
		{
		case TEXT:
			responseMsg = reponseText(msg);
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
		if (responseMsg != null)
		{
			model.addAttribute("fromUserName", responseMsg.getFromUserName());
			model.addAttribute("toUserName", responseMsg.getToUserName());
			model.addAttribute("msgType", responseMsg.getMsgType().toString());
			model.addAttribute("content", responseMsg.getContent());
			model.addAttribute("createTime", (long)(responseMsg.getCreateTime().getTime() / 1000));
			model.addAttribute("picUrl", responseMsg.getPicUrl());
			model.addAttribute("location_X", responseMsg.getLocation_X());
			model.addAttribute("location_Y", responseMsg.getLocation_Y());
			model.addAttribute("label", responseMsg.getLabel());
			model.addAttribute("title", responseMsg.getTitle());
			model.addAttribute("description", responseMsg.getDescription());
			model.addAttribute("url", responseMsg.getUrl());
			model.addAttribute("event", responseMsg.getEvent());
			model.addAttribute("eventKey", responseMsg.getEventKey());
			model.addAttribute("musicUrl", responseMsg.getMusicUrl());
			model.addAttribute("hqMusicUrl", responseMsg.getHqMusicUrl());
			// TODO:图消息消息需要添加
		}
		else
		{
			model.addAttribute("error_str", "It doesn't have a response.");
			return "wechat/error";
		}
		if(responseMsg.getMsgType() == Message.WeChatMessageType.TEXT)
		{
			return "wechat/message-text";
		}
		else if(responseMsg.getMsgType() == Message.WeChatMessageType.MUSIC)
		{
			return "wechat/message-music";
		}
		else
		{
			model.addAttribute("error_str", "It doesn't support this message type");
			return "wechat/error";
		}
	}
	
	/**
	 * 文本消息的回复处理
	 * @param msg
	 * @return
	 */
	private Message reponseText(Message msg)
	{
		log.info(msg.getContent());
		Message responseMsg = new Message();
		responseMsg.setFromUserName(msg.getToUserName());
		responseMsg.setToUserName(msg.getFromUserName());
		responseMsg.setCreateTime(new Date());
		responseMsg.setMsgType(Message.WeChatMessageType.TEXT);
		responseMsg.setContent("test");
		
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
