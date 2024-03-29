package com.example.network

import com.example.network.util.DateUtil

import android.app.ProgressDialog
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v7.app.AppCompatActivity
import android.view.View

import kotlinx.android.synthetic.main.activity_progress_dialog.*
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.progressDialog
import org.jetbrains.anko.selector

/**
 * Created by ouyangshen on 2017/9/24.
 */
class ProgressDialogActivity : AppCompatActivity() {
    //listOf,ListOfNotNull,mutableListOf,arrayListOf
    private val progressNames = listOf("圆圈进度", "水平进度条")
    //intArrayOf,floatArrayOf,etc
    private val progressStyles = intArrayOf(ProgressDialog.STYLE_SPINNER, ProgressDialog.STYLE_HORIZONTAL)
    private var dialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_progress_dialog)

        sp_style.visibility = View.GONE  //没有用到
        tv_spinner.visibility = View.VISIBLE  //用这个覆盖在spinner之上
        tv_spinner.text = progressNames[0]   //默认选中
        tv_spinner.setOnClickListener {
            selector("请选择对话框样式", progressNames) { i ->
                tv_spinner.text = progressNames[i]
                if (dialog==null || !dialog!!.isShowing) {
                    if (progressStyles[i] == ProgressDialog.STYLE_SPINNER) {
                        dialog = indeterminateProgressDialog("正在努力加载页面", "请稍候")
                        dialog!!.show()
                        handler.postDelayed(closeDialog, 5000)
                    } else {
                        dialog = progressDialog("正在努力加载页面", "请稍候")
                        dialog!!.show()
                        RefreshThread().start()
                    }
                }
            }
        }
    }

    private val closeDialog = Runnable {
        //如果正在显示，就去掉，然后显示展示结果
        if (dialog!!.isShowing) {
            dialog!!.dismiss()
            tv_result.text = "${DateUtil.nowTime}  ${tv_spinner.text}加载完成"
        }
    }

    private inner class RefreshThread : Thread() {
        override fun run() {
            //模拟加载过程
            for (i in 0..9) {
                val message = Message.obtain()
                message.what = 0
                message.arg1 = i * 10
                handler.sendMessage(message)
                Thread.sleep(1500)
            }
            //加载完之后，展示加载结果
            handler.sendEmptyMessage(1)
        }
    }

    private val handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                0 -> dialog!!.progress = msg.arg1
                else -> post(closeDialog)
            }
        }
    }

}
