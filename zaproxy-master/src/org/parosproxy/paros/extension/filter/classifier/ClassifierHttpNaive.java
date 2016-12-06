// TODO "resources/contentFormat.txt" should be passed as argument to increase flexibility.
// TODO the result of getClassification should contain the words that are common in the set,
// this is the explanation taken form the assignment:
// "Now take a page from a forum of Trump supporters, which contains the strings ’Trump’, 
// ’Nigger’, ’deport Mexicans’. Based on our common-sense content filter, this page scores a 
// 24, which is well above our threshold of 10. This content is considered inappropriate, and 
// the placeholder will tell the user it’s because of racist and fearmongering content."


package org.parosproxy.paros.extension.filter.classifier;

import java.util.ArrayList;

import org.javatuples.Triplet;
import org.parosproxy.paros.extension.filter.formatter.InappropriateElement;
import org.parosproxy.paros.extension.filter.formatter.FormatFileToFilterInfo;
import org.parosproxy.paros.network.HttpMessage;

public class ClassifierHttpNaive extends
		ClassifierAbstract<HttpMessage, String> {

	@Override
	public String getClassification(HttpMessage msg) {
		
		String result = "";
		int counter = 0;
		
		FormatFileToFilterInfo filterInfoParser = new FormatFileToFilterInfo("resources/contentFormat.txt");
		
		ArrayList<InappropriateElement<String>> inappropriate_tags = filterInfoParser.getFormat().getValue();
		
		if (msg.getResponseHeader().isImage()) {
			return result;
		}
		
//		if (!msg.getResponseHeader().isEmpty() && msg.getResponseBody().length() > 0){
//			String BodyAsString = msg.getResponseBody().toString();
//			for (InappropriateElement<String> inapEl : inappropriate_tags) {
//				if(BodyAsString.contains(tag.getValue0()) && ! tag.getValue0().equalsIgnoreCase("threshold")){
//					counter += Integer.parseInt(tag.getValue1());
//					result += tag.getValue2();
//				}
//			}
//		}
		
		// Inclusive threshold
//		if(counter <= Integer.parseInt(inappropriate_tags.get(0).getValue1())){
//			result = "";
//		}
		
		return result;

	}
}
