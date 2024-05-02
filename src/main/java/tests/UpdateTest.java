/**
 * 
 */
package tests;

/**
 * 
 */
public class UpdateTest {
	private final String 
		BASE_URL = "http://localhost:6900/";

	/**
	 * 
	 */
	public UpdateTest() {
		StringBuilder buf = new StringBuilder();
		buf.append("{\"verb\":\"update\","); // the verb
		buf.append("\"cargo\":[[\"foo\",\"7\"],[\"bar\",\"5\"]],"); // the field
		buf.append("\"clientId\":\"changeme\"}");
		String query = buf.toString();
		System.out.println("Q: "+query);
		String result = GetQuery.getQuery(query, BASE_URL);
		System.out.println("DID "+result);
	}
// after 3 tests: {"bar":15,"foo":21}

}
