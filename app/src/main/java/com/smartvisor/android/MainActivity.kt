package com.smartvisor.android

import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(createContent())
    }

    private fun createContent(): LinearLayout = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        gravity = Gravity.CENTER_HORIZONTAL
        setPadding(dp(20), dp(32), dp(20), dp(24))
        setBackgroundColor(0xFFF7F5FC.toInt())

        addView(TextView(this@MainActivity).apply {
            text = "스마트비서"
            textSize = 30f
            setTextColor(0xFF211B2E.toInt())
            setTypeface(typeface, Typeface.BOLD)
        })

        addView(TextView(this@MainActivity).apply {
            text = "오프라인 일정 · 할 일 · 메모"
            textSize = 15f
            setTextColor(0xFF6F687A.toInt())
        }, params(top = 6))

        addView(MaterialCardView(this@MainActivity).apply {
            radius = dp(24).toFloat()
            cardElevation = dp(4).toFloat()
            setCardBackgroundColor(0xFF6750A4.toInt())
            addView(LinearLayout(this@MainActivity).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(dp(20), dp(22), dp(20), dp(22))
                addView(TextView(this@MainActivity).apply {
                    text = "무엇을 도와드릴까요?"
                    textSize = 21f
                    setTextColor(0xFFFFFFFF.toInt())
                    setTypeface(typeface, Typeface.BOLD)
                })
                addView(TextView(this@MainActivity).apply {
                    text = "말하듯 입력하면 내용을 정리할 수 있습니다."
                    textSize = 14f
                    setTextColor(0xFFE9E1FF.toInt())
                }, params(top = 6))
                addView(MaterialButton(this@MainActivity).apply {
                    text = "빠른 입력 시작"
                    isAllCaps = false
                    cornerRadius = dp(18)
                    setOnClickListener {
                        text = "다음 버전에서 입력 기능을 연결합니다"
                    }
                }, params(top = 16))
            })
        }, params(top = 24))

        addView(summaryCard("오늘 일정", "등록된 일정이 없습니다."), params(top = 16))
        addView(summaryCard("오늘 할 일", "완료할 항목이 없습니다."), params(top = 12))
        addView(summaryCard("최근 메모", "저장된 메모가 없습니다."), params(top = 12))
    }

    private fun summaryCard(title: String, detail: String): MaterialCardView =
        MaterialCardView(this).apply {
            radius = dp(20).toFloat()
            cardElevation = dp(1).toFloat()
            setCardBackgroundColor(0xFFFFFFFF.toInt())
            addView(LinearLayout(this@MainActivity).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(dp(18), dp(16), dp(18), dp(16))
                addView(TextView(this@MainActivity).apply {
                    text = title
                    textSize = 17f
                    setTextColor(0xFF211B2E.toInt())
                    setTypeface(typeface, Typeface.BOLD)
                })
                addView(TextView(this@MainActivity).apply {
                    text = detail
                    textSize = 14f
                    setTextColor(0xFF6F687A.toInt())
                }, params(top = 4))
            })
        }

    private fun params(top: Int = 0): LinearLayout.LayoutParams =
        LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { topMargin = dp(top) }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
}
