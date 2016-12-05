// TODO The solution should include a well-designed parser for this format.
// 	- TODO The parser should ignore malformed lines.
//  - TODO The parser should ignore blank lines.

package org.zaproxy.zap.extension.formatter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.javatuples.Triplet;

public class FormatterFile extends FormatterAbstractReplace<String, ArrayList<Triplet<String,String,String>> > {

	@Override
	public ArrayList<Triplet<String,String,String>> getFormat(String src) {
		
		ArrayList<Triplet<String,String,String>> array = new ArrayList<Triplet<String,String,String>>();
		// Assuming that the threshold is always the first element and there is no errors in the format..
		try {
			FileReader fr = new FileReader(src);
			BufferedReader textReader = new BufferedReader(fr);
			while(textReader.ready()){
				String line = textReader.readLine();
				line = line.trim();
				if(!line.startsWith("#")){
					if(line.startsWith("threshold"))
						array.add(new Triplet<String,String,String>("threshold", line.substring(10), ""));
					else
						array.add(new Triplet<String,String,String>(line.split(";")[0], line.split(";")[1], line.split(";")[2]));
				}
			}
			textReader.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}

		return array;
	}
}
