
public class RateInfo {
	private long user;
	private long item;
	private int rating;
	
	public RateInfo(long user, long item) {
		this.user = user;
		this.item = item;
	}
	
	public RateInfo(long user, long item, int rating) {
		this(user, item);
		this.rating = rating;
	}
	
	public long getUser() {
		return user;
	}
	
	public long getItem() {
		return item;
	}
	
	public int getRating() {
		return rating;
	}
}
