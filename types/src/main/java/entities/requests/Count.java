package entities.requests;

public class Count {

	private long results;
	private long totalCount;
	private long page;
	private long pageSize;
	
	public Count() {}
	public Count(long results, long totalCount, long page, long pageSize) {
		this.results = results;
		this.totalCount = totalCount;
		this.page = page;
		this.pageSize = pageSize;
	}
	
	public Count(long results, long totalCount) {
		this.results = results;
		this.totalCount = totalCount;
		this.page = 1l;
		this.pageSize = 10l;
	}
	
	
	public long getResults() {
		return results;
	}
	public void setResults(long results) {
		this.results = results;
	}
	public long getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}
	public long getPage() {
		return page;
	}
	public void setPage(long page) {
		this.page = page;
	}
	public long getPageSize() {
		return pageSize;
	}
	public void setPageSize(long pageSize) {
		this.pageSize = pageSize;
	}
}
