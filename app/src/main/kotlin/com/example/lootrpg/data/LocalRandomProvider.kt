package com.example.lootrpg.data

import com.example.lootrpg.core.domain.RandomProvider
import kotlin.random.Random

class LocalRandomProvider(private val random: Random = Random.Default) : RandomProvider {
    override fun nextInt(boundExclusive: Int): Int = random.nextInt(boundExclusive)
    override fun nextDouble(): Double = random.nextDouble()
}
