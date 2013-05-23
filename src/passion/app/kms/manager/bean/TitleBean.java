package passion.app.kms.manager.bean;

import java.util.Date;

import org.apache.solr.client.solrj.beans.Field;

/**
 * 知识标题
 * @author gouchy
 *
 */
public class TitleBean {

	/**
	 * 数据库中的编号
	 */
	@Field
	private int id;
	
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
	private int knowledgeId;
	
	/**
	 * 所属分类的ID
	 */
	private int subjectId;
	
	/**
	 * 这个标题所属的用户
	 */
	private int userId;
	
	/**
	 * 更新的时间
	 */
	private Date updateDate;

	public int getId() {
		return id;
	}

	public void setId(int id) {
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

	public int getKnowledgeId() {
		return knowledgeId;
	}

	public void setKnowledgeId(int knowledgeId) {
		this.knowledgeId = knowledgeId;
	}

	public int getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(int subjectId) {
		this.subjectId = subjectId;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}
}
