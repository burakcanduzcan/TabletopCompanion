package com.burakcanduzcan.tabletopcompanion.ui.munchkin

import androidx.lifecycle.ViewModel
import com.burakcanduzcan.tabletopcompanion.R
import kotlin.random.Random

class MunchkinViewModel : ViewModel() {
    private val pawnImages = listOf(
        R.drawable.knight_male,
        R.drawable.knight_female,
        R.drawable.wizard,
        R.drawable.elf_male,
        R.drawable.elf_female,
        R.drawable.dwarf1,
        R.drawable.dwarf2,
        R.drawable.gnome,
        R.drawable.orc,
    )

    fun getShuffledPawnImages(): List<Int> {
        return pawnImages.shuffled(Random)
    }
}