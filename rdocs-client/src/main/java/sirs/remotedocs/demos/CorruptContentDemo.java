package sirs.remotedocs.demos;

import sirs.remotedocs.ImplementationClient;
import types.Document_t;


public class CorruptContentDemo {

    public static void main(String[] args) throws Exception {
        ImplementationClient client = new ImplementationClient();

        client.register("Hello", "helloworld");

        client.login("Hello", "helloworld");

        Document_t doc = client.createDocument("corrupt content");

        if (doc == null)
            doc = client.downloadDocument("corrupt content", client.getUsername(), client.getClientBox().getDocumentKey("corrupt content"));

        doc.setContent("someone tampered with the content", client.getUsername(), client.getClientBox().getPrivateKey());

        //since the content cannot be changed without the hash without the content variable public we simply changed the document integrity fault flag for this test
        doc.setIntegrityFaultFlag();

        client.uploadDocument(doc, false);

        //Check result in GUI with user 'Hello' password 'helloworld'
    }
}
