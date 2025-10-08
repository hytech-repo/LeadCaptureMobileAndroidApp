package com.eva.lead.capture.constants;


public class AppConstants {

    public static String APP_PREF = "app-pref";

    public static String LEAD_CODE = "lead_code";
    public static String LICENSE_ACTIVATED = "license_accepted";
    public static String APP_LANGUAGE = "app_lang";
    public static String USER_ID = "user_id";
    public static String FIRST_NAME = "first_name";
    public static String LAST_NAME = "last_name";
    public static String USER_NAME = "user_name";
    public static String USER_EMAIL = "user_email";
    public static String USER_MOBILE = "user_mobile";
    public static String USER_IMAGE_URL = "user_image_url";
    public static String REFRESH_TOKEN = "refresh_token";
    public static String TOKEN_EXPIRY = "token_expiry";
    public static String EVENT_ID = "event_id";
    public static String EVENT_NAME = "event_name";
    public static String EVENT_IMAGE = "event_image";
    public static final String BASE_URL = "server_url";

    // checkin type ()
    public static int ONLY_CHECKIN = 0;
    public static int PRINT_CHECKIN = 1;
    public static int SINGLE_CHECKIN = 0;
    public static int MULTIPLE_CHECKIN = 1;

    public static int ALLOWED_SCAN = 1;

    public static final String BARCODE_REGEX = "^[A-Za-z]+-?\\d+$";
    public static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    public static final String PHONE_REGEX = "^\\d{10}$";
    public static final String IPV4_REGEX = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}"
                    + "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
    public static final String OTP_REGEX = "^\\d{6}$";

    public static final String SINGLE_CHOICE = "Single Choice";
    public static final String MULTI_CHOICE = "Multiple Choice";
    public static final String RECORDING = "recordings";
//    public static final String INTENT_ACTION_DISCONNECT = BuildConfig.APPLICATION_ID + ".Disconnect";

}
