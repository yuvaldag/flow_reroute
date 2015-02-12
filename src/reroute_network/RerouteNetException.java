package reroute_network;

public abstract class RerouteNetException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8936997552297400149L;

	RerouteNetException(String errMsg) {
		super(errMsg);
	}
	
	RerouteNetException() {
	}
}