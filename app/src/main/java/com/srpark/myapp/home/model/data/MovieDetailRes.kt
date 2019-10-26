package com.srpark.myapp.home.model.data

data class MovieDetailRes(val movieInfoResult: MovieInfoResult) {
    data class MovieInfoResult(val movieInfo: MovieInfo) {
        data class MovieInfo(
            val movieNm: String,
            val movieNmEn: String,
            val movieNmOg: String,
            val showTm: String, // 상영시간
            val openDt: String, // 개봉일
            val nations: List<Nations>,
            val genres: List<Genres>,
            val directors: List<Directors>,
            val actors: List<Actors>,
            val audits: List<Audits>
        )

        data class Nations(val nationNm: String) // 국가
        data class Genres(val genreNm: String) // 타입
        data class Directors(val peopleNm: String) // 감독

        data class Actors(
            val peopleNm: String, // 배우이름
            val cast: String // 배역이름
        )

        data class Audits(val watchGradeNm: String)
    }
}