package com.emine.arasinav;

import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class RehberListAdapter extends BaseAdapter {

    private List<String> isimler = null;
    private List<String> teller = null;
    private List<Uri> resimler = null;
    private LayoutInflater mInflater;

    public RehberListAdapter(Context context, List<String> isimler, List<String> teller,List<Uri> resimler) {
        this.isimler = isimler ;
        this.teller=teller;
        this.resimler=resimler;
        mInflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return isimler.size();
    }

    public Object getItem(int position) {
        return isimler.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        // A ViewHolder keeps references to children views to avoid unnecessary calls
        // to findViewById() on each row.
        ViewHolder holder,holder1,holder2,holder3;

        // When convertView is not null, we can reuse it directly, there is no need
        // to reinflate it. We only inflate a new View when the convertView supplied
        // by ListView is null.
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.listview_adapter,parent,false);
            holder = new ViewHolder();

            // Creates a ViewHolder and store references to the two children views
            // we want to bind data to.


            convertView.setTag(holder);
        } else {
            // Get the ViewHolder back to get fast access to the TextView
            // and the ImageView.
            holder = (ViewHolder) convertView.getTag();
        }

        holder1= new ViewHolder();
        holder2=new ViewHolder();
        holder.text = (TextView) convertView.findViewById(R.id.adi);
        holder1.text1= (TextView) convertView.findViewById(R.id.tel);
        holder2.imageView=convertView.findViewById(R.id.image);
        holder1.text1.setText(teller.get(position));
        holder2.imageView.setImageURI(resimler.get(position));
        // If weren't re-ordering this you could rely on what you set last time
        holder.text.setText(isimler.get(position));


        return convertView;
    }

    static class ViewHolder {
        TextView text,text1,text2,text3;
        ImageView imageView;
    }

}
