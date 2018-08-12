package com.yl.distribute.scheduler.client.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

public class CommonTest {

	public static void main(String[] args) {
        List<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();        
        HashMap<String,String> map = new LinkedHashMap<String,String>();
        map.put("nj", "nanjing");
        map.put("szhu", "souzhou");
        map.put("xu", "xuzhou");        
        list.add(map);        
        HashMap<String,String> map1 = new LinkedHashMap<String,String>();
        map1.put("jn", "jinan");
        map1.put("qd", "qingdao");
        map1.put("yt", "yantai");
        map1.put("ly", "linyi");
        list.add(map1);        
        int maxColumn  = 0;
        for(int i= 0; i< list.size(); i++){
        	int columns  = list.get(i).size();
        	if(columns > maxColumn){
        		maxColumn = columns;
        	}
        }        
        String[][] strs = new String[list.size()][maxColumn];        
        int rowindex  = 0;        
        for(int i= 0; i<list.size(); i++){        	
        	HashMap<String,String> map2 = list.get(i);        	
        	int columnindex = 0;        	
        	for(Entry<String,String> entry : map2.entrySet()){        		
        		strs[rowindex][columnindex] = entry.getValue();        		
        		columnindex+=1;        		
        	}        	
        	rowindex+=1;        	
        }        
        for(String[] row : strs){
        	for(String column : row){
        		System.out.println(column);
        	}
        }
	}

}
