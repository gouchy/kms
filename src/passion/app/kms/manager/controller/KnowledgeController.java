package passion.app.kms.manager.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;


import passion.app.kms.manager.bean.ResultBean;
import passion.app.kms.manager.bean.TitleBean;
import passion.app.kms.manager.constant.ErrorCode;
import passion.app.kms.manager.dao.KnowledgeMapper;
import passion.app.kms.manager.dao.SubjectMapper;
import passion.app.kms.manager.dao.TitleMapper;

@Controller
@Path("/knowledge")
public class KnowledgeController
{
	@Autowired
	private KnowledgeMapper knowledgeMapper;
	
	@Autowired
	private TitleMapper titleMapper;
	
	@Autowired
	private SubjectMapper subjectMapper;
	
	/**
	 * 获取知识列表
	 * @param session 会话
	 * @param id 知识分类的ID，如果是0代表获取所有分类下面的知识
	 * @param rows 需要获取到的行数
	 * @param page 第几页
	 * @return 查询到的数据 PARA_IS_ERROR, NO_RIGHT
	 */
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/title/{id}")
	public ResultBean getTitleList(HttpSession session, @PathParam("id") long id, 
												  @QueryParam("rows") long rows,
												  @QueryParam("page") long page)
	{
		ResultBean result = new ResultBean(ErrorCode.OK);
		
		// 检测权限
		// 取出用户ID
		long userId = 0;
		try
		{
			userId = (long) session.getAttribute("userId");
		} catch (Exception e)
		{
			result.setRetcode(ErrorCode.PARA_IS_ERROR);
			return result;
		}
		if( id != 0 && subjectMapper.checkSubjectOwner(id, userId) == 0)
		{
			result.setRetcode(ErrorCode.NO_RIGHT);
			return result;
		}		
		
		List<TitleBean> titleList = new ArrayList<TitleBean>();
		TitleBean t1 = new TitleBean();
		t1.setId(1);
		t1.setName("测试1");
		t1.setUpdateDate(new Date());
		titleList.add(t1);
		
		result.addEntry("rows", titleList);
		result.addEntry("total", 1);
		result.addEntry("page", 1);
		result.addEntry("records", 1);

		
		return result;
	}
	
	/**
	 * 
	 * @param session
	 * @param title
	 * @param content
	 * @param subjectId
	 * @param otherTitleList
	 * @return
	 */
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	public ResultBean createKnowledge(HttpSession session, 
													 @FormParam("knowledge-title") String knowledgeTitle,
													 @FormParam("knowledge-title")  String knowledgeContent,
													 @FormParam("knowledge-title")  Long addSubjectId, 
													 @FormParam("knowledge-title")  List<String> otherKnowledgTitle)
	{
		ResultBean result = new ResultBean(ErrorCode.OK);
/*		
		// 检查用户是否有权限操作此类别
		long userId = 0;
		try
		{
			userId = (long) session.getAttribute("userId");
		} catch (Exception e)
		{
			result.setRetcode(ErrorCode.PARA_IS_ERROR);
			return result;
		}
		if (subjectMapper.checkSubjectOwner(addSubjectId, userId) == 0)
		{
			result.setRetcode(ErrorCode.NO_RIGHT);
			return result;
		}
		// 正文内容进入数据库
		KnowledgeBean knowledgeBean = new KnowledgeBean();
		knowledgeBean.setContent(knowledgeContent);
		knowledgeBean.setDeleteFlag(0);
		knowledgeBean.setType(KnowledgeBean.TYPE_TEXT);
		if( 0 == knowledgeMapper.createKnowledge(knowledgeBean))
		{
			result.setRetcode(ErrorCode.DB_FAIL);
			return result;
		}
		
		// 新加title进入DB
		TitleBean titleBean = new TitleBean();
		titleBean.setType(TitleBean.TYPE_TITLE_MAIN); // main topic
		titleBean.setName(knowledgeTitle);
		titleBean.setIndexStatus(TitleBean.INDEX_NOT_ADD); // not index
		titleBean.setDeleteFlag(0); // not deleted
		titleBean.setUpdateDate(new Date());
		titleBean.setKnowledgeId(knowledgeBean.getId());
		titleBean.setSubjectId(addSubjectId);
		
		if ( 0 == titleMapper.createTitile(titleBean))
		{
			result.setRetcode(ErrorCode.DB_FAIL);
			return result;
		}
		
		// other title
		for(String otherTitle : otherKnowledgTitle)
		{
			// Ignore null string
			if(otherTitle == null || otherTitle.trim().equals(""))
			{
				continue;
			}
			TitleBean otherTitleBean = new TitleBean();
			otherTitleBean.setType(TitleBean.TYPE_TITLE_OTHER); // main topic
			otherTitleBean.setName(otherTitle);
			otherTitleBean.setIndexStatus(TitleBean.INDEX_NOT_ADD); // not index
			otherTitleBean.setDeleteFlag(0); // not deleted
			otherTitleBean.setUpdateDate(new Date());
			otherTitleBean.setKnowledgeId(knowledgeBean.getId());
			otherTitleBean.setSubjectId(addSubjectId);
			
			if ( 0 == titleMapper.createTitile(otherTitleBean))
			{
				result.setRetcode(ErrorCode.DB_FAIL);
				return result;
			}
		}
		*/
		
		return result;
	}
}
