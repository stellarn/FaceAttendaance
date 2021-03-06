package com.kotlindev.noam.faceattendance

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.kotlindev.noam.faceattendance.ManageStudentsActivity.Companion.STUDENTS_DIR
import com.kotlindev.noam.faceattendance.SelectClassActivity.Companion.CLASS_LIST_TAG
import com.kotlindev.noam.faceattendance.SelectClassActivity.Companion.CLASS_OBJ_TAG
import com.kotlindev.noam.faceattendance.SelectClassActivity.Companion.EDIT_CLASS
import com.kotlindev.noam.faceattendance.adapters.StudentsSetAdapter
import com.kotlindev.noam.faceattendance.datasets.ClassObj
import com.kotlindev.noam.faceattendance.datasets.StudentSet
import kotlinx.android.synthetic.main.activity_edit_class.*
import org.jetbrains.anko.toast
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "UNCHECKED_CAST")
class EditClassActivity : AppCompatActivity() {

    companion object {
        const val TAG = "EditClassActivity"
    }
    private val allStudentList : ArrayList<StudentSet> = ArrayList()
    private val selectedStudents : TreeSet<StudentSet> = TreeSet()
    private lateinit var samplesDir : File
    private lateinit var studentListViewAdapter : StudentsSetAdapter
    private lateinit var oldClass: ClassObj
    private lateinit var classes : ArrayList<ClassObj>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_class)
        oldClass = intent.extras.get(CLASS_OBJ_TAG) as ClassObj
        classes = intent.extras.get(CLASS_LIST_TAG) as ArrayList<ClassObj>
        selectedStudents.addAll(oldClass.studentList)
        if (!oldClass.isNew){
            class_name.setText(oldClass.name)
            confirm_edit_class_btn.text = getString(R.string.confirm_btn)
        }
        samplesDir = intent.extras.getSerializable(STUDENTS_DIR) as File
        studentListViewAdapter = StudentsSetAdapter(this, allStudentList, selectedStudents,
                markColor = Color.parseColor("#2e7d32"))
        student_list_view.adapter = studentListViewAdapter
        student_list_view.setOnItemClickListener { _, _, position, _ ->
            setSelected(allStudentList[position])
        }
        confirm_edit_class_btn.setOnClickListener {
            submitClass()
        }
        readAllStudents()
    }

    private fun readAllStudents() {
        for (studentDir in samplesDir.listFiles().filter { it.isDirectory }){
            if (studentDir == samplesDir )
                continue
            val name = studentDir.name.filter { it.isLetter() || it.isWhitespace()}
            val id = studentDir.name.filter { it.isDigit() }.toInt()
            val numOfSamples = studentDir.listFiles().size
            val studentSet = StudentSet(name, studentDir, id, numOfSamples)
            allStudentList.add(studentSet)
        }
        studentListViewAdapter.notifyDataSetChanged()
    }


    private fun setSelected(currentItem: StudentSet) {
        if (currentItem in selectedStudents)
            selectedStudents.remove(currentItem)
        else
            selectedStudents.add(currentItem)
        studentListViewAdapter.notifyDataSetChanged()
    }

    private fun submitClass() {
        if (class_name.text.isBlank()){
            toast("Please fill Class name.")
            return
        }else{
            classes.forEach {
                if (!it.isNew  && it.name == class_name.text.toString()) {
                    toast("Class already exists!")
                    return
                }
            }
        }
        val result = Intent()
        result.putExtra(CLASS_OBJ_TAG, ClassObj(class_name.text.toString(), selectedStudents.size,
                selectedStudents)
        )
        setResult(EDIT_CLASS, result)
        this.finish()
    }
}
