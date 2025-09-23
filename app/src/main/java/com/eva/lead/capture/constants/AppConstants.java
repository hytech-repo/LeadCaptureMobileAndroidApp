package com.eva.lead.capture.constants;


public class AppConstants {

    public static String APP_PREF = "app-pref";

    public static String USER_TOKEN = "user_token";
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

    public static final String BARCODE_REGEX = "^[A-Za-z]+-\\d+$";
    public static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    public static final String IPV4_REGEX = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}"
                    + "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
    public static final String OTP_REGEX = "^\\d{6}$";

    public static final String LABEL_FULL_NAME = "{%FULL_NAME%}";
    public static final String LABEL_FIRST_NAME = "{%FIRST_NAME%}";
    public static final String LABEL_LAST_NAME = "{%LAST_NAME%}";
    public static final String LABEL_EMAIL = "{%EMAIL%}";
    public static final String LABEL_JOBTITLE = "{%JOB_TITLE%}";
    public static final String LABEL_QRCODE = "{%QR_CODE%}";
    public static final String LABEL_ORGANIZATION = "{%ORGANIZATION%}";
    public static final String LABEL_CITY = "{%CITY%}";
    public static final String LABEL_STREET = "{%STREET%}";
    public static final String LABEL_COUNTRY = "{%COUNTRY%}";
    public static final String LABEL_PHONE = "{%PHONE%}";
    public static final String LABEL_STATE = "{%STATE%}";
    public static final String LABEL_REGISTRANT_ID = "{%REGISTRANT_ID%}";
    public static final String LABEL_POSTAL_CODE = "{%POSTAL_CODE%}";
//    public static final String INTENT_ACTION_DISCONNECT = BuildConfig.APPLICATION_ID + ".Disconnect";

}
