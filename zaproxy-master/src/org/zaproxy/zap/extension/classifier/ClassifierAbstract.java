package org.zaproxy.zap.extension.classifier;

public abstract class ClassifierAbstract<I,O> {
	public abstract O getClassification (I input); 
}
