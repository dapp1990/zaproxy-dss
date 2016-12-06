package org.parosproxy.paros.extension.filter.formatter;

public abstract class FormatterAbstractReplace<I,O> {
	public abstract O getFormat (I input);
}
