package org.parosproxy.paros.filter;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.parosproxy.paros.extension.filter.algorithm.FilterApplyer;
import org.parosproxy.paros.extension.filter.formatter.InappropriateElement;

import javafx.util.Pair;

public class FilterApplyerUnitTest {

	private FilterApplyer filter;
	private Pair<Integer, ArrayList<InappropriateElement<String>>> parsedFormatFile;
	
	private String appropriateContent = "The little duck crossed the road.";
	private String inappropriateContent1 = "The little nigger crossed the road.";
	private String inappropriateContent2 = "The little duck crossed the nigger.";
	private String inappropriateContent3 = "The little niggers crossed the road.";
	private int threshold = 10;
	private InappropriateElement<String> inapEl1 = new InappropriateElement<String>("trump", 4, Arrays.asList("fearmongering", "mysogenist", "bigotry"));
	private InappropriateElement<String> inapEl2 = new InappropriateElement<String>("nigger", 10, Arrays.asList("racist"));
	private InappropriateElement<String> inapEl3 = new InappropriateElement<String>("deport mexicans", 10, Arrays.asList("racist", "fearmongering"));
	private InappropriateElement<String> inapEl4 = new InappropriateElement<String>("dick", 2, Arrays.asList("sexual"));
	
	@Before
	public void setUp() throws Exception {
		filter = new FilterApplyer();
		parsedFormatFile = new Pair<Integer, ArrayList<InappropriateElement<String>>>(threshold, new ArrayList<InappropriateElement<String>>(Arrays.asList(inapEl1, inapEl2, inapEl3, inapEl4)));
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testApplyBasicStringFilter() {
		assertEquals(filter.applyBasicStringFilter(appropriateContent, parsedFormatFile),appropriateContent);
		String result1 = filter.applyBasicStringFilter(inappropriateContent1, parsedFormatFile);
		String result2 = filter.applyBasicStringFilter(inappropriateContent2, parsedFormatFile);
		String result3 = filter.applyBasicStringFilter(inappropriateContent3, parsedFormatFile);
		System.out.println(result1);
		System.out.println(result2);
		System.out.println(result3);
		assertNotSame(result1, inappropriateContent1);
		assertNotSame(result2, inappropriateContent2);
		assertNotSame(result3, inappropriateContent3);
	}

}
