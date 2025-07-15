package com.burakcanduzcan.tabletopcompanion.ui.munchkin

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.burakcanduzcan.tabletopcompanion.core.BaseFragment
import com.burakcanduzcan.tabletopcompanion.databinding.FragmentMunchkinBinding
import com.burakcanduzcan.tabletopcompanion.model.Game

class MunchkinFragment : BaseFragment<FragmentMunchkinBinding>(FragmentMunchkinBinding::inflate) {

    override val viewModel: MunchkinViewModel by viewModels()
    private val args: MunchkinFragmentArgs by navArgs()

    override fun initUi() {
        setTitle(getString(Game.MUNCHKIN.nameRes))
        addPawns(args.playerCount)
    }

    override fun initListeners() {
    }

    override fun initObservables() {
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onResume() {
        super.onResume()
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    override fun onPause() {
        super.onPause()
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun addPawns(count: Int) {
        val imgList = viewModel.getShuffledPawnImages()

        for (i in 0 until count) {
            val pawn = ImageView(this.context)
            pawn.setImageResource(imgList[i])
            pawn.layoutParams = ConstraintLayout.LayoutParams(120, 120)

            //setting initial position
            pawn.x = (100 * i).toFloat()
            pawn.y = 100f

            pawn.setOnTouchListener(DragTouchListener())

            binding.root.addView(pawn)
        }
    }
}

class DragTouchListener : View.OnTouchListener {
    private var dX = 0f
    private var dY = 0f

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View, event: MotionEvent?): Boolean {
        when (event?.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                dX = v.x - event.rawX
                dY = v.y - event.rawY
            }

            MotionEvent.ACTION_MOVE -> {
                v.animate()
                    .x(event.rawX + dX)
                    .y(event.rawY + dY)
                    .setDuration(0)
                    .start()
            }
        }
        return true
    }
}