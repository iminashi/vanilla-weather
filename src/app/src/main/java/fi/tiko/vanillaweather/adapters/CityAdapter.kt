package fi.tiko.vanillaweather.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RadioButton
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import fi.tiko.vanillaweather.R

// RecyclerView adapter for city names.
class CityAdapter(
    private val dataSet: MutableList<String>,
    private val selectionChanged: (Int) -> Unit,
    var selectedIndex: Int = -1
) : RecyclerView.Adapter<CityAdapter.ViewHolder>() {

    // Updates the selected index and calls the selectionChanged callback.
    private fun changeSelection(index: Int) {
        selectedIndex = index
        selectionChanged(selectedIndex)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val radioName: RadioButton = view.findViewById(R.id.cityName)
        val buttonRemove: ImageButton = view.findViewById(R.id.removeCity)

        init {
            // Update the selected index when the radio button is clicked.
            radioName.setOnClickListener {
                changeSelection(adapterPosition)
                notifyDataSetChanged()
            }

            buttonRemove.setOnClickListener {
                dataSet.removeAt(adapterPosition)
                if (selectedIndex == adapterPosition) {
                    // Set to no selection if the selected item is deleted.
                    changeSelection(-1)
                } else if (selectedIndex > adapterPosition) {
                    // Adjust the selection to keep the correct item selected.
                    changeSelection(selectedIndex - 1)
                }
                notifyDataSetChanged()
            }
        }
    }

    // Create new views (invoked by the layout manager).
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item.
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.city_item, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager).
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val cityName = dataSet[position]
        viewHolder.radioName.text = cityName
        viewHolder.radioName.isChecked = position == selectedIndex

        // Hide the remove button when there is only one city name.
        viewHolder.buttonRemove.isVisible = dataSet.size != 1
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size
}
