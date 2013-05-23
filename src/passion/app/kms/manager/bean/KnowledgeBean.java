package passion.app.kms.manager.bean;

/**
 * 知识内容
 * @author gouchy
 *
 */
public class KnowledgeBean {
	
	/**
	 * 数据库中的ID
	 */
	private long id;
	
	/**
	 * 知识格式类型，1为TEXT
	 */
	private int type;
	
	/**
	 * 知识内容
	 */
	private String content;

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

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
