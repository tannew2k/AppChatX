package com.example.appchatx.util

import android.util.Log
import com.pusher.client.Pusher
import com.pusher.client.PusherOptions
import com.pusher.client.channel.Channel
import com.pusher.client.channel.PusherEvent
import com.pusher.client.channel.SubscriptionEventListener
import com.pusher.client.connection.ConnectionEventListener
import com.pusher.client.connection.ConnectionState
import com.pusher.client.connection.ConnectionStateChange
import com.pusher.client.util.HttpAuthorizer

object SocketUtil {
    private const val APP_KEY = "local" // Must match Laravel Reverb's app_key
    private const val HOST = "10.0.2.2" // Android emulator → host PC
    private const val PORT = 8080 // Must match Laravel Reverb's port
    private const val AUTH_ENDPOINT = "http://10.0.2.2:8000/broadcasting/auth" // Laravel auth endpoint

    private val options = PusherOptions().apply {
        setEncrypted(false) // Use ws:// instead of wss://
        setHost(HOST)
        setWsPort(PORT)
        setWssPort(PORT)
        setActivityTimeout(30_000)
        // Add authentication for private channels
        val authorizer = HttpAuthorizer(AUTH_ENDPOINT).apply {
            setHeaders(mapOf("Authorization" to "Bearer ${UserUtil.token.orEmpty()}"))
        }
        setAuthorizer(authorizer)
    }

    private val pusher = Pusher(APP_KEY, options)
    private val subscribedChannels = mutableSetOf<String>()

    fun connect(onConnected: () -> Unit = {}) {
        if (pusher.connection.state != ConnectionState.CONNECTED) {
            pusher.connect(object : ConnectionEventListener {
                override fun onConnectionStateChange(change: ConnectionStateChange) {
                    Log.d("SocketUtil", "Connection state: ${change.currentState}")
                    if (change.currentState == ConnectionState.CONNECTED) {
                        Log.d("SocketUtil", "Connected to Reverb")
                        onConnected()
                    }
                }

                override fun onError(message: String?, code: String?, e: Exception?) {
                    Log.e("SocketUtil", "Connection error [$code]: $message", e)
                }
            }, ConnectionState.ALL)
        } else {
            Log.d("SocketUtil", "Already connected to Reverb")
            onConnected()
        }
    }

    fun subscribe(channelName: String, eventName: String, callback: (String) -> Unit) {
        if (subscribedChannels.contains(channelName)) {
            Log.d("SocketUtil", "Already subscribed to $channelName, skipping")
            return
        }
        val channel: Channel = pusher.subscribe(channelName)
        channel.bind(eventName, object : SubscriptionEventListener {
            override fun onEvent(event: PusherEvent) {
                val data = event.data ?: ""
                Log.d("SocketUtil", "📩 Channel: ${event.channelName}, Event: ${event.eventName}, Data: $data")
                callback(data)
            }
        })
        subscribedChannels.add(channelName)
        Log.d("SocketUtil", "Subscribed to $channelName")
    }

    fun unsubscribe(channelName: String) {
        if (subscribedChannels.contains(channelName)) {
            pusher.unsubscribe(channelName)
            subscribedChannels.remove(channelName)
            Log.d("SocketUtil", "Unsubscribed from $channelName")
        }
    }

    fun disconnect() {
        subscribedChannels.clear()
        pusher.disconnect()
        Log.d("SocketUtil", "🔌 Disconnected")
    }
}