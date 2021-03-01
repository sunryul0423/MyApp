package com.srpark.myapp.home.activity

import android.os.Bundle
import com.srpark.myapp.R
import com.srpark.myapp.base.BaseActivity
import com.srpark.myapp.databinding.ActivityMapBinding
import com.srpark.myapp.home.model.data.LottoInfoRes
import com.srpark.myapp.utils.ActivityConstant.INTENT_MAP_DATA
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint


class MapViewActivity : BaseActivity<ActivityMapBinding>() {
    override val layoutResourceId: Int
        get() = R.layout.activity_map

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolbar(viewBinding.toolbar.toolbar)
        viewBinding.lifecycleOwner = this
        val lottoResult = intent.getSerializableExtra(INTENT_MAP_DATA) as LottoInfoRes
        //TODO 정보 수정
//        viewBinding.toolbar.toolbarTitle.text = lottoResult.address
//        viewBinding.mapView.apply {
//
//            val intentPoint = MapPoint.mapPointWithGeoCoord(lottoResult.lat, lottoResult.lng)
//            // 중심점 변경
//            setMapCenterPoint(intentPoint, true)
//            // 줌 레벨 변경
//            setZoomLevel(2, true)
//            // 중심점 변경 + 줌 레벨 변경
//            setMapCenterPointAndZoomLevel(intentPoint, 2, true)
//            // 줌 인
//            zoomIn(true)
//            // 줌 아웃
//            zoomOut(true)
//
//            val marker = MapPOIItem().apply {
//                mapPoint = intentPoint
//                itemName = lottoResult.shopName
//                tag = 0
//                markerType = MapPOIItem.MarkerType.BluePin
//                selectedMarkerType = MapPOIItem.MarkerType.RedPin
//            }
//            addPOIItem(marker)
//        }
    }
}