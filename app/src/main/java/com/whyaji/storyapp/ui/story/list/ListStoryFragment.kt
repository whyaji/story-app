package com.whyaji.storyapp.ui.story.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.whyaji.storyapp.R
import com.whyaji.storyapp.databinding.FragmentListStoryBinding
import com.whyaji.storyapp.util.ViewModelFactory


class ListStoryFragment : Fragment() {

    private var _binding: FragmentListStoryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ListStoryViewModel by viewModels {
        ViewModelFactory(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListStoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.rvListStory.layoutManager = LinearLayoutManager(requireContext())

        val adapter = ListStoryAdapter()
        binding.rvListStory.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )

        viewModel.getAllStories().observe(viewLifecycleOwner) {
            adapter.submitData(lifecycle, it)
        }

        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                if (positionStart == 0) {
                    binding.rvListStory.smoothScrollToPosition(0)
                }
            }
        })

        binding.fabCreateStory.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_listStoryFragment_to_createStoryFragment))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}