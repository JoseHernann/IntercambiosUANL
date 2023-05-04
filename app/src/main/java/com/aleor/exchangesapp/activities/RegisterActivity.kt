package com.aleor.exchangesapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import com.aleor.exchangesapp.R
import com.aleor.exchangesapp.databinding.ActivityRegisterBinding
import com.aleor.exchangesapp.models.Client
import com.aleor.exchangesapp.providers.AuthProvider
import com.aleor.exchangesapp.providers.ClientProvider

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val authProvider = AuthProvider()
    private val clientProvider = ClientProvider()
    var faculty = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)

        setContentView(binding.root)
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        binding.btnGoToLogin.setOnClickListener { goToLogin() }
        binding.btnRegister.setOnClickListener { register() }

        val spinner: Spinner = binding.listFac
        ArrayAdapter.createFromResource(
            this,
            R.array.Facultades,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }
        //listener del dropDown
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                faculty = parent.getItemAtPosition(position) as String
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
    }

    private fun register(){
        val name = binding.textFieldName.text.toString()
        val email = binding.textFieldEmail.text.toString()
        val password = binding.textFieldPassword.text.toString()
        val confirmPassword = binding.textFieldConfirmPassword.text.toString()

        if(isValidForm(name, email, password, confirmPassword,faculty)){

            authProvider.register(email, password).addOnCompleteListener { it ->
                if(it.isSuccessful){
                    val client = Client(
                        id = authProvider.getId(),
                        name = name,
                        email = email,
                        faculty = faculty
                    )
                    clientProvider.create(client).addOnCompleteListener {
                        if(it.isSuccessful){
                            Toast.makeText(this@RegisterActivity, "Registro exitoso", Toast.LENGTH_SHORT).show()


                        }
                        else{
                            Toast.makeText(this@RegisterActivity, "Hubo un error almacenando los datos del usuario ${it.exception.toString()}", Toast.LENGTH_SHORT).show()
                            Log.d("FIREBASE", "Error: ${it.exception.toString()}")
                        }
                    }
                }
                else{
                    Toast.makeText(this@RegisterActivity, "Registro fallido ${it.exception.toString()}", Toast.LENGTH_LONG).show()
                    Log.d("FIREBASE", "Error: ${it.exception.toString()}")
                }
                
            }

        }

    }


//    private fun goToHome(){
//        val i = Intent(this, HomeActivity::class.java)
//        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
//        startActivity(i)
//   }




    //Ingreso de los datos con validaciones en caso de que alguno no sea llenado no se realizara el registro 
    private fun isValidForm (
        name: String,
        email: String,
        password: String,
        confirmPassword: String,
        faculty: String
    ): Boolean {


        if (name.isEmpty()){
            Toast.makeText(this, "Debes ingresar tu nombre", Toast.LENGTH_SHORT).show()
            return false
        }
        if(email.isEmpty()){
            Toast.makeText(this, "Debes ingresar tu correo", Toast.LENGTH_SHORT).show()
            return false
        }
        if(password.isEmpty()){
            Toast.makeText(this, "Porfavor ingresa la contraseña", Toast.LENGTH_SHORT).show()
            return false
        }
        if(confirmPassword.isEmpty()){
            Toast.makeText(this, "Porfavor confirma la contraseña", Toast.LENGTH_SHORT).show()
            return false
        }
        if(password != confirmPassword){
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            return false
        }
        if(password.length < 6){
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_LONG).show()
        }
        if(faculty.isEmpty()){
            Toast.makeText(this, "Porfavor selecciona una facultad", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }


    private fun goToLogin(){
        val i = Intent(this, MainActivity::class.java)
        startActivity(i)
    }

}