package passion.app.kms.manager.util;

import java.util.List;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import passion.app.kms.manager.bean.TitleBean;
import passion.app.kms.manager.dao.TitleMapper;

public class IndexTimer extends TimerTask
{
	private Logger log = LoggerFactory.getLogger(IndexTimer.class);
	
	@Autowired
	private TitleMapper titleMapper;

	@Override
	public void run()
	{
		try
		{
			doBusiness();
		}
		catch(Exception e)
		{
			log.error(e.getMessage());
		}

	}
	
	public void doBusiness()
	{
		/**
		 * 获取 指定条数的需要索引的标题数据
		 */
		List<TitleBean> titleList = titleMapper.getNeedIndexTitle();
		
		for(TitleBean title : titleList)
		{
			// 是否有删除标记
			if(title.getDeleteFlag() == 1)
			{
				indexDeleteTitle(title);
			}
			else // 更新或者新增
			{
				indexTitle(title);
			}
		}
		SolrOperator.commit();		
	}
	
	public boolean indexDeleteTitle(TitleBean title)
	{
		boolean result = true;
		// 检查索引中是否有该条记录
		if(SolrOperator.isTitleExist(title.getId()))
		{
			result = SolrOperator.deleteTitle(title.getId());
		}
		
		if(result)
		{
			titleMapper.updateTitleIndexStatus(title.getId(), TitleBean.INDEX_STATUS_OK);
		}
		return result;
	}
	
	public boolean indexTitle(TitleBean title)
	{	
		boolean result = false;
		if(SolrOperator.isTitleExist(title.getId()))
		{
			result = SolrOperator.updateTitle(title);
		}
		else
		{
			result = SolrOperator.addTitle(title);
		}
		
		if(result)
		{
			titleMapper.updateTitleIndexStatus(title.getId(), TitleBean.INDEX_STATUS_OK);
		}
		return result;
	}

}
