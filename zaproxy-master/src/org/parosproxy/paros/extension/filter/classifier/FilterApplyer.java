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
import org.parosproxy.paros.network.HttpResponseHeader;
import org.zaproxy.zap.network.HttpResponseBody;

import javafx.util.Pair;

public class FilterApplyer extends
		ClassifierAbstract<HttpMessage, String> {

	@Override
	public String getClassification(HttpMessage msg) {
		
		String result = "";
		int totalWeight = 0;
		
		FormatFileToFilterInfo filterInfoParser = new FormatFileToFilterInfo("resources/contentFormat.txt");
		Pair<Integer, ArrayList<InappropriateElement<String>>> parsedFormatFile = filterInfoParser.getFormat();
		
		ArrayList<InappropriateElement<String>> inappropriate_tags = parsedFormatFile.getValue();
		
		HttpResponseHeader header = msg.getResponseHeader();
		HttpResponseBody body = msg.getResponseBody();
		
		if (header.isEmpty()) {
			return result;
		}
		
		if (msg.getResponseHeader().isImage()) {
			return result;
		}
		
		if (body.length() > 0){
			String BodyAsString = body.toString();
			for (InappropriateElement<String> inapElement : inappropriate_tags) {
				if(BodyAsString.contains(inapElement.getInappropriateContent())){
					totalWeight += inapElement.getWeight();
					result += inapElement.getInappropriateContent();
					for(String tag : inapElement.getTags()) {
						result = result + " " + tag;
					}
				}
			}
		}
		
		// Checking threshold
		if(totalWeight <= parsedFormatFile.getKey()){
			result = "";
		}
		
		return result;

	}
}
