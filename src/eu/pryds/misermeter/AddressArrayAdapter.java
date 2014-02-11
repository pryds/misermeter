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

public class AddressArrayAdapter extends ArrayAdapter<AddressArrayAdapter.AddressItem> {
    // Based on example at http://sogacity.com/how-to-make-a-custom-arrayadapter-for-listview/
    private ArrayList<AddressItem> entries;
    private Activity activity;

    public AddressArrayAdapter(Context context, int resource) {
        super(context, resource);
    }
    
    public AddressArrayAdapter(Activity activity, int textViewResourceId, ArrayList<AddressItem> entries) {
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
 
        final AddressItem custom = entries.get(position);
        if (custom != null) {
            holder.comment.setText(custom.getComment());
            holder.balance.setText(custom.getRoundedBalanceAsString());
            holder.address.setText(custom.getShortenedAddress());
            holder.balanceConverted.setText(custom.getConvertedBalanceAsString());
            holder.icon.setImageResource(custom.getIconRessource());
        }
        return v;
    }
    
    public interface AddressItem {
        public void updateFromFeed();
        public String getShortenedAddress();
        public String getRoundedBalanceAsString();
        public String getComment();
        public String getConvertedBalanceAsString();
        public int getIconRessource();
        public boolean supportsConverter(int converter);
    }
    
    public interface BalanceConverter {
        public static final int CONV_BITCOINCHARTS = 1;
        public static final int CONV_BITCOINAVERAGE_GLOBALTICKER = 2;
        
