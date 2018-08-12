# Tasks

- Server authentication tools
- Server general ORM
- Server external ORM endpoints
- Web basics
- Web with style
- iOS
- Auto-generate admin portal (cross-platform)
- Server ORM watch-with-socket endpoints
    
# Ideas

- Remake `JsonSerializer` for performance (low-level)
- Auto-generate test data? (reflection with annotations)
    - Might help greatly with testing
- Unit-conversion?
- Server Websocket Request object?

# In Progress

- Generic ser/des resource to pull from
    - Allows for quickly defining a way to ser/des anything across all types
    - Not done yet, need to consider ordering of generators
        - For example, what takes priority?  A specific ser/des's generator for reflection of the general one's?  How does that work with the others?

# Done

- Client Websocket
- Websocket with JSON
- Reflection annotations for fields
- Ser/des conditions and modifiers
- Ser/des class and field references