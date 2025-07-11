# ChatX - v1.0:

- It is a simple, reliable, fun and creative social network service.
- It is a valuable communication tool with others locally and worldwide, as well as to share, create, and spread information.
- ChatX allows you to share your photos and videos with your friends, families and followers.
- It also provides a chatroom for secure messaging. Multiple chatrooms can be created by a user.
- Users can like and comment on photos and follow other users to add their content to a personal feed.

# Preview:

<p float="left">
  <img src="preview/1.jpeg" width="190" />
  <img src="preview/2.jpeg" width="190" /> 
  <img src="preview/3.jpeg" width="190" />
  <img src="preview/4.jpeg" width="190" />
  <img src="preview/5.jpeg" width="190" />
  <img src="preview/6.jpeg" width="190" />
  <img src="preview/7.jpeg" width="190" />
  <img src="preview/8.jpeg" width="190" />
</p>

<!-- # Functionalities: -->

# Tools/Technologies Used:

- Kotlin
- Socket, Http
- Fragment, SharedPreferences, RecyclerView

# Project Structure:

```structure
- [:app]
    - com.example.appchatx
        - adapters
            - ChatAdapter
            - ChatroomAdapter
            - CommentsAdapter
            - FeedAdapter
            - SearchAdapter
        - auth
            - AuthenticationActivity
            - LoginFragment
            - RegisterFragment
        - models
            - Chat
            - Chatroom
            - Comment
            - Post
            - User
        - util
            - UserUtil
            - SessionUtil
            - TimeUtil
            - SecketUtil
        - ChatFragment
        - ChatroomFragment
        - CommentsActivity
        - CreatePostActivity
        - FeedFragment
        - MainActivity
        - ProfileActivity
        - SearchFragment
    - res
        - drawable
        - layout
        - menu
        - minmap
        - values
- Gradle Scripts
    - Build.gradle [Project]
    - Build.gradle [Module]
    - gradle-wrapper.properties
    - proguard-rules.pro
    - gradle.properties
    - settings.gradle
    - local.properties
```


