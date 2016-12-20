package org.parosproxy.paros.extension.filter.algorithm;

import java.util.ArrayList;

import org.parosproxy.paros.extension.filter.formatter.InappropriateElement;

import javafx.util.Pair;

public abstract class FilterApplyer<T> {
	public abstract T applyBasicStringFilter (T contentInput, Pair<Integer, ArrayList<InappropriateElement<T>>> filterTagInput); 
}
