package test.pecode.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import test.pecode.databinding.FragmentPageBinding

class PageFragment : Fragment() {

    private var _binding: FragmentPageBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        _binding = FragmentPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.takeIf { it.containsKey(KEY_PAGE) }?.apply {
            binding.pageNumber.text = getInt(KEY_PAGE).toString()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val KEY_PAGE = "page"
        fun newInstance(page: Int) = PageFragment().apply {
            arguments = bundleOf(
                KEY_PAGE to page
            )
        }
    }
}