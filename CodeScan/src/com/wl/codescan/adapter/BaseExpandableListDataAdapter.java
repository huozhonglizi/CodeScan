package com.wl.codescan.adapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

/**
 * 
 * @类描述:可悬停的listview数据适配器基类
 * @创建人:wanglei
 * @创建时间: 2014-11-22
 * @修改人:
 * @修改时间:
 * @修改备注:
 * @version:
 * @param <T>
 */
public abstract class BaseExpandableListDataAdapter<T> extends
		BaseExpandableListAdapter {
	protected ArrayList<ArrayList<T>> data = new ArrayList<ArrayList<T>>();
	protected Context context;
	protected LayoutInflater inflater;

	public BaseExpandableListDataAdapter(Context context) {
		this.context = context;
		inflater = LayoutInflater.from(context);
	}

	public BaseExpandableListDataAdapter(Context context,
			ArrayList<ArrayList<T>> data) {
		this.context = context;
		inflater = LayoutInflater.from(context);
		this.data.clear();
		this.data.addAll(data);
		notifyDataSetChanged();
	}

	/**
	 * 设置数据源
	 * 
	 * @param data
	 */
	public void setData(ArrayList<ArrayList<T>> data) {
		this.data.clear();
		this.data.addAll(data);
		notifyDataSetChanged();
	}

	/**
	 * 添加数据
	 * 
	 * @param data
	 */
	public void addData(ArrayList<ArrayList<T>> data) {
		if (this.data != null && data != null && data.size() > 0) {
			this.data.addAll(data);
			notifyDataSetChanged();
		}
	}

	/**
	 * 删除元素
	 * 
	 * @param position
	 */
	public void removeElement(int position) {
		if (data != null && data.size() > position) {
			data.remove(position);
			notifyDataSetChanged();
		}
	}

	/**
	 * 删除元素集合
	 * 
	 * @param elements
	 */
	public void removeElements(List<T> elements) {
		if (data != null && elements != null && elements.size() > 0
				&& data.size() >= elements.size()) {

			for (T element : elements) {
				if (data.contains(element)) {
					data.remove(element);
				}
			}

			notifyDataSetChanged();
		}
	}

	/**
	 * 删除元素集合
	 * 
	 * @param elements
	 */
	public void removeElements(T[] elements) {
		if (elements != null && elements.length > 0) {
			removeElements(Arrays.asList(elements));
		}
	}

	/**
	 * 获取数据源
	 * 
	 * @return
	 */
	public ArrayList<ArrayList<T>> getDataSource() {
		return data;
	}

	/**
	 * 获取数据源大小
	 * 
	 * @return
	 */
	public int getSize() {
		return data==null?0:data.size();
	}

	@Override
	public int getGroupCount() {
		return data == null ? 0 : data.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		if (data == null || data.size() < groupPosition
				|| data.get(groupPosition) == null) {
			return 0;
		}
		return data.get(groupPosition).size();
	}

	@Override
	public Object getGroup(int groupPosition) {

		return data.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return data.get(groupPosition).get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public abstract View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent);

	@Override
	public abstract View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent);

	@Override
	public abstract boolean isChildSelectable(int groupPosition,
			int childPosition);

}
