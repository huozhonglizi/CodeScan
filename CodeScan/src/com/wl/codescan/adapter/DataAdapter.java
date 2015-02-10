package com.wl.codescan.adapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * 数据适配器基类
 * 
 * @author wanglei
 * 
 * @param <T>
 */
public abstract class DataAdapter<T> extends BaseAdapter {
	protected List<T> data = new ArrayList<T>();
	protected Context context;

	public DataAdapter(Context context) {
		this.context = context;
	}

	public DataAdapter(Context context, List<T> data) {
		this.context = context;
		this.data.clear();
		this.data.addAll(data);
		notifyDataSetChanged();
	}

	/**
	 * 设置数据源
	 * 
	 * @param data
	 */
	public void setData(List<T> data) {
		this.data.clear();
		this.data.addAll(data);
		notifyDataSetChanged();
	}

	/**
	 * 设置数据源
	 * 
	 * @param data
	 */
	public void setData(T[] data) {
		if (data != null && data.length > 0) {
			setData(Arrays.asList(data));
		}
	}

	/**
	 * 添加数据
	 * 
	 * @param data
	 */
	public void addData(List<T> data) {
		if (this.data != null && data != null && data.size() > 0) {
			this.data.addAll(data);
			notifyDataSetChanged();
		}
	}

	/**
	 * 添加数据
	 * 
	 * @param data
	 */
	public void addData(T[] data) {
		addData(Arrays.asList(data));
	}

	/**
	 * 删除元素
	 * 
	 * @param element
	 */
	public void removeElement(T element) {
		if (data.contains(element)) {
			data.remove(element);
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
	 * 更新元素
	 * 
	 * @param element
	 *            新元素
	 * @param position
	 *            新元素的位置
	 */
	public void updateElement(T element, int position) {
		if (position > 0 && data.size() > position) {
			data.remove(position);
			data.set(position, element);
			notifyDataSetChanged();
		}
	}

	/**
	 * 添加元素
	 * 
	 * @param element
	 */
	public void addElement(T element) {
		if (element != null && data != null) {
			data.add(element);
			notifyDataSetChanged();
		}
	}

	/**
	 * 获取数据源
	 * 
	 * @return
	 */
	public List<T> getDataSource() {
		return data;
	}

	/**
	 * 获取数据源大小
	 * 
	 * @return
	 */
	public int getSize() {
		return data.size();
	}

	@Override
	public int getCount() {
		return data == null ? 0 : data.size();
	}

	@Override
	public Object getItem(int position) {
		return data != null ? data.get(position) : null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public abstract View getView(int position, View convertView,
			ViewGroup parent);
}
