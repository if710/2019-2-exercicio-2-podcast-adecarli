package br.ufpe.cin.android.podcast

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.lang.Exception
import java.net.URL

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val db = ItemsDB.getDatabase(this)
        // Carrega de maneira assíncrona o arquivo XML
        doAsync {
            var itemFeedList = listOf<ItemFeed>()
            // Se o carregamento pela internet falhar, use as informações armazenadas no banco de dados
            try {
                val xmlContent = URL("http://audio.globoradio.globo.com/podcast/feed/507/2-pontos").readText()
                itemFeedList = Parser.parse(xmlContent)
                // Armazena os episodios carregados no banco de dados
                for (item in itemFeedList) {
                    db.itemsDAO().inserirItem(item)
                }

            } catch (e : Exception) {
                itemFeedList = db.itemsDAO().todosItens()
            } finally {

                uiThread {
                    listRecyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
                    listRecyclerView.adapter = ItemFeedAdapter(itemFeedList, this@MainActivity)
                    listRecyclerView.addItemDecoration(DividerItemDecoration(this@MainActivity, LinearLayoutManager.VERTICAL))
                }

            }
        }
    }



}
