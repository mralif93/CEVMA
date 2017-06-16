package com.example.alip.evcma.app;

/**
 * Created by Alip on 4/8/2017.
 */

public class AppConfig {

//    public final static String ipAddress = "http://10.59.41.144";
    private final static String ipAddress = "http://192.168.0.3";

    public static String URL_LOGIN = ipAddress + "/evc/api/android_login_api.php";
    public static String URL_LOGIN_CANDIDATE = ipAddress + "/evc/api/android_login_candidate_api.php";
    public static String URL_LOGIN_CHECK = ipAddress + "/evc/api/android_login_check_api.php";
    public static String URL_SEARCH_USER = ipAddress + "/evc/api/android_search_user_api.php";
    public static String URL_UPDATE_USER = ipAddress + "/evc/api/android_update_user_api.php";
    public static String URL_UPDATE_PROFILE_USER =  ipAddress + "/evc/api/android_update_profile_api.php";
    public static String URL_PROFILE_IMAGE = ipAddress + "/evc/uploads/profile/";

    public static String URL_ANNOUNCEMENT = ipAddress + "/evc/api/android_announcement_api.php";
    public static String URL_ANNOUNCEMENT_PAGE = ipAddress + "/evc/api/android_announcement_page.php";
    public static String URL_ANNOUNCEMENT_IMAGE = ipAddress + "/evc/uploads/";

    public static String URL_POLL = ipAddress + "/evc/api/android_poll_api.php";
    public static String URL_POLL_PAGE = ipAddress + "/evc/api/android_poll_page.php";
    public static String URL_POLL_IMAGE = ipAddress + "/evc/uploads/";

    public static String URL_CANDIDATES = ipAddress + "/evc/api/android_candidates_api.php";
    public static String URL_CANDIDATES_POLL = ipAddress + "/evc/api/android_candidate_poll.php";
    public static String URL_CANDIDATES_IMAGE = ipAddress + "/evc/uploads/profile/";
    public static String URL_CANDIDATES_PAGE = ipAddress + "/evc/api/android_candidate_page.php";
    public static String URL_SEARCH_CANDIDATE_INFO = ipAddress + "/evc/api/android_candidate_search_info_api.php";
    public static String URL_CANDIDATES_UPDATE_INFO = ipAddress + "/evc/api/android_candidate_update_info_api.php";

    public static String URL_RESULT = ipAddress + "/evc/api/android_result_api.php";
    public static String URL_RESULT_VOTE = ipAddress + "/evc/api/android_result_vote.php";
    public static String URL_RESULT_POLL = ipAddress + "/evc/api/android_result_poll.php";

}