package com.wl.pluto.plutochat.chat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.wl.pluto.plutochat.R;

import java.util.List;

/**
 * Created by pluto on 15-12-18.
 */
public class ExpressionAdapter extends ArrayAdapter<String> {

    private Context context;

    public ExpressionAdapter(Context context, int resource, List<String> objects) {
        super(context, resource, objects);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.layout_expression_item, null);
            holder.expressionImage = (ImageView) convertView.findViewById(R.id.iv_expression_item_image_view);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String fileName = getItem(position);

        int resId = context.getResources().getIdentifier(fileName, "mipmap", context.getPackageName());
        holder.expressionImage.setImageResource(resId);

        return convertView;
    }


    public static class ViewHolder {

        public ImageView expressionImage;
    }
}
