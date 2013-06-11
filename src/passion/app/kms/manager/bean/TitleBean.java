package passion.app.kms.manager.bean;

import java.util.Date;

import org.apache.solr.client.solrj.beans.Field;

/**
 * 知识标题
 * @author gouchy
 *
 */
public class TitleBean {
		
	public final static int TYPE_TITLE_MAIN= 1;
	public final static int TYPE_TITLE_OTHER = 2;
	
	/**
	 * 已被索引
	 */
	public final static int INDEX_STATUS_OK = 0;
	
	/**
	 * 未被索引 
	 */
	public final static int NOT_INDEX = 1;
	
	/**
	 * 已被索引，但索引后内容有更新
	 */
	public final static int INDEX_BUT_UPDATE = 2;
	
	/**
	 * 已被索引，但索引后内容已被设置删除标记
	 */
	public final static int INDEX_BUT_DELETE = 3;

	/**
	 * 数据库中的编号
	 */
	@Field
	private long id;
	
	/**
	 * 标题类型，1为主标题，2为副标题
	 */
	private int type;
	
	/**
	 * 标题名称
	 */
	@Field
	private String name;
	
	/**
	 * 标题所对应的的知识ID
	 */
	@Field
	private long knowledgeId;
	
	/**
	 * 所属分类的ID
	 */
	private long subjectId;
	
	/**
	 * 这个标题所属的用户
	 */
	private long userId;
	
	/**
	 * 更新的时间
	 */
	private Date updateDate;
	
	/**
	 * 删除标记 0：未删除 1：已删除
	 */
	private int deleteFlag;
	
	/**
	 * 索引状态
	 * 0：成功索引，1：新增未索引 2：更新未索引，3：已标记删除，但未从索引中删除，4：已删除
	 */
	private int indexStatus;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getKnowledgeId() {
		return knowledgeId;
	}

	public void setKnowledgeId(long knowledgeId) {
		this.knowledgeId = knowledgeId;
	}

	public long getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(long subjectId) {
		this.subjectId = subjectId;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public int getDeleteFlag()
	{
		return deleteFlag;
	}

	public void setDeleteFlag(int deleteFlag)
	{
		this.deleteFlag = deleteFlag;
	}

	public int getIndexStatus()
	{
		return indexStatus;
	}

	public void setIndexStatus(int indexStatus)
	{
		this.indexStatus = indexStatus;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}
}
