package br.ufpe.cin.android.podcast

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.net.URL

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val db = ItemsDB.getDatabase(this)
        // Carrega de maneira assíncrona o arquivo XML
        doAsync {
            val xmlContent = URL("http://audio.globoradio.globo.com/podcast/feed/507/2-pontos").readText()
            val itemFeedList = Parser.parse(xmlContent)
            uiThread {
                listRecyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
                listRecyclerView.adapter = ItemFeedAdapter(itemFeedList, this@MainActivity)
                listRecyclerView.addItemDecoration(DividerItemDecoration(this@MainActivity, LinearLayoutManager.VERTICAL))
            }
            // Armazena os episodios carregados no banco de dados
            for (item in itemFeedList) {
                db.itemsDAO().inserirItem(item)
                println(item)
            }
        }
    }



}
