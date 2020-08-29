package com.federicocotogno.habittracker2020.ui.fragments.createhabit

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.federicocotogno.habittracker2020.R
import com.federicocotogno.habittracker2020.data.models.Habit
import com.federicocotogno.habittracker2020.logic.utils.Calculations
import com.federicocotogno.habittracker2020.ui.viewmodels.HabitViewModel
import kotlinx.android.synthetic.main.fragment_create_habit_item.*
import java.util.*

class CreateHabitItem : Fragment(R.layout.fragment_create_habit_item),
    TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    private var title = ""
    private var description = ""
    private var drawableSelected = 0
    private var timeStamp = ""

    private lateinit var habitViewModel: HabitViewModel

    private var day = 0
    private var month = 0
    private var year = 0
    private var hour = 0
    private var minute = 0

    private var cleanDate = ""
    private var cleanTime = ""


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        habitViewModel = ViewModelProvider(this).get(HabitViewModel::class.java)

        //Add habit to database
        btn_confirm.setOnClickListener {
            addHabitToDB()
        }
        //Pick a date and time
        pickDateAndTime()

        //Selected and image to put into our list
        drawableSelected()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    //set on click listeners for our data and time pickers
    private fun pickDateAndTime() {
        btn_pickDate.setOnClickListener {
            getDateCalendar()
            DatePickerDialog(requireContext(), this, year, month, day).show()
        }

        btn_pickTime.setOnClickListener {
            getTimeCalendar()
            TimePickerDialog(context, this, hour, minute, true).show()
        }

    }


    private fun addHabitToDB() {

        //Get text from editTexts
        title = et_habitTitle.text.toString()
        description = et_habitDescription.text.toString()

        //todo: create a utility function that checks whether the value of a month or day drops below 10,
        // and if it does, add a 0 to the end
        //Create a timestamp string for our recyclerview
        timeStamp = "$cleanDate $cleanTime"

        //Check that the form is complete before submitting data to the database
        if (formCompleted(title, description, timeStamp, drawableSelected)) {
            val habit = Habit(0, title, description, timeStamp, drawableSelected)

            //add the habit if all the fields are filled
            habitViewModel.addHabit(habit)
            Toast.makeText(context, "Habit created successfully!", Toast.LENGTH_SHORT).show()

            //navigate back to our home fragment
            findNavController().navigate(R.id.action_createHabitItem_to_habitList)
        } else {
            Toast.makeText(context, "Please fill all the fields", Toast.LENGTH_SHORT).show()
        }
    }

    //check that the form is complete before allowing the user to submit his request
    private fun formCompleted(
        _title: String,
        _description: String,
        _timeStamp: String,
        _drawableSelected: Int
    ): Boolean {
        return !(_title.isEmpty() || _description.isEmpty() || _timeStamp.isEmpty() || _drawableSelected == 0)
    }

    // Create a selector for our icons which will appear in the recycler view
    private fun drawableSelected() {
        iv_fastFoodSelected.setOnClickListener {
            iv_fastFoodSelected.isSelected = !iv_fastFoodSelected.isSelected
            drawableSelected = R.drawable.ic_fastfood

            //de-select the other options when we pick an image
            iv_smokingSelected.isSelected = false
            iv_teaSelected.isSelected = false

        }

        iv_smokingSelected.setOnClickListener {
            iv_smokingSelected.isSelected = !iv_smokingSelected.isSelected
            drawableSelected = R.drawable.ic_smoking2

            //de-select the other options when we pick an image
            iv_fastFoodSelected.isSelected = false
            iv_teaSelected.isSelected = false
        }

        iv_teaSelected.setOnClickListener {
            iv_teaSelected.isSelected = !iv_teaSelected.isSelected
            drawableSelected = R.drawable.ic_tea

            //de-select the other options when we pick an image
            iv_fastFoodSelected.isSelected = false
            iv_smokingSelected.isSelected = false
        }

    }

    //get the time set
    override fun onTimeSet(TimePicker: TimePicker?, p1: Int, p2: Int) {
        Log.d("Fragment", "Time: $p1:$p2")

        cleanTime = Calculations.cleanTime(p1, p2)
        tv_timeSelected.text = "Time: $cleanTime"
    }

    //get the date set
    override fun onDateSet(p0: DatePicker?, yearX: Int, monthX: Int, dayX: Int) {

        cleanDate = Calculations.cleanDate(dayX, monthX, yearX)
        tv_dateSelected.text = "Date: $cleanDate"
    }

    //get the current time
    private fun getTimeCalendar() {
        val cal = Calendar.getInstance()
        hour = cal.get(Calendar.HOUR_OF_DAY)
        minute = cal.get(Calendar.MINUTE)
    }

    //get the current date
    private fun getDateCalendar() {
        val cal = Calendar.getInstance()
        day = cal.get(Calendar.DAY_OF_MONTH)
        month = cal.get(Calendar.MONTH) + 1 //Month requires +1
        year = cal.get(Calendar.YEAR)
    }

}