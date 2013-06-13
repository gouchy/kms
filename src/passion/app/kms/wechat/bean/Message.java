package passion.app.kms.wechat.bean;

import java.util.Date;

/**
 * 微信的消息定义
 * @author gouchy
 *
 */
public class Message {
	
	public enum WeChatMessageType {
		TEXT("text"), IMAGE("image"), LOCATION("location"), LINK("link"), EVENT("event"), MUSIC("music"), NEWS("news");
		
		private String name;
		
		private WeChatMessageType(String name)
		{
			this.name = name;
		}
		@Override
		public String toString()
		{
			return this.name;
		}
	}
	
	/**
	 * 接受者微信号
	 */
	private String toUserName;
	
	/**
	 * 发送者微信号
	 */
	private String fromUserName;
	
	/**
	 * 创建时间
	 */
	private Date createTime;
	
	/**
	 * 消息类型
	 */
	private WeChatMessageType msgType;
	
	/**
	 * 消息内容
	 * MsgType为text时有效
	 */
	private String content;
	
	/**
	 * 图片地址
	 * MsgType为image时有效
	 */
	private String picUrl;
	
	/**
	 * 地理位置维度
	 * MsgType为location有效
	 */
	private String location_X;
	
	/**
	 * 地理位置进度
	 * MsgType为location有效
	 */
	private String location_Y;
	
	/**
	 * 地理位置信息
	 * MsgType为location有效
	 */
	private String label;
	
	/**
	 * 消息标题
	 * MsgType为link时有效
	 */
	private String title;
	
	/**
	 * 消息描述
	 * MsgType为link时有效
	 */
	private String description;
	
	/**
	 * 消息链接
	 * MsgType为link时有效
	 */
	private String url;
	
	/**
	 * 事件类型，可以为subscribe订阅、unsubscribe取消订阅，CLICK自定义菜单点击事件
	 * MsgType为event时有效
	 */
	private String event;
	
	/**
	 * 事件KEY值，与自定义菜单接口中的KEY值对应
	 * MsgType为event时有效
	 */
	private String eventKey;
	
	/**
	 * 音乐链接
	 * MsgType为music时有效，回复消息使用
	 */
	private String musicUrl;
	
	/**
	 * 高质量音乐链接，WIFI环境有限使用该链接播放音乐
	 * MsgType为music时有效，回复消息使用
	 */
	private String hqMusicUrl;
	
	// TODO: 图文消息涉及到嵌套接口，暂未加入
	
	/**
	 * 消息 ID
	 */
	private String msgId;
	
	/**
	 * 是否回答
	 */
	private boolean haveAnswer;
	
	public String getToUserName() {
		return toUserName;
	}

	public void setToUserName(String toUserName) {
		this.toUserName = toUserName;
	}

	public String getFromUserName() {
		return fromUserName;
	}

	public void setFromUserName(String fromUserName) {
		this.fromUserName = fromUserName;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public WeChatMessageType getMsgType() {
		return msgType;
	}

	public void setMsgType(WeChatMessageType msgType) {
		this.msgType = msgType;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getPicUrl() {
		return picUrl;
	}

	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}

	public String getLocation_X() {
		return location_X;
	}

	public void setLocation_X(String location_X) {
		this.location_X = location_X;
	}

	public String getLocation_Y() {
		return location_Y;
	}

	public void setLocation_Y(String location_Y) {
		this.location_Y = location_Y;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public String getEventKey() {
		return eventKey;
	}

	public void setEventKey(String eventKey) {
		this.eventKey = eventKey;
	}

	public String getMusicUrl() {
		return musicUrl;
	}

	public void setMusicUrl(String musicUrl) {
		this.musicUrl = musicUrl;
	}

	public String getHqMusicUrl() {
		return hqMusicUrl;
	}

	public void setHqMusicUrl(String hqMusicUrl) {
		this.hqMusicUrl = hqMusicUrl;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

	public String getMsgId() {
		return msgId;
	}

	public void setMsgID(String msgId) {
		this.msgId = msgId;
	}

	public boolean isHaveAnswer()
	{
		return haveAnswer;
	}

	public void setHaveAnswer(boolean haveAnswer)
	{
		this.haveAnswer = haveAnswer;
	}


}
