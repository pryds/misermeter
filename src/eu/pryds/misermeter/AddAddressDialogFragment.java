package eu.pryds.misermeter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class AddAddressDialogFragment extends DialogFragment {
    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View v = inflater.inflate(R.layout.dialogfragment_add_address, null);
        
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(v)
               .setTitle(R.string.action_add_address)
               .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int which) {
                       // no action; just close dialog
                   }
               })
               .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int which) {
                       AddAddressDialogListener activity =
                               (AddAddressDialogListener) getActivity();
                       
                       EditText address = (EditText)
                               v.findViewById(R.id.edittext_add_address_address);
                       EditText comment = (EditText)
                               v.findViewById(R.id.edittext_add_address_comment);
                       
                       Log.d(MainActivity.DEBUG_STR, "activity: " + activity + ", address: " + address + ", comment: " + comment);
                       activity.onReturnNewAddressValues(
                               address.getText().toString(),
                               comment.getText().toString());
                   }
               });
        
        return builder.create();
    }
    
    /*
     * The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it.
     */
    public interface AddAddressDialogListener {
        public void onReturnNewAddressValues(String address, String comment);
    }
    
    // Use this instance of the interface to deliver action events
    AddAddressDialogListener mListener;
    
    // Override the Fragment.onAttach() method to instantiate the AddAddressDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (AddAddressDialogListener) activity;
        } catch(ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement AddAddressDialogListener");
        }
    }
}
