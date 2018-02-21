package com.jordilucas.heremap;

import android.app.Activity;

import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.GeoPosition;
import com.here.android.mpa.common.PositioningManager;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapFragment;
import com.here.android.mpa.mapping.MapState;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by jordisantos on 21/02/2018.
 */

public abstract class MapStateListener {

    private boolean mMapTouched = false;
    private boolean mMapSettled = false;
    private Timer mTimer;
    private static final int SETTLE_TIME = 500;

    private MapFragment mMap;
    private GeoCoordinate mLastPosition;
    private Activity mActivity;

    public MapStateListener(MapFragment map, TouchableMapFragment touchableMapFragment, Activity activity) {
        this.mMap = map;
        this.mActivity = activity;

        /*new PositioningManager.OnPositionChangedListener().onPositionUpdated();


        map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(GeoPosition cameraPosition) {
                unsettleMap();
                if(!mMapTouched) {
                    runSettleTimer();
                }
            }
        });*/

        touchableMapFragment.setTouchListener(new TouchableWrapper.OnTouchListener() {
            @Override
            public void onTouch() {
                touchMap();
                unsettleMap();
            }

            @Override
            public void onRelease() {
                releaseMap();
                runSettleTimer();
            }
        });
    }

    private void updateLastPosition() {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mLastPosition = MapStateListener.this.mMap.getMap().getCenter();
            }
        });
    }

    private void runSettleTimer() {
        updateLastPosition();

        if(mTimer != null) {
            mTimer.cancel();
            mTimer.purge();
        }
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        GeoCoordinate currentPosition = MapStateListener.this.mMap.getMap().getCenter();
                        if (currentPosition.equals(mLastPosition)) {
                            settleMap();
                        }
                    }
                });
            }
        }, SETTLE_TIME);
    }

    private synchronized void releaseMap() {
        if(mMapTouched) {
            mMapTouched = false;
            onMapReleased();
        }
    }

    private void touchMap() {
        if(!mMapTouched) {
            if(mTimer != null) {
                mTimer.cancel();
                mTimer.purge();
            }
            mMapTouched = true;
            onMapTouched();
        }
    }

    public void unsettleMap() {
        if(mMapSettled) {
            if(mTimer != null) {
                mTimer.cancel();
                mTimer.purge();
            }
            mMapSettled = false;
            mLastPosition = null;
            onMapUnsettled();
        }
    }

    public void settleMap() {
        if(!mMapSettled) {
            mMapSettled = true;
            onMapSettled();
        }
    }

    public abstract void onMapTouched();
    public abstract void onMapReleased();
    public abstract void onMapUnsettled();
    public abstract void onMapSettled();
}
