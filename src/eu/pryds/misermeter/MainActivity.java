package eu.pryds.misermeter;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {
    
    private ArrayList<BitcoinAddress> addressArray = new ArrayList<BitcoinAddress>();
    private AddressArrayAdapter adapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        
        addressArray.add(new BitcoinAddress("1DBUPW5iJq13Ufb2TkiXbFPBGZzGP2ao2", "bitcoin address 1"));
        addressArray.add(new BitcoinAddress("1AXELoQwPgBeLs5UB9hRQoKR6HMnK4mU54", "Bitcoin address 2"));
        addressArray.add(new BitcoinAddress("19a6z2UjoePW923uS4uTyaCeXqGvYBx7Pf", "Bitcoin address 3"));
        addressArray.add(new BitcoinAddress("13EyH24DhHSdoNdXYTVQJMDvWQihpEhGqk", "Bitcoin address 3"));
        addressArray.add(new BitcoinAddress("1Hj6nKoPYhp8PtmxSZkZ9MWLpyW8C74Su9", "Bitcoin address 3"));
        
        
        /* for (int i = 4; i < 26; i++) {
            addressArray.add(new BitcoinAddress("YFG6d7SGFi7SF7i6ASfg76iafg7aSDg7ad", "Bitcoin auto-address " + i));
        } */
        
        
        // Set up ArrayAdapter that puts data from addressVector into ListView:
        // Ref: http://developer.android.com/guide/topics/ui/declaring-layout.html#AdapterViews
        adapter = new AddressArrayAdapter(this, 
                android.R.layout.simple_list_item_1, addressArray);
        
        ListView listView = (ListView) findViewById(R.id.address_list);
        listView.setAdapter(adapter);
        
        
        new Thread() {
            public void run() {
                for (int i = 0; i < addressArray.size(); i++) {
                    addressArray.get(i).updateFromFeed();
                    
                    runOnUiThread(new Runnable() {
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        }.start();
        
        
        
        
        
        
        double btc = 0.0001;
        BitcoinchartsExchangerate btcr = new BitcoinchartsExchangerate();
        
        TextView t = (TextView) findViewById(R.id.textbox);
        t.setText("BTC: " + formatBTC(btc) + "\nDKK: " + formatLocal(btcr.convertValue(btc, "DKK")));
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        
        return true;
    }
    
    private static String formatBTC(double btc) {
        return String.format("%.8f", btc);
    }
    
    private static String formatLocal(double localCurrency) {
        return String.format("%.2f", localCurrency);
    }
    
}
