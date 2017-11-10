# EECE4371-assignment-02

Protocol:

The client connects to the server and enters a name to join the group on the server.

The client requests the information of others in the group from the server.

  If there are no other users, the client waits until it is challenged to a game and then accepts.
  
  If there are other users, the client challenges the first user it finds to a game.
  
There are now two users engaged in a game on the server and the client has 2 main responsibilities:

  Checking if it is their turn:
  
    If it is, then they play and notify the other client (through the server) that they have played.
    
    If it is not, then they wait for a message from the other client signaling that it is now their turn.
    
  Checking if the game is over:
  
    Checking if anyone won.
    
    Checking if it is a cat game.

I am submitting this because I feel that I can't submit nothing, but I fully intend to explore this more whenever I have time.
