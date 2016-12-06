package org.parosproxy.paros.extension.filter.formatter;

import java.util.List;

public class InappropriateElement<T> {
	
	private final T inappropriateContent;
	private final int weight;
	private final List<String> tags;
	
	public T getInappropriateContent() {
		return inappropriateContent;
	}

	public int getWeight() {
		return weight;
	}

	public List<String> getTags() {
		return tags;
	}
	
	public InappropriateElement(T inappropriateContent, int weight, List<String> tags) {
		this.inappropriateContent = inappropriateContent;
		this.weight = weight;
		this.tags = tags;
	}
	
}
