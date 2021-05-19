package fi.tiko.vanillaweather

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RadioButton
import androidx.recyclerview.widget.RecyclerView

// Adapter for a list of city names.
class CityAdapter(private val dataSet: MutableList<String>, var selectedIndex: Int = -1) :
    RecyclerView.Adapter<CityAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val radioName: RadioButton = view.findViewById(R.id.cityName)

        init {
            radioName.setOnClickListener {
                selectedIndex = adapterPosition
                notifyDataSetChanged()
            }

            val buttonRemove: ImageButton = view.findViewById(R.id.removeCity)
            buttonRemove.setOnClickListener {
                dataSet.removeAt(adapterPosition)
                // Adjust the selection to keep the correct item selected
                if (selectedIndex > adapterPosition) {
                    selectedIndex--
                }
                notifyDataSetChanged()
            }
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.city_item, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val cityName = dataSet[position]
        viewHolder.radioName.text = cityName
        viewHolder.radioName.isChecked = position == selectedIndex
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size
}
