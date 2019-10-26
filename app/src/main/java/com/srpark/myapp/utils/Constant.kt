package com.srpark.myapp.utils

object RetrofitConstant {

    // Lotto
    const val LOTTO_MY_PAGE_URL = "https://m.dhlottery.co.kr/common.do?method=main"
    const val DATE_FORMAT = "yyyy-MM-dd"
    const val RANK_FIRST = "FIRST"
    const val RANK_SECOND = "SECOND"
    const val RANK_THIRD = "THIRD"
    const val RANK_FOURTH = "FOURTH"
    const val RANK_FIFTH = "FIFTH"

    //Shop
    const val SIM = "sim" // 유사도순
    const val DATE = "date" // 날짜순
    const val DSC = "dsc" // 높은가격순
    const val ASC = "asc" // 낮은가격순
}

object DatabaseConstant {
    const val LOTTO = "lotto"
    const val LAST_NO = "lastNo"
    const val LAST_DATE = "lastDate"
}

object ActivityConstant {
    const val INTENT_MAP_DATA = "mapData"
    const val INTENT_WEB_URL = "webUrl"
    const val INTENT_LOTTO_DETAIL = "lottoDetail"
    const val INTENT_RECORD_DATA = "recordData"
    const val INTENT_MOVIE_DATA = "movieData"

}

object ServiceConstant {
    const val LOTTO_ALARM_ID = 10
    const val LOTTO_ACTION = "com.srpark.myapp.action.LOTTO_NOTIFICATION"
    const val GPS_SERVICE_ID = 1
}