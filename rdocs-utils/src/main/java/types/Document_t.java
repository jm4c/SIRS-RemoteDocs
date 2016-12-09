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
    private boolean signatureFaultFlag;
    private boolean integrityFaultFlag;



    public Document_t(String docID, String owner, PrivateKey privateKey) throws Exception {
        this.docID = docID;
        this.owner = owner;
        this.lastEditor = owner;
        this.signatureFaultFlag = false;
        this.integrityFaultFlag = false;
        setContent("", owner, privateKey);
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

    public void setContent(String content, String editor, PrivateKey privateKey) throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        this.content = content;
        this.contentHash = hash(content, null);

        this.lastEditor = editor;
        this.timestamp = new Date();
        this.signature = sign(getContentHash(), privateKey);
        integrityFaultFlag = false;
        signatureFaultFlag = false;

    }


    public byte[] getContentHash() {
        return contentHash;
    }

    public byte[] getSignature(){
        return signature;
    }

    public void setSignatureFaultFlag(){
        signatureFaultFlag = true;
    }

    public boolean hasSignatureFaultFlag(){
        return signatureFaultFlag;
    }

    public void setIntegrityFaultFlag(){
        integrityFaultFlag = true;
    }

    public boolean hasIntegrityFaultFlag(){
        return integrityFaultFlag;
    }



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
