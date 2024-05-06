package com.khanhbq.socketio.spring.socket.adapter;

import com.khanhbq.socketio.spring.distribution.DistributedMachineIdGenerator;
import io.socket.socketio.server.SocketIoAdapter;
import io.socket.socketio.server.SocketIoMemoryAdapter;
import io.socket.socketio.server.SocketIoNamespace;
import io.socket.socketio.server.SocketIoSocket;
import io.socket.socketio.server.parser.IOParser;
import io.socket.socketio.server.parser.Packet;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;

public class RedisAdapter extends SocketIoAdapter {

    private static final String[] EMPTY_SOCKET_EXCLUSION = new String[0];

    private static final String REDIS_ADAPTER_TOPIC = "socketRedisAdapter.";

    private final IOParser.Encoder encoder;
    private final SocketIoAdapter memoryAdapter;
    private final RTopic topic;
    private final DistributedMachineIdGenerator machineIdentityService;

    public RedisAdapter(SocketIoNamespace namespace,
                        RedissonClient redissonClient,
                        DistributedMachineIdGenerator machineIdentityService) {
        super(namespace);
        this.encoder = new IOParser.Encoder();
        this.memoryAdapter = new SocketIoMemoryAdapter.Factory().createAdapter(namespace);
        this.machineIdentityService = machineIdentityService;
        this.topic = redissonClient.getTopic(REDIS_ADAPTER_TOPIC + namespace.getName());
        initializeMessageListener();
    }

    private void initializeMessageListener() {
        this.topic.addListener(SocketBroadcastMessage.class, (channel, msg) -> handleBroadcastFromAnotherInstance(msg));
    }

    @Override
    public void broadcast(Packet packet, String[] rooms, String[] socketsExcluded) {
        memoryAdapter.broadcast(packet, rooms, socketsExcluded);
        encoder.encode(packet, encodedPackets -> publishToTopic(rooms, encodedPackets));
    }

    private void publishToTopic(String[] rooms, Object[] encodedPackets) {
        SocketBroadcastMessage message = new SocketBroadcastMessage()
                .setRooms(rooms)
                .setSource(machineIdentityService.getCurrentMachineId())
                .setData(encodedPackets);
        topic.publish(message);
    }

    @Override
    public void add(String room, SocketIoSocket socket) throws IllegalArgumentException {
        memoryAdapter.add(room, socket);
    }

    @Override
    public void remove(String room, SocketIoSocket socket) throws IllegalArgumentException {
        memoryAdapter.remove(room, socket);
    }

    @Override
    public SocketIoSocket[] listClients(String room) throws IllegalArgumentException {
        return memoryAdapter.listClients(room);
    }

    @Override
    public String[] listClientRooms(SocketIoSocket socket) throws IllegalArgumentException {
        return memoryAdapter.listClientRooms(socket);
    }

    public void handleBroadcastFromAnotherInstance(SocketBroadcastMessage message) {
        if (message.getSource().equals(machineIdentityService.getCurrentMachineId())) {
            return;
        }
        IOParser.Decoder decoder = new IOParser.Decoder();
        decoder.onDecoded((packet -> {
            memoryAdapter.broadcast(packet, message.getRooms(), EMPTY_SOCKET_EXCLUSION);
        }));
        for (Object data : message.getData()) {
            if (data instanceof String) {
                decoder.add((String) data);
            } else if (data instanceof byte[]) {
                decoder.add((byte[]) data);
            }
        }
    }
}
