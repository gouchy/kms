package passion.app.kms.manager.param;

import java.util.List;

/**
 * 传递参数
 * @author gouchy
 *
 */
public class KnowledgeParam
{
	private String knowledgeTitle;
	private String knowledgeContent;
	private long addSubjectId;
	private List<String> otherKnowledgeTitleList;
	
	public String getKnowledgeTitle()
	{
		return knowledgeTitle;
	}
	public void setKnowledgeTitle(String knowledgeTitle)
	{
		this.knowledgeTitle = knowledgeTitle;
	}
	public String getKnowledgeContent()
	{
		return knowledgeContent;
	}
	public void setKnowledgeContent(String knowledgeContent)
	{
		this.knowledgeContent = knowledgeContent;
	}
	public long getAddSubjectId()
	{
		return addSubjectId;
	}
	public void setAddSubjectId(long addSubjectId)
	{
		this.addSubjectId = addSubjectId;
	}
	public List<String> getOtherKnowledgeTitleList()
	{
		return otherKnowledgeTitleList;
	}
	public void setOtherKnowledgeTitleList(List<String> otherKnowledgeTitleList)
	{
		this.otherKnowledgeTitleList = otherKnowledgeTitleList;
	}
	
}
