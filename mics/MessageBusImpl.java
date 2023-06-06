package bgu.spl.mics;


import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;


public class MessageBusImpl implements MessageBus {

    private ConcurrentHashMap<Class<? extends Message>, LinkedBlockingQueue<LinkedBlockingQueue<Message>>> bus;
    private ConcurrentHashMap<Event, Future> futures;
    private ConcurrentHashMap<MicroService, LinkedBlockingQueue<Message>> microServices;
    private static class SingletonHolder {
        private static MessageBusImpl instance = new MessageBusImpl();

    }

    private MessageBusImpl() {
        bus = new ConcurrentHashMap<>();
        futures = new ConcurrentHashMap<>();
        microServices = new ConcurrentHashMap<>();
    }

    public static MessageBusImpl getInstance() {
        return SingletonHolder.instance;
    }

    @Override
    public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
        subscribeMessage(type, m);
    }

    private void subscribeMessage(Class<? extends Message> type, MicroService m) {
        synchronized (type) {
            if (bus.containsKey(type))
                bus.get(type).add(microServices.get(m));
            else {
                LinkedBlockingQueue<LinkedBlockingQueue<Message>> queOfQue = new LinkedBlockingQueue<>();
                queOfQue.add(microServices.get(m));
                bus.put(type, queOfQue);
            }
        }
    }

    @Override
    public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
        subscribeMessage(type, m);
    }
    @SuppressWarnings("unchecked")
    @Override
    public <T> void complete(Event<T> e, T result) {
        futures.get(e).resolve(result);
        futures.remove(e);
    }

    @Override
    public void sendBroadcast(Broadcast b) {
        if (bus.containsKey(b.getClass())) {
            LinkedBlockingQueue<LinkedBlockingQueue<Message>> que = bus.get(b.getClass());
            int i = que.size();
            while (i > 0) {
                LinkedBlockingQueue<Message> mes = que.poll();
                try {
                    mes.put(b);
                    que.put(mes);
                } catch (InterruptedException e) {
                    }
                i--;
            }
        }
    }


    @Override
    public <T> Future<T> sendEvent(Event<T> e) {

        if (bus.containsKey(e.getClass())) {
            synchronized (bus.get(e.getClass())) {
                if (!bus.get(e.getClass()).isEmpty()) {
                    Future<T> f = new Future<>();
                    futures.put(e, f);
                    LinkedBlockingQueue<Message> que = bus.get(e.getClass()).poll();
                        try {
                            que.put(e);
                            bus.get(e.getClass()).put(que);
                        } catch (InterruptedException ie) {}

                    return f;
                }
            }
            return null;
        }
        return null;
    }

    @Override

    public void register(MicroService m) {
        LinkedBlockingQueue<Message> que = new LinkedBlockingQueue<>();
        this.microServices.put(m, que);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void unregister(MicroService m) {
            for (LinkedBlockingQueue<LinkedBlockingQueue<Message>> que : bus.values()) {
                if (que.contains(microServices.get(m)))
                    synchronized (que) {
                        que.remove(microServices.get(m));
                    }
            }
        LinkedBlockingQueue<Message> mes = microServices.get(m);
        while (!mes.isEmpty()) {
            if (mes.peek() instanceof Event)
                complete((Event) mes.poll(), null);
            else
                mes.poll();
        }
        microServices.remove(m);
    }

    @Override
    public Message awaitMessage(MicroService m) throws InterruptedException {
        return microServices.get(m).take();
    }


}
