package org.parosproxy.paros.extension.filter.formatter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.util.Pair;

public class FormatFileToFilterInfo { 

	private final String url;

	public FormatFileToFilterInfo(String formatFileURL) {
		this.url = formatFileURL;
	}
	
	public Pair<Integer,ArrayList<InappropriateElement<String>>> getFilterParameters() {
		
		ArrayList<InappropriateElement<String>> inappropriateElements = new ArrayList<InappropriateElement<String>>();
		int threshold = Integer.MAX_VALUE;
		// Assuming that the threshold is always the first element and there is no errors in the format..
		try {
			FileReader fr = new FileReader(url);
			BufferedReader textReader = new BufferedReader(fr);
			while(textReader.ready()){
				String line = textReader.readLine();
				line = line.trim();
				if(!line.startsWith("#")){
					if(line.startsWith("threshold"))
						threshold = Integer.parseInt(line.split(" ")[1]);
					else {
						String[] components = line.split(";");
						inappropriateElements.add(new InappropriateElement<String>(components[0], Integer.parseInt(components[1]), argumentationToList(components[2])));
					}
				}
			}
			textReader.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new Pair<Integer,ArrayList<InappropriateElement<String>>>(threshold, inappropriateElements);
	}
	
	private List<String> argumentationToList(String tags) {
		String[] splitTags = tags.split(",");
		return Arrays.asList(splitTags);
	}
}
