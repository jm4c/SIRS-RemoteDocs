package sirs.remotedocs;

import exceptions.IDMismatchException;
import exceptions.NullContentException;
import interfaces.InterfaceBlockServer;
import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import types.*;
import utils.CryptoUtils;
import utils.HashUtils;

public class ClientImplementation {

    private PrivateKey privateKey;
    private PublicKey publicKey;
    private Id_t clientID;

    private static InterfaceBlockServer server;

    private List fileList;

    public ClientImplementation() {

    }

    public void setClientID(Id_t headerID) throws NoSuchAlgorithmException, IOException {
        this.clientID = headerID;
    }

    private void setPrivateKey(KeyPair kp) {
        this.privateKey = kp.getPrivate();
    }

    private void setPublicKey(KeyPair kp) {
        this.publicKey = kp.getPublic();
    }

    public Id_t getClientID() {
        return clientID;
    }

    private PrivateKey getPrivateKey() {
        return privateKey;
    }

    private PublicKey getPublicKey() {
        return publicKey;
    }

    private void setFileList(List l) {
        fileList = l;
    }

    public List getFileList() {
        return fileList;
    }

    private byte[][] splitContent(Buffer_t content) {

        byte[][] filesArray = new byte[(int) Math.ceil(content.getValue().length / (double) InterfaceBlockServer.BLOCK_MAX_SIZE)][];

        int ptr = 0;

        for (int i = 0; i < filesArray.length - 1; i++) {
            filesArray[i] = Arrays.copyOfRange(content.getValue(), ptr, ptr + InterfaceBlockServer.BLOCK_MAX_SIZE);
            ptr += InterfaceBlockServer.BLOCK_MAX_SIZE;
        }
        filesArray[filesArray.length - 1] = Arrays.copyOfRange(content.getValue(), ptr, content.getValue().length);

        return filesArray;

    }

    private Buffer_t joinContent(byte[][] filesArray) {

        byte[] b = new byte[(filesArray.length - 1) * InterfaceBlockServer.BLOCK_MAX_SIZE + filesArray[filesArray.length - 1].length];
        int ptr = 0;

        for (int i = 0; i < filesArray.length; i++) {
            System.arraycopy(filesArray[i], 0, b, ptr, filesArray[i].length);
            ptr += filesArray[i].length;
        }

        return new Buffer_t(b);

    }

    public List fs_list(){
        try {
            return server.getPKeyList();
        } catch (Exception ex) {
            final String message = "Unable to retrieve Public Key list.";
            Logger.getLogger(ClientImplementation.class.getName()).log(Level.SEVERE, message, ex);
            return null;
        }
    }

    public Id_t fs_init() throws Exception {
        KeyPair kp = CryptoUtils.setKeyPair();

        setPrivateKey(kp);
        setPublicKey(kp);

        //current (empty) header file
        List<Id_t> emptyFileList = new ArrayList<>();
        Header_t header = new Header_t(emptyFileList);
        Data_t headerData = new Data_t(CryptoUtils.serialize(header));
        byte[] signature = CryptoUtils.sign(headerData.getValue(), getPrivateKey());

        Registry myReg = LocateRegistry.getRegistry("localhost");
        server = (InterfaceBlockServer) myReg.lookup("fs.Server");
        System.out.println(server.greeting() + "\n");

        System.out.println("DATA SENT (empty header): " + header.toString() + "\n");
        setClientID(server.put_k(headerData, signature, getPublicKey()));

        return getClientID();
    }

