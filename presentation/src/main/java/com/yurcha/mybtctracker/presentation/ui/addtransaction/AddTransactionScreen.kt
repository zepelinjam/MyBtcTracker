package com.yurcha.mybtctracker.presentation.ui.addtransaction

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.yurcha.domain.model.TransactionCategory
import com.yurcha.mybtctracker.presentation.R

@Composable
fun AddTransactionScreen(
    onAddClicked: (amount: String, category: TransactionCategory) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var amount by remember { mutableStateOf("") }
    val categories = TransactionCategory.entries
    var selectedCategory by remember { mutableStateOf(TransactionCategory.GROCERIES) }

    // Handle system back button
    BackHandler {
        onBack()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(30.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.add_new_transaction),
            style = MaterialTheme.typography.titleLarge
        )

        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text(stringResource(id = R.string.enter_amount)) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Text(stringResource(id = R.string.select_category))

        Column {
            categories.forEach { category ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = (category == selectedCategory),
                            onClick = { selectedCategory = category }
                        )
                        .padding(vertical = 4.dp)
                ) {
                    RadioButton(
                        selected = (category == selectedCategory),
                        onClick = { selectedCategory = category }
                    )
                    Text(text = category.displayName, modifier = Modifier.padding(start = 8.dp))
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Horizontal row with Cancel and OK buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton (
                onClick = { onBack() },
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(id = R.string.cancel))
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = {
                    onAddClicked(amount, selectedCategory)
                    onBack()
                },
                modifier = Modifier.weight(1f),
                enabled = amount.isNotBlank()
            ) {
                Text(stringResource(id = R.string.ok))
            }
        }
    }
}
