package org.zaproxy.zap.extension.imgreport;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class ImageStatisticsFactory {
	
	public static final String SIZE = "size";
	public static final String HEIGHT = "height";
	public static final String TYPE = "type";
	public static final String WIDTH = "width";
	
	   public ImageStatistics getStatistics(String type){
	      if(type == null){
	         return null;
	      }		
	      if(type.equalsIgnoreCase("size")){
	         return new ImageSizeStatistics();
	         
	      } else if(type.equalsIgnoreCase("height")){
	         return new ImageHeightStatistics();
	         
	      } else if(type.equalsIgnoreCase("type")){
	         return new ImageTypeStatistics();
	         
	      } else if(type.equalsIgnoreCase("width")){
		     return new ImageWidthStatistics();
		  }
	      
	      return null;
	   }
	   
	   public ArrayList<ImageStatistics> getAllStatistics(){
		   
		   ArrayList<ImageStatistics> list = new ArrayList<ImageStatistics>();
		    
		   Field[] imageStatisticsFactoryFields = ImageStatisticsFactory.class.getFields();
		   for(Field f:imageStatisticsFactoryFields) {
			   list.add(this.getStatistics(f.getName()));
		   }
		   
		   return list;
	   }
}