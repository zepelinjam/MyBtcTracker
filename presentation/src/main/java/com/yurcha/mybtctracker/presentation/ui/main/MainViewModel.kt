package com.yurcha.mybtctracker.presentation.ui.main

import androidx.lifecycle.viewModelScope
import com.yurcha.mybtctracker.presentation.ui.base.BaseViewModel
import com.yurcha.domain.business.main.MainDataUseCase
import com.yurcha.domain.model.usecase.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getMainDataUseCase: MainDataUseCase
) : BaseViewModel<MainScreenReducer.MainScreenState, MainScreenReducer.MainScreenEvent, MainScreenReducer.MainScreenEffect>(
    initialState = MainScreenReducer.MainScreenState.initial(),
    reducer = MainScreenReducer()
) {
    fun getMainData() {
        viewModelScope.launch {
            getMainDataUseCase(Unit).collect { result ->
                sendEvent(
                    event = MainScreenReducer.MainScreenEvent.UpdateLoading(
                        isLoading = result.isLoading()
                    )
                )

                when (result) {
                    is Result.BusinessRuleError -> when (result.error) {
                        MainDataUseCase.MainDataErrors.NoTransactionsFound -> {
                            Timber.e("BusinessRuleError: No transactions found")
                        }

                        MainDataUseCase.MainDataErrors.NoBitcoinRateFound -> {
                            Timber.e("BusinessRuleError: No bitcoin rate found")
                        }
                    }

                    is Result.Error -> Timber.e("Error: ${result.error}")
                    is Result.Loading -> Unit
                    is Result.Success -> when (val data = result.data) {
                        is MainDataUseCase.MainDataSuccess.TransactionsData -> sendEvent(
                            event = MainScreenReducer.MainScreenEvent.UpdateTransactions(
                                transactions = data.transactions
                            )
                        )

                        is MainDataUseCase.MainDataSuccess.BitcoinRateData -> sendEvent(
                            event = MainScreenReducer.MainScreenEvent.UpdateBtcRate(
                                rate = data.bitcoinRates
                            )
                        )
                    }
                }
            }
        }
    }

    fun onTopicClick() {
//        sendEffect(
//            effect = MainScreenReducer.MainScreenEffect.NavigateToTopic(
//                topicId = topicId
//            )
//        )
    }
}