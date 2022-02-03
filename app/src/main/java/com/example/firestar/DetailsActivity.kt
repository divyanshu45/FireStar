package com.example.firestar

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.firestar.adapters.MoviesAndTrendingAdapter
import com.example.firestar.adapters.SportsAdapter
import com.example.firestar.model.Item
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_details.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

class DetailsActivity : AppCompatActivity(), Player.EventListener {

    private lateinit var simpleExoplayer: SimpleExoPlayer
    private var playbackPosition: Long = 0
    private lateinit var item: Item
    var itemList = arrayListOf<Item>()

    private var totalDur: Long = 0

    lateinit var firebaseAuth: FirebaseAuth
    lateinit var databaseReference: DatabaseReference

    private lateinit var moviesAdapter: MoviesAndTrendingAdapter
    private lateinit var trendingAdapter: MoviesAndTrendingAdapter
    private lateinit var sportsAdapter: SportsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        firebaseAuth = FirebaseAuth.getInstance()

        item = intent.getSerializableExtra("Item") as Item
        itemList = intent.getSerializableExtra("ItemList") as ArrayList<Item>

        Glide.with(this).load(item.imageUri).placeholder(R.drawable.ic_launcher_foreground).into(iv_titleImage)
        tv_title.text = item.name
        tv_desc.text = item.duration
        tv_subTitle.text = when(item.type){
            0 -> "Hotstar Specials - Movies"
            1 -> "Hotstar Specials - Web Series"
            else -> "Hotstar Specials - Sports"
        }

        when(item.type){
            0 ->{
                moviesAdapter = MoviesAndTrendingAdapter(itemList)
                moviesAdapter.setOnItemClickListener { item ->
                    Intent(this, DetailsActivity::class.java).also {
                        it.putExtra("Item", item)
                        it.putExtra("ItemList", itemList)
                        startActivity(it)
                    }
                }
                rv_moreLikeThis.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                rv_moreLikeThis.adapter = moviesAdapter
            }

            1 ->{
                trendingAdapter = MoviesAndTrendingAdapter(itemList)
                trendingAdapter.setOnItemClickListener { item ->
                    Intent(this, DetailsActivity::class.java).also {
                        it.putExtra("Item", item)
                        it.putExtra("ItemList", itemList)
                        startActivity(it)
                    }
                }
                rv_moreLikeThis.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                rv_moreLikeThis.adapter = trendingAdapter
            }

            else ->{
                sportsAdapter = SportsAdapter(itemList)
                sportsAdapter.setOnItemClickListener { item ->
                    Intent(this, DetailsActivity::class.java).also {
                        it.putExtra("Item", item)
                        it.putExtra("ItemList", itemList)
                        startActivity(it)
                    }
                }
                rv_moreLikeThis.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                rv_moreLikeThis.adapter = sportsAdapter
            }
        }

    }

    private fun updateCoins(){
        CoroutineScope(Dispatchers.IO).launch{
            try{
                var initialCoins = 0
                databaseReference = FirebaseDatabase.getInstance("https://fire-star-229d4-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users")
                firebaseAuth = FirebaseAuth.getInstance()
                val uuid = FirebaseAuth.getInstance().currentUser?.uid

                databaseReference.child(uuid!!).get().addOnSuccessListener {
                    if(it.exists()){
                        initialCoins = it.child("coins").value.toString().toInt()
                        update(uuid, initialCoins+10)
                        Log.d("ExoUpdate", "Initial Coins: $initialCoins")
                    }
                }
            } catch (e: Exception){
                Toast.makeText(this@DetailsActivity, e.message.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun update(uuid: String, newCoins: Int){
        databaseReference.child(uuid).child("coins").setValue(newCoins).addOnSuccessListener {
            Toast.makeText(this@DetailsActivity, "You are rewarded with 10 coins", Toast.LENGTH_SHORT).show()
            Log.d("ExoUpdate", "Coins has updated to $newCoins")
        }.addOnFailureListener{
            Toast.makeText(this@DetailsActivity, "Error in updating coins", Toast.LENGTH_SHORT).show()
            Log.d("ExoUpdate", "Error in updating coins")
        }
    }

    private val dataSourceFactory: DataSource.Factory by lazy {
        DefaultDataSourceFactory(this, "exoplayer-sample")
    }

    private fun initializePlayer() {
        simpleExoplayer = SimpleExoPlayer.Builder(this).build()
        preparePlayer(item.videoUri!!,"default")
        ev_exoplayerView.player = simpleExoplayer
        simpleExoplayer.seekTo(playbackPosition)
        simpleExoplayer.playWhenReady = true
        simpleExoplayer.addListener(this)
    }

    private fun buildMediaSource(uri: Uri, type: String): MediaSource {
        return if (type == "dash") {
            DashMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri)
        } else {
            ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri)
        }
    }

    private fun preparePlayer(videoUrl: String, type: String) {
        val uri = Uri.parse(videoUrl)
        val mediaSource = buildMediaSource(uri, type)
        simpleExoplayer.prepare(mediaSource)
    }

    private fun releasePlayer() {
        playbackPosition = simpleExoplayer.currentPosition
//        if(playbackPosition >= totalDur){
//            updateCoins()
//        }
//        Toast.makeText(this, "Release Time: ${playbackPosition}", Toast.LENGTH_SHORT).show()
        Log.d("ExoRelease", playbackPosition.toString())
        simpleExoplayer.release()
    }

    override fun onPlayerError(error: PlaybackException) {

    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        if (playbackState == Player.STATE_BUFFERING)
            pb_progressBar.visibility = View.VISIBLE
        else if(playbackState == Player.STATE_READY){
            pb_progressBar.visibility = View.INVISIBLE
            totalDur = simpleExoplayer.duration
//            Toast.makeText(this, "Ready Time: ${totalDur}", Toast.LENGTH_SHORT).show()
            Log.d("ExoReady", totalDur.toString())
        }
        else if(playbackState == Player.STATE_ENDED){
            pb_progressBar.visibility = View.INVISIBLE
            playbackPosition = simpleExoplayer.currentPosition
            if(playbackPosition >= totalDur){
                updateCoins()
            }
//            Toast.makeText(this, "End Time: ${playbackPosition}", Toast.LENGTH_SHORT).show()
            Log.d("ExoEnded", playbackPosition.toString())
        }
//        else if (playbackState == Player.STATE_READY || playbackState == Player.STATE_ENDED) {
//
//        }
    }

    override fun onStart() {
        super.onStart()
        initializePlayer()
    }

    override fun onStop() {
        super.onStop()
        releasePlayer()
    }
}