        public static final int CURRENCY_AED = 1;
        public static final int CURRENCY_AFN = 2;
        public static final int CURRENCY_ALL = 3;
        public static final int CURRENCY_AMD = 4;
        public static final int CURRENCY_ANG = 5;
        public static final int CURRENCY_AOA = 6;
        public static final int CURRENCY_ARS = 7;
        public static final int CURRENCY_AUD = 8;
        public static final int CURRENCY_AWG = 9;
        public static final int CURRENCY_AZN = 10;
        public static final int CURRENCY_BAM = 11;
        public static final int CURRENCY_BBD = 12;
        public static final int CURRENCY_BDT = 13;
        public static final int CURRENCY_BGN = 14;
        public static final int CURRENCY_BHD = 15;
        public static final int CURRENCY_BIF = 16;
        public static final int CURRENCY_BMD = 17;
        public static final int CURRENCY_BND = 18;
        public static final int CURRENCY_BOB = 19;
        public static final int CURRENCY_BRL = 20;
        public static final int CURRENCY_BSD = 21;
        public static final int CURRENCY_BTC = 22;
        public static final int CURRENCY_BTN = 23;
        public static final int CURRENCY_BWP = 24;
        public static final int CURRENCY_BYR = 25;
        public static final int CURRENCY_BZD = 26;
        public static final int CURRENCY_CAD = 27;
        public static final int CURRENCY_CDF = 28;
        public static final int CURRENCY_CHF = 29;
        public static final int CURRENCY_CLF = 30;
        public static final int CURRENCY_CLP = 31;
        public static final int CURRENCY_CNY = 32;
        public static final int CURRENCY_COP = 33;
        public static final int CURRENCY_CRC = 34;
        public static final int CURRENCY_CUP = 35;
        public static final int CURRENCY_CVE = 36;
        public static final int CURRENCY_CZK = 37;
        public static final int CURRENCY_DJF = 38;
        public static final int CURRENCY_DKK = 39;
        public static final int CURRENCY_DOP = 40;
        public static final int CURRENCY_DZD = 41;
        public static final int CURRENCY_EEK = 42;
        public static final int CURRENCY_EGP = 43;
        public static final int CURRENCY_ERN = 44;
        public static final int CURRENCY_ETB = 45;
        public static final int CURRENCY_EUR = 46;
        public static final int CURRENCY_FJD = 47;
        public static final int CURRENCY_FKP = 48;
        public static final int CURRENCY_GBP = 49;
        public static final int CURRENCY_GEL = 50;
        public static final int CURRENCY_GHS = 51;
        public static final int CURRENCY_GIP = 52;
        public static final int CURRENCY_GMD = 53;
        public static final int CURRENCY_GNF = 54;
        public static final int CURRENCY_GTQ = 55;
        public static final int CURRENCY_GYD = 56;
        public static final int CURRENCY_HKD = 57;
        public static final int CURRENCY_HNL = 58;
        public static final int CURRENCY_HRK = 59;
        public static final int CURRENCY_HTG = 60;
        public static final int CURRENCY_HUF = 61;
        public static final int CURRENCY_IDR = 62;
        public static final int CURRENCY_ILS = 63;
        public static final int CURRENCY_INR = 64;
        public static final int CURRENCY_IQD = 65;
        public static final int CURRENCY_IRR = 66;
        public static final int CURRENCY_ISK = 67;
        public static final int CURRENCY_JEP = 68;
        public static final int CURRENCY_JMD = 69;
        public static final int CURRENCY_JOD = 70;
        public static final int CURRENCY_JPY = 71;
        public static final int CURRENCY_KES = 72;
        public static final int CURRENCY_KGS = 73;
        public static final int CURRENCY_KHR = 74;
        public static final int CURRENCY_KMF = 75;
        public static final int CURRENCY_KPW = 76;
        public static final int CURRENCY_KRW = 77;
        public static final int CURRENCY_KWD = 78;
        public static final int CURRENCY_KYD = 79;
        public static final int CURRENCY_KZT = 80;
        public static final int CURRENCY_LAK = 81;
        public static final int CURRENCY_LBP = 82;
        public static final int CURRENCY_LKR = 83;
        public static final int CURRENCY_LRD = 84;
        public static final int CURRENCY_LSL = 85;
        public static final int CURRENCY_LTL = 86;
        public static final int CURRENCY_LVL = 87;
        public static final int CURRENCY_LYD = 88;
        public static final int CURRENCY_MAD = 89;
        public static final int CURRENCY_MDL = 90;
        public static final int CURRENCY_MGA = 91;
        public static final int CURRENCY_MKD = 92;
        public static final int CURRENCY_MMK = 93;
        public static final int CURRENCY_MNT = 94;
        public static final int CURRENCY_MOP = 95;
        public static final int CURRENCY_MRO = 96;
        public static final int CURRENCY_MTL = 97;
        public static final int CURRENCY_MUR = 98;
        public static final int CURRENCY_MVR = 99;
        public static final int CURRENCY_MWK = 100;
        public static final int CURRENCY_MXN = 101;
        public static final int CURRENCY_MYR = 102;
        public static final int CURRENCY_MZN = 103;
        public static final int CURRENCY_NAD = 104;
        public static final int CURRENCY_NGN = 105;
        public static final int CURRENCY_NIO = 106;
        public static final int CURRENCY_NOK = 107;
        public static final int CURRENCY_NPR = 108;
        public static final int CURRENCY_NZD = 109;
        public static final int CURRENCY_OMR = 110;
        public static final int CURRENCY_PAB = 111;
        public static final int CURRENCY_PEN = 112;
        public static final int CURRENCY_PGK = 113;
        public static final int CURRENCY_PHP = 114;
        public static final int CURRENCY_PKR = 115;
        public static final int CURRENCY_PLN = 116;
        public static final int CURRENCY_PYG = 117;
        public static final int CURRENCY_QAR = 118;
        public static final int CURRENCY_RON = 119;
        public static final int CURRENCY_RSD = 120;
        public static final int CURRENCY_RUB = 121;
        public static final int CURRENCY_RWF = 122;
        public static final int CURRENCY_SAR = 123;
        public static final int CURRENCY_SBD = 124;
        public static final int CURRENCY_SCR = 125;
        public static final int CURRENCY_SDG = 126;
        public static final int CURRENCY_SEK = 127;
        public static final int CURRENCY_SGD = 128;
        public static final int CURRENCY_SHP = 129;
        public static final int CURRENCY_SLL = 130;
        public static final int CURRENCY_SOS = 131;
        public static final int CURRENCY_SRD = 132;
        public static final int CURRENCY_STD = 133;
        public static final int CURRENCY_SVC = 134;
        public static final int CURRENCY_SYP = 135;
        public static final int CURRENCY_SZL = 136;
        public static final int CURRENCY_THB = 137;
        public static final int CURRENCY_TJS = 138;
        public static final int CURRENCY_TMT = 139;
        public static final int CURRENCY_TND = 140;
        public static final int CURRENCY_TOP = 141;
        public static final int CURRENCY_TRY = 142;
        public static final int CURRENCY_TTD = 143;
        public static final int CURRENCY_TWD = 144;
        public static final int CURRENCY_TZS = 145;
        public static final int CURRENCY_UAH = 146;
        public static final int CURRENCY_UGX = 147;
        public static final int CURRENCY_USD = 148;
        public static final int CURRENCY_UYU = 149;
        public static final int CURRENCY_UZS = 150;
        public static final int CURRENCY_VEF = 151;
        public static final int CURRENCY_VND = 152;
        public static final int CURRENCY_VUV = 153;
        public static final int CURRENCY_WST = 154;
        public static final int CURRENCY_XAF = 155;
        public static final int CURRENCY_XAG = 156;
        public static final int CURRENCY_XAU = 157;
        public static final int CURRENCY_XCD = 158;
        public static final int CURRENCY_XDR = 159;
        public static final int CURRENCY_XOF = 160;
        public static final int CURRENCY_XPF = 161;
        public static final int CURRENCY_YER = 162;
        public static final int CURRENCY_ZAR = 163;
        public static final int CURRENCY_ZMK = 164;
        public static final int CURRENCY_ZMW = 165;
        public static final int CURRENCY_ZWL = 166;
        
        public void updateFromFeed();
        public double convertValue(double fromValue, int toCurrency);
    }
}
