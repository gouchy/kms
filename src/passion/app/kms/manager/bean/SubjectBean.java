package passion.app.kms.manager.bean;

/**
 * 知识分类
 * @author gouchy
 *
 */
public class SubjectBean {

	/**
	 * 数据库中的ID
	 */
	private long id;
	
	/**
	 * 分类名称
	 */
	private String name;
	
	/**
	 * 分类类型； 1为推荐类，2为非推荐类
	 */
	private int type;
	
	/**
	 * 分类所属用户的ID
	 */
	private long userId;
	
	/**
	 * 上一级分类的ID，-1代表为主分类
	 */
	private long parentSubject;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public long getParentSubject() {
		return parentSubject;
	}

	public void setParentSubject(long parentSubject) {
		this.parentSubject = parentSubject;
	}
}
