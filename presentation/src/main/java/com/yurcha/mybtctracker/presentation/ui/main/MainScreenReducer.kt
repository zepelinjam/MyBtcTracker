package com.yurcha.mybtctracker.presentation.ui.main

import androidx.compose.runtime.Immutable
import com.yurcha.mybtctracker.presentation.ui.base.Reducer
import com.yurcha.domain.model.BitcoinRate
import com.yurcha.domain.model.Transaction

class MainScreenReducer :
    Reducer<MainScreenReducer.MainScreenState, MainScreenReducer.MainScreenEvent, MainScreenReducer.MainScreenEffect> {
    @Immutable
    sealed class MainScreenEvent : Reducer.ViewEvent {
        data class UpdateLoading(val isLoading: Boolean) : MainScreenEvent()
        data class UpdateBtcBalanceLoading(val isLoading: Boolean) : MainScreenEvent()
        data class UpdateBtcRateLoading(val isLoading: Boolean) : MainScreenEvent()
        data class UpdateTransactions(
            val transactions: List<Transaction>,
            val isFirstPage: Boolean
        ) : MainScreenEvent()

        data class UpdateTransactionsLoading(val isLoading: Boolean) : MainScreenEvent()
        data class UpdateBtcRate(val rate: BitcoinRate) : MainScreenEvent()
        data class UpdateBtcBalance(val balance: String) : MainScreenEvent()
    }

    @Immutable
    sealed class MainScreenEffect : Reducer.ViewEffect {
        data class DisplayAddTransactionScreen(val isVisible: Boolean) : MainScreenEffect()
        data class DisplayRefillDialog(val isVisible: Boolean) : MainScreenEffect()
    }

    @Immutable
    data class MainScreenState(
        val isBitcoinRateLoading: Boolean,
        val isBalanceLoading: Boolean,
        val isRefillDialogVisible: Boolean,
        val isTransactionsLoading: Boolean,
        val transactions: List<Transaction>,
        val bitcoinRate: BitcoinRate,
        val bitcoinBalance: String
    ) : Reducer.ViewState {
        companion object {
            fun initial(): MainScreenState {
                return MainScreenState(
                    isBalanceLoading = true,
                    isBitcoinRateLoading = true,
                    isTransactionsLoading = false,
                    isRefillDialogVisible = false,
                    transactions = emptyList(),
                    bitcoinRate = BitcoinRate.fake(),
                    bitcoinBalance = "0.0"
                )
            }
        }
    }

    override fun reduce(
        previousState: MainScreenState,
        event: MainScreenEvent
    ): Pair<MainScreenState, MainScreenEffect?> {
        return when (event) {
            is MainScreenEvent.UpdateLoading -> {
                previousState.copy(
                    isBalanceLoading = event.isLoading,
                    isTransactionsLoading = event.isLoading,
                    isBitcoinRateLoading = event.isLoading
                ) to null
            }

            is MainScreenEvent.UpdateBtcRateLoading -> {
                previousState.copy(isBitcoinRateLoading = event.isLoading) to null
            }

            is MainScreenEvent.UpdateTransactions -> {
                val list = if (event.isFirstPage) {
                    event.transactions
                } else {
                    previousState.transactions + event.transactions
                }

                previousState.copy(transactions = list, isTransactionsLoading = false) to null
            }

            is MainScreenEvent.UpdateTransactionsLoading -> {
                previousState.copy(isTransactionsLoading = event.isLoading) to null
            }

            is MainScreenEvent.UpdateBtcBalance -> {
                previousState.copy(bitcoinBalance = event.balance) to null
            }

            is MainScreenEvent.UpdateBtcRate -> {
                previousState.copy(bitcoinRate = event.rate) to null
            }

            is MainScreenEvent.UpdateBtcBalanceLoading -> {
                previousState.copy(isBalanceLoading = event.isLoading) to null
            }
        }
    }
}