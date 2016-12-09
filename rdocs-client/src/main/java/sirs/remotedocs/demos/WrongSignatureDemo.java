package sirs.remotedocs.demos;


import sirs.remotedocs.ImplementationClient;
import types.Document_t;
import utils.CryptoUtils;

public class WrongSignatureDemo {

    public static void main(String[] args) throws Exception {
        ImplementationClient client = new ImplementationClient();

        client.register("Hello", "helloworld");

        client.login("Hello", "helloworld");

        Document_t doc = client.createDocument("wrong signature");

        if (doc == null)
            doc = client.downloadDocument("wrong signature", client.getUsername(), client.getClientBox().getDocumentKey("wrong signature"));

        //sign with random private key
        doc.setContent("who wrote this document?", client.getUsername(), CryptoUtils.generateKeyPair().getPrivate());

        client.uploadDocument(doc, false);

        //Check result in GUI with user 'Hello' password 'helloworld'
    }
}
