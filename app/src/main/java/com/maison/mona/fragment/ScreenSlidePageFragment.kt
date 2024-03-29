import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.maison.mona.R

class ScreenSlidePageFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ):View{
        val view: View?
        val bundle = arguments
        val position = bundle?.get("position")

        Log.d("TUTORIAL", position.toString())

        view = when(position){
            0 -> inflater.inflate(R.layout.fragmentscreenslidepage, container, false)
            1 -> inflater.inflate(R.layout.fragmentscreenslidepage2, container, false)
            2 -> inflater.inflate(R.layout.fragmentscreenslidepage3, container, false)
            3 -> inflater.inflate(R.layout.fragmentscreenslidepage4, container, false)
            4 -> inflater.inflate(R.layout.fragmentscreenslidepage5, container, false)
            5 -> inflater.inflate(R.layout.fragmentscreenslidepage6, container, false)
            6 -> inflater.inflate(R.layout.fragmentscreenslidepage7, container, false)
            7 -> inflater.inflate(R.layout.fragmentscreenslidepage8, container, false)
            8 -> inflater.inflate(R.layout.fragmentscreenslidepage9, container, false)
            9 -> inflater.inflate(R.layout.fragmentscreenslidepage10, container, false)
            10 -> inflater.inflate(R.layout.fragmentscreenslidepage11, container, false)
            else -> inflater.inflate(R.layout.fragmentscreenslidepage, container, false)
        }
        return view
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }
}