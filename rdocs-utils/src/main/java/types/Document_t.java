package types;

import utils.MiscUtils;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SignatureException;
import java.util.Date;

import static utils.CryptoUtils.sign;
import static utils.HashUtils.hash;
import static utils.HashUtils.hashInText;

public class Document_t extends Type_t {

    private final String docID;
    private final String owner;
    private String content;
    private byte[] contentHash;
    private byte[] signature;
    private String lastEditor;
    private Date timestamp;
    private boolean shared;

    public Document_t(String docID, String owner) throws Exception {
        this.docID = docID;
        this.owner = owner;
        this.lastEditor = owner;
        setContent("");
        this.shared = false;
    }

    public String getDocID() {
        return docID;
    }

    public String getOwner() {
        return owner;
    }

    public String getContent() {
        return content;
    }

    public String getLastEditor(){ return lastEditor;}

    public Date getTimestamp(){
        return timestamp;
    }

    public void setContent(String content) throws IOException, NoSuchAlgorithmException {
        this.content = content;
        this.contentHash = hash(content, null);
        this.timestamp = new Date();
        this.signature = null;
    }

    //only in trusted mode
    public void setSignedContent(String content, String editor, PrivateKey privateKey) throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeyException, NullPointerException {
        if(privateKey==null)
            throw new NullPointerException("Private Key can't be null when editing shared documents.");
        setContent(content);
        this.lastEditor = editor;
        this.signature = sign(getContentHash(), privateKey);
    }

    public byte[] getContentHash() {
        return contentHash;
    }

    public byte[] getSignature(){
        return signature;
    }

    public boolean isShared() {
        return shared;
    }

    public void setShared(boolean shared) {
        this.shared = shared;
    }

    @Override
    public void print() {
        System.out.println("----Document----");
        System.out.println("Doc ID: " + docID);
        System.out.println("Owner:  " + owner);
        System.out.println("Last Editor: " + lastEditor);
        System.out.println("Timestamp: " + MiscUtils.getDateFormatted(timestamp));
        try {
            System.out.println("Content Hash: " + hashInText(content, null));
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        System.out.println("Content:\n" + content);
    }
}
