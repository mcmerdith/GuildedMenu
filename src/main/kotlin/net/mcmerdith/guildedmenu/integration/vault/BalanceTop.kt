package net.mcmerdith.guildedmenu.integration.vault

data class BalanceTop(val balances: List<PlayerBalance>) {
    val offset = balances.lastOrNull()?.balance ?: 0.0
    val offsetMax = balances.firstOrNull()?.balance?.minus(offset) ?: 0.0

    fun percentile(balance: Double) = (balance - offset) / offsetMax
}