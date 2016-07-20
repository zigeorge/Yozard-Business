package com.yozard.business.pushNotification;

import android.content.Context;

import android.content.Intent;

public final class CommonUtilities {
	
	// give your server registration url here
//	public static final String SERVER_URL ="http://www.promopayout.co.ug/vendor/gcm_server/register.php"; //"http://10.0.2.2/gcm_server_php/register.php"; 

    // Google project id
    public static final String SENDER_ID = "17475840958";

    /**
     * Tag used on log messages.
     */
    public static final String TAG = "PromoPayout Merchant Gcm";

    public static final String DISPLAY_MESSAGE_ACTION =
           "com.yozard.business.pushNotification.DISPLAY_MESSAGE";

    public static final String EXTRA_MESSAGE = "message";
    public static final String EXTRA_TITLE = "title";

    /**
     * Notifies UI to display a message.
     * <p>
     * This method is defined in the common helper because it's used both by
     * the UI and the background service.
     *
     * @param context application's context.
     * @param message message to be displayed.
     */
    public static void displayMessage(Context context, String message) {
        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra(EXTRA_MESSAGE, message);
        context.sendBroadcast(intent);
    }
    
    public static void displayMessage(Context context, String message,String title) {
        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra(EXTRA_MESSAGE, message);
        intent.putExtra(EXTRA_TITLE, title);
        context.sendBroadcast(intent);
    }
    
    
}
