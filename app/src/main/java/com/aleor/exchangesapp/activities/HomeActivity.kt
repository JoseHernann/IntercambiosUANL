package com.aleor.exchangesapp.activities


import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aleor.exchangesapp.R
import com.aleor.exchangesapp.databinding.ActivityHomeBinding
import com.bumptech.glide.Glide
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage


class HomeActivity : AppCompatActivity() {

    private lateinit var productAdapter: ProductAdapter
    private lateinit var binding: ActivityHomeBinding
    val productList = mutableListOf<Product>()
    var productId = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val recyclerView = binding.recyclerView
        val layoutManager = GridLayoutManager(this, 2)
        val spacing = resources.getDimensionPixelSize(R.dimen.grid_spacing)
        recyclerView.addItemDecoration(GridSpacingItemDecoration(2, spacing, true))
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)

        // Obtener lista de productos desde Firebase Firestore
        val db = Firebase.firestore
        db.collection("Products")
            .whereEqualTo("available",true)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    productId = document.id
                    val productData = document.toObject(Product::class.java) //CONFIGURAR LA ACTIVIDAD DE DETALLE AQUI+
                    val product = Product(productId,productData.name,productData.category,productData.faculty)
                    productList.add(product)
                }
                // Configurar adaptador
                productAdapter = ProductAdapter(productList) { product ->
                    // Abrir actividad de detalle cuando se hace clic en una tarjeta
                    val intent = Intent(this, DetailActivity::class.java)
                    intent.putExtra("productId", product.id)
                    startActivity(intent)
                }
                recyclerView.adapter = productAdapter
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
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
    inner class ProductAdapter(private val products: List<Product>, private val onItemClick: (Product) -> Unit) : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val name: TextView = itemView.findViewById<TextView>(R.id.name)
            val category: TextView = itemView.findViewById<TextView>(R.id.category)
            val productImage: ImageView = itemView.findViewById(R.id.productImage)
            private val faculty:TextView = itemView.findViewById<TextView>(R.id.fac)
            fun bind(product: Product) {
                itemView.setOnClickListener { onItemClick(product) }
                name.text = product.name
                category.text = product.category
                faculty.text = product.faculty
                // Cargar imagen desde Firebase Storage
                    val storageRef = Firebase.storage.reference.child("/${product.id}")
                    /*println(storageRef)
                    println("PID: ${products.id}")*/
                    storageRef.listAll().addOnSuccessListener { listResult ->
                        if (listResult.items.isNotEmpty()) {
                            val firstImageRef = listResult.items.first()
                            firstImageRef.downloadUrl.addOnSuccessListener { uri ->
                                Glide.with(itemView.context)
                                    .load(uri)
                                    .into(productImage)
                            }
                        }
                    }

                when(product.faculty){
                    "FIME" -> faculty.setBackgroundResource(R.drawable.rounded_green)
                    "FACDyC" -> faculty.setBackgroundResource(R.drawable.rounded_green)
                    "FA" -> faculty.setBackgroundResource(R.drawable.rounded_green)
                    "FCF" -> faculty.setBackgroundResource(R.drawable.rounded_green)

                    "FFyL" -> faculty.setBackgroundResource(R.drawable.rounded_yellow)
                    "FTS" -> faculty.setBackgroundResource(R.drawable.rounded_yellow)
                    "FIC" -> faculty.setBackgroundResource(R.drawable.rounded_yellow)
                    "FCT" -> faculty.setBackgroundResource(R.drawable.rounded_yellow)
                    "FAMUS" -> faculty.setBackgroundResource(R.drawable.rounded_yellow)

                    "FARQ" -> faculty.setBackgroundResource(R.drawable.rounded_orange)
                    "FAV" -> faculty.setBackgroundResource(R.drawable.rounded_orange)
                    "FCC" -> faculty.setBackgroundResource(R.drawable.rounded_orange)
                    "FAE" -> faculty.setBackgroundResource(R.drawable.rounded_orange)

                    "FACPyA" -> faculty.setBackgroundResource(R.drawable.rounded_red)
                    "FCQ" -> faculty.setBackgroundResource(R.drawable.rounded_red)
                    "FACPyRI" -> faculty.setBackgroundResource(R.drawable.rounded_red)
                    "FE" -> faculty.setBackgroundResource(R.drawable.rounded_red)

                    "FOD" -> faculty.setBackgroundResource(R.drawable.rounded_blue)
                    "FCFM" -> faculty.setBackgroundResource(R.drawable.rounded_blue)

                    "FCB" -> faculty.setBackgroundResource(R.drawable.rounded_lightgreen)
                    "FASPyN" -> faculty.setBackgroundResource(R.drawable.rounded_lightgreen)
                    "FMVZ" -> faculty.setBackgroundResource(R.drawable.rounded_lightgreen)

                    "FAEN" -> faculty.setBackgroundResource(R.drawable.rounded_lightblue)
                    "FACMED" -> faculty.setBackgroundResource(R.drawable.rounded_lightblue)
                    "FAPSI" -> faculty.setBackgroundResource(R.drawable.rounded_lightblue)
                    "ODONTO" -> faculty.setBackgroundResource(R.drawable.rounded_lightblue)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.cards_item, parent, false)
            return ViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(products[position])
        }

        override fun getItemCount() = products.size

    }

    class Product(
        val id:String? = null,
        val name: String? = null,
        val category: String? = null,
        val faculty:String? = null,
        val state:String? = null,
        val description: String? = null,
        val userEmail:String? = null,
        var available:Boolean? = null
    ): Parcelable {

        constructor(parcel: Parcel) : this(
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!
        )

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(id)
            parcel.writeString(name)
            parcel.writeString(category)
            parcel.writeString(faculty)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<Product> {
            override fun createFromParcel(parcel: Parcel): Product {
                return Product(parcel)
            }

            override fun newArray(size: Int): Array<Product?> {
                return arrayOfNulls(size)
            }
        }
    }

    class GridSpacingItemDecoration(private val spanCount: Int, private val spacing: Int, private val includeEdge: Boolean) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            val position = parent.getChildAdapterPosition(view)
            val column = position % spanCount

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount
                outRect.right = (column + 1) * spacing / spanCount

                if (position < spanCount) {
                    outRect.top = spacing
                }
            } else {
                outRect.left = column * spacing / spanCount
                outRect.right = spacing - (column + 1) * spacing / spanCount
                if (position >= spanCount) {
                    outRect.top = spacing
                }
            }
        }
    }



    //    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        binding = ActivityHomeBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        binding.goToForms.setOnClickListener {
//            val intent =  Intent(this, FormsActivity::class.java)
//            startActivity(intent)
//        }
//    }
}


