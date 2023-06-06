package bgu.spl.mics;

import java.util.concurrent.ConcurrentHashMap;


public abstract class MicroService implements Runnable {

    private boolean terminated = false;
    private final String name;
    private ConcurrentHashMap<Class<? extends Message>, Callback> callBacks;


    public MicroService(String name) {
        this.name = name;
        callBacks = new ConcurrentHashMap<>();
    }

    protected final <T, E extends Event<T>> void subscribeEvent(Class<E> type, Callback<E> callback) {
        MessageBusImpl.getInstance().subscribeEvent(type,this);
        callBacks.put(type,callback);

    }


    protected final <B extends Broadcast> void subscribeBroadcast(Class<B> type, Callback<B> callback) {
        MessageBusImpl.getInstance().subscribeBroadcast(type,this);
        callBacks.put(type,callback);
    }


    protected final <T> Future<T> sendEvent(Event<T> e) {
       return MessageBusImpl.getInstance().sendEvent(e);
    }
    protected final void sendBroadcast(Broadcast b) {
        MessageBusImpl.getInstance().sendBroadcast(b);
    }
    protected final <T> void complete(Event<T> e, T result) {
        MessageBusImpl.getInstance().complete(e,result);
    }

    protected abstract void initialize();

    protected final void terminate() {
        this.terminated = true;
    }

    public final String getName() {
        return name;
    }

    @SuppressWarnings("unchecked")
    @Override
    public final void run() {
        MessageBusImpl.getInstance().register(this);
        initialize();
        while (!terminated) {
                try {
                    Message m = MessageBusImpl.getInstance().awaitMessage(this);
                    callBacks.get(m.getClass()).call(m);
                } catch (InterruptedException ie) {
                    // CHECK
                }
        }
        MessageBusImpl.getInstance().unregister(this);
    }
}
