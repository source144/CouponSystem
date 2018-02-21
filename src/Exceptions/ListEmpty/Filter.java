package Exceptions.ListEmpty;

/**
 * 
 * @author Gonen
 * @deprecated
 *
 */
public final class Filter {

	public static final Filter ALL = new Filter("ALL");
	public static final Filter UNSPECIFIED = new Filter("UNSPECIFIED");
	
	private final String filter;

	public Filter(String filter) {
		this.filter = filter;
	}

	@Override
	public String toString() {
		return this.filter;
	}
}
