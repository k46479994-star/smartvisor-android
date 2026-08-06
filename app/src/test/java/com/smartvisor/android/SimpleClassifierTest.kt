package com.smartvisor.android

import org.junit.Assert.assertEquals
import org.junit.Test

class SimpleClassifierTest {
    @Test
    fun classifiesCalendarTaskAndMemo() {
        assertEquals(ItemType.CALENDAR, SimpleClassifier.classify("내일 오후 3시 병원"))
        assertEquals(ItemType.TASK, SimpleClassifier.classify("금요일까지 보고서 제출"))
        assertEquals(ItemType.MEMO, SimpleClassifier.classify("아이디어 발표 순서 바꾸기"))
    }
}
