package com.ursa.tools.amazon.payload;

public class ListRequest {
	private Integer page;
	private Integer size;
	private String sortBy = "createdDate";
	private Boolean sortDesc = true;	
	private String search;
	
	public Integer getPage() {
		if(page == null)
			page = 1;
		return page;
	}
	
	public void setPage(Integer page) {
		this.page = page;
	}

	public Integer getSize() {
		if(size == null)
			size = 10;
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public String getSortBy() {
		if(sortBy == null || "".equals(sortBy)) 
			sortBy = "createdDate";
		return sortBy;
	}

	public void setSortBy(String sortBy) {
		this.sortBy = sortBy;
	}

	public Boolean getSortDesc() {
		if(sortDesc == null)
			sortDesc = true;
		return sortDesc;
	}

	public void setSortDesc(Boolean sortDesc) {
		this.sortDesc = sortDesc;
	}

	public String getSearch() {
		return search;
	}

	public void setSearch(String search) {
		this.search = search;
	}
	
}
