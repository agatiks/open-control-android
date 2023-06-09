package com.example.opencontrol.view.noteTab

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.opencontrol.MainViewModel
import com.example.opencontrol.R
import com.example.opencontrol.model.Note
import com.example.opencontrol.ui.theme.GreyDivider
import com.example.opencontrol.ui.theme.LightGrey
import com.example.opencontrol.view.HeaderBlock
import com.example.opencontrol.view.chatTab.VideoScreen
import org.koin.androidx.compose.getViewModel
import timber.log.Timber
import java.time.format.DateTimeFormatter

data class NoteInfoScreen(val noteId: String) : Screen {
    @Composable
    override fun Content() {
        NoteInfoContent(noteId = noteId)
    }
}

@Composable
private fun NoteInfoContent(noteId: String) {
    Timber.d("@@@ NoteInfo noteId = $noteId")
    val viewModel = getViewModel<MainViewModel>()
    val note = viewModel.getNoteById(noteId)
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HeaderBlock("Информация о записи")
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item { VideoBlock() }
            item { NoteInfoBlock(note) }
            item { CancelButton(viewModel::deleteNoteById, noteId) }
        }
    }
}

@Composable
private fun VideoBlock() {
    val navigator = LocalNavigator.currentOrThrow
    Button(
        onClick = { navigator.push(VideoScreen("roomName")) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp, horizontal = 16.dp),
        shape = RoundedCornerShape(32),
        colors = ButtonDefaults.buttonColors(containerColor = LightGrey, contentColor = Color.Black)
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.video_icon),
            contentDescription = "",
            modifier = Modifier.size(30.dp)
        )
        Text(
            text = "Начать консультирование",
            modifier = Modifier
                .padding(8.dp),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun NoteInfoBlock(note: Note) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        val formatter2 = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        FieldInfoRow("Тип:", note.type)
        InfoBlockDivider()
        FieldInfoRow("Дата:", note.date.format(formatter2))
        InfoBlockDivider()
        FieldInfoRow("Время:", note.time)
        InfoBlockDivider()
        FieldInfoRow("Формат:", note.format)
        InfoBlockDivider()
        FieldInfoRow("Номер объекта:", note.objectNumber)
        InfoBlockDivider()
        FieldInfoRow(
            "Инспектор:",
            note.inspectorFIO.lastName + " " + note.inspectorFIO.firstName + " " + note.inspectorFIO.patronymic
        )
        InfoBlockDivider()
        FieldInfoRow("Дополнительно:", note.info)
    }
}

@Composable
private fun FieldInfoRow(parameter: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = parameter,
            modifier = Modifier
                .padding(8.dp)
                .weight(1f),
            fontSize = 18.sp,
            fontWeight = FontWeight.Light
        )
        Text(
            text = value,
            modifier = Modifier
                .padding(8.dp)
                .weight(1f),
            fontSize = 18.sp,
            fontWeight = FontWeight.Light
        )
    }
}

@Composable
private fun InfoBlockDivider() {
    Divider(
        color = GreyDivider,
        thickness = 1.dp,
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .fillMaxWidth()
    )
}

@Composable
private fun CancelButton(
    deleteNote: (String) -> Boolean,
    noteId: String
) {
    val navigator = LocalNavigator.currentOrThrow
    val openDialog = remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                openDialog.value = true
            }, shape = RoundedCornerShape(32)
        ) {
            Text(
                text = "Отменить запись",
                modifier = Modifier
                    .padding(8.dp),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
        if (openDialog.value) {
            DeleteNoteDialog(onConfirm = {
                deleteNote(noteId)
                openDialog.value = false
                navigator.pop()
            },
                onDismiss = { openDialog.value = false })
        }
    }
}

@Composable
private fun DeleteNoteDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Отменить запись?") },
        text = { Text("Вы действительно хотите отменить эту запись?") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Да")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Нет")
            }
        }
    )
}
