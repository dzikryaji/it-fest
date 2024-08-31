package com.mobile.itfest.ui.notes.selection

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.mobile.itfest.R
import jp.wasabeef.glide.transformations.BlurTransformation

class ImageAdapter(private val imageList: ArrayList<Int>, private val viewPager2: ViewPager2, private val viewModel: NoteTypeSelectionViewModel ) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    private lateinit var context: Context

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.note_type_picture)
        val lockedLayout: View = itemView.findViewById(R.id.locked_viewGroup)
        val unlockButton: Button = itemView.findViewById(R.id.unlock_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.template_note, parent, false)
        return ImageViewHolder(view)
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        context = recyclerView.context
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.imageView.setImageResource(imageList[position])
        holder.unlockButton.setOnClickListener {
            viewModel.unlockPremium()
            (context as NoteTypeSelectionActivity).let {
                it.binding.selectButton.isEnabled = true
                it.binding.selectButton.setBackgroundColor(ContextCompat.getColor(it, R.color.blue_500))
            }

            Toast.makeText(context, "Premium unlocked", Toast.LENGTH_SHORT).show()
        }
        if (position == 0) {
            holder.lockedLayout.visibility = View.INVISIBLE
        }
        if (position == imageList.size - 1) {
            viewPager2.post(runnable)
        }

        if (position != 0) {
            Glide.with(context).load(R.drawable.img_regular_note).apply(RequestOptions.bitmapTransform(
                BlurTransformation(15, 3)
            )).into(holder.imageView)
        }

        viewModel.isBuyPremium.observe(context as NoteTypeSelectionActivity) {
            if (!it && position != 0) {
                holder.lockedLayout.visibility = View.VISIBLE
            } else if (it && position != 0) {
                holder.lockedLayout.visibility = View.INVISIBLE
                //set normal render
                holder.imageView.setImageResource(imageList[position])
            }
        }
    }

    private val runnable = Runnable {
        imageList.addAll(imageList)
        notifyDataSetChanged()
    }

    companion object {
        private const val TAG = "ImageAdapter"
    }
}
