package com.srpark.myapp.home.model.data

import java.io.Serializable

data class LottoInfoRes(
    val returnValue: String, // 성공여부 fail, success
    val drwNoDate: String, //당첨 날짜
    val totSellamnt: Long, // 총 금액
    val firstAccumamnt: Long, // 1등 총 당첨금
    val firstWinamnt: Long, // 1인당 1등 당첨금
    val firstPrzwnerCo: Int, // 1등 당첨자 수
    val drwNo: Long, // 회차
    val drwtNo1: Int,
    val drwtNo2: Int,
    val drwtNo3: Int,
    val drwtNo4: Int,
    val drwtNo5: Int,
    val drwtNo6: Int,
    val bnusNo: Int
) : Serializable