package org.parosproxy.paros.filter;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.parosproxy.paros.extension.filter.algorithm.StringFilterApplyer;
import org.parosproxy.paros.extension.filter.formatter.InappropriateElement;

import javafx.util.Pair;

public class FilterApplyerUnitTest {

	private StringFilterApplyer filter;
	private Pair<Integer, ArrayList<InappropriateElement<String>>> parsedFormatFile;
	
	private String appropriateContent = "The little duck crossed the road.";
	private String inappropriateContent1 = "The little nigger crossed the road.";
	private String inappropriateContent2 = "The little duck crossed the nigger.";
	private String inappropriateContent3 = "The little niggers crossed the road.";
	private String inappropriateContent4 = "The little n*gger crossed the road.";
	private String inappropriateContent5 = "Trump said he likes fanta.";
	private String inappropriateContent6 = "The little Niggers crossed the road.";
	private String inappropriateContent7 = "We should deport all the mexicans!";
	private String inappropriateContent8 = "He then showed me his d*ck";
	private String inappropriateContent9 = "The little n*gger crossed the road.";
	private String inappropriateContent10 = "mexicans deport";
	private String inappropriateContent11 = "deported mexicans";
	private int threshold = 10;
	private InappropriateElement<String> inapEl1 = new InappropriateElement<String>("trump", 4, Arrays.asList("fearmongering", "mysogenist", "bigotry"));
	private InappropriateElement<String> inapEl2 = new InappropriateElement<String>("nigger", 10, Arrays.asList("racist"));
	private InappropriateElement<String> inapEl3 = new InappropriateElement<String>("deport mexicans", 10, Arrays.asList("racist", "fearmongering"));
	private InappropriateElement<String> inapEl4 = new InappropriateElement<String>("dick", 2, Arrays.asList("sexual"));
	
	@Before
	public void setUp() throws Exception {
		filter = new StringFilterApplyer();
		parsedFormatFile = new Pair<Integer, ArrayList<InappropriateElement<String>>>(threshold, new ArrayList<InappropriateElement<String>>(Arrays.asList(inapEl1, inapEl2, inapEl3, inapEl4)));
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testApplyBasicStringFilter() {
		assertEquals(filter.applyBasicFilter(appropriateContent, parsedFormatFile),appropriateContent);
		String result1 = filter.applyBasicFilter(inappropriateContent1, parsedFormatFile);
		String result2 = filter.applyBasicFilter(inappropriateContent2, parsedFormatFile);
		String result3 = filter.applyBasicFilter(inappropriateContent3, parsedFormatFile);
		System.out.println(result1);
		System.out.println(result2);
		System.out.println(result3);
		assertNotSame(result1, inappropriateContent1);
		assertNotSame(result2, inappropriateContent2);
		assertNotSame(result3, inappropriateContent3);
	}
	
	//future tests, will fail now
	
	@Test
	public void testApplyBasicStringFilterCapitalLetters(){
		String result1 = filter.applyBasicFilter(inappropriateContent5, parsedFormatFile);
		String result2 = filter.applyBasicFilter(inappropriateContent6, parsedFormatFile);
		System.out.println(result1);
		System.out.println(result2);
		assertNotSame(result1, inappropriateContent5);
		assertNotSame(result2, inappropriateContent6);
	}
	
	@Test
	public void testApplyBasicStringFilterWithWordsInBetween(){
		String result1 = filter.applyBasicFilter(inappropriateContent7, parsedFormatFile);
		System.out.println(result1);
		assertNotSame(result1, inappropriateContent7);
	}
	
	@Test 
	public void testApplyBasicStringFilterWithChangedOrder(){
		String result1 = filter.applyBasicFilter(inappropriateContent10, parsedFormatFile);
		System.out.println(result1);
		assertNotSame(result1, inappropriateContent10);
	}
	
	@Test
	public void testApplyBasicStringFilterWithConjugatedVerbs(){
		String result1 = filter.applyBasicFilter(inappropriateContent11, parsedFormatFile);
		System.out.println(result1);
		assertNotSame(result1, inappropriateContent11);
	}
	
	@Test 
	public void testApplyBasicStringFilterPartialCensoring(){
		String result1 = filter.applyBasicFilter(inappropriateContent4, parsedFormatFile);
		String result2 = filter.applyBasicFilter(inappropriateContent8, parsedFormatFile);
		String result3 = filter.applyBasicFilter(inappropriateContent9, parsedFormatFile);
		System.out.println(result1);
		System.out.println(result2);
		System.out.println(result3);
		assertNotSame(result1, inappropriateContent4);
		assertNotSame(result2, inappropriateContent8);
		assertNotSame(result3, inappropriateContent9);
	}
}









































