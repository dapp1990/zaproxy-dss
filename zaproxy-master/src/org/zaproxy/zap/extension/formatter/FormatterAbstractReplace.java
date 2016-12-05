package org.zaproxy.zap.extension.formatter;

public abstract class FormatterAbstractReplace<I,O> {
	public abstract O getFormat (I input);
}
