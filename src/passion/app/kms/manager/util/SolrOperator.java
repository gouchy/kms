package passion.app.kms.manager.util;

import java.io.IOException;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.ConcurrentUpdateSolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;

import passion.app.kms.base.BaseConfig;
import passion.app.kms.manager.bean.TitleBean;

/**
 * Solr操作类
 * @author gouchy
 *
 */
public class SolrOperator
{
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
			querySolrServer = new HttpSolrServer(BaseConfig.SOLR_ADDRESS);
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
			updateSolrServer = new ConcurrentUpdateSolrServer(BaseConfig.SOLR_ADDRESS, 10, 2);
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
	public static boolean addTitle(TitleBean title) throws SolrServerException, IOException
	{
		SolrInputDocument document = new SolrInputDocument();
		document.addField("id", title.getId());
		document.addField("knowledge_id", title.getKnowledgeId());
		document.addField("title", title.getName());
		
		getUpdateSolrServer().add(document);
		getUpdateSolrServer().commit();
		return true;
	}
	
	/**
	 * 删除索引中的特定内容
	 * @param title
	 * @return  删除成功或者失败
	 */
	public static boolean deleteTitle(long title)
	{
		
		return true;
	}
	
	/**
	 * 更新索引中的记录信息
	 * @param title 更新的内容
	 * @return 更新成功或者失败
	 */
	public static boolean updateTitle(TitleBean title)
	{
		return true;
	}
}
