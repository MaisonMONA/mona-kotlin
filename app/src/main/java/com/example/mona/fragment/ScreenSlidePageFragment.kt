import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.mona.R

class ScreenSlidePageFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ):View{
        var view: View? = null
        val bundle = arguments
        var position = bundle?.get("position")
        Log.d("TUTORIAL", position.toString())
        when(position){
            0 -> view = inflater.inflate(R.layout.fragmentscreenslidepage, container, false)
            1 -> view = inflater.inflate(R.layout.fragmentscreenslidepage2, container, false)
            else -> view = inflater.inflate(R.layout.fragmentscreenslidepage, container, false)
        }
        return view
    }
}