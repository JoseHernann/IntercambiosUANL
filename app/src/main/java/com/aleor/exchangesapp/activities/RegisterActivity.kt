package com.aleor.exchangesapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import com.aleor.exchangesapp.databinding.ActivityRegisterBinding
import com.aleor.exchangesapp.models.Client
import com.aleor.exchangesapp.providers.AuthProvider
import com.aleor.exchangesapp.providers.ClientProvider

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val authProvider = AuthProvider()
    private val clientProvider = ClientProvider()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)

        setContentView(binding.root)
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        binding.btnGoToLogin.setOnClickListener { goToLogin() }
        binding.btnRegister.setOnClickListener { register() }
    }

    private fun register(){
        val name = binding.textFieldName.text.toString()
        val lastname = binding.textFieldLastname.text.toString()
        val email = binding.textFieldEmail.text.toString()
        val phone = binding.textFieldPhone.text.toString()
        val password = binding.textFieldPassword.text.toString()
        val confirmPassword = binding.textFieldConfirmPassword.text.toString()

        if(isValidForm(name, lastname, email, phone, password, confirmPassword)){

            authProvider.register(email, password).addOnCompleteListener { it ->
                if(it.isSuccessful){
                    val client = Client(
                        id = authProvider.getId(),
                        name = name,
                        lastname = lastname,
                        phone = phone,
                        email = email
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
//    }

    private fun isValidForm (name: String, lastname: String, email: String, phone: String, password: String, confirmPassword: String): Boolean {

        if (name.isEmpty()){
            Toast.makeText(this, "Debes ingresar tu nombre", Toast.LENGTH_SHORT).show()
            return false
        }
        if(lastname.isEmpty()){
            Toast.makeText(this, "Debes ingresar tu apellido", Toast.LENGTH_SHORT).show()
            return false
        }
        if(email.isEmpty()){
            Toast.makeText(this, "Debes ingresar tu correo", Toast.LENGTH_SHORT).show()
            return false
        }
        if(phone.isEmpty()){
            Toast.makeText(this, "Es necesario ingresar tu telefono", Toast.LENGTH_SHORT).show()
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
        return true
    }


    private fun goToLogin(){
        val i = Intent(this, MainActivity::class.java)
        startActivity(i)
    }

}