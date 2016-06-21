package com.ksddo.task2;

import android.support.wearable.watchface.CanvasWatchFaceService;

/**
 * Created by k_tetsuo on 2015/08/10.
 */
public class MyWatchFaceService extends CanvasWatchFaceService
{
    @Override
    public Engine onCreateEngine() {
        return new Engine();
    }

    private class Engine extends CanvasWatchFaceService.Engine {

        /**
         * called when ambient mode changed.
         * アンビエントモード(スクリーンオフ状態)が切り替わったときに呼ばれる
         */
        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            super.onAmbientModeChanged(inAmbientMode);

            invalidate();
        }
    }
}
