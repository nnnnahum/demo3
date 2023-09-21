package entities.requests;

import java.util.HashMap;

public class Params extends HashMap<String, String> {
	
	private static final long serialVersionUID = 1L;
	
	public static final String QUERY = "query";
	public static final String SORT = "sort";
	public static final String PAGE = "page";
	public static final String PAGE_SIZE = "pageSize";
	
	public Params(String query, String sort, String page, String pageSize) {
		setQuery(query);
		setSort(sort);
		setPage(page);
		setPageSize(pageSize);
	}

	
	public Params() {
	}


	public void setQuery(String query) {
		this.put(QUERY, query);
	}
	
	public String getQuery() {
		return this.get(QUERY);
	}
	
	public void setSort(String sort) {
		this.put(SORT, sort);
	}
	
	public String getSort() {
		return this.get(SORT);
	}
	
	public void setPage(String page) {
		this.put(PAGE, page);
	}
	
	public String getPage() {
		return this.get(PAGE);
	}
	
	public void setPageSize(String pageSize) {
		this.put(PAGE_SIZE, pageSize);
	}
	
	public String getPageSize() {
		return this.get(PAGE_SIZE);
	}
}
