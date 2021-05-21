package fi.tiko.vanillaweather

import android.content.Context
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

// Activity for editing the list of cities.
class CitiesActivity : AppCompatActivity() {
    // The editable list of city names.
    private lateinit var cities: MutableList<String>

    // Adapter for city names used by the RecyclerView.
    private lateinit var adapter: CityAdapter

    // The switch UI element for setting whether to use the current location or not.
    private lateinit var switchUseLocation: SwitchCompat

    // Changes the state of the "use current location" switch depending on the selection.
    private fun selectionChanged(selectedIndex: Int) {
        switchUseLocation.isChecked = selectedIndex == -1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cities)

        switchUseLocation = findViewById(R.id.switchUseLocation)

        val selectedCity =
            if (savedInstanceState != null) {
                // Get the values from the saved state.
                savedInstanceState.run {
                    getStringArray(CITIES).let { cities = it?.toMutableList() ?: mutableListOf() }
                    getInt(SELECTED_CITY, -1)
                }
            } else {
                // Get the values from the intent data.
                cities = intent.getStringArrayExtra(CITIES)?.toMutableList() ?: mutableListOf()
                intent.getIntExtra(SELECTED_CITY, -1)
            }
        selectionChanged(selectedCity)

        // Set up the RecyclerView.
        val citiesList = findViewById<RecyclerView>(R.id.cityList)
        adapter = CityAdapter(cities, ::selectionChanged, selectedCity)
        citiesList.adapter = adapter

        supportActionBar?.title = getString(R.string.cities)
    }

    // Preserves the selected city and the cities list.
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(SELECTED_CITY, adapter.selectedIndex)
        outState.putStringArray(CITIES, cities.toTypedArray())

        super.onSaveInstanceState(outState)
    }

    // Shows a dialog where a city name can be entered.
    fun showAddCityDialog(view: View) {
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)

        // Inflate and set the layout for the dialog.
        // Pass null as the parent view because its going in the dialog layout.
        val layout = inflater.inflate(R.layout.dialog_add_city, null)
        // Get a reference to the EditText in the dialog.
        val editText = layout.findViewById<EditText>(R.id.editCityName)

        // Build and show the dialog.
        builder.setView(layout)
            .setTitle(getString(R.string.add_city))
            // Add action buttons.
            .setPositiveButton(
                getString(R.string.add)
            ) { _, _ ->
                // Trim whitespace from the entered text.
                val cityName = editText.text.toString().trim()
                if (cityName.isNotEmpty() && !cities.contains(cityName)) {
                    cities.add(cityName)
                    adapter.notifyDataSetChanged()
                }
            }
            .setNegativeButton(
                getString(R.string.cancel)
            ) { dialog, _ ->
                dialog.cancel()
            }
            .create()
            .show()
    }

    // Adds the city list and the selected city index into the result intent.
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

    // Handles back button presses on the action bar.
    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            // The back button in the action bar was pressed.
            android.R.id.home -> {
                setIntentResult()
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    // Handles clicks on the "use current location" switch.
    fun useLocationClicked(view: View) {
        fun setSelectedIndex(index: Int) {
            adapter.selectedIndex = index
            adapter.notifyDataSetChanged()
        }

        if (switchUseLocation.isChecked) {
            // Clear the selected city when the switch is checked.
            setSelectedIndex(-1)
        } else if (adapter.selectedIndex == -1 && cities.size > 0) {
            // Select the first city when the switch is unchecked.
            setSelectedIndex(0)
        }
    }

    // Toggle the location switch when its parent layout is clicked.
    // Needed for setting the switch to the left side of its text.
    fun useLocationLayoutClicked(view: View) {
        switchUseLocation.isChecked = !switchUseLocation.isChecked
        useLocationClicked(view)
    }

    // Saves the city list and the selected city index into the shared preferences file.
    private fun savePreferences() {
        val sharedPref =
            getSharedPreferences(getString(R.string.preference_file), Context.MODE_PRIVATE)

        with(sharedPref.edit()) {
            putInt(SELECTED_CITY, adapter.selectedIndex)
            putString(CITIES, cities.joinToString(separator = ","))
            commit()
        }
    }

    override fun onPause() {
        savePreferences()
        super.onPause()
    }
}