package org.parosproxy.paros.extension.filter.classifier;

public abstract class ClassifierAbstract<I,O> {
	public abstract O getClassification (I input); 
}
