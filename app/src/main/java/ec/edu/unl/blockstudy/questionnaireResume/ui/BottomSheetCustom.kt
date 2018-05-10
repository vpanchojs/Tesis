package ec.edu.unl.blockstudy.questionnaireResume.ui

import android.content.Context
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.CoordinatorLayout
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View


/**
 * Created by victor on 22/3/18.
 */
class BottomSheetCustom<V : View> : BottomSheetBehavior<V> {

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor() : super()


    override fun onInterceptTouchEvent(parent: CoordinatorLayout?, child: V, event: MotionEvent?): Boolean {
        return false
    }

    override fun onTouchEvent(parent: CoordinatorLayout?, child: V, event: MotionEvent?): Boolean {
        return false
    }


    override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout, child: V, directTargetChild: View, target: View, axes: Int, type: Int): Boolean {
        return false
    }

    override fun onNestedPreFling(coordinatorLayout: CoordinatorLayout, child: V, target: View, velocityX: Float, velocityY: Float): Boolean {
        return false
    }

}