package com.srpark.myapp.home.model.data

data class ExchangeRes(
    val code: String, // 검색코드
    val currencyCode: String, // 화폐코드
    val currencyName: String, // 화페이름
    val currencyUnit: Int, //기준화폐
    val country: String, // 국가
    val name: String, // 검색코드이름
    val date: String, // 데이터 날짜
    val time: String, // 데이터 시간
    val recurrenceCount: Int,
    val basePrice: Double, // 환율
    val openingPrice: Double,
    val highPrice: Double, //최고 환율
    val lowPrice: Double, //최저 환율
    val change: String,
    val changePrice: Double, //전일대비
    val cashBuyingPrice: Double,
    val cashSellingPrice: Double,
    val ttBuyingPrice: Double,
    val ttSellingPrice: Double,
    val tcBuyingPrice: Double,
    val fcSellingPrice: Double,
    val exchangeCommission: Double,
    val usDollarRate: Double,
    val high52wPrice: Double,
    val high52wDate: String,
    val low52wPrice: Double,
    val low52wDate: String,
    val provider: String, //제공처
    val timestamp: Long,
    val id: Int,
    val modifiedAt: String,
    val createdAt: String,
    val changeRate: Double,
    val signedChangePrice: Double, //변동차
    val signedChangeRate: Double //등락률
)