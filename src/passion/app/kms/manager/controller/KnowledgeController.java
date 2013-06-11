package passion.app.kms.manager.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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
	@Path("/subject/{id}")
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
		// titleBean.setIndexStatus(TitleBean.INDEX_NOT_ADD); // not index
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
			//otherTitleBean.setIndexStatus(TitleBean.INDEX_NOT_ADD); // not index
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
	
	/**
	 * 根据主标题的ID删除整条知识
	 * 注：知识的删除，并不是正式的删除，有很多仅仅是标记内容
	 * @param id 主标题的id
	 * @param username 当前用户登录的名称
	 * @return
	 */
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/title/{id}")
	public ResultBean deleteKnowledgeByTitleId(@PathParam("id") long id,@CookieParam("username") String username)
	{
		ResultBean result = new ResultBean(ErrorCode.OK);
		
		// 检查当前主标题是否归属于该用户
		long userId = UserData.getBindId(username);
		if (titleMapper.checkTitleOwner(id, userId) == 0)
		{
			result.setRetcode(ErrorCode.NO_RIGHT);
			return result;
		}

		// 置知识内容为无效
		TitleBean title = titleMapper.readTitleById(id);
		knowledgeMapper.updateKnowledgeDeleteFlag(title.getKnowledgeId());
		
		// 置主标题为无效
		titleMapper.updateTitleDeleteFlag(id);
		
		// 取出副标题，并且将这些副标题置为无效
		titleMapper.updateOtherTitleDeleteFlagByKnowledgeId(title.getKnowledgeId());
		
		return result;
	}
	
	/**
	 * 根据知识的编号ID，获取知识的相关内容信息
	 * @param id 知识id
	 * @param username 登录用户名
	 * @return 知识内容
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{id}")
	public ResultBean getKnowledgeById(@PathParam("id") long id, @CookieParam("username") String username)
	{
		ResultBean result = new ResultBean(ErrorCode.OK);
		
		long userId = UserData.getBindId(username);
		if (knowledgeMapper.checkKnowledgeOwer(id, userId) == 0)
		{
			result.setRetcode(ErrorCode.NO_RIGHT);
			return result;
		}
		
		KnowledgeBean knowledge = knowledgeMapper.readKnowledgeById(id);		
		result.addEntry("knowledge", knowledge);
		
		return result;
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/title/{id}")
	public ResultBean getKnowledgeByTitleId(@PathParam("id") long titleId, @CookieParam("username") String username)
	{
		ResultBean result = new ResultBean(ErrorCode.OK);
		
		long userId = UserData.getBindId(username);
		if (titleMapper.checkTitleOwner(titleId, userId) == 0)
		{
			result.setRetcode(ErrorCode.NO_RIGHT);
			return result;
		}
		
		TitleBean mainTitle = titleMapper.readTitleById(titleId);
		if(mainTitle == null)
		{
			result.setRetcode(ErrorCode.PARA_IS_ERROR);
			return result;
		}
		result.addEntry("mainTitle", mainTitle);
		
		List<TitleBean> otherTitleList = titleMapper.getOtherTitleByKnowledgeId(mainTitle.getKnowledgeId());
		result.addEntry("otherTitle", otherTitleList);		
		
		KnowledgeBean knowledge = knowledgeMapper.readKnowledgeById(mainTitle.getKnowledgeId());		
		result.addEntry("knowledge", knowledge);
		
		return result;
	}
	
	/**
	 * 更新知识的信息
	 * @param knowledgeId 知识的ID
	 * @param knowledge 知识的内容信息
	 * @param username 操作者的用户名
	 * @return 操作结果
	 */
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/title/{id}")
	public ResultBean updateKnowledge(@PathParam("id") long titleId, 
									   KnowledgeParam knowledge, 
									   @CookieParam("username") String username)
	{
		ResultBean result = new ResultBean(ErrorCode.OK);
		
		// 检查当前用户是否有操作这条记录的权限
		long userId = UserData.getBindId(username);
		if(titleMapper.checkTitleOwner(titleId, userId) == 0)
		{
			result.setRetcode(ErrorCode.NO_RIGHT);
			return result;
		}
		
		// 从数据库中查询到这个记录的结构
		TitleBean dbMainTitle = titleMapper.readTitleById(titleId);
		KnowledgeBean dbKnowledge = knowledgeMapper.readKnowledgeById(dbMainTitle.getKnowledgeId());
		//List<TitleBean> dbOtherTitleList = titleMapper.getOtherTitleByKnowledgeId(knowledgeId);
		
		// 更新相关字段
		dbKnowledge.setContent(knowledge.getKnowledgeContent());		
		knowledgeMapper.updateKnowledge(dbKnowledge);
		
		dbMainTitle.setName(knowledge.getKnowledgeTitle());
		dbMainTitle.setUpdateDate(new Date());
		titleMapper.updateTitle(dbMainTitle);
		
		titleMapper.updateOtherTitleDeleteFlagByKnowledgeId(dbMainTitle.getKnowledgeId());
		
		// other title
		for (String otherTitle : knowledge.getOtherKnowledgeTitleList())
		{
			// Ignore null string
			if (otherTitle == null || otherTitle.trim().equals(""))
			{
				continue;
			}
			TitleBean otherTitleBean = new TitleBean();
			otherTitleBean.setType(TitleBean.TYPE_TITLE_OTHER); // main topic
			otherTitleBean.setName(otherTitle);
			// otherTitleBean.setIndexStatus(TitleBean.INDEX_NOT_ADD); // not
			// index
			otherTitleBean.setDeleteFlag(0); // not deleted
			otherTitleBean.setUpdateDate(new Date());
			otherTitleBean.setKnowledgeId(dbMainTitle.getKnowledgeId());
			otherTitleBean.setSubjectId(dbMainTitle.getSubjectId());
			otherTitleBean.setUserId(userId);

			if (0 == titleMapper.createTitile(otherTitleBean))
			{
				result.setRetcode(ErrorCode.DB_FAIL);
				return result;
			}

		}
		return result;
	}
}
