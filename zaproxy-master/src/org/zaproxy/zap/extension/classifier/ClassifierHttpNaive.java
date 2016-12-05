// TODO "resources/contentFormat.txt" should be passed as argument to increase flexibility.

package org.zaproxy.zap.extension.classifier;

import java.util.ArrayList;

import org.javatuples.Triplet;
import org.parosproxy.paros.network.HttpMessage;
import org.zaproxy.zap.extension.formatter.FormatterFile;

public class ClassifierHttpNaive extends
		ClassifierAbstract<HttpMessage, String> {

	@Override
	public String getClassification(HttpMessage msg) {
		
		String result = "";
		int counter = 0;
		
		ArrayList<Triplet<String, String, String>> inappropriate_tags = (new FormatterFile()).getFormat("resources/contentFormat.txt");
	
		if (!msg.getResponseHeader().isEmpty() && !msg.getResponseHeader().isImage() && msg.getResponseBody().length() > 0){
			String BodyInString = msg.getResponseBody().toString();
			for (Triplet<String, String, String> tag : inappropriate_tags) {
				if(BodyInString.contains(tag.getValue0()) && ! tag.getValue0().equalsIgnoreCase("threshold")){
					counter += Integer.parseInt(tag.getValue1());
					result += tag.getValue2();
				}
			}
		}
		
		// Inclusive threshold
		if(counter <= Integer.parseInt(inappropriate_tags.get(0).getValue1())){
			result = "";
		}
		
		return result;

	}
}
