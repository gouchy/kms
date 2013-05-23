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
	private int id;
	
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
	private int userId;
	
	/**
	 * 上一级分类的ID，-1代表为主分类
	 */
	private int parentSubject;

	public int getId() {
		return id;
	}

	public void setId(int id) {
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

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getParentSubject() {
		return parentSubject;
	}

	public void setParentSubject(int parentSubject) {
		this.parentSubject = parentSubject;
	}
}
