package org.parosproxy.paros.extension.filter.algorithm;

import java.util.ArrayList;

import org.parosproxy.paros.extension.filter.formatter.InappropriateElement;
import org.parosproxy.paros.extension.filter.content.PageStringContent;
import org.parosproxy.paros.extension.filter.formatter.FormatFileToFilterInfo;
import org.parosproxy.paros.network.HttpMessage;

import javafx.util.Pair;

public class StringFilterApplyer extends
FilterApplyer<String> {
	
	public String executeFiltering(HttpMessage httpMessage, String formatFileUrl) {
		PageStringContent pageContent = new PageStringContent(httpMessage);
		String content = pageContent.getContent();
		FormatFileToFilterInfo filterInfoParser = new FormatFileToFilterInfo(formatFileUrl);
		Pair<Integer, ArrayList<InappropriateElement<String>>> parsedFormatFile = filterInfoParser.getFilterParameters();
		return applyBasicFilter(content, parsedFormatFile);
	}

	@Override
	public String applyBasicFilter(String content, Pair<Integer, ArrayList<InappropriateElement<String>>> parsedFormatFile) {

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
