package com.example.android.politicalpreparedness.representative.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener

// adapted from https://medium.com/geekculture/simplifying-using-spinners-in-android-ad14f8f1213d
class BindableSpinnerAdapter (context: Context, textViewResourceId: Int, private val values: List<String>) :
    ArrayAdapter<String>(context, textViewResourceId, values) {

    override fun getCount() = values.size

    override fun getItem(position: Int) = values[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val label = super.getView(position, convertView, parent) as TextView
        label.text = values[position]
        return label
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val label = super.getDropDownView(position, convertView, parent) as TextView
        label.text = values[position]
        return label
    }


    companion object {

        @BindingAdapter(value = ["spinnerItems", "selectedSpinnerItem", "selectedSpinnerItemAttrChanged"], requireAll = false)
        @JvmStatic
        fun setSpinnerItems(spinner: Spinner, spinnerItems: List<String>?, selectedSpinnerItem: String?, listener: InverseBindingListener?) {
            val selectedItem = (spinner.selectedItem as? String)
            if (selectedItem != null && selectedSpinnerItem == selectedItem) {
                return
            }

            spinnerItems?.let {
                spinner.adapter = BindableSpinnerAdapter(spinner.context, android.R.layout.simple_spinner_dropdown_item, it)
                setCurrentSelection(spinner, selectedSpinnerItem)
                setSpinnerListener(spinner, listener)
            }
        }

        @InverseBindingAdapter(attribute = "selectedSpinnerItem")
        @JvmStatic
        fun getSelectedSpinnerItem(spinner: Spinner): String {
            return spinner.selectedItem as String
        }

        @JvmStatic
        private fun setSpinnerListener(spinner: Spinner, listener: InverseBindingListener?) {
            listener?.let {
                spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) = it.onChange()

                    override fun onNothingSelected(adapterView: AdapterView<*>) = it.onChange()
                }
            }
        }

        @JvmStatic
        private fun setCurrentSelection(spinner: Spinner, selectedItem: String?): Boolean {
            selectedItem?.let {
                for (index in 0 until spinner.adapter.count) {
                    if (spinner.getItemAtPosition(index) == it) {
                        spinner.setSelection(index)
                        return true
                    }
                }
            }

            return false
        }

    }

}