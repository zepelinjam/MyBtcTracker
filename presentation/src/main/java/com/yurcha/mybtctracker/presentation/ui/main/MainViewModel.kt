package com.yurcha.mybtctracker.presentation.ui.main

import androidx.lifecycle.viewModelScope
import com.yurcha.domain.business.addtransaction.AddTransactionUseCase
import com.yurcha.mybtctracker.presentation.ui.base.BaseViewModel
import com.yurcha.domain.business.main.MainDataUseCase
import com.yurcha.domain.business.main.balance.GetBalanceUseCase
import com.yurcha.domain.business.main.rate.GetBitcoinRateUseCase
import com.yurcha.domain.business.main.transaction.GetTransactionsUseCase
import com.yurcha.domain.model.Transaction
import com.yurcha.domain.model.TransactionCategory
import com.yurcha.domain.model.usecase.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Date

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getMainDataUseCase: MainDataUseCase,
    private val getBalanceUseCase: GetBalanceUseCase,
    private val getBitcoinRateUseCase: GetBitcoinRateUseCase,
    private val getTransactionUseCase: GetTransactionsUseCase,
    private val addTransactionUseCase: AddTransactionUseCase
) : BaseViewModel<MainScreenReducer.MainScreenState, MainScreenReducer.MainScreenEvent, MainScreenReducer.MainScreenEffect>(
    initialState = MainScreenReducer.MainScreenState.initial(),
    reducer = MainScreenReducer()
) {

    private var currentPage = 0
    private val pageSize = 20

    fun collectMainData() {
        loadTransactionsWithOffset()
        loadBalance()
        loadBitcoinRate()
    }

    fun addTransaction(amount: String, category: TransactionCategory, isRefill: Boolean) {
        val newTransaction = Transaction(
            date = Date(),
            amount = amount,
            isRefill = isRefill,
            category = category
        )

        viewModelScope.launch {
            addTransactionUseCase(newTransaction).collect { result ->
                sendEffect(
                    effect = MainScreenReducer.MainScreenEffect.DisplayRefillDialog(
                        isVisible = false
                    )
                )

                when (result) {
                    is Result.BusinessRuleError -> when (result.error) {
                        AddTransactionUseCase.AddTransactionDataErrors.Error -> {
                            Timber.e("BusinessRuleError: Failed to add transaction")
                        }
                    }

                    is Result.Error -> Timber.e("Error: ${result.error}")
                    is Result.Loading -> Unit
                    is Result.Success -> {
                        loadBalance()
                        loadFirstPage()
                    }
                }
            }
        }
    }

    fun onLoadMoreTransactions() {
        loadTransactionsWithOffset()
    }

    // provides transactions from DB with pagination (20 items per page)
    private fun loadTransactionsWithOffset() {
        viewModelScope.launch {
            getTransactionUseCase(parameters = currentPage * pageSize).collect { result ->
                sendEvent(
                    event = MainScreenReducer.MainScreenEvent.UpdateLoading(
                        isLoading = result.isLoading()
                    )
                )

                when (result) {
                    is Result.BusinessRuleError -> when (result.error) {
                        GetTransactionsUseCase.GetTransactionsErrors.NoTransactions -> {
                            Timber.e("BusinessRuleError: No transactions found")
                        }
                    }

                    is Result.Error -> Timber.e("Error: ${result.error}")
                    is Result.Loading -> Unit
                    is Result.Success -> when (val data = result.data) {
                        is GetTransactionsUseCase.GetTransactionsSuccess.TransactionsData -> {
                            sendEvent(
                                event = MainScreenReducer.MainScreenEvent.UpdateTransactions(
                                    transactions = data.transactions,
                                    isFirstPage = currentPage == 0
                                )
                            )
                            currentPage++ // mark the page is loaded
                        }
                    }
                }
            }
        }
    }

    // loads current BTC balance
    private fun loadBalance() {
        viewModelScope.launch {
            getBalanceUseCase(Unit).collect { result ->
                when (result) {
                    is Result.BusinessRuleError -> when (result.error) {
                        GetBalanceUseCase.GetBalanceErrors.NoBalance -> {
                            Timber.e("BusinessRuleError: No balance found")
                        }
                    }

                    is Result.Error -> Timber.e("Error: ${result.error}")
                    is Result.Loading -> Unit
                    is Result.Success -> when (val data = result.data) {
                        is GetBalanceUseCase.GetBalanceSuccess.CurrentBalance -> sendEvent(
                            event = MainScreenReducer.MainScreenEvent.UpdateBtcBalance(
                                balance = data.balance
                            )
                        )
                    }
                }
            }
        }
    }

    private fun loadBitcoinRate() {
        viewModelScope.launch {
            getBitcoinRateUseCase(Unit).collect { result ->
                when (result) {
                    is Result.BusinessRuleError -> when (result.error) {
                        GetBitcoinRateUseCase.GetBitcoinRateErrors.NoRate -> {
                            Timber.e("BusinessRuleError: No rate found")
                        }
                    }

                    is Result.Error -> Timber.e("Error: ${result.error}")
                    is Result.Loading -> Unit
                    is Result.Success -> when (val data = result.data) {
                        is GetBitcoinRateUseCase.GetBitcoinRateSuccess.BitcoinRateData -> sendEvent(
                            event = MainScreenReducer.MainScreenEvent.UpdateBtcRate(
                                rate = data.bitcoinRates
                            )
                        )
                    }
                }
            }
        }
    }

    private fun loadFirstPage() {
        currentPage = 0
        loadTransactionsWithOffset()
    }
}