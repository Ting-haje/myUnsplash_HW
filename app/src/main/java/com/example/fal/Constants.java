package com.example.fal;

class Constants {

    /*
     * CONSTANT for Manifest MetaData
     * */
    static final String EXTRA_API_KEY = "unsplash_api_key";


    //UnSplash URL
    static final String UNSPLASH_URL = "https://unsplash.com/";


    //base URL for UnSplash API
    static final String BASE_URL = "https://api.unsplash.com/";

    static final String SEARCH_URL = "https://api.unsplash.com/search/";


    static String getExceptionMsg() {
        return String.format("Cannot found <meta-data android:name= '%s' android:value='XXXXXXXXXX' /> in Manifest",
                Constants.EXTRA_API_KEY);
    }

}
