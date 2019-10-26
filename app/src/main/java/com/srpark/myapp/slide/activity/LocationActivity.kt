package com.srpark.myapp.slide.activity

import android.os.Bundle
import com.jakewharton.rxbinding2.view.RxView
import com.srpark.myapp.R
import com.srpark.myapp.base.BaseActivity
import com.srpark.myapp.databinding.ActivityLocationBinding
import com.srpark.myapp.preference.AppPreference
import com.srpark.myapp.utils.onClick
import io.reactivex.android.schedulers.AndroidSchedulers
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class LocationActivity : BaseActivity<ActivityLocationBinding>(), MapView.CurrentLocationEventListener,
    MapView.MapViewEventListener {

    override val layoutResourceId: Int
        get() = R.layout.activity_location

    @Inject
    lateinit var appPreference: AppPreference
    private lateinit var currentPoint: MapPoint
    private lateinit var marker: MapPOIItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding.lifecycleOwner = this
        val locationList: List<String> = appPreference.location.split(",")
        currentPoint = MapPoint.mapPointWithGeoCoord(locationList[0].toDouble(), locationList[1].toDouble())

        marker = MapPOIItem().apply {
            itemName = getString(R.string.my_location)
            tag = 0
            markerType = MapPOIItem.MarkerType.RedPin
            selectedMarkerType = MapPOIItem.MarkerType.RedPin
        }
        viewBinding.mapView.apply {
            setZoomLevel(2, true)
            zoomIn(true)
            zoomOut(true)

            removeAllPOIItems()
            marker.mapPoint = currentPoint
            addPOIItem(marker)
            setMapCenterPoint(currentPoint, true)
        }

        viewBinding.mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading
        viewBinding.mapView.setCurrentLocationEventListener(this)
        viewBinding.mapView.setMapViewEventListener(this)

        addDisposable(RxView.clicks(viewBinding.fbMyLocation)
            .throttleFirst(1, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
            .map {
                viewBinding.mapView.apply {
                    removeAllPOIItems()
                    marker.mapPoint = currentPoint
                    addPOIItem(marker)
                    setMapCenterPoint(currentPoint, true)
                }
            }.filter {
                it.currentLocationTrackingMode != MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading
            }
            .subscribe {
                it.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading
            }
        )
    }

    override fun onResume() {
        super.onResume()
        viewBinding.mapView.onResume()
    }

    override fun onPause() {
        viewBinding.mapView.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        if (viewBinding.mapView.currentLocationTrackingMode != MapView.CurrentLocationTrackingMode.TrackingModeOff) {
            viewBinding.mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOff
        }
        viewBinding.mapView.setCurrentLocationEventListener(null)
        super.onDestroy()
    }

    override fun onCurrentLocationUpdateFailed(mapView: MapView?) {
    }

    override fun onCurrentLocationUpdate(mapView: MapView?, point: MapPoint?, p2: Float) {
        if (mapView != null && point != null) {
            currentPoint = point
            mapView.removeAllPOIItems()
            marker.mapPoint = currentPoint
            mapView.addPOIItem(marker)
            mapView.setMapCenterPoint(point, true)
        }
    }

    override fun onCurrentLocationUpdateCancelled(mapView: MapView?) {
    }

    override fun onCurrentLocationDeviceHeadingUpdate(mapView: MapView?, p1: Float) {
    }

    override fun onMapViewInitialized(mapView: MapView?) {
    }

    override fun onMapViewDragStarted(mapView: MapView?, p1: MapPoint?) {
        if (mapView != null && mapView.currentLocationTrackingMode != MapView.CurrentLocationTrackingMode.TrackingModeOff) {
            mapView.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOff
        }
    }

    override fun onMapViewDragEnded(mapView: MapView?, p1: MapPoint?) {
    }

    override fun onMapViewCenterPointMoved(mapView: MapView?, p1: MapPoint?) {
    }

    override fun onMapViewMoveFinished(mapView: MapView?, p1: MapPoint?) {
    }

    override fun onMapViewSingleTapped(mapView: MapView?, p1: MapPoint?) {
    }

    override fun onMapViewDoubleTapped(mapView: MapView?, p1: MapPoint?) {
    }

    override fun onMapViewZoomLevelChanged(mapView: MapView?, p1: Int) {
    }

    override fun onMapViewLongPressed(mapView: MapView?, p1: MapPoint?) {
    }
}