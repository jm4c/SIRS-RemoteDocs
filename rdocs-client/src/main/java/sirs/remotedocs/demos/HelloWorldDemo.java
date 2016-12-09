package sirs.remotedocs.demos;


import sirs.remotedocs.ImplementationClient;
import types.Document_t;

//Example - setting up client 'Hello' with password 'helloworld' and with 2 docs named 'title example' and 'title example 3'


public class HelloWorldDemo {
    public static void main(String[] args) throws Exception {

        ImplementationClient client = new ImplementationClient();

        client.register("Hello", "helloworld");
        try {
            client.login("Hello", "helloworld");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("list of docs:");
        client.getClientBox().print();


        Document_t doc = client.createDocument("title example");
        Document_t doc2 = client.createDocument("title example"); //no document created since same title already exists
        Document_t doc3 = client.createDocument("title example 3");

        doc.setContent("example content!", client.getClientBox().getOwnerID(), client.getClientBox().getPrivateKey());

        try {
            doc2.setContent("example content!", client.getClientBox().getOwnerID(), client.getClientBox().getPrivateKey());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Expected exception, doesn't exist.");
        }

        doc3.setContent("example content 3", client.getClientBox().getOwnerID(), client.getClientBox().getPrivateKey());

        client.uploadDocument(doc, false);

        client.uploadDocument(doc3, false);

        doc.print();

        doc3.print();

        Document_t docServer = client.downloadDocument(doc.getDocID(), client.getUsername(), client.getClientBox().getDocumentKey(doc.getDocID()));
        Document_t doc3Server = client.downloadDocument(doc3.getDocID(), client.getUsername(), client.getClientBox().getDocumentKey(doc3.getDocID()));

        docServer.print();

        doc3Server.print();

        client.getClientBox().print();

    }
}
