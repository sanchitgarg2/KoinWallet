package domain;

import org.json.simple.JSONObject;

public class JSONResponseBody extends JSONObject{

	public JSONResponseBody() {
		super();
		this.put(APIStatusCodes.STATUS_CODE_KEY, null);
		this.put(APIStatusCodes.STATUS_DESC_KEY, null);
	}
	

}
