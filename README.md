# SSE(Server sent events) compare with Websocket

Advantages of SSE over Websockets:

    + Transported over simple HTTP instead of a custom protocol
    + Built in support for re-connection and event-id(Client tự khởi tạo lại connection, và server tự disable khi quá timeout)
    + Simpler protocol(Dễ triển khai hơn)
    + No trouble with corporate firewalls doing packet inspection

Advantages of Websockets over SSE:

    + Real time, two directional communication
    + Native support in more browsers

Ideal use cases of SSE:

    + Stock ticker streaming
    + twitter feed updating
    + Notifications to browser

SSE gotchas:

    + No binary support
    + MAXIMUM OPEN CONNECTIONS LIMIT(MAX 6 connection trên 1 domain)

Description:

     Spring version: >= 4.2

     Postman collection example: 
            - https://www.getpostman.com/collections/7ca43f002f6094496cbf

     Detailed implementation: 
            - Backend: Class SSEController.java
            - Frontend: index.html

Author:

    + Chuyen Nguyen
    + nchuyen128@gmail.com

Reference:

    - https://stackoverflow.com/questions/5195452/websockets-vs-server-sent-events-eventsource
    - https://www.youtube.com/watch?v=HoxPgU4lFGE&ab_channel=JavaGrowth
