package passion.app.kms.base.operator;

import java.io.IOException;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.impl.ConcurrentUpdateSolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import passion.app.kms.base.config.ConfigList;
import passion.app.kms.base.config.ConfigProperties;
import passion.app.kms.manager.bean.TitleBean;

/**
 * Solr操作类
 * @author gouchy
 *
 */
public class SolrOperator
{
	private static Logger log = LoggerFactory.getLogger(SolrOperator.class);
	
	/*
	 * 查询用Solr对象
	 */
	private static SolrServer querySolrServer = null;
	
	/**
	 * 更新用Solr对象
	 */
	private static SolrServer updateSolrServer = null;
	
	/**
	 * 获取查询使用的solr服务器对象
	 * @return
	 */
	private synchronized static  SolrServer getQuerySolrServer()
	{
		if(querySolrServer == null)
		{
			querySolrServer = new HttpSolrServer(ConfigProperties.getKey(ConfigList.BASIC, "SOLR_ADDRESS"));
		}
		return querySolrServer;
	}
	
	/**
	 * 获取能够并发插入的solr服务器对象
	 * @return
	 */
	private synchronized static SolrServer getUpdateSolrServer()
	{
		if(updateSolrServer == null)
		{
			// 第二个参数是发送给服务器前的缓存大小
			// 第三个参数是后台服务线程数量
			updateSolrServer = new ConcurrentUpdateSolrServer(ConfigProperties.getKey(ConfigList.BASIC, "SOLR_ADDRESS"), 10, 2);
		}
		return updateSolrServer;
	}
	
	/**
	 * 用于增加内容至索引中
	 * @param title 新增的知识标题
	 * @return 新增成功或者失败
	 * @throws IOException 
	 * @throws SolrServerException 
	 */
	public static boolean addTitle(TitleBean title)
	{
		SolrInputDocument document = new SolrInputDocument();
		document.addField("id", title.getId());
		document.addField("user_id", title.getUserId());
		document.addField("knowledge_id", title.getKnowledgeId());
		document.addField("title", title.getName());
		
		try
		{
			getUpdateSolrServer().add(document);

		} catch (SolrServerException | IOException e)
		{
			log.error(e.getMessage());
			return false;
		}
		
		return true;
	}
	
	/**
	 * 删除索引中的特定内容
	 * @param title
	 * @return  删除成功或者失败
	 */
	public static boolean deleteTitle(long title)
	{
		try
		{
			getUpdateSolrServer().deleteByQuery("id:" + Long.toString(title));

		} catch (SolrServerException | IOException e)
		{
			log.error(e.getMessage());
			return false;
		}
		return true;
	}
	
	/**
	 * 更新索引中的记录信息
	 * @param title 更新的内容
	 * @return 更新成功或者失败
	 */
	public static boolean updateTitle(TitleBean title)
	{
		SolrInputDocument document = new SolrInputDocument();
		document.addField("id", title.getId());
		document.addField("user_id", title.getUserId());
		document.addField("knowledge_id", title.getKnowledgeId());
		document.addField("title", title.getName());
		
		try
		{
			getUpdateSolrServer().add(document);
		} catch (SolrServerException | IOException e)
		{
			log.error(e.getMessage());
			return false;
		}
		
		return true;
	}
	
	/**
	 * 检查指定的标题是否在索引中存在
	 * @param titleId 知识标题ID
	 * @return 存在否？
	 */
	public static boolean isTitleExist(long titleId)
	{
		SolrQuery query = new SolrQuery();
		query.setQuery("id:" + Long.toString(titleId));
		QueryResponse response;
		try
		{
			response = getQuerySolrServer().query(query);
		} catch (SolrServerException e)
		{
			log.error(e.getMessage());
			return false;
		}
		SolrDocumentList docs = response.getResults();
		if(docs == null || docs.size() == 0)
		{
			return false;
		}
		
		return true;
	}
	
	public static long queryKnowledge(long userId, String question)
	{
		SolrQuery query = new SolrQuery();
		question.replace("\"", "");
		query.setQuery("title:" + question + " AND user_id:" + Long.toString(userId));
		query.setFields("score,knowledge_id,title");
		query.setSort("score", ORDER.desc);
		query.setStart(0);
		query.setRows(2);
		
		QueryResponse response;
		try
		{
			response = getQuerySolrServer().query(query);
		}
		catch(SolrServerException e)
		{
			log.error(e.getMessage());
			return 0;
		}
		SolrDocumentList docs = response.getResults();
		if(docs == null || docs.size() == 0)
		{
			return 0;
		}
		
		Long knowledgeId = (Long) docs.get(0).getFieldValue("knowledge_id");
		
		if(knowledgeId == null)
		{
			return 0;
		}
		return knowledgeId;
	}
	
	public static boolean commit()
	{
		try
		{
			getUpdateSolrServer().commit();
		} catch (SolrServerException | IOException e)
		{
			log.error(e.getMessage());
			return false;
		}
		return true;
	}
}
