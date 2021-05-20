package fi.tiko.vanillaweather

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import fi.tiko.vanillaweather.adapters.CityAdapter

class CitiesActivity : AppCompatActivity() {
    private lateinit var cities: MutableList<String>
    private lateinit var adapter: CityAdapter
    private lateinit var switchUseLocation: SwitchCompat

    private fun selectionChanged(selectedIndex: Int) {
        switchUseLocation.isChecked = selectedIndex == -1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cities)

        switchUseLocation = findViewById(R.id.switchUseLocation)

        cities = intent.getStringArrayExtra(CITIES)?.toMutableList() ?: mutableListOf()
        val selectedCity = intent.getIntExtra(SELECTED_CITY, -1)
        selectionChanged(selectedCity)

        val citiesList = findViewById<RecyclerView>(R.id.cityList)
        adapter = CityAdapter(cities, ::selectionChanged, selectedCity)
        citiesList.adapter = adapter

        supportActionBar?.title = getString(R.string.cities)
    }

    fun showDialog(view: View) {
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        val layout = inflater.inflate(R.layout.dialog_add_city, null)
        val editText = layout.findViewById<EditText>(R.id.editCityName)

        builder.setView(layout)
            .setTitle("Add City")
            // Add action buttons
            .setPositiveButton(
                "Add"
            ) { _, _ ->
                if (editText.text.isNotEmpty()) {
                    cities.add(editText.text.toString().trim())
                    adapter.notifyDataSetChanged()
                }
            }
            .setNegativeButton(
                "Cancel"
            ) { dialog, _ ->
                dialog.cancel()
            }

        builder.create().show()
    }

    private fun setIntentResult() {
        val intent = Intent()
        intent.putExtra(SELECTED_CITY, adapter.selectedIndex)
        intent.putExtra(CITIES, cities.toTypedArray())
        setResult(RESULT_OK, intent)
    }

    override fun onBackPressed() {
        setIntentResult()

        super.onBackPressed()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            // The back button in the action bar was pressed
            android.R.id.home -> {
                setIntentResult()
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun useLocationClicked(view: View) {
        if (switchUseLocation.isChecked) {
            // Clear the selected city when the switch is checked
            adapter.selectedIndex = -1
            adapter.notifyDataSetChanged()
        } else if (adapter.selectedIndex == -1 && cities.size > 0) {
            // Select the first city when the switch is unchecked
            adapter.selectedIndex = 0
            adapter.notifyDataSetChanged()
        }
    }

    // Toggle the location switch when the layout is clicked.
    // Needed for setting the switch to the left side of its text.
    fun useLocationLayoutClicked(view: View) {
        switchUseLocation.isChecked = !switchUseLocation.isChecked
        useLocationClicked(view)
    }
}