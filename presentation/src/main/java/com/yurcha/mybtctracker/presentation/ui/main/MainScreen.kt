package com.yurcha.mybtctracker.presentation.ui.main

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yurcha.mybtctracker.presentation.ui.utils.rememberFlowWithLifecycle
import com.yurcha.domain.model.BitcoinRate
import com.yurcha.domain.model.Transaction
import com.yurcha.domain.model.TransactionCategory
import com.yurcha.domain.utils.toDateFormat
import com.yurcha.domain.utils.toTimeFormat
import com.yurcha.mybtctracker.presentation.R
import com.yurcha.mybtctracker.presentation.ui.addtransaction.AddTransactionScreen
import com.yurcha.mybtctracker.presentation.ui.theme.Green40
import com.yurcha.mybtctracker.presentation.ui.theme.Green80
import com.yurcha.mybtctracker.presentation.ui.theme.Red40
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    val effect = rememberFlowWithLifecycle(viewModel.effect)
    var isAddTransactionScreenVisible by remember { mutableStateOf(false) }
    var isRefillDialogVisible by remember { mutableStateOf(false) }

    LaunchedEffect(effect) {
        effect.collect { action ->
            when (action) {
                is MainScreenReducer.MainScreenEffect.DisplayRefillDialog -> {
                    isRefillDialogVisible = action.isVisible
                }

                is MainScreenReducer.MainScreenEffect.DisplayAddTransactionScreen -> {
                    isAddTransactionScreenVisible = action.isVisible
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.collectMainData()
    }

    if (isAddTransactionScreenVisible) {
        AddTransactionScreen(
            onAddClicked = { amount, category ->
                viewModel.addTransaction(amount, category, false)
                isAddTransactionScreenVisible = false
            },
            onBack = {
                isAddTransactionScreenVisible = false
            },
            modifier = modifier
        )
    } else {
        MainScreenContent(
            modifier = modifier,
            isBitcoinRateLoading = state.value.isBitcoinRateLoading,
            isBalanceLoading = state.value.isBalanceLoading,
            isTransactionsLoading = state.value.isTransactionsLoading,
            isRefillDialogVisible = isRefillDialogVisible,
            transactions = state.value.transactions,
            bitcoinRate = state.value.bitcoinRate,
            bitcoinBalance = state.value.bitcoinBalance,
            onLoadMore = { viewModel.onLoadMoreTransactions() },
            onRefillConfirmed = {
                viewModel.addTransaction(
                    amount = it,
                    category = TransactionCategory.OTHER,
                    isRefill = true
                )
            },
            onDismissRefillDialog = {
                viewModel.sendEffect(
                    effect = MainScreenReducer.MainScreenEffect.DisplayRefillDialog(
                        isVisible = false
                    )
                )
            },
            onOpenRefillDialogClicked = {
                viewModel.sendEffect(
                    effect = MainScreenReducer.MainScreenEffect.DisplayRefillDialog(
                        isVisible = true
                    )
                )
            },
            onAddTransactionClicked = {
                viewModel.sendEffect(
                    effect = MainScreenReducer.MainScreenEffect.DisplayAddTransactionScreen(
                        isVisible = true
                    )
                )
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainScreenContent(
    isBitcoinRateLoading: Boolean,
    isBalanceLoading: Boolean,
    isTransactionsLoading: Boolean,
    transactions: List<Transaction>,
    bitcoinRate: BitcoinRate,
    bitcoinBalance: String,
    onOpenRefillDialogClicked: () -> Unit,
    onAddTransactionClicked: () -> Unit,
    onLoadMore: () -> Unit,
    isRefillDialogVisible: Boolean,
    onRefillConfirmed: (String) -> Unit,
    onDismissRefillDialog: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var refillAmount by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val groupedTransactions = transactions
        .groupBy { it.date.toDateFormat() }
        .toSortedMap(compareByDescending { it })

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // BTC Rate Top Right
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 30.dp)
            ) {
                if (!isBitcoinRateLoading) {
                    Text(
                        text = stringResource(
                            id = R.string.current_btc_price,
                            bitcoinRate.currentPrice,
                            bitcoinRate.lastUpdated
                        ),
                        modifier = Modifier.align(Alignment.TopEnd),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Balance with Refill Button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(id = R.string.your_balance, bitcoinBalance),
                    style = MaterialTheme.typography.titleMedium
                )
                Button(onClick = onOpenRefillDialogClicked) {
                    Text("Refill")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Button for opening new transaction screen
            Button(
                onClick = onAddTransactionClicked,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add transaction")
            }

            Spacer(modifier = Modifier.height(16.dp))

            LaunchedEffect(listState) {
                snapshotFlow { listState.layoutInfo.visibleItemsInfo }
                    .map { visibleItems ->
                        visibleItems.lastOrNull()?.index
                    }
                    .distinctUntilChanged()
                    .collect { lastVisibleIndex ->
                        val totalItemsCount = listState.layoutInfo.totalItemsCount
                        println("lastVisibleIndex = $lastVisibleIndex, totalItemsCount = $totalItemsCount")
                        if (lastVisibleIndex != null &&
                            lastVisibleIndex >= totalItemsCount - 1 &&
                            !isTransactionsLoading
                        ) {
                            onLoadMore()
                        }
                    }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = listState
            ) {
                groupedTransactions.forEach { (date, transactionsForDate) ->
                    stickyHeader {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.background)
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = date,
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    items(transactionsForDate) { tx ->
                        TransactionItem(transaction = tx)
                    }
                }

                if (isTransactionsLoading) {
                    item {
                        CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                    }
                }
            }
        }
    }

    if (isRefillDialogVisible) {
        AlertDialog(
            onDismissRequest = onDismissRefillDialog,
            title = { Text(stringResource(id = R.string.refill_amount)) },
            text = {
                OutlinedTextField(
                    value = refillAmount,
                    onValueChange = { newValue ->
                        // only 2 digits after the dot
                        val regex = Regex("^\\d*(\\.\\d{0,2})?$")
                        if (newValue.matches(regex)) {
                            refillAmount = newValue
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Done
                    ),
                    label = { Text(stringResource(id = R.string.enter_amount)) }
                )
            },
            confirmButton = {
                Button(onClick = {
                    onRefillConfirmed(refillAmount)
                    refillAmount = ""
                }) {
                    Text(stringResource(id = R.string.ok))
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissRefillDialog) {
                    Text(stringResource(id = R.string.cancel))
                }
            }
        )
    }
}

@Composable
private fun TransactionItem(transaction: Transaction) {
    val borderColor = if (transaction.isRefill) Green40 else Red40
    val shape = RoundedCornerShape(16.dp)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .border(2.dp, borderColor, shape),
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // display category
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                Text(
                    text = if (transaction.isRefill) "REFILL" else transaction.category.displayName,
                    color = if (transaction.isRefill) Green40 else Red40,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // display time
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Text(
                    text = transaction.date.toTimeFormat(),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // display amount
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterEnd) {
                Text(
                    text = "${transaction.amount} BTC",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