    public int fs_read(Id_t id, int pos, int size, Buffer_t contents) throws IOException {
        try {
            Data_t data = server.get(id);

            @SuppressWarnings("unchecked")
            List<Id_t> originalFileList = ((Header_t) CryptoUtils.deserialize(data.getValue())).getValue();

            byte[][] originalContentParts = new byte[originalFileList.size()][];
            for (int i = 0; i < originalFileList.size(); i++) {
                originalContentParts[i] = server.get(originalFileList.get(i)).getValue();
            }

            //all stored data
            Buffer_t src = joinContent(originalContentParts);

            byte[] buff;
            if (src.getValue().length < pos + size) {
                buff = new byte[src.getValue().length - pos];
            } else {
                buff = new byte[size];
            }

            System.arraycopy(src.getValue(), pos, buff, 0, buff.length);
            contents.setValue(buff);
            return buff.length;

        } catch (Exception ex) {
            final String message = "Unable to fulfill read request.";
            Logger.getLogger(ClientImplementation.class.getName()).log(Level.SEVERE, message, ex);
            return -1;
        }
    }

    public void fs_write(int pos, int size, Buffer_t contents) {
        System.out.println("\nNew FS write");
        try {
            if (contents == null) {
                throw new NullContentException("Content is null");
            }

            System.out.println(this.getClientID().getValue());
            //Client's ID can only be a header file
            Data_t data = server.get(this.getClientID());

            if (data == null) {
                System.out.println("data is null");
            }
            //Header file's data is always a list of other files' IDs
            @SuppressWarnings("unchecked")
            List<Id_t> originalFileList = ((Header_t) CryptoUtils.deserialize(data.getValue())).getValue();

            Buffer_t base;

            if (originalFileList.isEmpty()) {
                base = new Buffer_t(new byte[pos + size]);
            } else {
                byte[][] originalContentParts = new byte[originalFileList.size()][];
                for (int i = 0; i < originalFileList.size(); i++) {
                    originalContentParts[i] = server.get(originalFileList.get(i)).getValue();
                }
                base = joinContent(originalContentParts);

                //	puts old content into a bigger file
                if (base.getValue().length < pos + size) {
                    Buffer_t auxBase = new Buffer_t(new byte[pos + size]);
                    System.arraycopy(base.getValue(), 0, auxBase.value, 0, size);
                    base = auxBase;
                }
            }
            System.arraycopy(contents.getValue(), 0, base.value, pos, size);

            byte[][] filesArray = splitContent(base);

            List<Id_t> newFileList = new ArrayList<>();
            for (int i = 0; i < filesArray.length; i++) {
                newFileList.add(new Id_t(HashUtils.hash(filesArray[i], null)));
            }
            
            Header_t header = new Header_t(newFileList);

            Data_t headerData = new Data_t(CryptoUtils.serialize(header));
            byte[] signature = CryptoUtils.sign(headerData.getValue(), getPrivateKey());

            //uploads header first to check signature
            if (!getClientID().equals(server.put_k(headerData, signature, getPublicKey()))) {
                throw new IDMismatchException("Client's ID does not match main block ID!");
            }

            //uploads contents
            if (originalFileList.isEmpty()) {
                System.out.println("Original it's empty");
                for (int i = 0; i < newFileList.size(); i++) {
                    System.out.println("new block! (" + i + ")");
                    System.out.println(server.put_h(new Data_t(filesArray[i])).getValue());
                }
            } else {
                boolean addBlockFlag;
                for (int i = 0; i < newFileList.size(); i++) {
                    addBlockFlag = true;
                    System.out.println("\nNEW[" + i + "]:" + newFileList.get(i).getValue());

                    for (int j = 0; j < originalFileList.size(); j++) {
                        System.out.println("OLD[" + j + "]:" + originalFileList.get(j).getValue());
                        if (originalFileList.get(j).equals(newFileList.get(i))) {
                            addBlockFlag = false;
                            break;
                        }
                    }

                    if (addBlockFlag) {
                        System.out.println("new block!");
                        server.put_h(new Data_t(filesArray[i]));

                    }

                }
            }
            this.setFileList(newFileList);

        } catch (Exception ex) {
            final String message = "Unable to fulfill write request.";
            Logger.getLogger(ClientImplementation.class.getName()).log(Level.SEVERE, message, ex);
        }
    }
}
