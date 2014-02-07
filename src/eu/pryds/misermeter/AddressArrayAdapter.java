package eu.pryds.misermeter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AddressArrayAdapter extends ArrayAdapter<BitcoinAddress> {
    // Based on example at http://sogacity.com/how-to-make-a-custom-arrayadapter-for-listview/
    private ArrayList<BitcoinAddress> entries;
    private Activity activity;

    public AddressArrayAdapter(Context context, int resource) {
        super(context, resource);
    }
    
    public AddressArrayAdapter(Activity activity, int textViewResourceId, ArrayList<BitcoinAddress> entries) {
        super(activity, textViewResourceId, entries);
        this.entries = entries;
        this.activity = activity;
    }
    
    public static class ViewHolder{
        public TextView comment;
        public TextView balance;
        public TextView address;
        public TextView balanceConverted;
        public ImageView icon;
        
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        ViewHolder holder;
        if (v == null) {
            LayoutInflater vi =
                (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.grid_item, null);
            holder = new ViewHolder();
            holder.comment = (TextView) v.findViewById(R.id.item_comment);
            holder.balance = (TextView) v.findViewById(R.id.item_balance);
            holder.address = (TextView) v.findViewById(R.id.item_address);
            holder.balanceConverted = (TextView) v.findViewById(R.id.item_balance_converted);
            holder.icon = (ImageView) v.findViewById(R.id.item_icon);
            v.setTag(holder);
        }
        else
            holder=(ViewHolder)v.getTag();
 
        final BitcoinAddress custom = entries.get(position);
        if (custom != null) {
            holder.comment.setText(custom.getComment());
            holder.balance.setText(custom.getRoundedBalanceAsString());
            holder.address.setText(custom.getShortenedAddress());
            holder.balanceConverted.setText(custom.getConvertedBalanceAsString());
            holder.icon.setImageResource(custom.getIconRessource());
        }
        return v;
    }
}
