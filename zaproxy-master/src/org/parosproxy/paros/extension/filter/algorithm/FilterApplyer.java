// TODO "resources/contentFormat.txt" should be passed as argument to increase flexibility.
// TODO the result of getClassification should contain the words that are common in the set,
// this is the explanation taken form the assignment:
// "Now take a page from a forum of Trump supporters, which contains the strings ’Trump’, 
// ’Nigger’, ’deport Mexicans’. Based on our common-sense content filter, this page scores a 
// 24, which is well above our threshold of 10. This content is considered inappropriate, and 
// the placeholder will tell the user it’s because of racist and fearmongering content."


package org.parosproxy.paros.extension.filter.algorithm;

import java.util.ArrayList;

import org.javatuples.Triplet;
import org.parosproxy.paros.extension.filter.formatter.InappropriateElement;
import org.parosproxy.paros.extension.filter.formatter.FormatFileToFilterInfo;
import org.parosproxy.paros.network.HttpMessage;
import org.parosproxy.paros.network.HttpResponseHeader;
import org.zaproxy.zap.network.HttpResponseBody;

import javafx.util.Pair;

public class FilterApplyer extends
ClassifierAbstract<String> {

	@Override
	public String applyBasicStringFilter(String content, Pair<Integer, ArrayList<InappropriateElement<String>>> parsedFormatFile) {

		String resultDescription = "";
		int totalWeight = 0;

		ArrayList<InappropriateElement<String>> inappropriate_tags = parsedFormatFile.getValue();

		for (InappropriateElement<String> inapElement : inappropriate_tags) {
			if(content.contains(inapElement.getInappropriateContent())){
				totalWeight += inapElement.getWeight();
				resultDescription += inapElement.getInappropriateContent() + " due to: ";
				for(String tag : inapElement.getTags()) {
					resultDescription += " " + tag;
				}
				resultDescription += "\n";
			}
		}

		// Checking threshold
		if(totalWeight < parsedFormatFile.getKey()){
			return content;
		} else {
			return "PAGE WAS BLOCKED - The following tags were deemed inappropriate: \n" +resultDescription;
		}
	}
}
