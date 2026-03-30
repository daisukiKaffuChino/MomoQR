package github.daisukikaffuchino.momoqr.ui.pages.result.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import github.daisukikaffuchino.momoqr.R
import github.daisukikaffuchino.momoqr.ui.theme.Defaults

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ResultContentTextField(
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
            .clip(MaterialTheme.shapes.largeIncreased)
            .background(Defaults.Colors.Container)
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp, bottom = 4.dp)
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = value,
            onValueChange = onValueChange,
            label = { Text(stringResource(R.string.placeholder_edit_content)) },
            minLines = 4,
            maxLines = 8,
            shape = RoundedCornerShape(12.dp),
            isError = isError,
            supportingText = {
                AnimatedVisibility(
                    visible = isError,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Text(stringResource(R.string.error_no_content_entered_or_too_long))
                }
            }
        )
    }
}

@Composable
fun ResultCategoryTextField(
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean,
    supportingText: String = stringResource(R.string.tip_short_category)
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(stringResource(R.string.label_enter_category_name)) },
        isError = isError,
        supportingText = { Text(supportingText) },
        maxLines = 1,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    )
}