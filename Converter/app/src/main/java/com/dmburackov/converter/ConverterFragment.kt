package com.dmburackov.converter

import android.graphics.Color
import android.opengl.Visibility
import android.os.Bundle
import android.text.Selection.setSelection
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.dmburackov.converter.databinding.FragmentConverterBinding
import java.math.BigDecimal
import java.math.BigDecimal.ROUND_HALF_UP
import java.math.MathContext
import java.util.*
import java.util.Collections.swap


class ConverterFragment : Fragment() {
    private lateinit var binding : FragmentConverterBinding
    private val viewModel: MainViewModel by activityViewModels()

    private val categories = arrayOf("Length", "Weight", "Volume")
    private val weight = arrayOf("kg", "g", "q", "lb", "t")
    private val weightValue = arrayOf(1.0, 1000.0, 0.01, 2.2046226218488, 0.001)
    private val length = arrayOf("m", "cm", "km", "mi", "in", "ft", "yd")
    private val lengthValue = arrayOf(1.0, 100.0, 0.001, 0.00062137119223734, 39.3701, 3.28084, 1.0936132983378)
    private val volume = arrayOf("m3", "l", "hl", "qt", "gal")
    private val volumeValue =  arrayOf(1.0, 1000.0, 10.0, 1056.69, 264.172)

    private lateinit var limitToast : Toast

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentConverterBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.fromText.observe(activity as LifecycleOwner) {
            binding.fromEditText.setText(it)
        }

        viewModel.toText.observe(activity as LifecycleOwner) {
            binding.toEditText.text = it
        }

        viewModel.newKey.observe(activity as LifecycleOwner) {
            keyPressed(it)
        }

//        viewModel.showSwapButton.observe(activity as LifecycleOwner) {
//            if (viewModel.showSwapButton.value == true) {
//                binding.swapButton.visibility = View.VISIBLE
//            } else {
//                binding.swapButton.visibility = View.GONE
//            }
//        }

        viewModel.showSwapButton.observe(activity as LifecycleOwner, androidx.lifecycle.Observer { value ->
            if (value) {
                binding.swapButton.visibility = View.VISIBLE
            } else {
                binding.swapButton.visibility = View.GONE
            }
        })

        binding.swapButton.setOnClickListener { swap() }

        limitToast = Toast.makeText(requireActivity(), "Input max length limit.", Toast.LENGTH_SHORT)
        limitToast.setGravity(Gravity.CENTER, 0, 0);

        binding.fromEditText.requestFocus()
        setupEditText(binding.fromEditText)

