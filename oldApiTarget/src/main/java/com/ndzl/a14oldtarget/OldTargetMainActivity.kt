package com.ndzl.a14oldtarget

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import java.util.UUID
import java.util.regex.Pattern

class OldTargetMainActivity : AppCompatActivity()  {


    private lateinit var ctx: Context



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        ctx = this@OldTargetMainActivity

    }

    fun onClickbtn_JDK17_1(v: View?) {
//        try {

        var invalidUuidString: String = " 123e4567-e89b-12d3-a456-426655440000 " // Leading/trailing spaces;
        var uuid: UUID = UUID.fromString(invalidUuidString)  //onClickbtn_JDK17 Invalid UUID string: 12345 exception


        // a java.lang.IllegalStateException is raised,  Caused by: java.lang.NumberFormatException: For input string: " 123e4567" under radix 16

//
//        } catch (e: Exception) {
//            Log.e("TAG", "onClickbtn_JDK17 " + e.printStackTrace())
//
//        }
    }

    fun onClickbtn_JDK17_2(v: View?) {
        //intentionally left without try/catch block to raise an exception and compare across android targets
        //REGEX

        OldTargetRegexStressTest.go()

    }

    fun onClickbtn_JDK17_3(v: View?) {
        //intentionally left without try/catch block to raise an exception and compare across android targets
        //REGEX

        OldTargetRegexStressTest.three()

    }

}