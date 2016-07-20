package com.yozard.business.adapter;

import java.util.Vector;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.TextView;

import com.yozard.business.R;
import com.yozard.business.utils.TypeFace_MY;

/**
 * Created by AAPBD k on 9/13/2015.
 */
public class WhatsNewAdapter extends ArrayAdapter<String> implements Filterable {

	Context con;
	Vector<String> whatsNewList;
	int resource;
	Typeface tf_roboto;

	public WhatsNewAdapter(Context context, int resource, Vector<String> objects) {
		super(context, resource, objects);
		// TODO Auto-generated constructor stub
		this.con = context;
		this.resource = resource;
		this.whatsNewList = objects;
		this.tf_roboto = TypeFace_MY.getRoboto_condensed(context);
	}

	private class ViewHolder {
		TextView tvWhatsNew;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		LayoutInflater inflater = (LayoutInflater) con
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			convertView = inflater.inflate(resource, null);
			holder = new ViewHolder();
			holder.tvWhatsNew = (TextView) convertView.findViewById(R.id.tvWhatsNewText);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		if (position < whatsNewList.size()) {
			holder.tvWhatsNew.setText(whatsNewList.get(position));
			holder.tvWhatsNew.setTypeface(tf_roboto);
		}

		return convertView;
	}

}
