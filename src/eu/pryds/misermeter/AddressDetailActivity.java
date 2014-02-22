package eu.pryds.misermeter;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

public class AddressDetailActivity extends Activity {
    public static final String EXTRA_MESSAGE_ID = "eu.pryds.misermeter.messageId";
    public static final String EXTRA_MESSAGE_COMMENT = "eu.pryds.misermeter.messageComment";
    public static final String EXTRA_MESSAGE_ADDRESS = "eu.pryds.misermeter.messageAddress";
    public static final String EXTRA_MESSAGE_BALANCE = "eu.pryds.misermeter.messageBalance";
    public static final String EXTRA_MESSAGE_CONVBALANCE = "eu.pryds.misermeter.messageConvBalance";
    public static final String EXTRA_MESSAGE_LASTADDRUPDATE = "eu.pryds.misermeter.messageLastAddrUpdate";
    public static final String EXTRA_MESSAGE_LASTCONVUPDATE = "eu.pryds.misermeter.messageLastConvUpdate";
    
    private long id;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_detail);
        
        Intent intent = getIntent();
        
        this.id = intent.getLongExtra(EXTRA_MESSAGE_ID, -1);
        
        TextView comment = (TextView) findViewById(R.id.detail_comment);
        comment.setText(intent.getStringExtra(EXTRA_MESSAGE_COMMENT));
        
        TextView address = (TextView) findViewById(R.id.detail_address);
        address.setText(intent.getStringExtra(EXTRA_MESSAGE_ADDRESS));
        
        TextView balance = (TextView) findViewById(R.id.detail_balance);
        long lastAddrUpdate = intent.getLongExtra(EXTRA_MESSAGE_LASTADDRUPDATE, -1);
        balance.setText(intent.getStringExtra(EXTRA_MESSAGE_BALANCE) + " " + (
                lastAddrUpdate != -1 ?
                getUpdatedString(lastAddrUpdate) :
                getResources().getString(R.string.detail_updateunknown)
                ));
        
        TextView convBalance = (TextView) findViewById(R.id.detail_converted_balance);
        long lastConvUpdate = intent.getLongExtra(EXTRA_MESSAGE_LASTCONVUPDATE, -1);
        convBalance.setText(intent.getStringExtra(EXTRA_MESSAGE_CONVBALANCE) + " " + (
                lastConvUpdate != -1 ?
                getUpdatedString(lastConvUpdate) :
                getResources().getString(R.string.detail_updateunknown)
                ));
        
    }
    
    private String getUpdatedString(long updateTime) {
        long now = System.currentTimeMillis();
        long timeSinceUpdate = now - updateTime;
        
        int seconds = (int) (timeSinceUpdate / 1000);
        int minutes = seconds / 60;
        int hours = minutes / 60;
        
        if (hours > 0)
            return getResources().getQuantityString(
                    R.plurals.detail_updatedhours, hours, hours);
        
        if (minutes > 0)
            return getResources().getQuantityString(
                    R.plurals.detail_updatedminutes, minutes, minutes);
        
        return getResources().getQuantityString(
                R.plurals.detail_updatedseconds, seconds, seconds);
    }
    
    public void buttonClick(View view) {
        if (view.getId() == R.id.detail_remove_button) {
            // Pack id of this item into an intent
            // so that MainActivity knows which item to remove:
            Intent resultIntent = new Intent();
            resultIntent.putExtra(EXTRA_MESSAGE_ID, id);
            setResult(Activity.RESULT_OK, resultIntent);
            this.finish();
        }
    }
}
