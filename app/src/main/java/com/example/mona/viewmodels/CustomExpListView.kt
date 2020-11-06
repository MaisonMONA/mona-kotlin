import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import com.example.mona.R


class CustomExpListView(context: Context?,sizeChildren: Int) :
    ExpandableListView(context) {
    var sizeChildren = sizeChildren
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        //Log.d("Custom", numberChild.toString())
        var widthMeasureSpec = widthMeasureSpec
        //widthMeasureSpec = MeasureSpec.makeMeasureSpec(960 , MeasureSpec.AT_MOST)
        var heightMeasureSpec = MeasureSpec.makeMeasureSpec(sizeChildren, MeasureSpec.AT_MOST)
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }
}