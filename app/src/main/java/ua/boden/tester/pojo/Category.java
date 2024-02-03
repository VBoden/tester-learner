package ua.boden.tester.pojo;

import java.util.Objects;

public class Category {

	private int id;
	
	private String name;

	private Category supperCategory;
	
//	private List<Category>

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

	public Category getSupperCategory() {
		return supperCategory;
	}

	public void setSupperCategory(Category supperCategory) {
		this.supperCategory = supperCategory;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Category other = (Category) obj;
		return id == other.id;
	}

}
