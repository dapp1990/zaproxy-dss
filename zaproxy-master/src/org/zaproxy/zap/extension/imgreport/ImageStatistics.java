package org.zaproxy.zap.extension.imgreport;

import java.util.List;

public abstract class ImageStatistics {
	public abstract String getXML(List<HttpImage> siteImages);
}
