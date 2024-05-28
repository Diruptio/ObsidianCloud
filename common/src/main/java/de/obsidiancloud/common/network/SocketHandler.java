package de.obsidiancloud.common.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.channels.SocketChannel;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

import org.jetbrains.annotations.NotNull;

import javax.crypto.KeyGenerator;
import javax.net.ssl.SSLSocket;

public class SocketHandler {
    private final SSLSocket socket;
    private final ObjectInputStream inputStream;
    private final ObjectOutputStream outputStream;
    private byte[] privateKey;
    private byte[] publicKey;

    public SocketHandler(@NotNull SSLSocket socket) {
        this.socket = socket;
        try {
            inputStream = new ObjectInputStream(socket.getInputStream());
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            KeyPair keyPair = generator.generateKeyPair();
            privateKey = keyPair.getPrivate().getEncoded();
            publicKey = keyPair.getPublic().getEncoded();
        } catch (IOException | NoSuchAlgorithmException exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * Get the socket.
     *
     * @return The socket.
     */
    public @NotNull SSLSocket getSocket() {
        return socket;
    }

    /**
     * Get the input stream of the socket.
     *
     * @return The input stream of the socket.
     */
    public @NotNull ObjectInputStream getInputStream() {
        return inputStream;
    }

    /**
     * Get the output stream of the socket.
     *
     * @return The output stream of the socket.
     */
    public @NotNull ObjectOutputStream getOutputStream() {
        return outputStream;
    }

    /**
     * Get the public key.
     *
     * @return The public key.
     */
    public byte[] getPrivateKey() {
        return privateKey;
    }

    /**
     * Get the public key.
     *
     * @return The public key.
     */
    public byte[] getPublicKey() {
        return publicKey;
    }
}
