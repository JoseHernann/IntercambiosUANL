package com.aleor.exchangesapp.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aleor.exchangesapp.R
import com.aleor.exchangesapp.databinding.ActivityFormsBinding
import com.aleor.exchangesapp.models.Client
import com.aleor.exchangesapp.models.Products
import com.aleor.exchangesapp.providers.ClientProvider
import com.bumptech.glide.Glide


class FormsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFormsBinding
    private lateinit var imageRV: RecyclerView
    private lateinit var imageAdapter: ImageAdapter
    private var imagesSelected = false
    private val clientProvider = ClientProvider()

    var canSave: Boolean = true
    private var state: String = ""
    var category: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFormsBinding.inflate(layoutInflater)
        setContentView(binding.root)

       val grupEstado = binding.grupEstado
        grupEstado.setOnCheckedChangeListener{
            grup, checkedId ->
            state = when(checkedId) {
                R.id.radioNuevo -> "Nuevo"
                R.id.radioUsado -> "Usado"
                R.id.radioSeminuevo -> "Seminuevo"
                else -> ""
            }
        }

        //ESTO SE PODRIA MODIFICAR CREO
        binding.back.setOnClickListener{
            backToHome()
        }
        binding.btnCancelar.setOnClickListener {
            backToHome()
        }

        val spinner: Spinner = binding.listCat
        ArrayAdapter.createFromResource(
            this,
            R.array.Categorias,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        //listener del dropDown
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
               category = parent.getItemAtPosition(position) as String
                canSave = true
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                canSave = false
            }
        }

        binding.btnGuardar.setOnClickListener{
            if(canSave) {
                saveProduct()

            }
            else{
                println("NO SE PUEDE GUARDAR")
            }
        }


        imageRV = findViewById(R.id.imageRV)
        imageAdapter = ImageAdapter()

        val layoutManager = GridLayoutManager(this, 3)
        imageRV.layoutManager = layoutManager
        imageRV.adapter = imageAdapter

        //Solicitar permiso para la galeria
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // El permiso no ha sido otorgado
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                1
            )
        }

        // Asignar listener del botón para seleccionar imágenes
        binding.pickImage.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
            ) {
                pickImages.launch("image/*")
            } else {
                // El permiso no ha sido otorgado
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    1
                )
            }
        }
    }

    private fun saveProduct() {
        val name = binding.etProductName.text.toString()
        val description = binding.etDescription.text.toString()

        val product = Products(
            name = name,
            category = category,
            description = description,
            sate = state
        )
        clientProvider.create(product).addOnCompleteListener {
            if(it.isSuccessful){
                Toast.makeText(this@FormsActivity, "Registro exitoso", Toast.LENGTH_SHORT).show()
                backToHome()
            }
            else{
                Toast.makeText(this@FormsActivity, "Hubo un error almacenando los datos ${it.exception.toString()}", Toast.LENGTH_SHORT).show()
                Log.d("FIREBASE", "Error: ${it.exception.toString()}")
            }
        }
    }
    private fun backToHome(){
        val intent =  Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }

    //Seleccion de imagenes
    private val pickImages =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
            imageAdapter.addImages(uris)
            imagesSelected = true
            binding.pickImage.visibility = View.GONE
        }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // El permiso ha sido otorgado, asignar listener del botón para seleccionar imágenes
            binding.pickImage.setOnClickListener {
                pickImages.launch("image/*")
            }
        } else {
            // El permiso no ha sido otorgado
            Toast.makeText(this@FormsActivity, "Permiso denegado", Toast.LENGTH_SHORT).show()
        }
    }

    class ImageAdapter : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {
        private val images = mutableListOf<Uri>()

        fun addImages(uris: List<Uri>) {
            images.addAll(uris)
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.image_item, parent, false)
            return ImageViewHolder(view)
        }

        override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
            val imageUri = images[position]
            holder.bind(imageUri)
        }

        override fun getItemCount(): Int = images.size

        class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val imageView: ImageView = itemView.findViewById(R.id.imageView)

            fun bind(imageUri: Uri) {
                // Carga la imagen en el ImageView usando Glide o Picasso
                Glide.with(itemView.context).load(imageUri).into(imageView)
            }
        }
    }
}
