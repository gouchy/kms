<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<xml>
 <ToUserName><![CDATA[${toUserName }]]></ToUserName>
 <FromUserName><![CDATA[${fromUserName }]]></FromUserName>
 <CreateTime>${createTime}</CreateTime>
 <MsgType><![CDATA[${msgType}]]></MsgType>
 	<Music>
 		<Title><![CDATA[${title}]]></Title>
 		<Description><![CDATA[${description }]]></Description>
 		<MusicUrl><![CDATA[${musicUrl }]]></MusicUrl>
 		<HQMusicUrl><![CDATA[${hqMusicUrl }]]></HQMusicUrl>
 	</Music>
 <FuncFlag>0</FuncFlag>
 </xml>