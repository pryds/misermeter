package eu.pryds.misermeter;

import android.app.backup.BackupAgentHelper;
import android.app.backup.SharedPreferencesBackupHelper;

public class MiserBackupAgent extends BackupAgentHelper {
    public void onCreate() {
        SharedPreferencesBackupHelper helper =
                new SharedPreferencesBackupHelper(this, MainActivity.PREFS_NAME);
        addHelper(MainActivity.PREFS_NAME, helper);
    }
}
