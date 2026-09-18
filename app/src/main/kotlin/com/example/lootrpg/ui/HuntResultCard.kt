package com.example.lootrpg.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.lootrpg.R
import com.example.lootrpg.hunt.domain.CombatOutcome
import com.example.lootrpg.hunt.domain.HuntResult

@Composable
internal fun HuntResultCard(result: HuntResult) {
    val won = result.combat.outcome == CombatOutcome.Victory
    Panel {
        Text(stringResource(if (won) R.string.victory else R.string.defeat),
            style = MaterialTheme.typography.headlineMedium,
            color = if (won) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            MonsterEmblem(result.monsterId)
            Column(Modifier.weight(1f)) {
                Text(result.monsterName, style = MaterialTheme.typography.titleLarge)
                Text(stringResource(R.string.level_value, result.monsterLevel))
            }
        }
        Text(result.monsterDescription, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(stringResource(if (won) R.string.survived else R.string.retreated))
        if (result.combat.roundLimitReached) Text(stringResource(R.string.round_limit))
        Text(stringResource(R.string.combat_summary, result.combat.damageDealt, result.combat.damageTaken, result.combat.rounds))
        Text(stringResource(R.string.reward_summary, result.experienceGained, result.goldGained),
            style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
        result.levelUp?.let { levelUp ->
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            SectionHeading(stringResource(R.string.level_up, levelUp.toLevel))
            Text(stringResource(R.string.stat_change, stringResource(R.string.health), levelUp.before.maxHealth, levelUp.after.maxHealth))
            Text(stringResource(R.string.stat_change, stringResource(R.string.attack), levelUp.before.attack, levelUp.after.attack))
            Text(stringResource(R.string.stat_change, stringResource(R.string.defense), levelUp.before.defense, levelUp.after.defense))
        }
    }
}

@Composable
internal fun RecentHunts(hunts: List<HuntResult>) {
    SectionHeading(stringResource(R.string.recent_hunts))
    if (hunts.isEmpty()) Text(stringResource(R.string.no_hunts))
    else Panel {
        hunts.forEachIndexed { index, hunt ->
            if (index > 0) HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Column(Modifier.fillMaxWidth()) {
                Text(stringResource(if (hunt.combat.outcome == CombatOutcome.Victory) R.string.history_victory else R.string.history_defeat,
                    hunt.monsterName), style = MaterialTheme.typography.titleSmall)
                Text(stringResource(R.string.reward_summary, hunt.experienceGained, hunt.goldGained),
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
