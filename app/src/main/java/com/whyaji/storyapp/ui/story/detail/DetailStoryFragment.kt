package com.whyaji.storyapp.ui.story.detail

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.whyaji.storyapp.databinding.FragmentDetailStoryBinding

class DetailStoryFragment : Fragment() {

    private lateinit var binding: FragmentDetailStoryBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailStoryBinding.inflate(inflater, container, false)

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(DetailStoryFragmentDirections.actionDetailStoryFragmentToListStoryFragment())
            }
        })
        playAnimation()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = requireArguments()
        val storyName = args.getString("storyName")
        val storyDesc = args.getString("storyDesc")
        val storyPhotoUrl = args.getString("storyPhotoUrl")

        binding.tvDetailName.text = storyName
        binding.tvDetailDescription.text = storyDesc

        Glide.with(binding.root)
            .load(storyPhotoUrl)
            .centerCrop()
            .into(binding.ivDetailPhoto)
    }

    private fun playAnimation() {
        val animator = ObjectAnimator.ofPropertyValuesHolder(
            binding.ivDetailPhoto,
            PropertyValuesHolder.ofFloat(View.SCALE_X, 1.0f, 1.2f),
            PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.0f, 1.2f)
        )
        animator.duration = 1000
        animator.repeatCount = 1
        animator.repeatMode = ObjectAnimator.REVERSE
        animator.start()

        val fadeIn = AlphaAnimation(0f, 1f)
        fadeIn.duration = 1000
        binding.tvDetailName.startAnimation(fadeIn)
        binding.tvDetailDescription.startAnimation(fadeIn)
    }
}