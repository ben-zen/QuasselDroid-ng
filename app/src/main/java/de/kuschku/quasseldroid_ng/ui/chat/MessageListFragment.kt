package de.kuschku.quasseldroid_ng.ui.chat

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.libquassel.protocol.BufferId
import de.kuschku.quasseldroid_ng.R
import de.kuschku.quasseldroid_ng.persistence.QuasselDatabase
import de.kuschku.quasseldroid_ng.util.AndroidHandlerThread
import de.kuschku.quasseldroid_ng.util.helper.invoke
import de.kuschku.quasseldroid_ng.util.helper.switchMap
import de.kuschku.quasseldroid_ng.util.helper.toggle
import de.kuschku.quasseldroid_ng.util.service.ServiceBoundFragment

class MessageListFragment : ServiceBoundFragment() {
  val currentBuffer: MutableLiveData<LiveData<BufferId?>?> = MutableLiveData()
  private val buffer = currentBuffer.switchMap { it }

  private val handler = AndroidHandlerThread("Chat")

  private lateinit var database: QuasselDatabase

  @BindView(R.id.messages)
  lateinit var messageList: RecyclerView

  @BindView(R.id.scrollDown)
  lateinit var scrollDown: FloatingActionButton

  override fun onCreate(savedInstanceState: Bundle?) {
    handler.onCreate()
    super.onCreate(savedInstanceState)
    setHasOptionsMenu(true)
  }

  private val boundaryCallback = object :
    PagedList.BoundaryCallback<QuasselDatabase.DatabaseMessage>() {
    override fun onItemAtFrontLoaded(itemAtFront: QuasselDatabase.DatabaseMessage)
      = Unit

    override fun onItemAtEndLoaded(itemAtEnd: QuasselDatabase.DatabaseMessage)
      = loadMore()
  }

  override fun onCreateView(inflater: LayoutInflater,
                            container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    val view = inflater.inflate(R.layout.fragment_messages, container, false)
    ButterKnife.bind(this, view)

    val adapter = MessageAdapter(context!!)

    messageList.adapter = adapter
    val linearLayoutManager = LinearLayoutManager(context)
    linearLayoutManager.reverseLayout = true
    messageList.layoutManager = linearLayoutManager

    messageList.addOnScrollListener(
      object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
          val canScrollDown = recyclerView.canScrollVertically(1)
          val isScrollingDown = dy > 0

          scrollDown.visibility = View.VISIBLE
          scrollDown.toggle(canScrollDown && isScrollingDown)
        }
      }
    )

    database = QuasselDatabase.Creator.init(context!!.applicationContext)
    val data = buffer.switchMap {
      LivePagedListBuilder(database.message().findByBufferIdPaged(it), 20)
        .setBoundaryCallback(boundaryCallback)
        .setInitialLoadKey(null)
        .build()
    }

    data.observe(
      this, Observer { list ->
      val findFirstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition()
      adapter.setList(list)
      if (findFirstVisibleItemPosition < 2) {
        activity?.runOnUiThread {
          messageList.smoothScrollToPosition(0)
        }
        handler.postDelayed(
          {
            activity?.runOnUiThread {
              messageList.smoothScrollToPosition(0)
            }
          }, 16
        )
      }
    }
    )

    buffer.observe(
      this, Observer {
      handler.post {
        // Try loading messages when switching to isEmpty buffer
        if (it != null) {
          if (database.message().bufferSize(it) == 0) {
            loadMore()
          }
          activity?.runOnUiThread {
            messageList.scrollToPosition(0)
          }
        }
      }
    }
    )

    scrollDown.hide()
    scrollDown.setOnClickListener {
      messageList.scrollToPosition(0)
    }

    return view
  }

  private fun loadMore() {
    handler.post {
      buffer { bufferId ->
        backend()?.sessionManager()?.backlogManager?.requestBacklog(
          bufferId = bufferId,
          last = database.message().findFirstByBufferId(bufferId)?.messageId ?: -1,
          limit = 20
        )
      }
    }
  }

  override fun onDestroy() {
    handler.onDestroy()
    super.onDestroy()
  }
}