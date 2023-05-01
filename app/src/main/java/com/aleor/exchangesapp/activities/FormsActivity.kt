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
import com.aleor.exchangesapp.models.Products
import com.aleor.exchangesapp.providers.ClientProvider
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage


class FormsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFormsBinding
    private lateinit var imageRV: RecyclerView
    private lateinit var imageAdapter: ImageAdapter
    private var imagesSelected = false
    var imagesGlobal = mutableListOf<Uri>()


    private val clientProvider = ClientProvider()
    private val myAuth = FirebaseAuth.getInstance()
    private val db = Firebase.firestore
    private val products = db.collection("Products")
    private val newProductRef = products.document()
    val storage = Firebase.storage
    val productosStorageRef = storage.reference.child("productos")
    val newProductId = newProductRef.id


    private var state: String = "none"
    private var category: String = ""
    private var isLoading:Boolean = false
    private var name:String = ""
    private var description:String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFormsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        /*BOTONES PARA REGRESAR A HOME*/
        binding.back.setOnClickListener{
            backToHome()
        }
        binding.btnCancelar.setOnClickListener {
            backToHome()
        }
        binding.btnGuardar.setOnClickListener{
            saveProduct()
        }

       val grupEstado = binding.grupEstado
        grupEstado.setOnCheckedChangeListener{
                _, checkedId ->
            state = when(checkedId) {
                R.id.radioNuevo -> "Nuevo"
                R.id.radioUsado -> "Usado"
                R.id.radioSeminuevo -> "Seminuevo"
                else -> "none"
            }
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
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

        //im
        setupImageRecyclerView()
        requestGalleryPermission()
        setupPickImageButton()
    }
    private fun showLoader(isLoading: Boolean) {
        val loader = binding.loader // O progressDialog en su caso
        if (isLoading) {
            loader.visibility = View.VISIBLE
        } else {
            loader.visibility = View.GONE
        }
    }

    private fun saveProduct() {
        showLoader(true)
        name = binding.etProductName.text.toString()
        description = binding.etDescription.text.toString()

        val userEmail = myAuth.currentUser?.email
        val newProduct = hashMapOf(
            "name" to name,
            "description" to description,
            "category" to category,
            "state" to state,
            "userEmail" to userEmail
        )

        if(name != "" && description != "" && category != "-Seleccione una categoria-" && state != "none"){
            newProductRef.set(newProduct).addOnCompleteListener {
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
        else{
            Toast.makeText(this@FormsActivity, "No puedes tener campos vacios", Toast.LENGTH_SHORT).show()
        }
        for (i in imagesGlobal.indices) {
            val storageRef = FirebaseStorage.getInstance().reference.child("${newProductId}/${imagesGlobal[i].lastPathSegment}")
            val uploadTask = storageRef.putFile(imagesGlobal[i])
            uploadTask.addOnSuccessListener {
                val downloadUrl = it.metadata?.reference?.downloadUrl.toString()
                // Aquí puedes almacenar la URL de descarga en la base de datos de Firestore o Realtime Database
                // Por ejemplo, aquí lo almacenaremos en un campo "imageUrls" en el documento del producto
                db.collection("products").document(newProductId).update("imageUrls", FieldValue.arrayUnion(downloadUrl))
            }.addOnFailureListener {
                // La imagen no se ha podido cargar en Firebase Storage
            }
        }
        showLoader(false)
    }

    private fun backToHome(){
        val intent =  Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }

    //Seleccion de imagenes
    private val pickImages =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
            (binding.imageRV.adapter as ImageAdapter).addImages(uris)
            imagesSelected = true
            binding.pickImage.visibility = View.GONE }

    private fun setupImageRecyclerView() {
        binding.imageRV.apply {
            layoutManager = GridLayoutManager(context, 3)
            adapter = ImageAdapter()
        }
    }
    private fun requestGalleryPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                1
            )
        }
    }
    private fun setupPickImageButton() {
        binding.pickImage.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
            ) {
                pickImages.launch("image/*")
            } else {
                requestGalleryPermission()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            setupPickImageButton()
        } else {
            Toast.makeText(this@FormsActivity, "Permiso denegado", Toast.LENGTH_SHORT).show()
        }
    }

    inner class ImageAdapter : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {
        private var images = mutableListOf<Uri>()

        fun addImages(uris: List<Uri>) {
            images.addAll(uris)
            imagesGlobal = uris as MutableList<Uri>
            /*println("IMAGEN: $imagesGlobal")*/
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
            return with(LayoutInflater.from(parent.context).inflate(R.layout.image_item, parent, false)) {
                ImageViewHolder(this, newProductId)
            }
        }


        override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
            val imageUri = images[position]
            holder.bind(imageUri)
        }

        override fun getItemCount(): Int = images.size

        inner class ImageViewHolder(itemView: View, private val newProductId: String) : RecyclerView.ViewHolder(itemView){
            private val imageView: ImageView = itemView.findViewById(R.id.imageView)

             fun bind(imageUri: Uri) {
                Glide.with(itemView.context).load(imageUri).into(imageView)
            }
      }
    }

}
