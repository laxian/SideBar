package com.zhouweixian.sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.zhouweixian.sidebar.OnCurrentIndexCallback
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), OnCurrentIndexCallback {

    private var list = mutableListOf<String>()

    override fun onCurrentIndex(pos: Int, chr: Char) {
        val index = bi_search(list, 0, list.size - 1, chr)
        linearLayoutManager.scrollToPositionWithOffset(index, 0)
    }

    private fun bi_search(list: List<String>, from: Int, to: Int, pos: Char): Int {
        var mid = (from + to) / 2
        if (list[mid][0] == pos) {
            while (mid > -1 && list[mid][0] == pos)
                --mid
            return mid + 1
        } else if (list[mid][0] > pos) {
            return bi_search(list, from, mid - 1, pos);
        } else {
            return bi_search(list, mid + 1, to, pos);
        }
    }

    private lateinit var linearLayoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        linearLayoutManager = LinearLayoutManager(this)
        rv.layoutManager = linearLayoutManager

        makeList();

        rv.adapter = RvAdapter(this, list)

        sidebar.setCurrentIndexCallback(this)
    }

    private fun makeList() {
        for (a in "ABCDEFGHIJKLMNOPQRSTUVWXYZ") {
            var i: Int = 0

            while (i < 5) {
                list.add(a + " --> ${i}")
                ++i
            }
        }
    }
}