        setupMainSpinner()
        setupFromSpinner()
        setupToSpinner()
    }

    private fun swap() {

    }

    private fun setupEditText(view: EditText){
        view.showSoftInputOnFocus = false
        view.setOnClickListener {
            view.isCursorVisible = false
            view.isCursorVisible = true
            view.setSelection(view.length())
        }
        //view.setHighlightColor(Color.TRANSPARENT)
        view.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                //Toast.makeText(requireActivity(), "focus", Toast.LENGTH_SHORT).show()
            } else {
                //Toast.makeText(requireActivity(), "anfocus", Toast.LENGTH_SHORT).show()
            }
        }
        view.isLongClickable = false
        view.customSelectionActionModeCallback = object : ActionMode.Callback{
            override fun onCreateActionMode(p0: ActionMode?, p1: Menu?): Boolean {
                return false
            }

            override fun onPrepareActionMode(p0: ActionMode?, p1: Menu?): Boolean {
                return false
            }

            override fun onActionItemClicked(p0: ActionMode?, p1: MenuItem?): Boolean {
                return true
            }

            override fun onDestroyActionMode(p0: ActionMode?) {}
        }
    }

    private fun numberCount(curr: String) : Int {
        var count : Int = 0
        for (item in curr) {
            if (item in '0'..'9') {
                ++count
            }
        }
        return count
    }

    private fun keyPressed(value: String) {
        var focusView = activity?.currentFocus

        if (focusView == binding.fromEditText) {
            var curr : String = binding.fromEditText.text.toString()
            when (value) {
                "clear" -> {
                    curr = ""
                }
                "delete" -> {
                    if (curr.isNotEmpty()) {
                        curr = curr.substring(0, curr.length - 1)
                    }
                }
                "." -> {
                    var pointExist = false
                    for (item in curr) {
                        if (item == '.') {
                            pointExist = true
                        }
                    }
                    if (!pointExist) {
                        if (curr.isEmpty()) {
                            curr = curr.plus("0$value")
                        } else {
                            curr = curr.plus(value)
                        }
                    }
                }
                else -> {
                    var count : Int = numberCount(curr)

                    if (count == 15) {
                        limitToast.show()
                    } else {
                        if (curr.length == 1 && curr[0] == '0') {
                            curr = value
                        } else {
                            curr = curr.plus(value)
                        }
                    }
                }
            }

            Toast.makeText(requireActivity(), numberCount(binding.toEditText.text.toString()).toString(), Toast.LENGTH_SHORT).show()
            viewModel.showSwapButton.value = numberCount(binding.toEditText.text.toString()) <= 15
            viewModel.fromText.value = curr
            binding.fromEditText.setSelection(binding.fromEditText.length())
            updateResultText()
        }
    }

    private fun updateResultText() {
        var curr : String = binding.fromEditText.text.toString()
        if (curr.isNotEmpty()) {
            Formatter.BigDecimalLayoutForm.SCIENTIFIC
            var convertValue: BigDecimal = curr.toBigDecimal()
            var coefFrom: Double = 0.0
            var coefTo: Double = 0.0

            when (viewModel.currentCategory) {
                0 -> {
                    coefFrom = lengthValue[viewModel.currentFromUnit]
                    coefTo = lengthValue[viewModel.currentToUnit]
                }
                1 -> {
                    coefFrom = weightValue[viewModel.currentFromUnit]
                    coefTo = weightValue[viewModel.currentToUnit]
                }
                else -> {
                    coefFrom = volumeValue[viewModel.currentFromUnit]
                    coefTo = volumeValue[viewModel.currentToUnit]
                }
            }
            convertValue = convertValue * (coefTo / coefFrom).toBigDecimal()

            var resultString = convertValue.toString()

            if (resultString.indexOf("E") != -1) {
                var power = resultString.substring(resultString.indexOf('-') + 1, resultString.length).toInt()
                var str = "0."
                for (i in 1..power) {
                    str = str.plus("0")
                }
                str = str.plus(resultString[0])
                str = str.plus(resultString.substring(resultString.indexOf(".") + 1, resultString.indexOf("E") - 1))
                resultString = str
            }

            if (resultString.indexOf(".") != -1) {
                resultString = resultString.substring(0, min(resultString.length, 23))
            }

            while (resultString.last() == '0') {
                resultString = resultString.substring(0, resultString.length - 1)
            }

            if (resultString.last() == '.') {
                resultString = resultString.substring(0, resultString.length - 1)
            }

            viewModel.toText.value = resultString
        } else {
            viewModel.toText.value = ""
        }
    }

    private fun min(x: Int, y: Int): Int {
        return if (x < y) x else y
    }

    private fun setupMainSpinner() {
        binding.spinner.adapter = ArrayAdapter<String>(requireActivity(), android.R.layout.simple_list_item_1, categories)

        binding.spinner.setSelection(viewModel.currentCategory)

        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (viewModel.currentCategory != p2) {
                    viewModel.currentFromUnit = 0
                    viewModel.currentToUnit = 0
                }
                viewModel.currentCategory = p2
                setupFromSpinner()
                setupToSpinner()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                viewModel.currentCategory = 0
            }

        }
    }

    private fun setupFromSpinner() {
        when (viewModel.currentCategory) {
            0 -> binding.fromSpinner.adapter = ArrayAdapter<String>(requireActivity(), android.R.layout.simple_list_item_1, length)
            1 -> binding.fromSpinner.adapter = ArrayAdapter<String>(requireActivity(), android.R.layout.simple_list_item_1, weight)
            else -> binding.fromSpinner.adapter = ArrayAdapter<String>(requireActivity(), android.R.layout.simple_list_item_1, volume)
        }

        binding.fromSpinner.setSelection(viewModel.currentFromUnit)

        binding.fromSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                viewModel.currentFromUnit = p2
                updateResultText()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                viewModel.currentFromUnit = 0
            }

        }
    }

    private fun setupToSpinner() {
        when (viewModel.currentCategory) {
            0 -> binding.toSpinner.adapter = ArrayAdapter<String>(requireActivity(), android.R.layout.simple_list_item_1, length)
            1 -> binding.toSpinner.adapter = ArrayAdapter<String>(requireActivity(), android.R.layout.simple_list_item_1, weight)
            else -> binding.toSpinner.adapter = ArrayAdapter<String>(requireActivity(), android.R.layout.simple_list_item_1, volume)
        }

        binding.toSpinner.setSelection(viewModel.currentToUnit)

        binding.toSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                viewModel.currentToUnit = p2
                updateResultText()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                viewModel.currentToUnit = 0
            }

        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = ConverterFragment()
    }
}
