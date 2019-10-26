package com.srpark.myapp.home.model.data

import java.io.Serializable

data class LottoInfoRes(
    val resultCode: String, //응답코드
    val statusCode: String, //HTTP 상태코드. (200성공, 404실패)
    val body: Body
) {
    data class Body(
        val drawNo: Long, //당첨회차
        val drawDate: String?, //당첨일자
        val drawDateYn: String?,
        val winningPlaces: List<WinningPlaces>,
        // 로또 정보
        val totalSellingPrice: Long, //총 판매액
        val num1: Int,
        val num2: Int,
        val num3: Int,
        val num4: Int,
        val num5: Int,
        val num6: Int,
        val bonusNum: Int, //보너스번호
        val lottoResult: List<LottoResult>,
        val description: String? //오류시 메세지
    ) : Serializable {
        data class LottoResult(
            val rank: String, //등수
            val sellingPriceByRank: Long, //등수 총금액
            val winningPriceByRank: Long, //당첨액
            val winningCnt: Int //당첨인원
        ) : Serializable

        data class WinningPlaces(
            val shopName: String,
            val gameType: String,
            val address: String,
            val lat: Double,
            val lng: Double
        ) : Serializable
    }
}