package com.smartvisor.android

import android.content.res.ColorStateList
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

class MainActivity : AppCompatActivity() {
    private lateinit var content: FrameLayout
    private lateinit var navBar: LinearLayout
    private val prefs by lazy { getSharedPreferences("smartvisor_items", MODE_PRIVATE) }
    private var current = Screen.HOME

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(root())
        show(Screen.HOME)
    }

    private fun root(): View = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        setBackgroundColor(BG)
        addView(header(), LinearLayout.LayoutParams(-1, -2))
        content = FrameLayout(this@MainActivity)
        addView(content, LinearLayout.LayoutParams(-1, 0, 1f))
        navBar = bottomNav()
        addView(navBar, LinearLayout.LayoutParams(-1, -2))
    }

    private fun header(): View = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        setPadding(dp(20), dp(18), dp(20), dp(12))
        setBackgroundColor(WHITE)
        addView(label("오프라인 개인 비서", 12f, PRIMARY, true))
        addView(label("스마트비서", 25f, TEXT, true))
    }

    private fun bottomNav(): LinearLayout = LinearLayout(this).apply {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER
        setPadding(dp(6), dp(8), dp(6), dp(14))
        setBackgroundColor(WHITE)
        navItems().forEach { nav ->
            addView(MaterialButton(this@MainActivity).apply {
                tag = nav.screen
                text = nav.title
                textSize = 12f
                isAllCaps = false
                minWidth = 0
                minHeight = dp(58)
                cornerRadius = dp(18)
                setPadding(0, dp(4), 0, dp(4))
                setOnClickListener { show(nav.screen) }
            }, LinearLayout.LayoutParams(0, dp(62), 1f).apply {
                marginStart = dp(2)
                marginEnd = dp(2)
            })
        }
    }

    private fun navItems() = listOf(
        Nav("⌂\n홈", Screen.HOME),
        Nav("＋\n입력", Screen.INPUT),
        Nav("▣\n일정", Screen.CALENDAR),
        Nav("✓\n할 일", Screen.TASKS),
        Nav("▤\n메모", Screen.NOTES)
    )

    private fun show(screen: Screen) {
        current = screen
        content.removeAllViews()
        val view = when (screen) {
            Screen.HOME -> home()
            Screen.INPUT -> inputScreen()
            Screen.CALENDAR -> listScreen("일정", ItemType.CALENDAR, "새 일정")
            Screen.TASKS -> taskScreen()
            Screen.NOTES -> listScreen("메모", ItemType.MEMO, "새 메모")
        }
        content.addView(view, FrameLayout.LayoutParams(-1, -1))
        updateNavSelection()
    }

    private fun updateNavSelection() {
        if (!::navBar.isInitialized) return
        for (i in 0 until navBar.childCount) {
            val button = navBar.getChildAt(i) as MaterialButton
            val selected = button.tag == current
            button.setTextColor(if (selected) WHITE else TEXT_MUTED)
            button.backgroundTintList = ColorStateList.valueOf(if (selected) PRIMARY else WHITE)
        }
    }

    private fun home(): View = scrollColumn().apply {
        addView(label("좋은 하루예요 👋", 26f, TEXT, true))
        addView(label("필요한 내용을 바로 기록해 보세요.", 14f, TEXT_MUTED, false), top(4))
        addView(primaryCard(), top(18))
        addView(summaryCard("오늘 일정", count(ItemType.CALENDAR), "저장된 일정", Screen.CALENDAR), top(12))
        addView(summaryCard("오늘 할 일", taskPendingCount(), "남은 할 일", Screen.TASKS), top(12))
        addView(summaryCard("최근 메모", count(ItemType.MEMO), "저장된 메모", Screen.NOTES), top(12))
        if (taskCount() > 0) addView(progressCard(), top(12))
    }

    private fun primaryCard(): View = MaterialCardView(this).apply {
        radius = dp(24).toFloat()
        cardElevation = dp(4).toFloat()
        setCardBackgroundColor(PRIMARY)
        addView(LinearLayout(this@MainActivity).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(20), dp(20), dp(20))
            addView(label("✨ 무엇을 도와드릴까요?", 20f, WHITE, true))
            addView(label("말하듯 입력하면 일정·할 일·메모로 분류합니다.", 13f, 0xFFEAE3FF.toInt(), false), top(5))
            addView(MaterialButton(this@MainActivity).apply {
                text = "빠른 입력 시작"
                isAllCaps = false
                cornerRadius = dp(18)
                setTextColor(PRIMARY)
                backgroundTintList = ColorStateList.valueOf(WHITE)
                setOnClickListener { show(Screen.INPUT) }
            }, top(14))
        })
    }

    private fun summaryCard(title: String, count: Int, unit: String, screen: Screen): View = card().apply {
        isClickable = true
        setOnClickListener { show(screen) }
        addView(LinearLayout(this@MainActivity).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(18), dp(16), dp(18), dp(16))
            addView(LinearLayout(this@MainActivity).apply {
                orientation = LinearLayout.VERTICAL
                addView(label(title, 17f, TEXT, true))
                addView(label(if (count == 0) "등록된 항목이 없습니다." else "$unit ${count}개", 14f, TEXT_MUTED, false), top(4))
            }, LinearLayout.LayoutParams(0, -2, 1f))
            addView(label("›", 28f, PRIMARY, true))
        })
    }

    private fun progressCard(): View = card().apply {
        val total = taskCount()
        val complete = taskCompleteCount()
        val percent = if (total == 0) 0 else complete * 100 / total
        addView(LinearLayout(this@MainActivity).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(18), dp(16), dp(18), dp(16))
            addView(label("할 일 완료율", 17f, TEXT, true))
            addView(label("$percent% · $complete/$total 완료", 22f, PRIMARY, true), top(6))
        })
    }

    private fun inputScreen(): View = scrollColumn().apply {
        addView(label("빠른 입력", 28f, TEXT, true))
        addView(label("예: 내일 오후 3시 병원 / 금요일까지 보고서 제출", 14f, TEXT_MUTED, false), top(5))
        val input = EditText(this@MainActivity).apply {
            hint = "내용을 입력하세요"
            minLines = 5
            gravity = Gravity.TOP
            setPadding(dp(16), dp(16), dp(16), dp(16))
            setTextColor(TEXT)
            setHintTextColor(TEXT_MUTED)
            setBackgroundColor(WHITE)
        }
        addView(input, top(18))
        addView(MaterialButton(this@MainActivity).apply {
            text = "자동 분류해서 저장"
            isAllCaps = false
            cornerRadius = dp(18)
            backgroundTintList = ColorStateList.valueOf(PRIMARY)
            setOnClickListener {
                val text = input.text.toString().trim()
                if (text.isNotEmpty()) {
                    val type = SimpleClassifier.classify(text)
                    if (type == ItemType.TASK) saveTask(text) else save(type, text)
                    show(screenFor(type))
                }
            }
        }, top(14))
    }

    private fun listScreen(title: String, type: ItemType, addText: String): View = scrollColumn().apply {
        addView(label(title, 28f, TEXT, true))
        addView(MaterialButton(this@MainActivity).apply {
            text = "+ $addText"
            isAllCaps = false
            cornerRadius = dp(18)
            backgroundTintList = ColorStateList.valueOf(PRIMARY)
            setOnClickListener { showAddDialog(type) }
        }, top(12))
        val values = items(type)
        if (values.isEmpty()) addView(emptyCard(), top(14)) else values.forEach { item ->
            addView(itemCard(type, item), top(10))
        }
    }

    private fun taskScreen(): View = scrollColumn().apply {
        addView(label("할 일", 28f, TEXT, true))
        if (taskCount() > 0) addView(label("${taskCompleteCount()}개 완료 · ${taskPendingCount()}개 남음", 15f, PRIMARY, true), top(5))
        addView(MaterialButton(this@MainActivity).apply {
            text = "+ 새 할 일"
            isAllCaps = false
            cornerRadius = dp(18)
            backgroundTintList = ColorStateList.valueOf(PRIMARY)
            setOnClickListener { showAddDialog(ItemType.TASK) }
        }, top(12))
        val tasks = taskItems()
        if (tasks.isEmpty()) addView(emptyCard(), top(14)) else tasks.forEach { task ->
            addView(taskCard(task), top(10))
        }
    }

    private fun emptyCard(): View = card().apply {
        addView(label("아직 저장된 항목이 없습니다.", 15f, TEXT_MUTED, false).apply {
            setPadding(dp(18), dp(24), dp(18), dp(24))
            gravity = Gravity.CENTER
        })
    }

    private fun itemCard(type: ItemType, item: String): View = card().apply {
        addView(LinearLayout(this@MainActivity).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(18), dp(12), dp(8), dp(12))
            addView(label(item, 16f, TEXT, true), LinearLayout.LayoutParams(0, -2, 1f))
            addView(actionButton("수정", PRIMARY) { showEditDialog(type, item) })
            addView(actionButton("삭제", DANGER) { remove(type, item); show(current) })
        })
    }

    private fun taskCard(task: TaskItem): View = card().apply {
        addView(LinearLayout(this@MainActivity).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(12), dp(10), dp(8), dp(10))
            addView(CheckBox(this@MainActivity).apply {
                isChecked = task.done
                buttonTintList = ColorStateList.valueOf(PRIMARY)
                setOnCheckedChangeListener { _, checked ->
                    updateTask(task, task.copy(done = checked))
                    show(Screen.TASKS)
                }
            })
            addView(label(task.text, 16f, if (task.done) TEXT_MUTED else TEXT, true).apply {
                if (task.done) paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            }, LinearLayout.LayoutParams(0, -2, 1f))
            addView(actionButton("수정", PRIMARY) { showTaskEditDialog(task) })
            addView(actionButton("삭제", DANGER) { removeTask(task); show(Screen.TASKS) })
        })
    }

    private fun actionButton(title: String, color: Int, action: () -> Unit) = MaterialButton(this).apply {
        text = title
        textSize = 12f
        isAllCaps = false
        minWidth = 0
        setTextColor(color)
        backgroundTintList = ColorStateList.valueOf(WHITE)
        setOnClickListener { action() }
    }

    private fun showAddDialog(type: ItemType) {
        val input = dialogInput()
        AlertDialog.Builder(this)
            .setTitle("새 항목")
            .setView(input)
            .setNegativeButton("취소", null)
            .setPositiveButton("저장") { _, _ ->
                val text = input.text.toString().trim()
                if (text.isNotEmpty()) {
                    if (type == ItemType.TASK) saveTask(text) else save(type, text)
                }
                show(current)
            }.show()
    }

    private fun showEditDialog(type: ItemType, oldText: String) {
        val input = dialogInput(oldText)
        AlertDialog.Builder(this)
            .setTitle("항목 수정")
            .setView(input)
            .setNegativeButton("취소", null)
            .setPositiveButton("저장") { _, _ ->
                val newText = input.text.toString().trim()
                if (newText.isNotEmpty()) replace(type, oldText, newText)
                show(current)
            }.show()
    }

    private fun showTaskEditDialog(task: TaskItem) {
        val input = dialogInput(task.text)
        AlertDialog.Builder(this)
            .setTitle("할 일 수정")
            .setView(input)
            .setNegativeButton("취소", null)
            .setPositiveButton("저장") { _, _ ->
                val newText = input.text.toString().trim()
                if (newText.isNotEmpty()) updateTask(task, task.copy(text = newText))
                show(Screen.TASKS)
            }.show()
    }

    private fun dialogInput(text: String = "") = EditText(this).apply {
        hint = "내용을 입력하세요"
        setText(text)
        setSelection(this.text.length)
        setPadding(dp(16), dp(12), dp(16), dp(12))
    }

    private fun save(type: ItemType, text: String) {
        val values = items(type).toMutableList().apply { add(0, text) }
        put(type.name, values)
    }

    private fun replace(type: ItemType, oldText: String, newText: String) {
        val values = items(type).toMutableList()
        val index = values.indexOf(oldText)
        if (index >= 0) values[index] = newText
        put(type.name, values)
    }

    private fun remove(type: ItemType, text: String) {
        val values = items(type).toMutableList().apply { remove(text) }
        put(type.name, values)
    }

    private fun saveTask(text: String) {
        val values = taskItems().toMutableList().apply { add(0, TaskItem(text, false)) }
        putTasks(values)
    }

    private fun updateTask(old: TaskItem, new: TaskItem) {
        val values = taskItems().toMutableList()
        val index = values.indexOf(old)
        if (index >= 0) values[index] = new
        putTasks(values)
    }

    private fun removeTask(task: TaskItem) {
        val values = taskItems().toMutableList().apply { remove(task) }
        putTasks(values)
    }

    private fun put(key: String, values: List<String>) {
        prefs.edit().putString(key, values.joinToString(SEPARATOR)).apply()
    }

    private fun putTasks(values: List<TaskItem>) {
        put(ItemType.TASK.name, values.map { "${if (it.done) "1" else "0"}|${it.text}" })
    }

    private fun items(type: ItemType): List<String> = prefs.getString(type.name, "")
        .orEmpty().split(SEPARATOR).filter { it.isNotBlank() }

    private fun taskItems(): List<TaskItem> = items(ItemType.TASK).map { raw ->
        when {
            raw.startsWith("1|") -> TaskItem(raw.drop(2), true)
            raw.startsWith("0|") -> TaskItem(raw.drop(2), false)
            else -> TaskItem(raw, false)
        }
    }

    private fun count(type: ItemType) = items(type).size
    private fun taskCount() = taskItems().size
    private fun taskCompleteCount() = taskItems().count { it.done }
    private fun taskPendingCount() = taskItems().count { !it.done }
    private fun screenFor(type: ItemType) = when (type) {
        ItemType.CALENDAR -> Screen.CALENDAR
        ItemType.TASK -> Screen.TASKS
        ItemType.MEMO -> Screen.NOTES
    }

    private fun scrollColumn(): LinearLayout = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        setPadding(dp(20), dp(18), dp(20), dp(28))
        setBackgroundColor(BG)
    }

    private fun card(): MaterialCardView = MaterialCardView(this).apply {
        radius = dp(20).toFloat()
        cardElevation = dp(1).toFloat()
        setCardBackgroundColor(WHITE)
    }

    private fun label(text: String, size: Float, color: Int, bold: Boolean) = TextView(this).apply {
        this.text = text
        textSize = size
        setTextColor(color)
        if (bold) setTypeface(typeface, Typeface.BOLD)
    }

    private fun top(value: Int) = LinearLayout.LayoutParams(-1, -2).apply { topMargin = dp(value) }
    private fun dp(value: Int) = (value * resources.displayMetrics.density).toInt()

    private data class TaskItem(val text: String, val done: Boolean)
    private data class Nav(val title: String, val screen: Screen)
    private enum class Screen { HOME, INPUT, CALENDAR, TASKS, NOTES }

    companion object {
        private const val SEPARATOR = "\u001F"
        private const val BG = 0xFFF7F5FC.toInt()
        private const val WHITE = 0xFFFFFFFF.toInt()
        private const val PRIMARY = 0xFF6750A4.toInt()
        private const val TEXT = 0xFF211B2E.toInt()
        private const val TEXT_MUTED = 0xFF6F687A.toInt()
        private const val DANGER = 0xFFB3261E.toInt()
    }
}
