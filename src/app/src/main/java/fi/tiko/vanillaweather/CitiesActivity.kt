package fi.tiko.vanillaweather

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView

class CitiesActivity : AppCompatActivity() {
    private lateinit var cities: MutableList<String>
    private lateinit var adapter: CityAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cities)

        cities = intent.getStringArrayExtra("cities")?.toMutableList() ?: mutableListOf()
        val selectedCity = intent.getIntExtra("selectedCity", -1)
        val citiesList = findViewById<RecyclerView>(R.id.cityList)
        adapter = CityAdapter(cities, selectedCity)
        citiesList.adapter = adapter
    }

    fun showDialog(view: View) {
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        val layout = inflater.inflate(R.layout.dialog_add_city, null)
        val editText = layout.findViewById<EditText>(R.id.editCityName)

        builder.setView(layout)
            // Add action buttons
            .setPositiveButton(
                "Add"
            ) { _, _ ->
                if (editText.text.isNotEmpty()) {
                    cities.add(editText.text.toString())
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
        intent.putExtra("selectedCity", adapter.selectedIndex)
        intent.putExtra("cities", cities.toTypedArray())
        setResult(RESULT_OK, intent)
    }

    override fun onBackPressed() {
        setIntentResult()
        super.onBackPressed()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                setIntentResult()
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}