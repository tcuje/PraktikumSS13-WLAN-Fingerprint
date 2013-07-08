package de.rwth.ti.layouthelper;

import java.util.ArrayList;

import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class CustomTabHelper implements
	OnClickListener {
	
	int size;
	ArrayList<View> headerList;
	ArrayList<ViewGroup> contentList;
	
	private CustomTabHelper() {
		size = 0;
		headerList = new ArrayList<View>();
		contentList = new ArrayList<ViewGroup>();
	}
	
	public static CustomTabHelper createInstance(View header, ViewGroup content) {
		CustomTabHelper helper = new CustomTabHelper();
		helper.addTabItem(header, content);
		return helper;
	}
	
	public static CustomTabHelper createInstance(ArrayList<View> headerList, ArrayList<ViewGroup> contentList) {
		if (headerList.size() == contentList.size()) {
			CustomTabHelper helper = new CustomTabHelper();
			
			for (int i = 0; i < headerList.size(); i++) {
				helper.addTabItem(headerList.get(i), contentList.get(i));
			}
			return helper;
		}
		return null;
	}
	
	public boolean addTabItem(View header, ViewGroup content) {
		if (headerList.add(header)) {
			if (contentList.add(content)) {
				size++;
		
				header.setBackgroundColor(Color.LTGRAY);
				header.setOnClickListener(this);
		
				content.setVisibility(View.GONE);
				
				return true;
			}
			else {
				headerList.remove(header);
			}
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		for (int i = 0; i < size; i++) {
			if (v.getId() == headerList.get(i).getId()) {
				contentList.get(i).setVisibility(View.VISIBLE);
			}
			else {
				contentList.get(i).setVisibility(View.GONE);
			}
		}
	}

}
