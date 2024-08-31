package com.mobile.itfest.ui.notes.selection

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.mobile.itfest.R
import com.mobile.itfest.databinding.ActivityNoteTypeSelectionBinding
import com.mobile.itfest.ui.ViewModelFactory
import com.mobile.itfest.ui.main.MainViewModel
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlin.math.abs

class NoteTypeSelectionActivity : AppCompatActivity() {

    lateinit var binding: ActivityNoteTypeSelectionBinding
    private lateinit var handler: Handler
    private lateinit var imageList: ArrayList<Int>
    private lateinit var adapter: ImageAdapter
    private val viewModel by viewModels<NoteTypeSelectionViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private var buyPremium = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoteTypeSelectionBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        binding.selectButton
        init()
        setUpTransformer()
        binding.noteSelectionContainer.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                Log.d("NoteTypeSelectionActivity", "onPageSelected: $position")
                updateSelectButtonState(position, buyPremium)

            }
        })

    }

    override fun onPause() {
        super.onPause()

        /*handler.removeCallbacks(runnable)*/
    }

    override fun onResume() {
        super.onResume()
       /* handler.postDelayed(runnable, 2000)*/
    }

    private fun updateSelectButtonState(position: Int, state: Boolean) {
        if (position == 0) {
            /*binding.selectButton.setOnClickListener{
                //TODO: navigate to note creation
            }*/

            binding.selectButton.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.blue_500
                )
            )
            binding.selectButton.isEnabled = true
        }

        Log.d("NoteTypeSelectionActivity", "updateSelectButtonState: $buyPremium")

        if (position != 0 && !state) {
            binding.selectButton.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.slate_400
                )
            )

            binding.selectButton.isEnabled = false
        }

        else if (position != 0 && state) {
            binding.selectButton.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.blue_500
                )
            )
            binding.selectButton.isEnabled = true
            //TODO: navigate to note creation

        }

        Log.d("NoteTypeSelectionActivity", "updateSelectButtonState: $position")
    }

    private val runnable = Runnable {
        binding.noteSelectionContainer.currentItem = binding.noteSelectionContainer.currentItem + 1
    }

    private fun setUpTransformer() {
        val transformer = CompositePageTransformer()
        transformer.addTransformer(MarginPageTransformer(40))
        transformer.addTransformer{page, position ->
            val r = 1 - abs(position)
            page.scaleY = 0.85f + r * 0.15f
        }
        binding.noteSelectionContainer.setPageTransformer(transformer)
    }

    private fun init() {
        handler = Handler(Looper.myLooper()!!)
        imageList = ArrayList()
        for (i in 0..5) {
            imageList.add(R.drawable.img_regular_note)
        }

        viewModel.isBuyPremium.observe(this) {
            buyPremium = it
        }

        adapter = ImageAdapter(imageList, binding.noteSelectionContainer, viewModel)

        binding.noteSelectionContainer.adapter = adapter
        binding.noteSelectionContainer.offscreenPageLimit = 3
        binding.noteSelectionContainer.clipToPadding = false
        binding.noteSelectionContainer.clipChildren = false
        binding.noteSelectionContainer.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

        binding.backArrow.setOnClickListener {
            finish()
        }
    }
}