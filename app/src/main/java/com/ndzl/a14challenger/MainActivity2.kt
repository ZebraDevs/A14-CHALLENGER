package com.ndzl.a14challenger

import android.content.Context
import android.content.Intent

import android.os.Build
import android.os.Bundle

import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.credentials.CreateCredentialRequest
import androidx.credentials.CreateCredentialResponse
import androidx.credentials.CreatePasswordRequest
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.GetPasswordOption
import androidx.credentials.exceptions.CreateCredentialCancellationException
import androidx.credentials.exceptions.CreateCredentialException
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainActivity2 : AppCompatActivity() {

    private lateinit var credentialManager: CredentialManager

    private lateinit var ctx: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        ctx = this@MainActivity2

    }


    fun onClickbtn_TYPED_FGS(v: View?) {
        try {
            //TO PLAY WITH https://github.com/NDZL/-blog-A14/wiki/1-%E2%80%90-Min-Installable-Target-API
            //CHANGE TARGET API TO 29, THEN TEST THE COMPATIBILITY FRAMEWORK TOOL
            val serviceIntent = Intent(this, MyForegroundService::class.java)
            startForegroundService(serviceIntent)
        } catch (e: Exception) {
            Log.e("TAG", "onClickbtn_TYPED_FGS " + e.message)
        }
    }

    //https://developer.android.com/identity/sign-in/credential-provider#handle-password-queries
    //https://medium.com/novumlogic/using-androids-new-credential-manager-api-14a661cca66f
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    fun  onClickbtn_CREDENTIAL_MANAGER_STORE_PWD(v: View?) {


            try {
                //CREDENTIAL MANAGER NEEDS THIS APP TO TARGET API 34
                //HERE WE ARE GOING TEST THE SIMPLE USERNAME/PASSWORD STORAGE AND RETRIEVAL
                credentialManager = CredentialManager.create( applicationContext )

                val createPasswordRequest = CreatePasswordRequest(id = "ndzl", password = "Zebra123")
                GlobalScope.launch {
                    try {
                        val result = credentialManager.createCredential(ctx, createPasswordRequest)
                        //handleRegisterPasswordResult(result)
                        Log.d("onClickbtn_CREDENTIAL_MANAGER", "create result ${result.type} ${result.data}")
                    } catch (e: CreateCredentialCancellationException) {
                            //do nothing, the user chose not to save the credential
                            Log.v("onClickbtn_CREDENTIAL_MANAGER", "User cancelled the save")
                    } catch (e: CreateCredentialException) {
                        Log.d("onClickbtn_CREDENTIAL_MANAGER", "CreateCredentialException ${e.message}")
                    }
                }

            } catch (e: Exception) {
                Log.e("TAG", "onClickbtn_CREDENTIAL_MANAGER " + e.message)
            }


    }

    //result  get result android.credentials.TYPE_PASSWORD_CREDENTIAL Bundle[{androidx.credentials.BUNDLE_KEY_ID=ndzl, androidx.credentials.BUNDLE_KEY_PASSWORD=Zebra123}]
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    fun  onClickbtn_CREDENTIAL_MANAGER_RETRIEVE_PWD(v: View?) {
        try {
            if(!this::credentialManager.isInitialized)
                return

            val getCredRequest = GetCredentialRequest(listOf(GetPasswordOption()))

            GlobalScope.launch {

                    try {
                        val result = credentialManager.getCredential(ctx, getCredRequest)
                        //handleGetPasswordResult(result)
                        Log.d("onClickbtn_CREDENTIAL_MANAGER_RETRIEVE_PWD", "get result ${result.credential.type} ${result.credential.data}")
                    } catch (e: Exception) {
                        Log.d("onClickbtn_CREDENTIAL_MANAGER_RETRIEVE_PWD", "GetCredentialException ${e.message}")
                    }

            }

        } catch (e: Exception) {
            Log.e("TAG", "onClickbtn_CREDENTIAL_MANAGER " + e.message)
        }


    }


}