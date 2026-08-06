package com.smartvisor.android

enum class ItemType { CALENDAR, TASK, MEMO }

object SimpleClassifier {
    fun classify(text: String): ItemType {
        val normalized = text.lowercase()
        return when {
            listOf("오늘", "내일", "모레", "오전", "오후", "시 ", "예약", "회의", "일정").any(normalized::contains) -> ItemType.CALENDAR
            listOf("까지", "제출", "해야", "할 일", "완료", "마감").any(normalized::contains) -> ItemType.TASK
            else -> ItemType.MEMO
        }
    }
}
