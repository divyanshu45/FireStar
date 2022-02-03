package com.example.firestar

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firestar.adapters.MoviesAndTrendingAdapter
import com.example.firestar.adapters.SportsAdapter
import com.example.firestar.auth.LoginActivity
import com.example.firestar.model.Item
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var databaseRef: DatabaseReference
    private lateinit var moviesAdapter: MoviesAndTrendingAdapter
    private lateinit var trendingAdapter: MoviesAndTrendingAdapter
    private lateinit var sportsAdapter: SportsAdapter

    lateinit var firebaseAuth: FirebaseAuth

    var trendingList = arrayListOf<Item>()
    var sportsList = arrayListOf<Item>()
    var moviesList = arrayListOf<Item>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        iv_profile.setOnClickListener {
            Intent(this@MainActivity, ProfileActivity::class.java).also {
                startActivity(it)
            }
        }

        firebaseAuth = FirebaseAuth.getInstance()

        fetch()

        moviesAdapter = MoviesAndTrendingAdapter(moviesList)
        moviesAdapter.setOnItemClickListener { item ->
            Intent(this@MainActivity, DetailsActivity::class.java).also {
                it.putExtra("Item", item)
                it.putExtra("ItemList", moviesList)
                startActivity(it)
            }
        }

        trendingAdapter = MoviesAndTrendingAdapter(trendingList)
        trendingAdapter.setOnItemClickListener { item ->
            Intent(this@MainActivity, DetailsActivity::class.java).also {
                it.putExtra("Item", item)
                it.putExtra("ItemList", trendingList)
                startActivity(it)
            }
        }

        sportsAdapter = SportsAdapter(sportsList)
        sportsAdapter.setOnItemClickListener { item ->
            Intent(this@MainActivity, DetailsActivity::class.java).also {
                it.putExtra("Item", item)
                it.putExtra("ItemList", sportsList)
                startActivity(it)
            }
        }

        rv_movies.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rv_movies.adapter = moviesAdapter

        rv_trending.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rv_trending.adapter = trendingAdapter

        rv_sports.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rv_sports.adapter = sportsAdapter
    }

    private fun fetch() = CoroutineScope(Dispatchers.IO).launch{
        databaseRef = FirebaseDatabase.getInstance("https://fire-star-229d4-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Items")
        databaseRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(item in snapshot.children){
                    val i = item.getValue(Item::class.java)
                    if(i?.type == 0){
                        trendingList.add(i)
                    }else if(i?.type == 1){
                        moviesList.add(i)
                    }else{
                        sportsList.add(i!!)
                    }
                }
                moviesAdapter.notifyDataSetChanged()
                trendingAdapter.notifyDataSetChanged()
                sportsAdapter.notifyDataSetChanged()
                Log.d("Response", trendingList.toString())
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        val inflater = menuInflater
//        inflater.inflate(R.menu.home_menu, menu)
//        return super.onCreateOptionsMenu(menu)
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            R.id.menu_profile -> {
//                Intent(this@MainActivity, ProfileActivity::class.java).also {
//                    startActivity(it)
//                }
//                return true
//            }
//            R.id.menu_signOut -> {
//                firebaseAuth.signOut().also {
//                    Intent(this@MainActivity, LoginActivity::class.java).also {
//                        startActivity(it)
//                        finish()
//                    }
//                }
//                return true
//            }
//        }
//        return super.onOptionsItemSelected(item)
//    }
}