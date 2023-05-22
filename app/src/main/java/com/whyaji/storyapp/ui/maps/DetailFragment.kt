package com.whyaji.storyapp.ui.maps

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.whyaji.storyapp.R
import com.whyaji.storyapp.databinding.FragmentDetailBinding

class DetailFragment : DialogFragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val storyName = arguments?.getString("storyName")
        val storyDescription = arguments?.getString("storyDesc")
        val storyPhotoUrl = arguments?.getString("storyPhotoUrl")

        binding.tvDetailName.text = storyName
        binding.tvDetailDescription.text = storyDescription

        val drawable = CircularProgressDrawable(binding.root.context)
        drawable.strokeWidth = 5f
        drawable.centerRadius = 30f
        drawable.start()

        Glide.with(binding.root)
            .load(storyPhotoUrl)
            .placeholder(drawable)
            .centerCrop()
            .into(binding.ivDetailPhoto)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.apply {
            setGravity(Gravity.BOTTOM) // Set gravity to bottom
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            setWindowAnimations(R.style.DialogAnimation)
        }
        return dialog
    }

    override fun onStart() {
        super.onStart()

        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(storyId: String, storyName: String, storyDescription: String, storyPhotoUrl: String): DetailFragment {
            val fragment = DetailFragment()
            val bundle = Bundle().apply {
                putString("storyId", storyId)
                putString("storyName", storyName)
                putString("storyDesc", storyDescription)
                putString("storyPhotoUrl", storyPhotoUrl)
            }
            fragment.arguments = bundle
            return fragment
        }
    }
}