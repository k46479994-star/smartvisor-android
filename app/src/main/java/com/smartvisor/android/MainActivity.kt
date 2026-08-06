package com.smartvisor.android

import android.content.res.ColorStateList
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
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
        addView(bottomNav(), LinearLayout.LayoutParams(-1, -2))
    }

    private fun header(): View = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        setPadding(dp(20), dp(20), dp(20), dp(12))
        setBackgroundColor(WHITE)
        addView(label("오프라인 개인 비서", 12f, PRIMARY, true))
        addView(label("스마트비서", 25f, TEXT, true))
    }

    private fun bottomNav(): View = LinearLayout(this).apply {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER
        setPadding(dp(6), dp(8), dp(6), dp(16))
        setBackgroundColor(WHITE)
        listOf(
            Nav("⌂\n홈", Screen.HOME),
            Nav("＋\n입력", Screen.INPUT),
            Nav("▣\n일정", Screen.CALENDAR),
            Nav("✓\n할 일", Screen.TASKS),
            Nav("▤\n메모", Screen.NOTES)
        ).forEach { nav ->
            addView(MaterialButton(this@MainActivity).apply {
                text = nav.title
                textSize = 12f
                isAllCaps = false
                minWidth = 0
                minHeight = dp(58)
                cornerRadius = dp(18)
                setPadding(0, dp(4), 0, dp(4))
                setTextColor(TEXT_MUTED)
                backgroundTintList = ColorStateList.valueOf(WHITE)
                setOnClickListener { show(nav.screen) }
            }, LinearLayout.LayoutParams(0, dp(62), 1f).apply {
                marginStart = dp(2)
                marginEnd = dp(2)
            })
        }
    }

    private fun show(screen: Screen) {
        current = screen
        content.removeAllViews()
        val view = when (screen) {
            Screen.HOME -> home()
            Screen.INPUT -> inputScreen()
            Screen.CALENDAR -> listScreen("일정", ItemType.CALENDAR, "새 일정")
            Screen.TASKS -> listScreen("할 일", ItemType.TASK, "새 할 일")
            Screen.NOTES -> listScreen("메모", ItemType.MEMO, "새 메모")
        }
        content.addView(view, FrameLayout.LayoutParams(-1, -1))
    }

    private fun home(): View = scrollColumn().apply {
        addView(label("좋은 하루예요 👋", 26f, TEXT, true))
        addView(label("필요한 내용을 바로 기록해 보세요.", 14f, TEXT_MUTED, false), top(4))
        addView(primaryCard(), top(18))
        addView(summaryCard("오늘 일정", count(ItemType.CALENDAR), Screen.CALENDAR), top(12))
        addView(summaryCard("오늘 할 일", count(ItemType.TASK), Screen.TASKS), top(12))
        addView(summaryCard("최근 메모", count(ItemType.MEMO), Screen.NOTES), top(12))
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

    private fun summaryCard(title: String, count: Int, screen: Screen): View = card().apply {
        isClickable = true
        setOnClickListener { show(screen) }
        addView(LinearLayout(this@MainActivity).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(18), dp(16), dp(18), dp(16))
            addView(LinearLayout(this@MainActivity).apply {
                orientation = LinearLayout.VERTICAL
                addView(label(title, 17f, TEXT, true))
                addView(label(if (count == 0) "등록된 항목이 없습니다." else "저장된 항목 ${count}개", 14f, TEXT_MUTED, false), top(4))
            }, LinearLayout.LayoutParams(0, -2, 1f))
            addView(label("›", 28f, PRIMARY, true))
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
                    save(type, text)
                    input.setText("")
                    show(when (type) {
                        ItemType.CALENDAR -> Screen.CALENDAR
                        ItemType.TASK -> Screen.TASKS
                        ItemType.MEMO -> Screen.NOTES
                    })
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
        if (values.isEmpty()) {
            addView(card().apply {
                addView(label("아직 저장된 항목이 없습니다.", 15f, TEXT_MUTED, false).apply {
                    setPadding(dp(18), dp(24), dp(18), dp(24))
                    gravity = Gravity.CENTER
                })
            }, top(14))
        } else {
            values.forEach { item ->
                addView(card().apply {
                    addView(LinearLayout(this@MainActivity).apply {
                        orientation = LinearLayout.HORIZONTAL
                        gravity = Gravity.CENTER_VERTICAL
                        setPadding(dp(18), dp(14), dp(10), dp(14))
                        addView(label(item, 16f, TEXT, true), LinearLayout.LayoutParams(0, -2, 1f))
                        addView(MaterialButton(this@MainActivity).apply {
                            text = "삭제"
                            isAllCaps = false
                            setTextColor(0xFFB3261E.toInt())
                            backgroundTintList = ColorStateList.valueOf(WHITE)
                            setOnClickListener {
                                remove(type, item)
                                show(current)
                            }
                        })
                    })
                }, top(10))
            }
        }
    }

    private fun showAddDialog(type: ItemType) {
        val input = EditText(this).apply {
            hint = "내용을 입력하세요"
            setPadding(dp(16), dp(12), dp(16), dp(12))
        }
        AlertDialog.Builder(this)
            .setTitle("새 항목")
            .setView(input)
            .setNegativeButton("취소", null)
            .setPositiveButton("저장") { _, _ ->
                val text = input.text.toString().trim()
                if (text.isNotEmpty()) save(type, text)
                show(current)
            }
            .show()
    }

    private fun save(type: ItemType, text: String) {
        val values = items(type).toMutableList()
        values.add(0, text)
        prefs.edit().putString(type.name, values.joinToString("\u001F")).apply()
    }

    private fun remove(type: ItemType, text: String) {
        val values = items(type).toMutableList()
        values.remove(text)
        prefs.edit().putString(type.name, values.joinToString("\u001F")).apply()
    }

    private fun items(type: ItemType): List<String> = prefs.getString(type.name, "")
        .orEmpty().split("\u001F").filter { it.isNotBlank() }

    private fun count(type: ItemType): Int = items(type).size

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

    private data class Nav(val title: String, val screen: Screen)
    private enum class Screen { HOME, INPUT, CALENDAR, TASKS, NOTES }

    companion object {
        private const val BG = 0xFFF7F5FC.toInt()
        private const val WHITE = 0xFFFFFFFF.toInt()
        private const val PRIMARY = 0xFF6750A4.toInt()
        private const val TEXT = 0xFF211B2E.toInt()
        private const val TEXT_MUTED = 0xFF6F687A.toInt()
    }
}
