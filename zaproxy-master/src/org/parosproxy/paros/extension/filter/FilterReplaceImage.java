package org.parosproxy.paros.extension.filter;

import org.parosproxy.paros.Constant;
import org.parosproxy.paros.network.HttpMessage;

public class FilterReplaceImage extends FilterAdaptor {

	@Override
	public int getId() {
		return 2345678;
	}

	@Override
	public String getName() {
		// TODO We should use the Constant.messages.getString() method to be coherent with zaproxy
		return "Filter Image";
	}

	@Override
	public void onHttpRequestSend(HttpMessage httpMessage) {
		//Array 
		// TODO Auto-generated method stub

	}

	@Override
	public void onHttpResponseReceive(HttpMessage httpMessage) {
		// TODO Auto-generated method stub

	}

}
