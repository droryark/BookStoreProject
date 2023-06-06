package bgu.spl.mics;


public interface MessageBus {

    <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m);


    void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m);


    <T> void complete(Event<T> e, T result);


    void sendBroadcast(Broadcast b);


    <T> Future<T> sendEvent(Event<T> e);


    void register(MicroService m);


    void unregister(MicroService m);


    Message awaitMessage(MicroService m) throws InterruptedException;
    
}
