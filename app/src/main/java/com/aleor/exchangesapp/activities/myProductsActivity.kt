    package com.aleor.exchangesapp.activities

    import android.content.ContentValues.TAG
    import android.content.Intent
    import android.net.Uri
    import android.os.Bundle
    import android.util.Log
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.widget.ImageView
    import android.widget.Switch
    import android.widget.TextView
    import androidx.appcompat.app.AppCompatActivity
    import androidx.recyclerview.widget.LinearLayoutManager
    import androidx.recyclerview.widget.RecyclerView
    import com.aleor.exchangesapp.R
    import com.aleor.exchangesapp.activities.HomeActivity.Product
    import com.aleor.exchangesapp.databinding.ActivityMyproductsBinding
    import com.bumptech.glide.Glide
    import com.google.firebase.auth.FirebaseAuth
    import com.google.firebase.firestore.SetOptions
    import com.google.firebase.firestore.ktx.firestore
    import com.google.firebase.ktx.Firebase
    import com.google.firebase.storage.ktx.storage


    class myProductsActivity: AppCompatActivity() {
        private lateinit var binding: ActivityMyproductsBinding
        private lateinit var imageRV: RecyclerView
        private var productList = mutableListOf<HomeActivity.Product>()
        val emailCurrentUser = FirebaseAuth.getInstance().currentUser?.email.toString()

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = ActivityMyproductsBinding.inflate(layoutInflater)
            setContentView(binding.root)
            imageRV = binding.rvProducts

            val layoutManager = LinearLayoutManager(this)
            imageRV.layoutManager = layoutManager

            val db = Firebase.firestore
            db.collection("Products")
                .whereEqualTo("userEmail" , emailCurrentUser)
                .get()
                .addOnSuccessListener { documents ->
                    productList.clear()
                    for (document in documents) {
                        val product = document.toObject(HomeActivity.Product::class.java)
                        product.id = document.id
                        productList.add(product)
                    }
                    val adapter = MyProductsAdapter(productList)
                    imageRV.adapter = adapter
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Error al obtener los productos de Firebase", exception)
                }


            val bottomNavigationView = binding.bottomNavigationView

            bottomNavigationView.setOnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.menu_home -> {
                        val intent = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                        true
                    }R.id.menu_map -> {
                    val intent = Intent(this, MapActivity::class.java)
                    startActivity(intent)
                    true
                }
                    R.id.menu_add -> {
                        val intent = Intent(this, FormsActivity::class.java)
                        startActivity(intent)
                        true
                    }
                    R.id.menu_profile -> {

                        val intent = Intent(this, UserProfile::class.java)
                        startActivity(intent)
                        true
                    }R.id.menu_chat -> {
                    val intent = Intent(this, myProductsActivity::class.java)
                    startActivity(intent)
                    true
                }
                    else -> false
                }
            }
        }


        inner class MyProductsAdapter(private val productList: List<HomeActivity.Product>) :
            RecyclerView.Adapter<MyProductsAdapter.ViewHolder>() {
            private val switchStates = mutableMapOf<Int, Boolean>()

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.large_cards, parent, false)
                return ViewHolder(view)
            }

            override fun onBindViewHolder(holder: ViewHolder, position: Int) {
                val product = productList[position]
                holder.productName.text = product.name
                holder.availableSwitch.setOnCheckedChangeListener(null)
                holder.availableSwitch.isChecked = (switchStates[position] ?: product.available) == true


                // Listener para cambiar el valor booleano en Firebase
                holder.availableSwitch.setOnCheckedChangeListener { _, isChecked ->
                    val db = Firebase.firestore
                    switchStates[position] = isChecked
                    db.collection("Products")
                        .whereEqualTo("userEmail" , emailCurrentUser)
                        .whereEqualTo("name", product.name)
                        .get()
                        .addOnSuccessListener { documents ->
                            for (document in documents) {
                                document.reference.set(mapOf("available" to isChecked), SetOptions.merge())
                                    .addOnSuccessListener {
                                        Log.d(TAG, "Estado de producto actualizado en Firebase")
                                    }
                                    .addOnFailureListener {
                                        Log.e(TAG, "Error al actualizar el estado de producto en Firebase", it)
                                    }
                            }
                        }
                        .addOnFailureListener {
                            Log.e(TAG, "Error al obtener los productos de Firebase", it)
                        }
                }

                //imagenes de los productos
                val storageRef = Firebase.storage.reference.child("/${product.id}")
                storageRef.listAll().addOnSuccessListener { listResult ->
                    if (listResult.items.isNotEmpty()) {
                        val firstImageRef = listResult.items.first()
                        firstImageRef.downloadUrl.addOnSuccessListener { uri ->
                            Glide.with(holder.itemView)
                                .load(uri)
                                .into(holder.productImage)
                        }
                    }
                }


            }

            override fun getItemCount(): Int {
                return productList.size
            }

            inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
                val productName: TextView = view.findViewById(R.id.name_product)
                val availableSwitch: Switch = view.findViewById(R.id.available)
                val productImage: ImageView = view.findViewById(R.id.productImage)
            }
        }
    }
