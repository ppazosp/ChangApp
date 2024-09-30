package ppazosp.changapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

interface MessagesUpdateListener {
    fun onMessagesUpdated()
}

data class Chat(
    val username: String,
    val advertTitle: String,
)

class ChatsFragment : Fragment(), MessagesUpdateListener {

    private lateinit var chatsLinerLayout: LinearLayout

    override fun onMessagesUpdated() {
        setUI()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chats, container, false)

        initializeVars(view)

        setOnClickListeners()

        setUI()

        return view
    }

    private fun initializeVars(view: View)
    {
        chatsLinerLayout = view.findViewById(R.id.chats_linear_layout)
    }

    private fun setOnClickListeners()
    {

    }

    private fun setUI()
    {
        CoroutineScope(Dispatchers.Main).launch {
            val chats = getUserItems()

            for (chat in chats)
            {
                val chatView = LayoutInflater.from(context).inflate(R.layout.user_item, chatsLinerLayout, false)

                val usernameView = chatView.findViewById<TextView>(R.id.username)
                val lastMessageView =chatView.findViewById<TextView>(R.id.last_message)

                usernameView.text = chat.username
                val newText = "Ha solicitado unirse a ${chat.advertTitle}"
                lastMessageView.text = newText

                chatsLinerLayout.addView(chatView)
            }
        }
    }

    private suspend fun getUserItems(): ArrayList<Chat>
    {
        val userItems: ArrayList<Chat> = ArrayList()

        val messages = getMessages(requireContext())

        for (mss in messages)
        {
            val user = fetchUser(mss.sender_id)

            val advert = fetchAdvert(mss.advert_id)

            userItems.add(Chat(user.fullname, advert.title))
        }

        return userItems
    }
}