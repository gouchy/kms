package passion.app.kms.manager.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;


import passion.app.kms.manager.bean.KnowledgeBean;
import passion.app.kms.manager.bean.ResultBean;
import passion.app.kms.manager.bean.TitleBean;
import passion.app.kms.manager.constant.ErrorCode;
import passion.app.kms.manager.dao.KnowledgeMapper;
import passion.app.kms.manager.dao.SubjectMapper;
import passion.app.kms.manager.dao.TitleMapper;
import passion.app.kms.manager.data.UserData;
import passion.app.kms.manager.param.KnowledgeParam;

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
	 * @param page 第几页 从1开始计数
	 * @return 查询到的数据 PARA_IS_ERROR, NO_RIGHT
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/title/{id}")
	public ResultBean getTitleList(@PathParam("id") long id, 
									@QueryParam("rows") long rows,
									@QueryParam("page") long page,
									@CookieParam("username") String username)
	{
		ResultBean result = new ResultBean(ErrorCode.OK);
		
		// 检测权限
		// 取出用户ID
		long userId = UserData.getBindId(username);
		if( id != 0 && subjectMapper.checkSubjectOwner(id, userId) == 0)
		{
			result.setRetcode(ErrorCode.NO_RIGHT);
			return result;
		}		
		
		List<TitleBean> titleList = new ArrayList<TitleBean>();
		long titleCount = 0;
				
		if(id == 0)
		{
			titleList = titleMapper.getAllTitleList( (page - 1) * rows, rows, userId);
			titleCount = titleMapper.getAllTitleListCount(userId);
		}
		else
		{
			titleList = titleMapper.getTitleList( (page - 1) * rows, rows, id, userId);
			titleCount = titleMapper.getTitleListCount(id, userId);
		}
		
		// 返回的实际数据
		result.addEntry("rows", titleList);
		// 查询的总页数
		result.addEntry("total", (long) ((titleCount + rows -1 )/ rows) );
		// 当前页数
		result.addEntry("page", page);
		// 所有记录条数
		result.addEntry("records", titleCount);

		
		return result;
	}
	
	/**
	 * 新增知识到数据库
	 * @param knowledge 知识内容
	 * @param username 用户名称
	 * @return 结果信息
	 * 
	 * TODO: 此处涉及到事务处理的过程
	 * @throws IOException 
	 * @throws SolrServerException 
	 */
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public ResultBean createKnowledge(KnowledgeParam knowledge,
									   @CookieParam("username") String username) throws SolrServerException, IOException
	{
		ResultBean result = new ResultBean(ErrorCode.OK);
		
		// 检查用户是否有权限操作此类别
		long userId = UserData.getBindId(username);
		
		if (subjectMapper.checkSubjectOwner(knowledge.getAddSubjectId(), userId) == 0)
		{
			result.setRetcode(ErrorCode.NO_RIGHT);
			return result;
		}
		
		// 正文内容进入数据库
		KnowledgeBean knowledgeBean = new KnowledgeBean();
		knowledgeBean.setContent(knowledge.getKnowledgeContent());
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
		titleBean.setName(knowledge.getKnowledgeTitle());
		titleBean.setIndexStatus(TitleBean.INDEX_NOT_ADD); // not index
		titleBean.setDeleteFlag(0); // not deleted
		titleBean.setUpdateDate(new Date());
		titleBean.setKnowledgeId(knowledgeBean.getId());
		titleBean.setSubjectId(knowledge.getAddSubjectId());
		titleBean.setUserId(userId);
		
		// 放置进入缓存中
		//SolrOperator.addTitle(titleBean);
		
		if ( 0 == titleMapper.createTitile(titleBean))
		{
			result.setRetcode(ErrorCode.DB_FAIL);
			return result;
		}
		
		// other title
		for(String otherTitle : knowledge.getOtherKnowledgeTitleList())
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
			otherTitleBean.setSubjectId(knowledge.getAddSubjectId());
			otherTitleBean.setUserId(userId);
			
			if ( 0 == titleMapper.createTitile(otherTitleBean))
			{
				result.setRetcode(ErrorCode.DB_FAIL);
				return result;
			}
			
			//SolrOperator.addTitle(otherTitleBean);
		}
		
		return result;
	}
}
