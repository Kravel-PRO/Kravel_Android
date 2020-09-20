package com.kravelteam.kravel_android.ui.search

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.view.inputmethod.EditorInfo
import androidx.lifecycle.lifecycleScope
import com.kravelteam.kravel_android.KravelApplication
import com.kravelteam.kravel_android.R
import com.kravelteam.kravel_android.common.setOnDebounceClickListener
import com.kravelteam.kravel_android.data.common.SearchWord
import com.kravelteam.kravel_android.util.hideKeyboard
import com.kravelteam.kravel_android.util.showKeyboard
import com.kravelteam.kravel_android.util.toast
import kotlinx.android.synthetic.main.activity_search_content.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class SearchContentActivity : AppCompatActivity() {

    private var data : List<SearchWord> = emptyList()
    private var dataSize = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_content)

        initSetting()
        initGetRecentWord()
        initSetBtn()
    }

    private fun initSetting(){
        overridePendingTransition(0,0)

        img_search_back.setOnDebounceClickListener { finish() }

        supportFragmentManager.beginTransaction().replace(R.id.frame_division,RecentWordFragment()).commit()
    }

    private fun changeFragment(){
        supportFragmentManager.beginTransaction().replace(R.id.frame_division,SearchResultFragment()).commit()
    }

    private fun initGetRecentWord(){
        lifecycleScope.launch(Dispatchers.IO) {
            data = KravelApplication.db.searchWordDao().getAll()
            dataSize = data.size
            Timber.e("$data")
        }
    }

    private fun initSetBtn(){
        edt_search_word.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if(edt_search_word.text.toString() == ""){
                    toast(resources.getString(R.string.errorSearch))
                } else {
                    addRecentWord(edt_search_word.text.toString())
                    hideKeyboard()
                }
            }
            true
        }
        img_search_btn.setOnDebounceClickListener {
            if(edt_search_word.text.toString() == ""){
                toast(resources.getString(R.string.errorSearch))
            } else {
                addRecentWord(edt_search_word.text.toString())
                hideKeyboard()
            }
        }
    }

    fun addRecentWord(edtWord: String){
        edt_search_word.text = Editable.Factory.getInstance().newEditable(edtWord)
        changeFragment()
        initGetRecentWord()

        val word = SearchWord(word = edtWord)

        var find: SearchWord? = null
        lifecycleScope.launch(Dispatchers.IO) {
            find = KravelApplication.db.searchWordDao().findWord(edtWord)
            if (find != null) {
                KravelApplication.db.searchWordDao().deleteWord(edtWord)
            }else {
                if (dataSize >= 10) { //10개 이상일 때 첫번째 data 삭제
                    KravelApplication.db.searchWordDao().deleteWord(data[0].word)
                }
            }

            KravelApplication.db.searchWordDao().insertWord(word)
        }

    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0,0)
        hideKeyboard()
    }
}